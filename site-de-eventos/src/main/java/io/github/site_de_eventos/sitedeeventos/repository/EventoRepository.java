package  io.github.site_de_eventos.sitedeeventos.repository;

import java.util.List;
import java.util.Optional;

import io.github.site_de_eventos.sitedeeventos.model.Evento;

public interface EventoRepository {
	Evento save(Evento evento);

	Optional<Evento> findById(int id);

	Optional<Evento> findByNome(String nome);

	List<Evento> findAll();

	boolean deleteById(int id);

}
