package io.github.site_de_eventos.sitedeeventos.repository.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

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

/**
 * Implementação do {@link EventoRepository} que utiliza um arquivo JSON como meio de persistência de dados.
 * <p>
 * Esta classe simula um banco de dados mantendo uma coleção de eventos em memória para acesso rápido
 * e sincronizando todas as alterações com um arquivo local chamado "eventos.json".
 * A anotação {@code @Repository} indica ao Spring que esta classe é um componente de acesso a dados.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Repository
public class EventoArquivoRepository implements EventoRepository {

    /**
     * O banco de dados em memória. Utiliza um {@link ConcurrentHashMap} para garantir a segurança
     * em ambientes com múltiplas threads, como uma aplicação web. A chave é o ID do evento (Integer)
     * e o valor é o objeto {@link Evento} completo.
     */
    private final Map<Integer, Evento> database = new ConcurrentHashMap<>();

    /**
     * Um gerador de IDs atômico para garantir que cada novo evento receba um identificador único,
     * mesmo em operações concorrentes. {@link AtomicInteger} é uma forma thread-safe de incrementar um número.
     */
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    /**
     * O nome do arquivo que será usado para armazenar os dados dos eventos de forma persistente.
     */
    private static final String FILE_NAME = "eventos.json";

    /**
     * Instância do Gson configurada para serializar e desserializar os objetos de Evento.
     * A configuração inclui:
     * - {@code excludeFieldsWithoutExposeAnnotation()}: Apenas os campos marcados com @Expose no modelo Evento serão incluídos no JSON.
     * - {@code registerTypeAdapter(...)}: Um adaptador customizado para converter objetos {@link LocalDateTime} para o formato de String padrão ISO-8601 e vice-versa,
     * já que o Gson não suporta nativamente os tipos de data/hora do Java 8.
     * - {@code setPrettyPrinting()}: Formata o JSON de saída para ser legível por humanos, com indentação e quebras de linha.
     */
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
     * Método de inicialização executado pelo Spring uma vez, logo após a criação do componente.
     * Sua função é carregar os dados persistidos no arquivo "eventos.json" para o mapa em memória,
     * garantindo que o estado da aplicação seja restaurado ao reiniciar.
     */
    @PostConstruct
    private void init() {
        loadDataFromFile();
    }

    /**
     * Se o evento for novo (ID é 0), um novo ID único é gerado. Em seguida, o evento
     * é inserido ou atualizado no mapa em memória e o estado completo do mapa é salvo no arquivo JSON.
     */
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

    /**
     * A busca é feita diretamente no mapa em memória, que oferece acesso de tempo constante (O(1)),
     * sendo extremamente eficiente.
     */
    @Override
    public Optional<Evento> findById(int id) {
        return Optional.ofNullable(database.get(id));
    }

    /**
     * Este método percorre todos os valores do mapa em memória e retorna o primeiro que
     * corresponde ao nome fornecido, ignorando maiúsculas e minúsculas.
     */
    @Override
    public Optional<Evento> findByNome(String nome) {
        return database.values().stream()
                .filter(evento -> evento.getNomeEvento().equalsIgnoreCase(nome))
                .findFirst();
    }
    
    /**
     * Filtra os eventos em memória, convertendo tanto o nome do evento quanto o termo de busca
     * para minúsculas para garantir uma comparação que não diferencia maiúsculas de minúsculas.
     */
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

    /**
     * Retorna uma nova {@link ArrayList} contendo todos os eventos do mapa em memória.
     * Uma nova lista é criada para evitar que modificações externas afetem o banco de dados interno.
     */
    @Override
    public List<Evento> findAll() {
        return new ArrayList<>(database.values());
    }

    /**
     * A operação de remoção é feita no mapa em memória. Se um item for efetivamente removido,
     * a alteração é persistida no arquivo JSON.
     */
    @Override
    public boolean deleteById(int id) {
        boolean removed = database.remove(id) != null;
        if (removed) {
            saveDataToFile();
        }
        return removed;
    }

    /**
     * Serializa a lista completa de eventos do mapa em memória para o formato JSON
     * e sobrescreve o arquivo "eventos.json". Este método centraliza a lógica de escrita em disco.
     */
    private void saveDataToFile() {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            gson.toJson(new ArrayList<>(database.values()), writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados de eventos: " + e.getMessage());
        }
    }

    /**
     * Desserializa o conteúdo do arquivo "eventos.json" para uma lista de objetos {@link Evento}.
     * Em seguida, limpa o mapa em memória e o repopula com os dados carregados, atualizando
     * também o gerador de IDs para o maior ID encontrado, evitando conflitos futuros.
     */
    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) return;

        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_NAME))) {
            // Usa TypeToken para informar ao Gson o tipo exato da coleção a ser desserializada,
            // contornando a limitação de "type erasure" do Java.
            java.lang.reflect.Type listType = new TypeToken<ArrayList<Evento>>() {}.getType();
            List<Evento> eventos = gson.fromJson(reader, listType);

            if (eventos != null) {
                database.clear();
                for (Evento evento : eventos) {
                    database.put(evento.getIdEvento(), evento);
                }
                // Garante que o próximo ID a ser gerado seja maior que o maior ID já existente.
                int maxId = eventos.stream().mapToInt(Evento::getIdEvento).max().orElse(0);
                idGenerator.set(maxId);
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de eventos: " + e.getMessage());
        }
    }
}
