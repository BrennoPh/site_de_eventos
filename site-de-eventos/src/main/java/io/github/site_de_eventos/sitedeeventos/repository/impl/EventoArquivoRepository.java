package io.github.site_de_eventos.sitedeeventos.repository.impl;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Repository;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;


@Repository
public class EventoArquivoRepository implements EventoRepository {

    private final Map<Integer, Evento> database = new ConcurrentHashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);

    public EventoArquivoRepository() {
        
    }

    @Override
    public Evento save(Evento evento) {
        if (evento.getIdEvento() == 0) {
            int newId = idGenerator.incrementAndGet();
            evento.setIdEvento(newId);
        }
        database.put(evento.getIdEvento(), evento);
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
        return database.remove(id) != null;
    }
}
