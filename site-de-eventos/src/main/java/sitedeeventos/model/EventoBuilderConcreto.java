package sitedeeventos.model;

import sitedeeventos.model.builder.IEventoBuilder;
import java.time.LocalDateTime;

public class EventoBuilderConcreto implements IEventoBuilder{
	private Evento evento;

	public EventoBuilderConcreto() {
		this.evento = new Evento();
	}
	
    @Override
    public IEventoBuilder idEvento(int idEvento) {
        this.evento.setIdEvento(idEvento);
        return this;
    }

    @Override
    public IEventoBuilder capacidade(int capacidade) {
        this.evento.setCapacidade(capacidade);
        return this;
    }

    @Override
    public IEventoBuilder ingressosDisponiveis(int ingressosDisponiveis) {
        this.evento.setIngressosDisponiveis(ingressosDisponiveis);
        return this;
    }

    @Override
    public IEventoBuilder nomeEvento(String nomeEvento) {
        this.evento.setNomeEvento(nomeEvento);
        return this;
    }

    @Override
    public IEventoBuilder local(String local) {
        this.evento.setLocal(local);
        return this;
    }
    
    @Override
    public IEventoBuilder categoria(String categoria) {
        this.evento.setCategoria(categoria);
        return this;
    }
    
    @Override
    public IEventoBuilder organizador(Organizador organizador) {
        this.evento.setOrganizador(organizador);
        return this;
    }
    
    @Override
    public IEventoBuilder descricao(String descricao) {
        this.evento.setDescrição(descricao);
        return this;
    }
    
    @Override
    public IEventoBuilder preco(double preco) {
        this.evento.setPreco(preco);
        return this;
    }

    @Override
    public IEventoBuilder dataEvento(LocalDateTime dataEvento) {
        this.evento.setDataEvento(dataEvento);
        return this;
    }

    @Override
    public Evento build() {
        return this.evento;
    }

}
