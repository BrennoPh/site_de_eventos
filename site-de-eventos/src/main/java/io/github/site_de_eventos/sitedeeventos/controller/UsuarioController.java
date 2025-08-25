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

import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.service.UsuarioService;
import jakarta.servlet.http.HttpSession;

/**
 * Controlador responsável por gerenciar o ciclo de vida do usuário (cadastro, login, logout).
 */
@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Exibe o formulário de cadastro de usuário.
     */
    @GetMapping("/cadastro")
    public String exibirFormularioCadastro() {
    	return "cadastro";
    }

    /**
     * Processa a submissão do formulário de cadastro.
     */
    @PostMapping("/cadastro")
    public String processarCadastro(
            @RequestParam String nome,
            @RequestParam String email,
            @RequestParam String senha,
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
            // Converte LocalDate (só data, vindo do form) para LocalDateTime (data e hora), que é o tipo no modelo.
            LocalDateTime dataNascimentoTime = (dataNascimento != null) ? dataNascimento.atStartOfDay() : null;

            // Chama o serviço para registrar o usuário, agora incluindo a senha.
            usuarioService.registrar(nome, email, senha , cpf, telefone, dataNascimentoTime, cidade, endereco, isOrganizador, cnpj, contaBancaria);
            
            // Se o registro for bem-sucedido, adiciona uma mensagem de sucesso ao 'Model'.
            model.addAttribute("sucesso", "Cadastro realizado! Faça o login para continuar.");
            // Renderiza a página de login para o novo usuário se autenticar.
            return "login";
        } catch (RuntimeException e) {
            // Se ocorrer um erro no serviço (ex: e-mail já em uso)...
            // Adiciona a mensagem de erro ao 'Model' para ser exibida ao usuário.
            model.addAttribute("erro", e.getMessage());
            // Retorna para a própria página de cadastro para o usuário corrigir os dados.
            return "cadastro";
        }
    }

    /**
     * Exibe o formulário de login.
     */
    @GetMapping("/login")
    public String exibirFormularioLogin() {
        return "login";
    }

    /**
     * Processa a submissão do formulário de login, validando e-mail e senha.
     */
    @PostMapping("/login")
    public String processarLogin(@RequestParam String email,@RequestParam String senha, HttpSession session, Model model) {
    	// Chama o serviço para autenticar o usuário apenas pelo e-mail primeiro.
    	Optional<Usuario> usuarioOpt = usuarioService.autenticar(email);
    	// Verifica se um usuário com este e-mail foi encontrado no banco de dados.
    	if (usuarioOpt.isPresent()) {
            // Se o usuário existe, pega o objeto.
            Usuario usuario = usuarioOpt.get();
            // Agora, compara a senha enviada no formulário com a senha armazenada no banco.
            if(usuario.getSenha().equals(senha)){
    		    // Se as senhas baterem, armazena o objeto completo do usuário na sessão HTTP.
                // É isso que "mantém o usuário logado" durante a navegação.
    		    session.setAttribute("usuarioLogado", usuario);
    		    // Redireciona para a página principal.
    		    return "redirect:/";
            } else {
                // Se a senha estiver incorreta, adiciona uma mensagem de erro ao 'Model'.
        	    model.addAttribute("erro", "Email ou Senha incorretos");
        	    // E renderiza a página de login novamente para o usuário tentar de novo.
        	    return "login";
            }
        } else {
            // Se o e-mail não for encontrado, adiciona uma mensagem de erro ao 'Model'.
        	model.addAttribute("erro", "Email não encontrado. Verifique os dados ou cadastre-se.");
        	// E renderiza a página de login novamente.
        	return "login";
        }
    }
    
    /**
     * Processa a solicitação de logout do usuário.
     */
    @GetMapping("/logout")
    public String processarLogout(HttpSession session) {
    	// Invalida a sessão HTTP atual do usuário.
        // Isso remove todos os atributos da sessão, incluindo o "usuarioLogado".
    	session.invalidate();
    	// Redireciona o usuário para a página principal, agora como um visitante.
    	return "redirect:/";
    }
}