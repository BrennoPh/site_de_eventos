package io.github.site_de_eventos.sitedeeventos.repository.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.EventoBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.builder.IEventoBuilder;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
import jakarta.annotation.PostConstruct;

@Repository
public class EventoArquivoRepository implements EventoRepository {

    private final Map<Integer, Evento> database = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final String FILE_NAME = "eventos.json";
    @Autowired
    private UsuarioRepository usuarioRepository;

    public EventoArquivoRepository() {
    }
    @PostConstruct
    private void init() {
        loadDataFromFile();
    }

    @Override
    public Evento save(Evento evento) {
        if (evento.getIdEvento() == 0) {
            int newId = idGenerator.incrementAndGet();
            evento.setIdEvento(newId);
        }
        database.put(evento.getIdEvento(), evento);
        saveDataToFile();
        return evento;
    }

       @Override
    public Optional<Evento> findById(int id) {
        return Optional.ofNullable(database.get(id));
    }

    @Override
    public Optional<Evento> findByNome(String nome) {
        return database.values().stream()
                .filter(evento -> evento.getNomeEvento().equalsIgnoreCase(nome))
                .findFirst();
    }

    @Override
    public List<Evento> findAll() {
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
                    .map(this::convertEventoToJson)
                    .collect(Collectors.toList());
            Files.write(Paths.get(FILE_NAME), lines);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados de eventos: " + e.getMessage());
        }
    }

    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try {
            // Limpa o banco de dados em memória antes de carregar para evitar duplicatas
            database.clear();
            List<String> lines = Files.readAllLines(Paths.get(FILE_NAME));
            int maxId = 0;
            for (String line : lines) {
                // Adiciona um try-catch para ignorar linhas corrompidas e não quebrar a aplicação
                try {
                    Evento evento = convertJsonToEvento(line);
                    if (evento != null) {
                        database.put(evento.getIdEvento(), evento);
                        if (evento.getIdEvento() > maxId) {
                            maxId = evento.getIdEvento();
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha do arquivo de eventos (será ignorada): " + line);
                }
            }
            idGenerator.set(maxId);
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de eventos: " + e.getMessage());
        }
    }

    private String convertEventoToJson(Evento evento) {
        int organizadorId = (evento.getOrganizador() != null) ? evento.getOrganizador().getIdUsuario() : 0;
        
        // SOLUÇÃO: Corrigido para gerar uma string no formato JSON válido.
        return String.format("{\"idEvento\":%d,\"nomeEvento\":\"%s\",\"local\":\"%s\",\"dataEvento\":\"%s\",\"categoria\":\"%s\",\"preco\":%.2f,\"organizadorId\":%d}",
                evento.getIdEvento(), evento.getNomeEvento(), evento.getLocal(), evento.getDataEvento().toString(),
                evento.getCategoria(), evento.getPreco(), organizadorId);
    }

    private Evento convertJsonToEvento(String json) {
        // Se a linha estiver vazia ou não for um JSON, ignora
        if (json == null || !json.startsWith("{") || !json.endsWith("}")) {
            return null;
        }
    
        Map<String, String> data = parseJson(json);
        
        IEventoBuilder builder = new EventoBuilderConcreto();
        builder.idEvento(Integer.parseInt(data.get("idEvento")))
               .nomeEvento(data.get("nomeEvento"))
               .local(data.get("local"))
               .dataEvento(LocalDateTime.parse(data.get("dataEvento")))
               .categoria(data.get("categoria"))
               .preco(Double.parseDouble(data.get("preco")));
        
        int organizadorId = Integer.parseInt(data.get("organizadorId"));
        usuarioRepository.findById(organizadorId).ifPresent(usuario -> {
            if (usuario instanceof Organizador) {
                builder.organizador((Organizador) usuario);
            }
        });

        return builder.build();
    }

    private Map<String, String> parseJson(String json) {
        String content = json.substring(1, json.length() - 1); // Remove chaves {}
        return java.util.Arrays.stream(content.split(","))
            .map(entry -> entry.split(":", 2)) 
            .collect(Collectors.toMap(
                parts -> parts[0].trim().replace("\"", ""),
                parts -> (parts.length > 1) ? parts[1].trim().replace("\"", "") : ""
            ));
    }
}
