package io.github.site_de_eventos.sitedeeventos.repository.impl;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import  java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.OrganizadorBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.model.UsuarioBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.builder.IOrganizadorBuilder;
import io.github.site_de_eventos.sitedeeventos.model.builder.IUsuarioBuilder;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final Map<Integer, Usuario> database = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final String FILE_NAME = "usuarios.json";

    public UsuarioRepositoryImpl() {
        loadDataFromFile();
    }
    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getIdUsuario() == 0) {
            int newId = idGenerator.incrementAndGet();
            usuario.setIdUsuario(newId);
        }
        database.put(usuario.getIdUsuario(), usuario);
        saveDataToFile();
        return usuario;
    }
    @Override
    public Optional<Usuario> findById(int id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public Optional<Usuario> findByEmail(String email) {
        return database.values().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();
    }

    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public boolean deleteById(int id) {
        boolean removed = database.remove(id) != null;
        if (removed) {
            saveDataToFile();
        }
        return removed;
    }

    private void saveDataToFile() {
        try {
            List<String> lines = database.values().stream()
                    .map(this::convertUsuarioToJson)
                    .collect(Collectors.toList());
            Files.write(Paths.get(FILE_NAME), lines);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados de usuários: " + e.getMessage());
        }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            int maxId = 0;
            for (String line : lines) {
                Usuario usuario = convertJsonToUsuario(line);
                if (usuario != null) {
                    database.put(usuario.getIdUsuario(), usuario);
                    if (usuario.getIdUsuario() > maxId) {
                        maxId = usuario.getIdUsuario();
                    }
                }
            }
            idGenerator.set(maxId);
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de usuários: " + e.getMessage());
        }
    }

    private String convertUsuarioToJson(Usuario usuario) {
        String baseJson = String.format("{\"idUsuario\":%d, \"nome\":\"%s\", \"email\":\"%s\", \"tipo\":\"%s\"",
                usuario.getIdUsuario(), usuario.getNome(), usuario.getEmail(),
                (usuario instanceof Organizador) ? "ORGANIZADOR" : "USUARIO");

        if (usuario instanceof Organizador) {
            Organizador org = (Organizador) usuario;
            baseJson += String.format(", \"cnpj\":\"%s\", \"contaBancaria\":\"%s\"", org.getCnpj(), org.getContaBancaria());
        }
        return baseJson + "}";
    }
    
    private Usuario convertJsonToUsuario(String json) {
        Map<String, String> data = parseJson(json);
        String tipo = data.get("tipo");
        if ("ORGANIZADOR".equals(tipo)) {
            IOrganizadorBuilder builder = new OrganizadorBuilderConcreto();
            return ((IOrganizadorBuilder) builder.idUsuario(Integer.parseInt(data.get("idUsuario")))
                          .nome(data.get("nome"))
                          .email(data.get("email")))
                          .cnpj(data.get("cnpj"))
                          .contaBancaria(data.get("contaBancaria"))
                          .build();
        } else {
            IUsuarioBuilder builder = new UsuarioBuilderConcreto();
            return builder.idUsuario(Integer.parseInt(data.get("idUsuario")))
                          .nome(data.get("nome"))
                          .email(data.get("email"))
                          .build();
        }
    }

    private Map<String, String> parseJson(String json) {
        return java.util.Arrays.stream(json.replace("{", "").replace("}", "").split(","))
            .map(entry -> entry.split(":", 2))
            .collect(Collectors.toMap(
                parts -> parts[0].trim().replace("\"", ""),
                parts -> (parts.length > 1) ? parts[1].trim().replace("\"", "") : ""
            ));
    }
}