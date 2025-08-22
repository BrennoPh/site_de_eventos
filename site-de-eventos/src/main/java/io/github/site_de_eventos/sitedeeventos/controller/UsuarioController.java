package io.github.site_de_eventos.sitedeeventos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.model.UsuarioBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.builder.IUsuarioBuilder;
import io.github.site_de_eventos.sitedeeventos.service.UsuarioService;

@Controller
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Mapeia a requisição GET para /login e retorna a página de login.
     * @return o nome do template "login.html"
     */
    @GetMapping("/login")
    public String exibirPaginaLogin() {
        return "login"; // Retorna o arquivo templates/login.html
    }

    /**
     * Mapeia a requisição POST para /usuarios para criar um novo usuário.
     * Os dados vêm do formulário de login/cadastro.
     * @param nome O nome do usuário vindo do formulário.
     * @param email O email do usuário vindo do formulário.
     * @param senha A senha do usuário vinda do formulário.
     * @return Redireciona para a página inicial ("/") após o cadastro.
     */
    @PostMapping("/usuarios")
    public String criarUsuario(@RequestParam String nome, @RequestParam String email, @RequestParam String senha) {
        
        // Usando o Design Pattern Builder que já existe no seu projeto para criar o objeto Usuario
        IUsuarioBuilder builder = new UsuarioBuilderConcreto();
        Usuario novoUsuario = builder
                                .nome(nome)
                                .email(email) // Em um projeto real, a senha deveria ser criptografada aqui
                                .build();

        // Salva o novo usuário usando o service
        usuarioService.salvar(novoUsuario);

        System.out.println("Novo usuário criado: " + novoUsuario.getNome());

        // Redireciona para a página inicial após o sucesso
        return "redirect:/";
    }
}