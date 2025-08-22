package io.github.site_de_eventos.sitedeeventos.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Repository;

import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.UsuarioRepository;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final Map<Integer, Usuario> database = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    public UsuarioRepositoryImpl() {
    }

    @Override
    public Usuario save(Usuario usuario) {
        if (usuario.getIdUsuario() == 0) {
            int newId = idGenerator.incrementAndGet();
            usuario.setIdUsuario(newId);
        }
        database.put(usuario.getIdUsuario(), usuario);
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
        return database.remove(id) != null;
    }
}