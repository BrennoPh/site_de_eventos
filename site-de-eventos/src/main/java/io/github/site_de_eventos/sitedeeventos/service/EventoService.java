package sitedeeventos.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sitedeeventos.model.Evento;
import sitedeeventos.repository.EventoRepository;

/**
 * Camada de Serviço para a entidade Evento.
 * Responsável por conter as regras de negócio e intermediar a comunicação
 * entre o Controller e o Repository.
 */
@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    /**
     * Salva um evento no sistema após aplicar validações de negócio.
     *
     * @param evento O objeto Evento a ser persistido.
     * @return O evento salvo com seu ID.
     * @throws IllegalArgumentException se os dados do evento forem inválidos.
     */
    public Evento save(Evento evento) {
        // 1. Validação de dados essenciais
        if (evento == null) {
            throw new IllegalArgumentException("O objeto de evento não pode ser nulo.");
        }
        if (evento.getNomeEvento() == null || evento.getNomeEvento().trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do evento é obrigatório.");
        }
        if (evento.getLocal() == null || evento.getLocal().trim().isEmpty()) {
            throw new IllegalArgumentException("O local do evento é obrigatório.");
        }
        if (evento.getDataEvento() == null) {
            throw new IllegalArgumentException("A data do evento é obrigatória.");
        }

        // 3. Delega a persistência para a camada de repositório
        return eventoRepository.save(evento);
    }

    /**
     * Busca todos os eventos cadastrados no sistema.
     *
     * @return Uma lista de todos os eventos.
     */
    public List<Evento> buscarTodos() {
        return eventoRepository.findAll();
    }

    public Optional<Evento> buscarPorId(Long id) {
        if (id == null || id <= 0L) {
            return Optional.empty();
        }
        return eventoRepository.findById(Math.toIntExact(id));
    }
}