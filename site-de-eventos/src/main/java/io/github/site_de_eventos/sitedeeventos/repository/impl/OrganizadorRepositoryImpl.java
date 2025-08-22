package io.github.site_de_eventos.sitedeeventos.repository.impl;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Repository;

import io.github.site_de_eventos.sitedeeventos.model.Evento;
import io.github.site_de_eventos.sitedeeventos.model.Organizador;
import io.github.site_de_eventos.sitedeeventos.model.OrganizadorBuilderConcreto;
import io.github.site_de_eventos.sitedeeventos.model.Usuario;
import io.github.site_de_eventos.sitedeeventos.repository.OrganizadorRepository;

@Repository
public class OrganizadorRepositoryImpl implements OrganizadorRepository {
	
    private final Map<Integer, Organizador> database = new ConcurrentHashMap<>();
	
    public OrganizadorRepositoryImpl(){
        Organizador organizadorInicial = ((OrganizadorBuilderConcreto) new OrganizadorBuilderConcreto()
                .idUsuario(1) // Definindo o ID como 1
                .nome("Admin Organizador")
                .email("admin@evento.com")
                .dataNascimento(LocalDateTime.now()))
                .cnpj("00.111.222/0001-33")
                .contaBancaria("12345-6")
                .eventoOrganizado(null) // O organizador inicial pode não ter um evento ainda
                .build();

            // 2. Salvar o organizador inicial no banco de dados em memória
            this.save(organizadorInicial);
    }
    
	@Override
	public Organizador save(Organizador organizador) {
		database.put(organizador.getIdUsuario(), organizador);
		return organizador;
	}

    @Override
    public Optional<Organizador> findById(int id) {
        return Optional.ofNullable(database.get(id));
    }

	@Override
	public Optional<Organizador> findByEmail(String email) {
		// Procura em todos os valores do mapa um usuário com o e-mail correspondente.
		return database.values().stream().filter(organizador -> organizador.getEmail().equalsIgnoreCase(email)).findFirst();
	}

    @Override
    public List<Organizador> findAll() {
        return new ArrayList<>(database.values());
    }

    @Override
    public boolean deleteById(int id) {
        return database.remove(id) != null;
    }

}
