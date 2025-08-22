package io.github.site_de_eventos.sitedeeventos.repository;

import java.util.List;
import java.util.Optional;

import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;


public interface OrganizadorRepository {
	Organizador save(Organizador organizador);
	
	Optional<Organizador> findById(int id);
	
	Optional<Organizador> findByEmail(String email);

	List<Organizador> findAll();

	boolean deleteById(int id);
	
	

}
