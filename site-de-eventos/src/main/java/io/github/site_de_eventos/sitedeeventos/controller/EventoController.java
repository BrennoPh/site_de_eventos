package io.github.site_de_eventos.sitedeeventos.controller;

//Importações de classes necessárias do projeto e do Spring Framework
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

/**
 * Controlador responsável por gerenciar as requisições web relacionadas a Eventos.
 * <p>
 * Esta classe lida com a exibição, criação e busca de eventos, interagindo com
 * a camada de serviço ({@link EventoService}) para executar a lógica de negócio.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Controller // Anotação que marca esta classe como um Controller do Spring MVC, responsável por lidar com requisições web.
public class EventoController {

    /**
     * Serviço para gerenciar a lógica de negócio de Eventos.
     */
    @Autowired // Anotação que instrui o Spring a injetar uma instância de EventoService aqui.
    private EventoService eventoService;

    /**
     * Exibe a página principal (index) com a lista de eventos.
     * Mapeado para requisições GET em "/".
     *
     * @param model (Model) Objeto do Spring para adicionar atributos para a view.
     * @param session (HttpSession) A sessão HTTP para obter o usuário logado.
     * @param query (String) Parâmetro de busca opcional para filtrar eventos por nome.
     * @return O nome da view "index" para renderização.
     */
    @GetMapping("/") // Mapeia requisições HTTP GET para a URL raiz ("/") para este método.
    public String index(Model model, HttpSession session, @RequestParam(name = "q", required = false) String query) {
    	// Pega o objeto 'usuarioLogado' da sessão HTTP. Se não houver ninguém logado, será null.
    	Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
    	 // Adiciona o objeto do usuário ao 'Model', para que a página (view) possa acessá-lo.
    	model.addAttribute("usuarioLogado", usuarioLogado);
    	// Adiciona o nome do usuário (ou "visitante" caso não tenha logado) para ser exibido na página.
    	model.addAttribute("nomeUsuario", usuarioLogado != null ? usuarioLogado.getNome() : "visitante");
        
        
    	// Declara uma lista para armazenar os eventos a serem exibidos.
        List<Evento> eventos;
        // Verifica se um parâmetro de busca 'q' foi enviado na URL.
        if (query != null && !query.trim().isEmpty()) {
        	// Se houver uma busca, chama o serviço para buscar eventos pelo nome.
        	eventos = eventoService.buscarPorNome(query);
        } else {
        	// Se não houver busca, chama o serviço para listar todos os eventos.
        	eventos = eventoService.buscarTodos();
        }
        // Adiciona a lista de eventos (filtrada ou completa) ao 'Model'.
        model.addAttribute("eventos", eventos);
        // Adiciona a chave da API do Google Maps ao 'Model' para ser usada no frontend.
        model.addAttribute("googleMapsApiKey", "SUA_CHAVE_API_AQUI");
     // Retorna o nome do arquivo de template (ex: "index.html") que o Spring deve renderizar.
        return "index";
    }
    
    /**
     * Exibe o formulário para criação de um novo evento.
     * Mapeado para requisições GET em "/eventos/novo".
     * Apenas usuários do tipo {@link Organizador} podem acessar esta página.
     *
     * @param session (HttpSession) A sessão HTTP para verificar o tipo de usuário logado.
     * @return O nome da view "criar-evento" ou um redirecionamento para "/" se o usuário não for organizador.
     */
    @GetMapping("/eventos/novo") // Mapeia requisições GET para a URL "/eventos/novo".
    public String exibirFormularioCriacao(HttpSession session) {
    	// Pega o usuário da sessão para verificar suas permissões.
    	Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
    	// Verifica se o usuário logado é uma instância da classe Organizador.
    	if (usuarioLogado instanceof Organizador) {
    		// Se for um organizador, permite o acesso e renderiza a página de criação de evento.
    		return "criar-evento";
        }
    	// Se não for um organizador, redireciona o usuário para a página inicial.
        return "redirect:/";
    }

    /**
     * Processa a submissão do formulário de criação de um novo evento.
     * Mapeado para requisições POST em "/eventos".
     *
     * @param nomeEvento (String) Nome do evento.
     * @param dataEvento (LocalDateTime) Data e hora do evento.
     * @param local (String) Local do evento.
     * @param descricao (String) Descrição do evento.
     * @param categoria (String) Categoria do evento.
     * @param preco (double) Preço do ingresso.
     * @param capacidade (int) Capacidade máxima de participantes.
     * @param imageUrl (String) URL da imagem de divulgação (opcional).
     * @param cupomCode (String) Código do cupom de desconto (opcional).
     * @param cupomDiscountValue (double) Valor do desconto do cupom.
     * @param session (HttpSession) A sessão HTTP para obter o organizador logado.
     * @return Uma string de redirecionamento para a página principal ("/").
     */
    @PostMapping("/eventos") // Mapeia requisições POST de formulárips para a URL "/eventos".
    public String criarEvento(@RequestParam String nomeEvento, @RequestParam LocalDateTime dataEvento, @RequestParam String local,
                              @RequestParam String descricao, @RequestParam String categoria, @RequestParam double preco,
                              @RequestParam int capacidade, 
                              @RequestParam(required = false) String imageUrl,
                              @RequestParam(required = false) String cupomCode,
                              @RequestParam(defaultValue = "0.0") double cupomDiscountValue,
                              HttpSession session) {
    	// Novamente, verifica se o usuário logado é um organizador.
    	Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado instanceof Organizador) {
        	// Converte o tipo do usuário para Organizador.
        	Organizador organizador = (Organizador) usuarioLogado;
        	 // Utiliza o padrão de projeto Builder para criar o objeto Evento de forma limpa.
        	IEventoBuilder builder = new EventoBuilderConcreto();
            Evento novoEvento = builder
            		.nomeEvento(nomeEvento)
            		.dataEvento(dataEvento)
            		.local(local)
	                .descricao(descricao)
	                .categoria(categoria)
	                .preco(preco)
	                .capacidade(capacidade)
	                .ingressosDisponiveis(capacidade) // Ingressos disponíveis iniciam com a capacidade total.
	                .imageUrl(imageUrl) 
	                .cupomCode(cupomCode)
	                .cupomDiscountValue(cupomDiscountValue)
	                .organizador(organizador) // Associa o evento ao organizador logado.
	                .build(); // Constrói o objeto Evento final.
            
            // Chama o serviço para salvar o novo evento no banco de dados.
            eventoService.save(novoEvento);
        }
        // Independentemente de o evento ter sido criado ou não (porque o usuário não era um organizador),
        // o método sempre terminará redirecionando para a página inicial.
        return "redirect:/";
    }

    /**
     * Exibe a página com o mapa de eventos.
     * Mapeado para requisições GET em "/mapa".
     *
     * @param model (Model) Objeto do Spring para adicionar atributos para a view.
     * @return O nome da view "mapa" para renderização.
     */
    @GetMapping("/mapa")
    public String exibirMapa(Model model) {
        model.addAttribute("googleMapsApiKey", "SUA_CHAVE_API_AQUI");
        return "mapa";
    }

    /**
     * Exibe a página "Meus Eventos", que lista os pedidos do usuário logado.
     * Mapeado para requisições GET em "/meus-eventos".
     *
     * @param session (HttpSession) A sessão HTTP para obter o usuário logado.
     * @param model (Model) Objeto do Spring para adicionar atributos para a view.
     * @return O nome da view "meus-eventos" para renderização.
     */
    @GetMapping("/meus-eventos") // Mapeia requisições GET para a URL "/meus-eventos".
    public String exibirMeusEventos(HttpSession session, Model model) {
    	// Pega o objeto 'usuarioLogado' da sessão HTTP para identificar quem está acessando a página.
    	Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");

    	// Verifica se há um usuário logado e se sua lista de pedidos não é nula, para evitar erros.
        if (usuarioLogado != null && usuarioLogado.getPedidos() != null) {
        	// Se o usuário e a lista são válidos, adiciona a lista de pedidos ao 'Model'.
            // A view poderá então acessar essa lista pela variável "pedidos".
        	model.addAttribute("pedidos", usuarioLogado.getPedidos());
        } else {
        	// Caso contrário (ninguém logado ou a lista é nula)...
            // Adiciona uma lista vazia e segura (imutável) ao 'Model'. 
            // Isso garante que a página não quebre ao tentar iterar sobre uma lista nula.
            model.addAttribute("pedidos", Collections.emptyList());
        }
     // Retorna o nome do arquivo de template (ex: "meus-eventos.html") a ser renderizado.
        return "meus-eventos";
    }
    
    /**
     * Exibe a página de detalhes de um evento específico.
     * Mapeado para requisições GET em "/evento/{id}".
     *
     * @param id (int) O ID do evento extraído da URL.
     * @param model (Model) Objeto do Spring para adicionar atributos para a view.
     * @return O nome da view "detalhes-evento" para renderização.
     */
    @GetMapping("/evento/{id}") // Mapeia a URL, onde {id} é uma variável (ex: /evento/123).
    public String exibirDetalhesEvento(@PathVariable("id") int id, Model model) {
    	// @PathVariable extrai o valor de 'id' da URL e o atribui ao parâmetro 'id' do método.
        // Busca o evento pelo ID; o retorno é um Optional para evitar NullPointerException.
        Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
        // Verifica se o Optional contém um objeto Evento.
    	Optional<Evento> eventoOpt = eventoService.buscarPorId(id);
        if (eventoOpt.isPresent()) {
        	// Se o evento foi encontrado, adiciona-o ao 'Model'.
        	model.addAttribute("evento", eventoOpt.get());
        } else {
        	// Se não foi encontrado, adiciona null ao 'Model'.
        	model.addAttribute("evento", null);
        }
        // Renderiza a página de detalhes do evento.
        return "detalhes-evento";
    }

    /**
     * Endpoint de API que retorna todos os eventos em formato JSON.
     * Mapeado para requisições GET em "/api/eventos".
     * Utilizado para alimentar o mapa de eventos dinamicamente.
     *
     * @return Uma lista (List) de {@link Evento} em formato JSON.
     */
    @GetMapping("/api/eventos") // Mapeia a URL para um endpoint de API.
    @ResponseBody // Indica que o retorno do método (a lista de eventos) deve ser escrito diretamente no corpo da resposta HTTP.
    public List<Evento> getEventosParaMapa() {
    	// Retorna a lista de todos os eventos, que o Spring converterá para JSON.
    	return eventoService.buscarTodos();
    }
}	