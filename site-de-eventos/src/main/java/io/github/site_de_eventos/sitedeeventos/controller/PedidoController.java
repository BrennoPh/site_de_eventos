package io.github.site_de_eventos.sitedeeventos.controller;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Ingresso;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controlador responsável por gerenciar as requisições web relacionadas a Pedidos.
 * <p>
 * Lida com o fluxo de compra de ingressos em múltiplas etapas, incluindo
 * cálculo de preço, coleta de dados de participantes e página de confirmação.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Controller
public class PedidoController {

    private final PedidoService pedidoService;
    private final EventoService eventoService;
    private final UsuarioRepository usuarioRepository;

    /**
     * Construtor para injeção de dependências dos serviços e repositórios necessários.
     * @param pedidoService Serviço para a lógica de negócio de pedidos.
     * @param eventoService Serviço para a lógica de negócio de eventos.
     * @param usuarioRepository Repositório para acesso direto aos dados do usuário.
     */
    @Autowired
    public PedidoController(PedidoService pedidoService, EventoService eventoService, UsuarioRepository usuarioRepository) {
        this.pedidoService = pedidoService;
        this.eventoService = eventoService;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Já calcula o preço inicial para 1 ingresso.
     * @param id O ID do evento a ser comprado.
     * @param model Objeto para passar dados para a view.
     * @param session A sessão HTTP para verificar o login do usuário.
     * @return O nome da view "pedido" para renderização.
     */
    @GetMapping("/pedidos/evento/{id}")
    public String exibirPaginaPedido(@PathVariable("id") int id, Model model, HttpSession session) {
        // Verifica se o usuário está logado; se não, redireciona para a página de login.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        // Busca o evento no banco de dados ou lança uma exceção se não for encontrado.
        Evento evento = eventoService.buscarPorId(id).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        // Adiciona o evento ao model para ser exibido na página.
        model.addAttribute("evento", evento);

        // Calcula um resumo do preço inicial (para 1 ingresso, sem cupom).
        Map<String, Object> resumoMap = pedidoService.calcularPrecoPreview(evento, 1, "");
        // Adiciona todos os valores calculados (subtotal, taxas, total, etc.) ao model de uma vez.
        model.addAllAttributes(resumoMap);

        // Adiciona a quantidade e o cupom atuais ao model para preencher o formulário.
        model.addAttribute("quantidadeAtual", 1);
        model.addAttribute("cupomAtual", "");
        // Renderiza a página "pedido.html".
        return "pedido";
    }

    /**
     * e renderiza a mesma página com os valores atualizados.
     * @param eventoId ID do evento.
     * @param quantidade Nova quantidade de ingressos.
     * @param cupomCode Novo código de cupom inserido.
     * @param model Objeto para passar os dados atualizados para a view.
     * @return O nome da view "pedido" para renderização.
     */
    @PostMapping("/pedidos/calcular")
    public String calcularEAtualizarPedido(
            @RequestParam int eventoId,
            @RequestParam int quantidade,
            @RequestParam(required = false) String cupomCode,
            Model model) {

        // Busca o evento novamente.
    	Evento evento = eventoService.buscarPorId(eventoId).orElseThrow(() -> new RuntimeException("Evento não encontrado"));
        // Adiciona o evento ao model.
        model.addAttribute("evento", evento);

        // Chama o serviço para recalcular o resumo do preço com os novos dados.
        Map<String, Object> resumoMap = pedidoService.calcularPrecoPreview(evento, quantidade, cupomCode);
        // Adiciona os novos valores calculados ao model.
        model.addAllAttributes(resumoMap);

        // Devolve a quantidade e o cupom para a view para que os campos continuem preenchidos.
        model.addAttribute("quantidadeAtual", quantidade);
        model.addAttribute("cupomAtual", cupomCode);
        // Re-renderiza a mesma página "pedido.html" com os dados atualizados.
        return "pedido";
    }

    /**
     * para a página de preenchimento dos dados dos participantes.
     * @param eventoId ID do evento.
     * @param quantidade Quantidade de ingressos.
     * @param cupomCode Cupom de desconto.
     * @param redirectAttributes Objeto para passar atributos através do redirecionamento.
     * @return Uma string de redirecionamento para a próxima etapa.
     */
    @PostMapping("/pedidos")
    public String iniciarPedido(@RequestParam int eventoId, @RequestParam int quantidade, @RequestParam(required = false) String cupomCode, RedirectAttributes redirectAttributes) {
        // Adiciona os dados como parâmetros na URL de redirecionamento.
        redirectAttributes.addAttribute("eventoId", eventoId);
        redirectAttributes.addAttribute("quantidade", quantidade);
        redirectAttributes.addAttribute("cupomCode", cupomCode);
        // Redireciona para o endpoint que exibe o formulário dos participantes.
        return "redirect:/pedidos/participantes";
    }

    /**
     * @param eventoId ID do evento (recebido via redirecionamento).
     * @param quantidade Quantidade de ingressos (recebido via redirecionamento).
     * @param cupomCode Cupom (recebido via redirecionamento).
     * @param model Objeto para passar dados para a view.
     * @return O nome da view "dados-participantes" para renderização.
     */
    @GetMapping("/pedidos/participantes")
    public String exibirFormularioParticipantes(@RequestParam int eventoId, @RequestParam int quantidade, @RequestParam(required = false) String cupomCode, Model model) {
        // Busca o evento para exibir detalhes na página.
        model.addAttribute("evento", eventoService.buscarPorId(eventoId).get());
        // Passa a quantidade para a view renderizar o número correto de campos.
        model.addAttribute("quantidade", quantidade);
        // Passa o cupom para a próxima etapa.
        model.addAttribute("cupomCode", cupomCode);
        // Renderiza a página "dados-participantes.html".
        return "dados-participantes";
    }

    /**
     * @param eventoId ID do evento.
     * @param nomeParticipante Lista com os nomes dos participantes.
     * @param emailParticipante Lista com os e-mails dos participantes.
     * @param cupomCode Cupom de desconto.
     * @param model Objeto para passar todos os dados para a página de confirmação.
     * @param session A sessão HTTP.
     * @return O nome da view "confirmacao-pedido" para renderização.
     */
    @PostMapping("/pedidos/confirmar")
    public String revisarPedido(
            @RequestParam int eventoId,
            @RequestParam List<String> nomeParticipante,
            @RequestParam List<String> emailParticipante,
            @RequestParam(required = false) String cupomCode,
            Model model, HttpSession session) {

        try {
            // Garante que o usuário ainda está logado.
            if (session.getAttribute("usuarioLogado") == null) {
                return "redirect:/login";
            }

            // Busca os dados do evento.
            Evento evento = eventoService.buscarPorId(eventoId)
                    .orElseThrow(() -> new RuntimeException("Evento não encontrado"));

            // Determina a quantidade de ingressos pelo tamanho da lista de nomes recebida.
            int quantidade = nomeParticipante.size();

            // Cria uma estrutura de dados mais organizada (lista de mapas) para os participantes.
            List<Map<String, String>> participantes = new ArrayList<>();
            for (int i = 0; i < quantidade; i++) {
                Map<String, String> participante = new HashMap<>();
                participante.put("nome", nomeParticipante.get(i));
                participante.put("email", emailParticipante.get(i));
                participantes.add(participante);
            }

            // Calcula novamente o resumo final do preço para exibir na tela de confirmação.
            Map<String, Object> resumoMap = pedidoService.calcularPrecoPreview(evento, quantidade, cupomCode);

            // Adiciona todos os dados necessários para a página de confirmação ao model.
            model.addAttribute("evento", evento);
            model.addAttribute("quantidade", quantidade);
            model.addAttribute("cupomCode", cupomCode);
            model.addAttribute("participantes", participantes);
            model.addAllAttributes(resumoMap); // Adiciona subtotal, taxas, total, etc.

            // Renderiza a página "confirmacao-pedido.html".
            return "confirmacao-pedido";

        } catch (Exception e) {
            // Em caso de erro, imprime no console e relança a exceção para depuração.
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * @param eventoId ID do evento.
     * @param nomeParticipante Lista com os nomes dos participantes.
     * @param emailParticipante Lista com os e-mails dos participantes.
     * @param cupomCode Cupom de desconto.
     * @param session A sessão HTTP.
     * @param redirectAttributes Objeto para passar a mensagem de sucesso após o redirecionamento.
     * @return Uma string de redirecionamento para a página de "meus-eventos".
     */
    @PostMapping("/pedidos/finalizar")
    public String finalizarPedido(
            @RequestParam int eventoId,
            @RequestParam List<String> nomeParticipante,
            @RequestParam List<String> emailParticipante,
            @RequestParam(required = false) String cupomCode,
            HttpSession session, RedirectAttributes redirectAttributes) {

        // Pega o usuário logado da sessão.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) {
            return "redirect:/login";
        }

        try {
            // Chama o serviço para efetivamente criar o pedido, os ingressos e salvar no banco.
            pedidoService.criarPedido(usuarioLogado.getIdUsuario(), eventoId, nomeParticipante, emailParticipante, cupomCode);
            
            // Busca a versão mais recente do usuário no banco para atualizar a sessão.
            Usuario usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getIdUsuario()).get();
            // Substitui o objeto antigo na sessão pelo novo, que agora contém o pedido recém-criado.
            session.setAttribute("usuarioLogado", usuarioAtualizado);
            
            // Adiciona uma mensagem de sucesso que será exibida na página "meus-eventos".
            redirectAttributes.addFlashAttribute("sucesso", "Compra realizada com sucesso! Seus ingressos foram gerados.");
            // Redireciona para a página de "meus eventos".
            return "redirect:/meus-eventos";

        } catch (RuntimeException e) {
            // Em caso de erro (ex: ingressos esgotaram no último segundo), envia mensagem de erro.
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
            // Redireciona de volta para a página inicial da compra.
            return "redirect:/pedidos/evento/" + eventoId;
        }
    }

    /**
     * Processa o cancelamento de um pedido.
     */
    @PostMapping("/pedidos/{pedidoId}/cancelar")
    public String cancelarPedido(@PathVariable("pedidoId") int pedidoId, HttpSession session, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) { return "redirect:/login"; }
        try {
            // Chama o serviço para executar a lógica de cancelamento.
            pedidoService.cancelarPedido(usuarioLogado.getIdUsuario(), pedidoId);
            // Adiciona mensagem de sucesso.
            redirectAttributes.addFlashAttribute("sucesso", "Sua compra foi cancelada.");
            // Atualiza a sessão para remover o pedido cancelado da lista do usuário.
            Usuario usuarioAtualizado = usuarioRepository.findById(usuarioLogado.getIdUsuario()).get();
            session.setAttribute("usuarioLogado", usuarioAtualizado);
        } catch (RuntimeException e) {
            // Em caso de erro, adiciona a mensagem de falha.
            redirectAttributes.addFlashAttribute("erro", e.getMessage());
        }
        // Redireciona de volta para a lista de pedidos do usuário.
        return "redirect:/meus-eventos";
    }

