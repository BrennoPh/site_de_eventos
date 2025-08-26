package io.github.site_de_eventos.sitedeeventos.repository.impl;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;
import jakarta.annotation.PostConstruct;

/**
 * Implementação do {@link EventoRepository} que utiliza um arquivo JSON como meio de persistência.
 * <p>
 * Esta classe gerencia uma coleção de eventos em memória e sincroniza os dados com um arquivo "eventos.json".
 * É marcada como um {@code @Repository} para ser gerenciada pelo contêiner do Spring.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Repository
public class EventoArquivoRepository implements EventoRepository {

    private final Map<Integer, Evento> database = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final String FILE_NAME = "eventos.json";

    private final Gson gson = new GsonBuilder()
    	.excludeFieldsWithoutExposeAnnotation() 
    	.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
    	    @Override
    	    public void write(JsonWriter out, LocalDateTime value) throws IOException {
    	        if (value == null) {
    	            out.nullValue();
    	            return;
    	        }
    	        out.value(value.toString());
    	    }
    	    @Override
    	    public LocalDateTime read(JsonReader in) throws IOException {
    	        if (in.peek() == com.google.gson.stream.JsonToken.NULL) {
    	            in.nextNull();
    	            return null; 
    	        }
    	        return LocalDateTime.parse(in.nextString());
    	    }
    	})
        .setPrettyPrinting()
        .create();

    /**
     * Inicializa o repositório carregando os dados do arquivo JSON na memória
     * assim que a aplicação é iniciada, graças à anotação {@code @PostConstruct}.
     */
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
    public List<Evento> findByNomeContaining(String termo) {
        if (termo == null || termo.trim().isEmpty()) {
            return findAll();
        }
        String termoLowerCase = termo.toLowerCase();
        
        return database.values().stream()
                .filter(evento -> evento.getNomeEvento().toLowerCase().contains(termoLowerCase))
                .collect(Collectors.toList());
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

    /**
     * Método privado para persistir o estado atual do banco de dados em memória para o arquivo JSON.
     * É chamado após qualquer operação de modificação (save, delete).
     */
    private void saveDataToFile() {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            gson.toJson(new ArrayList<>(database.values()), writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados de eventos: " + e.getMessage());
        }
    }

    /**
     * Método privado que carrega os dados do arquivo JSON para o banco de dados em memória.
     * É chamado na inicialização do repositório. Se o arquivo não existir ou estiver vazio, nada é feito.
     */
    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) return;

        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_NAME))) {
            java.lang.reflect.Type listType = new TypeToken<ArrayList<Evento>>() {}.getType();
            List<Evento> eventos = gson.fromJson(reader, listType);

            if (eventos != null) {
                database.clear();
                for (Evento evento : eventos) {
                    database.put(evento.getIdEvento(), evento);
                }
                int maxId = eventos.stream().mapToInt(Evento::getIdEvento).max().orElse(0);
                idGenerator.set(maxId);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de eventos: " + e.getMessage());
        }
    }
}
