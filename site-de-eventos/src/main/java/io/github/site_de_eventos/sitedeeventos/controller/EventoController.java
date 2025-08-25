package io.github.site_de_eventos.sitedeeventos.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.service.EventoService;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador responsável por gerenciar as requisições web relacionadas a
 * Eventos.
 * <p>
 * Esta classe lida com a exibição, criação, busca e cancelamento de eventos,
 * interagindo com a camada de serviço ({@link EventoService}) para executar a
 * lógica de negócio.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Controller // Anotação que marca esta classe como um Controller do Spring MVC.
public class EventoController {

    /**
     * Serviço para gerenciar a lógica de negócio de Eventos.
     */
    @Autowired // Anotação que instrui o Spring a injetar uma instância de EventoService aqui.
    private EventoService eventoService;

    /**
     * Exibe a página principal (index) com a lista de eventos, com suporte a busca.
     * Mapeado para requisições GET em "/".
     */
    @GetMapping("/") // Mapeia requisições HTTP GET para a URL raiz ("/") para este método.
    public String index(Model model, HttpSession session, @RequestParam(name = "q", required = false) String query) {
        // Pega o objeto 'usuarioLogado' da sessão HTTP. Se não houver ninguém logado,
        // será null.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        // Adiciona o objeto do usuário ao 'Model', para que a página (view) possa
        // acessá-lo.
        model.addAttribute("usuarioLogado", usuarioLogado);
        // Adiciona o nome do usuário (ou "visitante") para ser exibido na página.
        model.addAttribute("nomeUsuario", usuarioLogado != null ? usuarioLogado.getNome() : "visitante");
        // Chama o serviço para buscar os eventos, passando o termo de busca (pode ser
        // nulo).
        List<Evento> eventos = eventoService.buscarEventos(query);
        // Adiciona a lista de eventos resultante ao 'Model'.
        model.addAttribute("eventos", eventos);
        // Adiciona a chave da API do Google Maps ao 'Model' para ser usada no frontend.
        model.addAttribute("googleMapsApiKey", "SUA_CHAVE_API_AQUI");
        // Retorna o nome do arquivo de template (ex: "index.html") que o Spring deve
        // renderizar.
        return "index";
    }

    /**
     * Exibe o formulário para criação de um novo evento.
     * Apenas usuários do tipo {@link Organizador} podem acessar esta página.
     */
    @GetMapping("/eventos/novo")
    public String exibirFormularioCriacao(HttpSession session) {
        // Pega o usuário da sessão para verificar suas permissões.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        // Verifica se o usuário logado é uma instância da classe Organizador.
        if (usuarioLogado instanceof Organizador) {
            // Se for um organizador, renderiza a página de criação de evento.
            return "criar-evento";
        }
        // Se não for um organizador, redireciona para a página inicial.
        return "redirect:/";
    }

    /**
     * Processa a submissão do formulário de criação de um novo evento.
     */
    @PostMapping("/eventos")
    public String criarEvento(@RequestParam String nomeEvento, @RequestParam LocalDateTime dataEvento,
            @RequestParam String local,
            @RequestParam String descricao, @RequestParam String categoria, @RequestParam double preco,
            @RequestParam int capacidade,
            @RequestParam(required = false) String imageUrl,
            @RequestParam(required = false) String cupomCode,
            @RequestParam(defaultValue = "0.0") double cupomDiscountValue,
            HttpSession session, RedirectAttributes redirectAttributes) {
        // Pega o usuário da sessão para verificar se ele pode criar eventos.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // Apenas continua se o usuário for um organizador.
        if (usuarioLogado instanceof Organizador) {
            try {
                // Converte o tipo do usuário para Organizador.
                Organizador organizador = (Organizador) usuarioLogado;
                // Delega toda a lógica de criação do evento para a camada de serviço.
                eventoService.criarNovoEvento(nomeEvento, dataEvento, local, descricao, categoria,
                        preco, capacidade, imageUrl, cupomCode,
                        cupomDiscountValue, organizador);
                // Adiciona uma mensagem de sucesso que será exibida após o redirecionamento.
                redirectAttributes.addFlashAttribute("sucesso", "Evento criado com sucesso!");
                // Redireciona para a página que lista os eventos do organizador.
                return "redirect:/meus-eventos-organizados";
            } catch (Exception e) {
                // Em caso de erro, adiciona a mensagem da exceção para ser exibida.
                redirectAttributes.addFlashAttribute("erro", e.getMessage());
                // Redireciona de volta para o formulário de criação.
                return "redirect:/eventos/novo";
            }
        }
        // Se não for um organizador, redireciona para a página inicial.
        return "redirect:/";
    }

    // ... (demais métodos já comentados anteriormente) ...

}