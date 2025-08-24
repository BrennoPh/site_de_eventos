// Conteúdo completo e corrigido para PedidoController.java

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // <-- Importe esta classe

import java.util.Optional;

@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final EventoService eventoService;
    
    @Autowired
    public PedidoController(PedidoService pedidoService, EventoService eventoService) {
        this.pedidoService = pedidoService;
        this.eventoService = eventoService;
    }

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

    @PostMapping("/pedidos")
    public String processarPedido(@RequestParam int eventoId, @RequestParam int usuarioId, 
                                  @RequestParam int quantidade, @RequestParam(required = false) String cupomCode,HttpSession session,
                                  RedirectAttributes redirectAttributes) { // <-- Adicione RedirectAttributes
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        if (usuarioLogado.getIdUsuario() == usuarioId) {
            try {
                // Tenta criar o pedido
                pedidoService.criarPedido(usuarioId, eventoId, quantidade, cupomCode);
                
                // Atualiza o usuário na sessão para refletir o novo pedido
                session.setAttribute("usuarioLogado", usuarioLogado);
                
                // Adiciona uma mensagem de sucesso
                redirectAttributes.addFlashAttribute("sucesso", "Compra realizada com sucesso!");
                return "redirect:/meus-eventos";

            } catch (RuntimeException e) {
                // Se der erro (ex: falta de ingressos), captura a mensagem
                redirectAttributes.addFlashAttribute("erro", e.getMessage());
                
                // Redireciona de volta para a página do pedido para mostrar o erro
                return "redirect:/pedidos/evento/" + eventoId;
            }
        }
        
        return "redirect:/";
    }
}