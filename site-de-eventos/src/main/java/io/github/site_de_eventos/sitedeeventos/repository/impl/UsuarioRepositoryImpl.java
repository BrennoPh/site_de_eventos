package io.github.site_de_eventos.sitedeeventos.repository.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;
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
