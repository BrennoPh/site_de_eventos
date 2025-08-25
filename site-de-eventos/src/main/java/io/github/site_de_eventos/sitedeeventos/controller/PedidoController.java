package io.github.site_de_eventos.sitedeeventos.controller;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Ingresso;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;
import io.github.site_de_eventos.sitedeeventos.service.PedidoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/pedidos/evento/{id}")
    public String exibirPaginaPedido(@PathVariable("id") int id, Model model, HttpSession session) {
        Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
        if (eventoOpt.isPresent()) {
            model.addAttribute("evento", eventoOpt.get());
            return "pedido";
        }
        return "redirect:/";
    }

    // --- PASSO 1 DO FLUXO ---
    // Este método agora apenas redireciona para a página de preenchimento dos dados.
    @PostMapping("/pedidos")
    public String iniciarPedido(@RequestParam int eventoId, @RequestParam int quantidade, RedirectAttributes redirectAttributes) {
        redirectAttributes.addAttribute("eventoId", eventoId);
        redirectAttributes.addAttribute("quantidade", quantidade);
        return "redirect:/pedidos/participantes";
    }

    // --- NOVO MÉTODO (GET) ---
    // Mostra a página com os formulários para cada participante.
    @GetMapping("/pedidos/participantes")
    public String exibirFormularioParticipantes(@RequestParam int eventoId, @RequestParam int quantidade, Model model) {
        model.addAttribute("evento", eventoService.buscarPorId(eventoId).get());
        model.addAttribute("quantidade", quantidade);
        return "dados-participantes"; // Nome do novo arquivo HTML
    }

    // --- NOVO MÉTODO (POST) E PASSO 2 DO FLUXO ---
    // Processa os dados dos participantes e finaliza a compra.
    @PostMapping("/pedidos/confirmar")
    public String processarPedido(
            @RequestParam int eventoId,
            @RequestParam List<String> nomeParticipante, // Recebe uma lista de nomes
            @RequestParam List<String> emailParticipante, // Recebe uma lista de e-mails
            @RequestParam(required = false) String cupomCode,
            HttpSession session, RedirectAttributes redirectAttributes) {
        
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        try {
            // Chama o serviço (que vamos modificar) para criar o pedido com os dados dos participantes
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

    @GetMapping("/ingressos/{ingressoId}/imprimir")
    public String exibirPaginaIngresso(@PathVariable("ingressoId") String ingressoId, 
                                       HttpSession session, 
                                       Model model, 
                                       RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) { return "redirect:/login"; }

        Pedido pedidoPai = null;
        Ingresso ingressoEncontrado = null;

        // Itera sobre cada pedido do usuário
        for (Pedido pedido : usuarioLogado.getPedidos()) {
            // Itera sobre cada ingresso dentro do pedido
            for (Ingresso ingresso : pedido.getIngressos()) {
                if (ingresso.getIdIncricao().equals(ingressoId)) {
                    // Se encontrarmos o ingresso, guardamos ele e o seu pedido pai
                    ingressoEncontrado = ingresso;
                    pedidoPai = pedido;
                    break; // Para o loop interno
                }
            }
            if (ingressoEncontrado != null) {
                break; // Para o loop externo
            }
        }

        // Verifica se o ingresso foi encontrado
        if (ingressoEncontrado != null) {
            // Se encontrou, popula o modelo com os dados corretos
            model.addAttribute("usuario", usuarioLogado);
            model.addAttribute("ingresso", ingressoEncontrado);
            model.addAttribute("pedido", pedidoPai); // Usa o pedido pai que encontramos
            model.addAttribute("evento", pedidoPai.getEvento()); // Pega o evento a partir do pedido pai
            return "ingresso";
        } else {
            // Se NÃO encontrou, redireciona com uma mensagem de erro
            redirectAttributes.addFlashAttribute("erro", "Ingresso não encontrado ou você não tem permissão para acessá-lo.");
            return "redirect:/meus-eventos";
        }
    }
}