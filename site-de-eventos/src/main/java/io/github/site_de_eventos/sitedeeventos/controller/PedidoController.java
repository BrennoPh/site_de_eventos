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
            model.addAttribute("session", session); // Passa a sessão para a view
            return "pedido";
        }
        return "redirect:/";
    }

    @PostMapping("/pedidos")
    public String processarPedido(@RequestParam int eventoId, @RequestParam int usuarioId, @RequestParam int quantidade, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        if (usuarioLogado.getIdUsuario() == usuarioId) {
            try {
                pedidoService.criarPedido(usuarioId, eventoId, quantidade, "");
                // Atualiza o usuário na sessão para refletir o novo pedido
                session.setAttribute("usuarioLogado", usuarioLogado);
                return "redirect:/meus-eventos";
            } catch (Exception e) {
                System.err.println("Erro ao processar pedido: " + e.getMessage());
                return "redirect:/"; 
            }
        }
        
        return "redirect:/";
    }
}