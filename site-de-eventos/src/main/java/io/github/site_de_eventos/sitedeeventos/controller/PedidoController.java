package io.github.site_de_eventos.sitedeeventos.controller;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
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

import java.util.Optional;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;

/**
 * Controlador responsável por gerenciar as requisições web relacionadas a Pedidos.
 * ... (Javadoc completo omitido para brevidade) ...
 */
@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final EventoService eventoService;
    /**
     * Repositório de Usuário, para acesso direto aos dados atualizados do usuário no banco.
     */
    private final UsuarioRepository usuarioRepository;
    
    /**
     * Construtor para injeção de dependências dos serviços e repositórios necessários.
     */
    @Autowired // A injeção de dependência via construtor é uma boa prática.
    public PedidoController(PedidoService pedidoService, EventoService eventoService, UsuarioRepository usuarioRepository) {
        this.pedidoService = pedidoService;
        this.eventoService = eventoService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Exibe a página para realizar um pedido de um evento específico.
     */
    @GetMapping("/pedidos/evento/{id}")
    public String exibirPaginaPedido(@PathVariable("id") int id, Model model, HttpSession session) {
    	Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
    	if (eventoOpt.isPresent()) {
    		model.addAttribute("evento", eventoOpt.get());
    		model.addAttribute("session", session);
    		return "pedido";
        }
        return "redirect:/";
    }

    /**
     * Processa a submissão do formulário de compra, criando um novo pedido.
     */
    @PostMapping("/pedidos")
    public String processarPedido(@RequestParam int eventoId, @RequestParam int usuarioId, 
                                  @RequestParam int quantidade, @RequestParam(required = false) String cupomCode, HttpSession session,
                                  RedirectAttributes redirectAttributes) {
    	Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        if (usuarioLogado.getIdUsuario() == usuarioId) {
            try {
                pedidoService.criarPedido(usuarioId, eventoId, quantidade, cupomCode);
                // ATUALIZAÇÃO DA SESSÃO APÓS COMPRA
                // O objeto 'usuarioLogado' na sessão está desatualizado (não tem o novo pedido).
                // Buscamos a versão mais recente do usuário no banco de dados.
                Usuario usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getIdUsuario()).get();
                // Substituímos o objeto antigo na sessão pelo novo, já com o pedido incluído.
                session.setAttribute("usuarioLogado", usuarioAtualizado);
                
                redirectAttributes.addFlashAttribute("sucesso", "Compra realizada com sucesso!");
                return "redirect:/meus-eventos";

            } catch (RuntimeException e) {
            	redirectAttributes.addFlashAttribute("erro", e.getMessage());
            	return "redirect:/pedidos/evento/" + eventoId;
            }
        }
        return "redirect:/";
    }
    
    /**
     * Processa a solicitação de cancelamento de um pedido pelo usuário.
     * Mapeado para requisições POST em "/pedidos/{pedidoId}/cancelar".
     *
     * @param pedidoId (int) O ID do pedido a ser cancelado, extraído da URL.
     * @param session (HttpSession) A sessão HTTP para validar o usuário logado.
     * @param redirectAttributes (RedirectAttributes) Para enviar mensagens de feedback.
     * @return Uma string de redirecionamento para a página "meus-eventos".
     */
    @PostMapping("/pedidos/{pedidoId}/cancelar")
    public String cancelarPedido(@PathVariable("pedidoId") int pedidoId, HttpSession session, RedirectAttributes redirectAttributes) {
        // Pega o usuário da sessão para garantir que ele está logado.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // Se não houver usuário logado, redireciona para a página de login.
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        try {
            // Chama o serviço para cancelar o pedido, passando o ID do usuário e do pedido.
            // O serviço deve conter a lógica para verificar se o usuário é o dono do pedido.
            pedidoService.cancelarPedido(usuarioLogado.getIdUsuario(), pedidoId);
            // Adiciona uma mensagem de sucesso para a próxima página.
            redirectAttributes.addFlashAttribute("sucesso", "Sua compra foi cancelada e o ingresso foi removido.");

            // --- ATUALIZAÇÃO DA SESSÃO APÓS CANCELAMENTO ---
            // O objeto 'usuarioLogado' na sessão está desatualizado (ainda contém o pedido cancelado).
            // Busca o usuário novamente do repositório para obter a lista de pedidos atualizada.
            Usuario usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getIdUsuario()).get();
            // Coloca o objeto de usuário atualizado de volta na sessão.
            session.setAttribute("usuarioLogado", usuarioAtualizado);
            
        } catch (RuntimeException e) {
            // Se o serviço lançar um erro (ex: pedido não encontrado, permissão negada)...
            // Adiciona a mensagem de erro para ser exibida na próxima página.
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }

        // Redireciona o usuário de volta para a lista de seus eventos/pedidos.
        return "redirect:/meus-eventos";
    }
}