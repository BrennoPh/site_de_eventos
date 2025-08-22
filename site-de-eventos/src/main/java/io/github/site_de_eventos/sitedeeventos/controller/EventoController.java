package io.github.site_de_eventos.sitedeeventos.controller;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpSession;
import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.model.builder.IEventoBuilder;
import io.github.site_de_eventos.sitedeeventos.model.EventoBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;

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
        // Apenas permite o acesso se o usuário for um Organizador
        if (usuarioLogado instanceof Organizador) {
            return "criar-evento";
        }
        return "redirect:/"; // Se não for, redireciona para a home
    }

    @PostMapping("/eventos")
    public String criarEvento(@RequestParam String nomeEvento,@RequestParam LocalDateTime dataEvento,@RequestParam String local, 
    @RequestParam String descricao, 
    @RequestParam String categoria,
    @RequestParam double preco, 
    HttpSession session) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        // Apenas processa se o usuário for um Organizador
        if (usuarioLogado instanceof Organizador) {
            Organizador organizador = (Organizador) usuarioLogado;
            IEventoBuilder builder = new EventoBuilderConcreto();
            Evento novoEvento = builder.nomeEvento(nomeEvento).dataEvento(dataEvento).local(local)
                .descricao(descricao).categoria(categoria).preco(preco)
                .organizador(organizador).build();
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
    public String exibirMeusEventos() {
        return "meus-eventos";
    }

    @GetMapping("/api/eventos")
    @ResponseBody
    public List<Evento> getEventosParaMapa() {
        return eventoService.buscarTodos();
    }
}