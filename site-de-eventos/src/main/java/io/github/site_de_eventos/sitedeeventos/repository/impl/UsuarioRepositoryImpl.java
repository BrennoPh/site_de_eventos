package io.github.site_de_eventos.sitedeeventos.repository.impl;

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

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
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
import jakarta.annotation.PostConstruct;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final Map<Integer, Usuario> database = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);
    private static final String FILE_NAME = "usuarios.json";

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
        .registerTypeAdapter(Usuario.class, new UsuarioTypeAdapter())
        .setPrettyPrinting()
        .create();

    @PostConstruct
    public void init() {
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
        try (Writer writer = Files.newBufferedWriter(Paths.get(FILE_NAME))) {
            gson.toJson(new ArrayList<>(database.values()), writer);
        } catch (IOException e) {
            System.err.println("Erro ao salvar dados de usuários: " + e.getMessage());
        }
    }

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
            }
        } catch (IOException e) {
            System.err.println("Erro ao carregar dados de usuários: " + e.getMessage());
        }
    }
}

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

        if (jsonObject.has("cnpj")) {
            IOrganizadorBuilder builder = new OrganizadorBuilderConcreto();
            ((OrganizadorBuilderConcreto) builder.idUsuario(jsonObject.get("idUsuario").getAsInt())
                   .nome(jsonObject.has("nome") ? jsonObject.get("nome").getAsString() : null)
                   .email(jsonObject.has("email") ? jsonObject.get("email").getAsString() : null)
                   .pegaPedidos(pedidos))
                   .cnpj(jsonObject.has("cnpj") ? jsonObject.get("cnpj").getAsString() : null)
                   .contaBancaria(jsonObject.has("contaBancaria") ? jsonObject.get("contaBancaria").getAsString() : null); 
            return builder.build();
        } else {
            IUsuarioBuilder builder = new UsuarioBuilderConcreto();
            builder.idUsuario(jsonObject.get("idUsuario").getAsInt())
                   .nome(jsonObject.has("nome") ? jsonObject.get("nome").getAsString() : null)
                   .email(jsonObject.has("email") ? jsonObject.get("email").getAsString() : null)
                   .pegaPedidos(pedidos); 
            return builder.build();
        }
    }
}