    /**
     * Exibe uma página para visualização/impressão de um ingresso específico.
     */
    @GetMapping("/ingressos/{ingressoId}/imprimir")
    public String exibirPaginaIngresso(@PathVariable("ingressoId") String ingressoId, 
                                       HttpSession session, 
                                       Model model, 
                                       RedirectAttributes redirectAttributes) {
        // Garante que o usuário está logado.
        Usuario usuarioLogado = (Usuario) session.getAttribute("usuarioLogado");
        if (usuarioLogado == null) { return "redirect:/login"; }

        // Itera sobre os pedidos e ingressos do usuário logado para encontrar o ingresso solicitado.
        // Isso garante que um usuário não possa acessar o ingresso de outro pela URL.
        for (Pedido pedido : usuarioLogado.getPedidos()) {
            for (Ingresso ingresso : pedido.getIngressos()) {
                if (ingresso.getIdIncricao().equals(ingressoId)) {
                    // Se o ingresso for encontrado, adiciona todos os dados necessários ao model.
                    model.addAttribute("usuario", usuarioLogado);
                    model.addAttribute("ingresso", ingresso);
                    model.addAttribute("pedido", pedido);
                    model.addAttribute("evento", pedido.getEvento());
                    // Renderiza a página "ingresso.html".
                    return "ingresso";
                }
            }
        }

        // Se o loop terminar e o ingresso não for encontrado, significa que ele não existe ou não pertence ao usuário.
        redirectAttributes.addFlashAttribute("erro", "Ingresso não encontrado ou você não tem permissão para acessá-lo.");
        return "redirect:/meus-eventos";
    }
}