package sitedeeventos.controller;

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

import sitedeeventos.model.Evento;
import sitedeeventos.model.EventoBuilderConcreto;
import sitedeeventos.model.Organizador; // Importe a classe Organizador
import sitedeeventos.model.OrganizadorBuilderConcreto;
import sitedeeventos.model.builder.IEventoBuilder;
import sitedeeventos.model.builder.IOrganizadorBuilder; // Importe a interface do Builder
import sitedeeventos.service.EventoService;

@Controller
public class EventoController {

    @Autowired
    private EventoService eventoService;

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

    @PostMapping("/eventos")
    public String criarEvento(
            @RequestParam String nomeEvento,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dataEvento,
            @RequestParam String local,
            @RequestParam String descricao,
            @RequestParam String categoria,
            @RequestParam double preco) {

        // **A CORREÇÃO DEFINITIVA ESTÁ AQUI**
        // 1. Instancie o Builder do Organizador.
        IOrganizadorBuilder organizadorBuilder = new OrganizadorBuilderConcreto();
        
        // 2. Configure o builder e, em seguida, chame .build() para criar o objeto Organizador final.
        Organizador organizadorFinal = organizadorBuilder.idUsuario(1).nome("Organizador Principal").build();

        // 3. Agora, construa o Evento, passando o objeto 'organizadorFinal' que acabamos de criar.
        IEventoBuilder builder = new EventoBuilderConcreto();
        Evento novoEvento = builder
                            .nomeEvento(nomeEvento)
                            .dataEvento(dataEvento)
                            .local(local)
                            .descricao(descricao)
                            .categoria(categoria)
                            .preco(preco)
                            .organizador(organizadorFinal) // Agora está correto!
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