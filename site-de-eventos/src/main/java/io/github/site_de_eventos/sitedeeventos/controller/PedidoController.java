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

/**
 * Controlador responsável por gerenciar as requisições web relacionadas a Pedidos.
 * Lida com o fluxo de compra de ingressos em múltiplas etapas.
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
     * Exibe a página de detalhes do evento, onde o usuário inicia a compra.
     */
    @GetMapping("/pedidos/evento/{id}")
    public String exibirPaginaPedido(@PathVariable("id") int id, Model model, HttpSession session) {
        Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
        if (eventoOpt.isPresent()) {
            model.addAttribute("evento", eventoOpt.get());
            return "pedido"; // Página que mostra detalhes e permite escolher a quantidade.
        }
        return "redirect:/";
    }

    /**
     * PASSO 1 (POST): Recebe a quantidade de ingressos e redireciona para o formulário de participantes.
     */
    @PostMapping("/pedidos")
    public String iniciarPedido(@RequestParam int eventoId, @RequestParam int quantidade, RedirectAttributes redirectAttributes) {
        // Adiciona os parâmetros ao RedirectAttributes para que eles sejam passados na URL do redirecionamento.
        redirectAttributes.addAttribute("eventoId", eventoId);
        redirectAttributes.addAttribute("quantidade", quantidade);
        // Redireciona para o endpoint que exibirá o formulário de dados dos participantes.
        return "redirect:/pedidos/participantes";
    }

    /**
     * PASSO 1 (GET): Exibe o formulário para o usuário preencher os dados de cada participante/ingresso.
     */
    @GetMapping("/pedidos/participantes")
    public String exibirFormularioParticipantes(@RequestParam int eventoId, @RequestParam int quantidade, Model model) {
        // Busca o evento para exibir suas informações na página.
        model.addAttribute("evento", eventoService.buscarPorId(eventoId).get());
        // Passa a quantidade de ingressos para a view, para que ela possa renderizar o número correto de campos de formulário.
        model.addAttribute("quantidade", quantidade);
        // Renderiza a página "dados-participantes.html".
        return "dados-participantes";
    }

    /**
     * PASSO 2 (POST): Processa os dados dos participantes e finaliza a compra.
     */
    @PostMapping("/pedidos/confirmar")
    public String processarPedido(
            @RequestParam int eventoId,
            @RequestParam List<String> nomeParticipante, // Spring popula esta lista com todos os inputs de nome="nomeParticipante".
            @RequestParam List<String> emailParticipante, // Spring popula esta lista com todos os inputs de nome="emailParticipante".
            @RequestParam(required = false) String cupomCode,
            HttpSession session, RedirectAttributes redirectAttributes) {
        
        // Verifica se o usuário está logado.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login"; // Se não estiver, redireciona para o login.
        }

        try {
            // Chama o serviço para criar o pedido, agora passando as listas com os dados dos participantes.
            pedidoService.criarPedido(usuarioLogado.getIdUsuario(), eventoId, nomeParticipante, emailParticipante, cupomCode);
            
            // Atualiza o objeto do usuário na sessão para refletir o novo pedido.
            Usuario usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getIdUsuario()).get();
            session.setAttribute("usuarioLogado", usuarioAtualizado);
            
            // Adiciona uma mensagem de sucesso para a próxima página.
            redirectAttributes.addFlashAttribute("sucesso", "Compra realizada com sucesso! Seus ingressos foram gerados.");
            // Redireciona para a página de "meus eventos".
            return "redirect:/meus-eventos";

        } catch (RuntimeException e) {
            // Em caso de erro, adiciona a mensagem e redireciona de volta para a página do evento.
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
            // Atualiza a sessão para remover o pedido cancelado da lista do usuário.
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
        // Garante que o usuário está logado.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) { return "redirect:/login"; }

        // Variáveis para armazenar o ingresso e o pedido ao qual ele pertence.
        Pedido pedidoPai = null;
        Ingresso ingressoEncontrado = null;

        // Itera sobre cada pedido do usuário logado.
        for (Pedido pedido : usuarioLogado.getPedidos()) {
            // Dentro de cada pedido, itera sobre cada ingresso.
            for (Ingresso ingresso : pedido.getIngressos()) {
                // Compara o ID do ingresso atual com o ID solicitado na URL.
                if (ingresso.getIdIncricao().equals(ingressoId)) {
                    // Se encontrar o ingresso, armazena o objeto do ingresso e do seu pedido "pai".
                    ingressoEncontrado = ingresso;
                    pedidoPai = pedido;
                    break; // Para a busca no loop interno, pois já encontramos o ingresso.
                }
            }
            // Se o ingresso foi encontrado no loop interno, podemos parar a busca externa também.
            if (ingressoEncontrado != null) {
                break;
            }
        }

        // Após a busca, verifica se um ingresso foi de fato encontrado.
        if (ingressoEncontrado != null) {
            // Se sim, adiciona todas as informações necessárias ao 'Model' para a view.
            model.addAttribute("usuario", usuarioLogado);
            model.addAttribute("ingresso", ingressoEncontrado);
            model.addAttribute("pedido", pedidoPai);
            model.addAttribute("evento", pedidoPai.getEvento());
            // Renderiza a página "ingresso.html".
            return "ingresso";
        } else {
            // Se o ingresso não foi encontrado na lista do usuário, ele não existe ou não pertence a ele.
            redirectAttributes.addFlashAttribute("erro", "Ingresso não encontrado ou você não tem permissão para acessá-lo.");
            return "redirect:/meus-eventos";
        }
    }
}