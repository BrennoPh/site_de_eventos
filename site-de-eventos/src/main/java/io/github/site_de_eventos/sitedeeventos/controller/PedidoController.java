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

/**
 * Controlador responsável por gerenciar as requisições web relacionadas a Pedidos.
 * <p>
 * Esta classe lida com a exibição da página de compra e o processamento
 * de novos pedidos de ingressos.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Controller
public class PedidoController {

    /**
     * Serviço para gerenciar a lógica de negócio de Pedidos.
     * É declarado como 'final' para garantir que, após a injeção via construtor,
     * sua referência não seja alterada, promovendo a imutabilidade.
     */
    private final PedidoService pedidoService;
    
    /**
     * Serviço para gerenciar a lógica de negócio de Eventos.
     * Utilizado para buscar informações do evento ao qual o pedido está associado.
     * Também é 'final' pela mesma razão de imutabilidade.
     */
    private final EventoService eventoService;
    
    /**
     * Construtor para injeção de dependências dos serviços necessários.
     *
     * @param pedidoService (PedidoService) Serviço para a lógica de negócio de pedidos.
     * @param eventoService (EventoService) Serviço para a lógica de negócio de eventos.
     */
    @Autowired // A injeção de dependência via construtor é uma boa prática.
    public PedidoController(PedidoService pedidoService, EventoService eventoService) {
        this.pedidoService = pedidoService;
        this.eventoService = eventoService;
    }

    /**
     * Exibe a página para realizar um pedido de um evento específico.
     * Mapeado para requisições GET em "/pedidos/evento/{id}".
     *
     * @param id (int) O ID do evento para o qual o pedido será feito.
     * @param model (Model) Objeto do Spring para adicionar atributos para a view.
     * @param session (HttpSession) A sessão HTTP para obter dados do usuário.
     * @return O nome da view "pedido" para renderização, ou um redirecionamento para "/" se o evento não existir.
     */
    @GetMapping("/pedidos/evento/{id}") // Mapeia requisições HTTP GET para a URL descrita.
    public String exibirPaginaPedido(@PathVariable("id") int id, Model model, HttpSession session) {
    	// Busca o evento pelo ID fornecido na URL.
    	Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
    	// Se o evento existir...
    	if (eventoOpt.isPresent()) {
    		// Adiciona o evento encontrado ao 'Model' para a view usar.
    		model.addAttribute("evento", eventoOpt.get());
    		  // Adiciona a sessão para que a view possa acessar dados do usuário logado.
    		model.addAttribute("session", session);
    		// Renderiza a página de pedido.
    		return "pedido";
        }
    	// Se o evento não for encontrado, redireciona para a página inicial.
        return "redirect:/";
    }

    /**
     * Processa a submissão do formulário de compra, criando um novo pedido.
     * Mapeado para requisições POST em "/pedidos".
     *
     * @param eventoId (int) ID do evento sendo comprado.
     * @param usuarioId (int) ID do usuário que está comprando.
     * @param quantidade (int) Quantidade de ingressos desejada.
     * @param cupomCode (String) Código de cupom de desconto (opcional).
     * @param session (HttpSession) A sessão HTTP para validar o usuário logado.
     * @param redirectAttributes (RedirectAttributes) Para enviar mensagens de feedback (sucesso/erro) após o redirecionamento.
     * @return Uma string de redirecionamento para "/meus-eventos" em caso de sucesso, ou de volta para a página do pedido em caso de erro.
     */
    @PostMapping("/pedidos")
    public String processarPedido(@RequestParam int eventoId, @RequestParam int usuarioId, 
                                  @RequestParam int quantidade, @RequestParam(required = false) String cupomCode, HttpSession session,
                                  RedirectAttributes redirectAttributes) {
    	// Obtém o usuário logado da sessão.
    	Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

    	// Se ninguém estiver logado, redireciona para a página de login.
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        // Medida de segurança: verifica se o ID do usuário da sessão é o mesmo que está tentando fazer a compra.
        if (usuarioLogado.getIdUsuario() == usuarioId) {
            try {
            	// Tenta criar o pedido através do serviço, que contém a lógica de negócio (verificar ingressos, etc.).
                pedidoService.criarPedido(usuarioId, eventoId, quantidade, cupomCode);
                
                // Atualiza o objeto 'usuarioLogado' na sessão (caso o pedido tenha sido adicionado a ele).
                session.setAttribute("usuarioLogado", usuarioLogado);
                
                // Adiciona um "Flash Attribute": uma mensagem que sobrevive a um redirecionamento.
                redirectAttributes.addFlashAttribute("sucesso", "Compra realizada com sucesso!");
                // Redireciona para a página de "Meus Eventos" para o usuário ver sua compra.
                return "redirect:/meus-eventos";

            } catch (RuntimeException e) {
            	// Se o serviço lançar uma exceção (ex: "Ingressos esgotados")...
                // Adiciona a mensagem de erro como um Flash Attribute.
            	redirectAttributes.addFlashAttribute("erro", e.getMessage());
            	// Redireciona de volta para a página de compra, onde a mensagem de erro será exibida.
            	return "redirect:/pedidos/evento/" + eventoId;
            }
        }
        // Se o ID do usuário não bater, redireciona para a home como medida de segurança.
        return "redirect:/";
    }
}