package io.github.site_de_eventos.sitedeeventos.repository.impl;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.site_de_eventos.sitedeeventos.model.OrganizadorBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.Pedido;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.model.UsuarioBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.builder.IOrganizadorBuilder;
import io.github.site_de_eventos.sitedeeventos.model.builder.IUsuarioBuilder;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
import io.github.site_de_eventos.sitedeeventos.service.PedidoService;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementação do {@link UsuarioRepository} que persiste os dados dos usuários em um arquivo JSON.
 * Gerencia os dados em memória para acesso rápido e sincroniza com "usuarios.json" para persistência.
 * Utiliza um {@link UsuarioTypeAdapter} customizado para lidar com a herança entre Usuário e Organizador.
 *
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */
@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final Map<Integer, Usuario> database = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final String FILE_NAME = "usuarios.json";

    // Instância do Gson configurada para serialização e desserialização.
    private final Gson gson = new GsonBuilder()
    	.excludeFieldsWithoutExposeAnnotation() 
    	.registerTypeAdapter(LocalDateTime.class, new TypeAdapter<LocalDateTime>() {
    	    @Override
    	    public void write(JsonWriter out, LocalDateTime value) throws IOException {
    	        if (value == null) { out.nullValue(); return; }
    	        out.value(value.toString());
    	    }
    	    @Override
    	    public LocalDateTime read(JsonReader in) throws IOException {
    	        if (in.peek() == com.google.gson.stream.JsonToken.NULL) { in.nextNull(); return null; }
    	        return LocalDateTime.parse(in.nextString());
    	    }
    	})
        .registerTypeAdapter(Usuario.class, new UsuarioTypeAdapter())
        .setPrettyPrinting()
        .create();

    /**
     * Carrega os dados do arquivo JSON para a memória na inicialização do repositório.
     */
    @PostConstruct
    public void init() {
        loadDataFromFile();
    }

    /**
     * Se o ID do usuário for 0, gera um novo ID antes de salvar no mapa em memória e
     * persistir a alteração no arquivo JSON.
     */
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

    /**
     * Busca um usuário diretamente no mapa em memória pelo seu ID.
     */
    @Override
    public Optional<Usuario> findById(int id) {
        return Optional.ofNullable(database.get(id));
    }

    /**
     * Busca na coleção de usuários em memória o primeiro que corresponde ao e-mail fornecido,
     * ignorando diferenças de maiúsculas e minúsculas.
     */
    @Override
    public Optional<Usuario> findByEmail(String email) {
        return database.values().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();
    }

    /**
     * Retorna uma nova lista contendo todos os usuários para evitar modificações externas na base de dados interna.
     */
    @Override
    public List<Usuario> findAll() {
        return new ArrayList<>(database.values());
    }

    /**
     * Remove o usuário do mapa em memória e, se a operação for bem-sucedida,
     * atualiza o arquivo JSON para refletir a remoção.
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
     * Salva a lista atual de usuários do mapa em memória para o arquivo "usuarios.json".
     */
    private void saveDataToFile() {
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            gson.toJson(new ArrayList<>(database.values()), writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados de usuários: " + e.getMessage());
        }
    }

    /**
     * Carrega os usuários do arquivo "usuarios.json" para o mapa em memória na inicialização.
     * Também atualiza o contador de IDs para o maior valor encontrado no arquivo.
     */
    private void loadDataFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists() || file.length() == 0) return;

        try (Reader reader = Files.newBufferedReader(Paths.get(FILE_NAME))) {
            java.lang.reflect.Type listType = new TypeToken<ArrayList<Usuario>>() {}.getType();
            List<Usuario> usuarios = gson.fromJson(reader, listType);

            if (usuarios != null) {
                database.clear();
                for (Usuario usuario : usuarios) {
                    database.put(usuario.getIdUsuario(), usuario);
                }
                int maxId = usuarios.stream().mapToInt(Usuario::getIdUsuario).max().orElse(0);
                idGenerator.set(maxId);
                
                // 1. Encontra o maior ID de PEDIDO entre todos os usuários
                int maxPedidoId = usuarios.stream() // Pega a lista de usuários
                    .flatMap(usuario -> usuario.getPedidos().stream()) // Transforma em uma única lista de todos os pedidos
                    .mapToInt(Pedido::getIdPedido) // Pega apenas o ID de cada pedido
                    .max() // Encontra o maior ID
                    .orElse(0); // Se não houver nenhum pedido, começa do 0

                // 2. Ajusta o contador de ID do PedidoService com o maior ID encontrado
                PedidoService.pedidoIdGenerator.set(maxPedidoId);
                
                System.out.println("Contador de PedidoIDs inicializado em: " + maxPedidoId); // Log para confirmação

            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de usuários: " + e.getMessage());
        }
    }
}

/**
 * Desserializador customizado para o Gson, responsável por instanciar a classe correta
 * ({@link Usuario} ou {@link io.github.site_de_eventos.sitedeeventos.model.Organizador})
 * ao ler o JSON, com base na presença do campo "cnpj".
 */
class UsuarioTypeAdapter implements JsonDeserializer<Usuario> {
    @Override
    public Usuario deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Type pedidoListType = new TypeToken<ArrayList<Pedido>>() {}.getType();

        List<Pedido> pedidos = Collections.emptyList(); 
        if (jsonObject.has("pedidos") && jsonObject.get("pedidos").isJsonArray()) {
            JsonArray pedidosArray = jsonObject.getAsJsonArray("pedidos");
            pedidos = context.deserialize(pedidosArray, pedidoListType);
        }

        // Se o objeto JSON tiver a propriedade "cnpj", ele é tratado como um Organizador.
        if (jsonObject.has("cnpj")) {
            IOrganizadorBuilder builder = new OrganizadorBuilderConcreto();
            ((OrganizadorBuilderConcreto) builder.idUsuario(jsonObject.get("idUsuario").getAsInt())
                   .nome(jsonObject.has("nome") ? jsonObject.get("nome").getAsString() : null)
                   .email(jsonObject.has("email") ? jsonObject.get("email").getAsString() : null)
                   .senha(jsonObject.has("senha") ? jsonObject.get("senha").getAsString() : null)
                   .pegaPedidos(pedidos))
                   .cnpj(jsonObject.has("cnpj") ? jsonObject.get("cnpj").getAsString() : null)
                   .contaBancaria(jsonObject.has("contaBancaria") ? jsonObject.get("contaBancaria").getAsString() : null); 
            return builder.build();
        } else { // Caso contrário, é um Usuário padrão.
            IUsuarioBuilder builder = new UsuarioBuilderConcreto();
            builder.idUsuario(jsonObject.get("idUsuario").getAsInt())
                   .nome(jsonObject.has("nome") ? jsonObject.get("nome").getAsString() : null)
                   .email(jsonObject.has("email") ? jsonObject.get("email").getAsString() : null)
                   .senha(jsonObject.has("senha") ? jsonObject.get("senha").getAsString() : null)
                   .pegaPedidos(pedidos); 
            return builder.build();
        }
    }
}
