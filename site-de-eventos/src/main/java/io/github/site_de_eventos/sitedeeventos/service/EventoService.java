package io.github.site_de_eventos.sitedeeventos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.repository.EventoRepository;

@Service
public class EventoService {

    private EventoRepository eventoRepository;
    
    @Autowired
    public EventoService(EventoRepository eventoRepository) {
        this.eventoRepository = eventoRepository;
    }

    public Evento save(Evento evento) {
        if (evento == null) {
            throw new IllegalArgumentException("O objeto de evento não pode ser nulo.");
        }
        return eventoRepository.save(evento);
    }

    public List<Evento> buscarTodos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> buscarPorId(int id) {
        if (id <= 0) {
            return Optional.empty();
        }
        return eventoRepository.findById(id);
    }
    public List<Evento> buscarPorNome(String termo) {
        return eventoRepository.findByNomeContaining(termo);
    }
}