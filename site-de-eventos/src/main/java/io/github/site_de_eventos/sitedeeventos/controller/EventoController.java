package io.github.site_de_eventos.sitedeeventos.controller;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.model.builder.IEventoBuilder;
import io.github.site_de_eventos.sitedeeventos.model.EventoBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
public class EventoController {

    @Autowired private EventoService eventoService;

    @GetMapping("/")
    public String index(Model model, HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        model.addAttribute("usuarioLogado", usuarioLogado);
        model.addAttribute("nomeUsuario", usuarioLogado != null ? usuarioLogado.getNome() : "visitante");
        model.addAttribute("eventos", eventoService.buscarTodos());
        model.addAttribute("googleMapsApiKey", "SUA_CHAVE_API_AQUI");
        return "index";
    }
    
    @GetMapping("/eventos/novo")
    public String exibirFormularioCriacao(HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado instanceof Organizador) {
            return "criar-evento";
        }
        return "redirect:/";
    }


	@PostMapping("/eventos")
	public String criarEvento(@RequestParam String nomeEvento, @RequestParam LocalDateTime dataEvento, @RequestParam String local,
	                          @RequestParam String descricao, @RequestParam String categoria, @RequestParam double preco,
	                          @RequestParam int capacidade, 
	                          @RequestParam(required = false) String imageUrl,
	                          @RequestParam(required = false) String cupomCode,
	                          @RequestParam(defaultValue = "0.0") double cupomDiscountValue,
	                          HttpSession session) {
	    Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
	    if (usuarioLogado instanceof Organizador) {
	        Organizador organizador = (Organizador) usuarioLogado;
	        IEventoBuilder builder = new EventoBuilderConcreto();
	        Evento novoEvento = builder.nomeEvento(nomeEvento).dataEvento(dataEvento).local(local)
	            .descricao(descricao).categoria(categoria).preco(preco)
	            .capacidade(capacidade)
	            .ingressosDisponiveis(capacidade)
	            .imageUrl(imageUrl) 
	            .cupomCode(cupomCode)
	            .cupomDiscountValue(cupomDiscountValue)
	            .organizador(organizador)
	            .build();
	        eventoService.save(novoEvento);
	        return "redirect:/";
	    }
	    return "redirect:/";
	}

    @GetMapping("/mapa")
    public String exibirMapa(Model model) {
        model.addAttribute("googleMapsApiKey", "SUA_CHAVE_API_AQUI");
        return "mapa";
    }

    @GetMapping("/meus-eventos")
    public String exibirMeusEventos(HttpSession session, Model model) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        if (usuarioLogado != null && usuarioLogado.getPedidos() != null) {
            model.addAttribute("pedidos", usuarioLogado.getPedidos());
        } else {
            model.addAttribute("pedidos", Collections.emptyList()); // Envia lista vazia se não houver pedidos
        }

        return "meus-eventos";
    }
    
    @GetMapping("/evento/{id}")
    public String exibirDetalhesEvento(@PathVariable("id") int id, Model model) {
        Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
        if (eventoOpt.isPresent()) {
            model.addAttribute("evento", eventoOpt.get());
        } else {
            model.addAttribute("evento", null); // Garante que o objeto seja nulo se não encontrado
        }
        return "detalhes-evento"; // Renderiza a nova página que criamos
    }

    @GetMapping("/api/eventos")
    @ResponseBody
    public List<Evento> getEventosParaMapa() {
        return eventoService.buscarTodos();
    }
}