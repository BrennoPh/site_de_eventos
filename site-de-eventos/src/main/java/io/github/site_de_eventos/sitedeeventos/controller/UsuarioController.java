package io.github.site_de_eventos.sitedeeventos.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.service.UsuarioService;

/**
 * Controlador responsável por gerenciar o ciclo de vida do usuário.
 * <p>
 * Esta classe lida com as requisições de cadastro, login e logout de usuários,
 * interagindo com o {@link UsuarioService}.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Controller
public class UsuarioController {

    /**
     * Serviço para gerenciar a lógica de negócio de Usuários.
     */
    @Autowired
    private UsuarioService usuarioService;

    /**
     * Exibe o formulário de cadastro de usuário.
     * Mapeado para requisições GET em "/cadastro".
     *
     * @return O nome da view "cadastro" para renderização.
     */
    @GetMapping("/cadastro") // Mapeia as requisições HTTp GET do URL indicado
    public String exibirFormularioCadastro() {
    	// Apenas retorna o nome do arquivo de template para ser renderizado.
    	return "cadastro";
    }

    /**
     * Processa a submissão do formulário de cadastro.
     * Mapeado para requisições POST em "/cadastro".
     * Este método lida com o registro de ambos, usuários comuns e organizadores.
     *
     * @param nome (String) Nome do usuário.
     * @param email (String) E-mail do usuário.
     * @param cpf (String) CPF do usuário (opcional).
     * @param telefone (String) Telefone do usuário (opcional).
     * @param dataNascimento (LocalDate) Data de nascimento (opcional).
     * @param cidade (String) Cidade do usuário (opcional).
     * @param endereco (String) Endereço do usuário (opcional).
     * @param isOrganizador (boolean) Flag que indica se o cadastro é de um organizador.
     * @param cnpj (String) CNPJ (obrigatório se for organizador, opcional caso contrário).
     * @param contaBancaria (String) Conta bancária (obrigatório se for organizador, opcional caso contrário).
     * @param model (Model) Objeto do Spring para adicionar mensagens de feedback.
     * @return O nome da view "login" em caso de sucesso, ou "cadastro" em caso de erro.
     */
    @PostMapping("/cadastro")
    public String processarCadastro(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String endereco,
            @RequestParam(defaultValue = "false") boolean isOrganizador,
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String contaBancaria,
            Model model) {
        try {
        	// Converte LocalDate (só data) para LocalDateTime (data e hora), necessário pelo modelo.
            LocalDateTime dataNascimentoTime = (dataNascimento != null) ? dataNascimento.atStartOfDay() : null;

            // Chama o serviço para registrar o usuário, passando todos os dados do formulário.
            usuarioService.registrar(nome, email, cpf, telefone, dataNascimentoTime, cidade, endereco, isOrganizador, cnpj, contaBancaria);
            
            // Se o registro for bem-sucedido, adiciona uma mensagem de sucesso ao 'Model'.
            model.addAttribute("sucesso", "Cadastro realizado! Faça o login para continuar.");
            return "login";
        } catch (RuntimeException e) {
        	// Se ocorrer um erro no registro (ex: e-mail já existe)...
            // Adiciona a mensagem de erro ao 'Model'.
            model.addAttribute("erro", e.getMessage());
            // Retorna para a própria página de cadastro para o usuário corrigir os dados.
            return "cadastro";
        }
    }

    /**
     * Exibe o formulário de login.
     * Mapeado para requisições GET em "/login".
     *
     * @return O nome da view "login" para renderização.
     */
    @GetMapping("/login")
    public String exibirFormularioLogin() {
        return "login";
    }

    /**
     * Processa a submissão do formulário de login.
     * Mapeado para requisições POST em "/login".
     *
     * @param email (String) E-mail fornecido para autenticação.
     * @param session (HttpSession) A sessão HTTP onde o usuário será armazenado após o login.
     * @param model (Model) Objeto do Spring para adicionar mensagens de erro.
     * @return Uma string de redirecionamento para "/" em caso de sucesso, ou a view "login" em caso de falha.
     */
    @PostMapping("/login")
    public String processarLogin(@RequestParam String email, HttpSession session, Model model) {
    	// Chama o serviço para autenticar o usuário pelo e-mail.
    	Optional<Usuario> usuarioOpt = usuarioService.autenticar(email);
    	// Verifica se o usuário foi encontrado.
    	if (usuarioOpt.isPresent()) {
    		// Se sim, armazena o objeto do usuário na sessão HTTP. Isso "conecta" o usuário.
    		session.setAttribute("usuarioLogado", usuarioOpt.get());
    		// Redireciona para a página principal.
    		return "redirect:/";
        } else {
        	// Se não, adiciona uma mensagem de erro ao 'Model'.
        	model.addAttribute("erro", "Email não encontrado. Verifique os dados ou cadastre-se.");
        	// E renderiza a página de login novamente para o usuário tentar de novo.
        	return "login";
        }
    }
    
    /**
     * Processa a solicitação de logout do usuário.
     * Mapeado para requisições GET em "/logout".
     *
     * @param session (HttpSession) A sessão HTTP a ser invalidada.
     * @return Uma string de redirecionamento para a página principal ("/").
     */
    @GetMapping("/logout")
    public String processarLogout(HttpSession session) {
    	// Invalida a sessão, removendo todos os atributos (incluindo 'usuarioLogado').
    	session.invalidate();
    	// Redireciona o usuário para a página principal.
    	return "redirect:/";
    }
}