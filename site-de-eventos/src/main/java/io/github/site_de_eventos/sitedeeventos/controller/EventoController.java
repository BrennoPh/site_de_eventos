package io.github.site_de_eventos.sitedeeventos.controller;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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
                // Delega toda a lógica de criação do evento (incluindo o uso do Builder) para a
                // camada de serviço.
                eventoService.criarNovoEvento(nomeEvento, dataEvento, local, descricao, categoria,
                        preco, capacidade, imageUrl, cupomCode,
                        cupomDiscountValue, organizador);
                redirectAttributes.addFlashAttribute("sucesso", "Evento criado com sucesso!");
                return "redirect:/meus-eventos-organizados";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("erro", e.getMessage());
                return "redirect:/eventos/novo";
            }
        }
        // Se não for um organizador, redireciona para a página inicial.
        return "redirect:/";
    }

    /**
     * Exibe a página com o mapa de eventos.
     */
    @GetMapping("/mapa")
    public String exibirMapa(Model model) {
        // Adiciona a chave da API do Google Maps para o frontend.
        model.addAttribute("googleMapsApiKey", "SUA_CHAVE_API_AQUI");
        // Renderiza a página do mapa.
        return "mapa";
    }

    /**
     * Exibe a página "Meus Eventos", que lista os pedidos do usuário logado.
     */
    @GetMapping("/meus-eventos")
    public String exibirMeusEventos(HttpSession session, Model model) {
        // Pega o objeto do usuário da sessão.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        // Verifica se o usuário está logado e se a lista de pedidos não é nula.
        if (usuarioLogado != null && usuarioLogado.getPedidos() != null) {
            // Se sim, adiciona a lista de pedidos ao 'Model'.
            model.addAttribute("pedidos", usuarioLogado.getPedidos());
        } else {
            // Caso contrário, adiciona uma lista vazia para evitar erros na view.
            model.addAttribute("pedidos", Collections.emptyList());
        }
        // Renderiza a página "meus-eventos".
        return "meus-eventos";
    }

    /**
     * Exibe a página de detalhes de um evento específico.
     */
    @GetMapping("/evento/{id}")
    public String exibirDetalhesEvento(@PathVariable("id") int id, Model model) {
        // Busca o evento pelo ID; o retorno é um Optional para tratar o caso de não
        // encontrar.
        Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
        // Se o evento foi encontrado...
        if (eventoOpt.isPresent()) {
            // Adiciona o objeto Evento ao 'Model'.
            model.addAttribute("evento", eventoOpt.get());
        } else {
            // Se não, adiciona null (a view precisa tratar este caso).
            model.addAttribute("evento", null);
        }
        // Renderiza a página de detalhes do evento.
        return "detalhes-evento";
    }

    /**
     * Endpoint de API que retorna todos os eventos em formato JSON.
     */
    @GetMapping("/api/eventos")
    @ResponseBody // Indica que o retorno do método é o corpo da resposta, não o nome de uma view.
    public List<Evento> getEventosParaMapa() {
        // Retorna a lista de eventos, que o Spring automaticamente converte para JSON.
        return eventoService.buscarTodos();
    }

    /**
     * Exibe a página com os eventos criados pelo organizador logado.
     */
    @GetMapping("/meus-eventos-organizados")
    public String exibirMeusEventosOrganizados(HttpSession session, Model model) {
        // Pega o usuário da sessão.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // Verifica se o usuário logado NÃO é um organizador.
        if (!(usuarioLogado instanceof Organizador)) {
            // Se não for, redireciona para a página inicial, pois não tem permissão.
            return "redirect:/";
        }
        // Converte o usuário para o tipo Organizador.
        Organizador organizador = (Organizador) usuarioLogado;
        // Busca no serviço todos os eventos associados a este organizador.
        List<Evento> meusEventos = eventoService.buscarPorOrganizador(organizador);
        // Adiciona a lista encontrada ao 'Model'.
        model.addAttribute("eventosOrganizados", meusEventos);
        // Renderiza a página correspondente.
        return "meus-eventos-organizados";
    }

    /**
     * Processa a solicitação de cancelamento de um evento por seu organizador.
     */
    @PostMapping("/eventos/{id}/cancelar") // Mapeia requisições POST para esta URL com ID variável.
    public String cancelarEvento(@PathVariable("id") int eventoId, HttpSession session,
            RedirectAttributes redirectAttributes) {
        // Pega o usuário logado da sessão.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

        // Medida de segurança: verifica se o usuário é um organizador.
        if (!(usuarioLogado instanceof Organizador)) {
            // Se não for, adiciona uma mensagem de erro e redireciona.
            redirectAttributes.addFlashAttribute("erro", "Acesso negado.");
            return "redirect:/";
        }
        try {
            // Tenta cancelar o evento através do serviço, passando o ID do evento e o
            // organizador.
            eventoService.cancelarEvento(eventoId, (Organizador) usuarioLogado);
            // Se tiver sucesso, adiciona uma mensagem de sucesso para ser exibida após o
            // redirecionamento.
            redirectAttributes.addFlashAttribute("sucesso", "Evento cancelado com sucesso!");
        } catch (Exception e) {
            // Se ocorrer um erro (ex: não é o dono do evento), captura a exceção.
            // Adiciona a mensagem de erro da exceção para ser exibida na próxima página.
            redirectAttributes.addFlashAttribute("erro", "Erro ao cancelar evento: " + e.getMessage());
        }

        // Redireciona de volta para a lista de eventos organizados.
        return "redirect:/meus-eventos-organizados";
    }
}
