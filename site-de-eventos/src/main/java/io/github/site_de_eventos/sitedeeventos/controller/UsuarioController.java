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

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping("/cadastro")
    public String exibirFormularioCadastro() {
        return "cadastro";
    }

    @PostMapping("/cadastro")
    public String processarCadastro(
            // Parâmetros de Usuário (obrigatórios)
            @RequestParam String nome,
            @RequestParam String email,
            // Parâmetros de Usuário (opcionais)
            @RequestParam(required = false) String cpf,
            @RequestParam(required = false) String telefone,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataNascimento,
            @RequestParam(required = false) String cidade,
            @RequestParam(required = false) String endereco,
            // Parâmetro de tipo
            @RequestParam(defaultValue = "false") boolean isOrganizador,
            // Parâmetros de Organizador (opcionais)
            @RequestParam(required = false) String cnpj,
            @RequestParam(required = false) String contaBancaria,
            Model model) {
        try {
            // Converte LocalDate para LocalDateTime, se a data for fornecida
            LocalDateTime dataNascimentoTime = (dataNascimento != null) ? dataNascimento.atStartOfDay() : null;

            usuarioService.registrar(nome, email, cpf, telefone, dataNascimentoTime, cidade, endereco, isOrganizador, cnpj, contaBancaria);
            
            model.addAttribute("sucesso", "Cadastro realizado! Faça o login para continuar.");
            return "login";
        } catch (RuntimeException e) {
            model.addAttribute("erro", e.getMessage());
            return "cadastro";
        }
    }

    @GetMapping("/login")
    public String exibirFormularioLogin() {
        return "login";
    }

    @PostMapping("/login")
    public String processarLogin(@RequestParam String email, HttpSession session, Model model) {
        Optional<Usuario> usuarioOpt = usuarioService.autenticar(email);
        if (usuarioOpt.isPresent()) {
            session.setAttribute("usuarioLogado", usuarioOpt.get());
            return "redirect:/";
        } else {
            model.addAttribute("erro", "Email não encontrado. Verifique os dados ou cadastre-se.");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String processarLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}