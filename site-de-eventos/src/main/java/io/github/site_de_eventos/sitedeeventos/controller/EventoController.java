package io.github.site_de_eventos.sitedeeventos.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.EventoBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.OrganizadorBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.builder.IEventoBuilder;
import io.github.site_de_eventos.sitedeeventos.model.builder.IOrganizadorBuilder;
import io.github.site_de_eventos.sitedeeventos.repository.OrganizadorRepository;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;

@Controller
public class EventoController {

    @Autowired
    private EventoService eventoService;
    private OrganizadorRepository organizadorRepository;		
    
    public EventoController(EventoService eventoService, OrganizadorRepository organizadorRepository) {
    	this.eventoService = eventoService;
    	this.organizadorRepository = organizadorRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<Evento> eventos = eventoService.buscarTodos();
        model.addAttribute("eventos", eventos);
        model.addAttribute("googleMapsApiKey", "SUA_CHAVE_API_AQUI");
        return "index";
    }

    @GetMapping("/eventos/novo")
    public String exibirFormularioCriacao() {
        return "criar-evento";
    }

    @PostMapping("/")
    public String criarEvento(
            @RequestParam String nomeEvento,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataEvento,
            @RequestParam String local,
            @RequestParam String descricao,
            @RequestParam String categoria,
            @RequestParam double preco) {

        Organizador organizadorExistente = organizadorRepository.findById(1).orElseThrow(() -> new IllegalStateException("ERRO: Organizador com ID 1 não foi encontrado no banco de dados."));


        // 3. Agora, construa o Evento, passando o objeto 'organizadorFinal' que acabamos de criar.
        IEventoBuilder builder = new EventoBuilderConcreto();
        Evento novoEvento = builder
                            .nomeEvento(nomeEvento)
                            .dataEvento(dataEvento)
                            .local(local)
                            .descricao(descricao)
                            .categoria(categoria)
                            .preco(preco)
                            .organizador(organizadorExistente) // Agora está correto!
                            .build();
        
        eventoService.save(novoEvento);

        return "redirect:/"; // Sucesso!
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