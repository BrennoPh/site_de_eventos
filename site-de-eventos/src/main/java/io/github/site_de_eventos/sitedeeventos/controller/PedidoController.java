package io.github.site_de_eventos.sitedeeventos.controller;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Ingresso;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;
import io.github.site_de_eventos.sitedeeventos.service.PedidoService;
import io.github.site_de_eventos.sitedeeventos.service.strategy.CalculoComTaxaServico;
import io.github.site_de_eventos.sitedeeventos.service.strategy.ICalculoPrecoPedidoStrategy;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador responsável por gerenciar as requisições web relacionadas a Pedidos.
 * Lida com o fluxo de compra de ingressos em múltiplas etapas, incluindo
 * cálculo de preço, coleta de dados de participantes e página de confirmação.
 */
@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final EventoService eventoService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public PedidoController(PedidoService pedidoService, EventoService eventoService, UsuarioRepository usuarioRepository) {
        this.pedidoService = pedidoService;
        this.eventoService = eventoService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Exibe a página inicial da compra, onde o usuário escolhe a quantidade de ingressos.
     * Já calcula o preço inicial para 1 ingresso.
     */
    @GetMapping("/pedidos/evento/{id}")
    public String exibirPaginaPedido(@PathVariable("id") int id, Model model, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        Evento evento = eventoService.buscarPorId(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        model.addAttribute("evento", evento);

        // Chama o serviço e obtém o Map com os resultados
        Map<String, Object> resumoMap = pedidoService.calcularPrecoPreview(evento, 1, "");
        // Adiciona todos os itens do Map ao Model de uma só vez
        model.addAllAttributes(resumoMap);

        model.addAttribute("quantidadeAtual", 1);
        model.addAttribute("cupomAtual", "");
        return "pedido";
    }

    /**
     * Recalcula o preço total com base na quantidade e cupom informados pelo usuário
     * e renderiza a mesma página de pedido com os valores atualizados.
     */
    @PostMapping("/pedidos/calcular")
    public String calcularEAtualizarPedido(
            @RequestParam int eventoId,
            @RequestParam int quantidade,
            @RequestParam(required = false) String cupomCode,
            Model model) {

    	Evento evento = eventoService.buscarPorId(eventoId).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        model.addAttribute("evento", evento);

        // Chama o serviço e obtém o Map
        Map<String, Object> resumoMap = pedidoService.calcularPrecoPreview(evento, quantidade, cupomCode);
        // Adiciona tudo ao Model
        model.addAllAttributes(resumoMap);

        model.addAttribute("quantidadeAtual", quantidade);
        model.addAttribute("cupomAtual", cupomCode);
        return "pedido";
    }

    /**
     * Recebe a quantidade e o cupom e redireciona para a página de dados dos participantes.
     */
    @PostMapping("/pedidos")
    public String iniciarPedido(@RequestParam int eventoId, @RequestParam int quantidade, @RequestParam(required = false) String cupomCode, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("eventoId", eventoId);
        redirectAttributes.addAttribute("quantidade", quantidade);
        redirectAttributes.addAttribute("cupomCode", cupomCode);
        return "redirect:/pedidos/participantes";
    }

    /**
     * Exibe o formulário para preenchimento dos dados dos participantes.
     */
    @GetMapping("/pedidos/participantes")
    public String exibirFormularioParticipantes(@RequestParam int eventoId, @RequestParam int quantidade, @RequestParam(required = false) String cupomCode, Model model) {
        model.addAttribute("evento", eventoService.buscarPorId(eventoId).get());
        model.addAttribute("quantidade", quantidade);
        model.addAttribute("cupomCode", cupomCode);
        return "dados-participantes";
    }

    /**
     * MÉTODO COM MARCADORES DE DEPURAÇÃO
     */
    @PostMapping("/pedidos/confirmar")
    public String revisarPedido(
            @RequestParam int eventoId,
            @RequestParam List<String> nomeParticipante,
            @RequestParam List<String> emailParticipante,
            @RequestParam(required = false) String cupomCode,
            Model model, HttpSession session) {


        try {
            if (session.getAttribute("usuarioLogado") == null) {
                return "redirect:/login";
            }

            Evento evento = eventoService.buscarPorId(eventoId)
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

            int quantidade = nomeParticipante.size();

            List<Map<String, String>> participantes = new ArrayList<>();
            for (int i = 0; i < quantidade; i++) {
                Map<String, String> participante = new HashMap<>();
                participante.put("nome", nomeParticipante.get(i));
                participante.put("email", emailParticipante.get(i));
                participantes.add(participante);
            }

            Map<String, Object> resumoMap = pedidoService.calcularPrecoPreview(evento, quantidade, cupomCode);

            model.addAttribute("evento", evento);
            model.addAttribute("quantidade", quantidade);
            model.addAttribute("cupomCode", cupomCode);
            model.addAttribute("participantes", participantes);
            model.addAllAttributes(resumoMap);

            return "confirmacao-pedido";

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Recebe a confirmação final e cria o pedido no banco de dados.
     */
    @PostMapping("/pedidos/finalizar")
    public String finalizarPedido(
            @RequestParam int eventoId,
            @RequestParam List<String> nomeParticipante,
            @RequestParam List<String> emailParticipante,
            @RequestParam(required = false) String cupomCode,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        try {
            pedidoService.criarPedido(usuarioLogado.getIdUsuario(), eventoId, nomeParticipante, emailParticipante, cupomCode);
            
            Usuario usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getIdUsuario()).get();
            session.setAttribute("usuarioLogado", usuarioAtualizado);
            
            redirectAttributes.addFlashAttribute("sucesso", "Compra realizada com sucesso! Seus ingressos foram gerados.");
            return "redirect:/meus-eventos";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            return "redirect:/pedidos/evento/" + eventoId;
        }
    }

    /**
     * Processa o cancelamento de um pedido.
     */
    @PostMapping("/pedidos/{pedidoId}/cancelar")
    public String cancelarPedido(@PathVariable("pedidoId") int pedidoId, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) { return "redirect:/login"; }
        try {
            pedidoService.cancelarPedido(usuarioLogado.getIdUsuario(), pedidoId);
            redirectAttributes.addFlashAttribute("sucesso", "Sua compra foi cancelada.");
            Usuario usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getIdUsuario()).get();
            session.setAttribute("usuarioLogado", usuarioAtualizado);
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        return "redirect:/meus-eventos";
    }

    /**
     * Exibe uma página para visualização/impressão de um ingresso específico.
     */
    @GetMapping("/ingressos/{ingressoId}/imprimir")
    public String exibirPaginaIngresso(@PathVariable("ingressoId") String ingressoId, 
                                       HttpSession session, 
                                       Model model, 
                                       RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) { return "redirect:/login"; }

        for (Pedido pedido : usuarioLogado.getPedidos()) {
            for (Ingresso ingresso : pedido.getIngressos()) {
                if (ingresso.getIdIncricao().equals(ingressoId)) {
                    model.addAttribute("usuario", usuarioLogado);
                    model.addAttribute("ingresso", ingresso);
                    model.addAttribute("pedido", pedido);
                    model.addAttribute("evento", pedido.getEvento());
                    return "ingresso";
                }
            }
        }

        redirectAttributes.addFlashAttribute("erro", "Ingresso não encontrado ou você não tem permissão para acessá-lo.");
        return "redirect:/meus-eventos";
    }

}