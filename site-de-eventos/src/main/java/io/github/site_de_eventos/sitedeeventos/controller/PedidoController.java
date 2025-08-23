package io.github.site_de_eventos.sitedeeventos.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;
import io.github.site_de_eventos.sitedeeventos.service.PedidoService;

@Controller
public class PedidoController {

    private PedidoService pedidoService;
    private EventoService eventoService;
    
    @Autowired
    public PedidoController(PedidoService pedidoService, EventoService eventoService) {
        this.pedidoService = pedidoService;
        this.eventoService = eventoService;
    }

    @GetMapping("/pedidos/evento/{id}")
    public String exibirPaginaPedido(@PathVariable("id") int id, Model model) {
        Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
        if (eventoOpt.isPresent()) {
            model.addAttribute("evento", eventoOpt.get());
            return "pedido";
        }
        return "redirect:/";
    }

    @PostMapping("/pedidos")
    public String processarPedido(@RequestParam int eventoId, @RequestParam int usuarioId, @RequestParam int quantidade) {
        pedidoService.criarPedido(usuarioId, eventoId, quantidade, "");
        return "redirect:/meus-eventos";
    }
}