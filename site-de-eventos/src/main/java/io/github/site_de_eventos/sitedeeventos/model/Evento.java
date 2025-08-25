package io.github.site_de_eventos.sitedeeventos.model;

import com.google.gson.annotations.Expose;
import java.time.LocalDateTime;

/**
 * Representa a entidade Evento do sitema.
 * 
 * Esta classe é um modelo de dados que armzena todas informações pertinentes
 * a um evento, como nome, local, data, preço e capacidade.
 * 
 * Os campos anotados com {@code @Expose} são para a conversão do objeto para 
 * o formato JSON e vice-versa.
 * 
 * @author Brenno P. S. Santos, Sibele C. Oliveira, Silas S. Santos
 * @version 1.0
 * @since 25-08-2025
 */

public class Evento {
	
	/**
	 * Identificador único do evento, gerado pelo EventoRepository.
	 */
    @Expose
    private int idEvento;
    
    /**
     * Capacidade máima de participantes do evento.
     */
    @Expose
    private int capacidade;
    
    /**
     * Quantidade ingressos disponíveis para venda.
     */
    @Expose
    private int ingressosDisponiveis;
    
    /**
     * Nome oficial do evento.
     */
    @Expose
    private String nomeEvento;
    
    /**
     * local onde ocorrera o evento.
     */
    @Expose
    private String local;
    
    /**
     * Categoria do evento (ex: "Musica", "Esporte").
     */
    @Expose
    private String categoria;
    
    /**
     * O organizador responsável pelo evento.
     * @see Organizador
     */
    private Organizador organizador;
    
    /**
     * Descrição do que se trata o evento.
     */
    @Expose
    private String descrição;
    
    /**
     * Preço base do ingresso para o evento.
     */
    @Expose
    private double preco;
    
    /**
     * Data e hora em que o evento irá acontecer.
     */
    @Expose
    private LocalDateTime dataEvento;
    
    /**
     * URL da imagem de divulgação do evento
     */
    @Expose
    private String imageUrl;
    
    /**
     * Cupom de desconto para o evento, se houver.
     */
    @Expose
    private String cupomCode;
    
    /**
     * Valor de desconto oferecido pelo cupom. Se trata de um valor
     * fixo devido a lógica de negócio implementada.
     */
    @Expose
    private double cupomDiscountValue;
    
    @Expose
    private String status; 
    
    @Expose 
    private int organizadorId;

    /**
     * Construtor padrão, sem argumentos devido ao padrão de projeto BUILDER
     * que implementará a criação do objeto de uma forma mais flexivel.
     */
    public Evento() {}

	
	//Getters
    
    /**
     * Obtem o identificador unico do evento.
     * @return O ID (inteiro) do evento.
     */
	public int getIdEvento() {
		return idEvento;
	}
	
	/**
	 * Obtem a capacidade máxima de participantes do evento.
	 * @return A capacidade total (inteiro) do evento.
	 */
	public int getCapacidade() {
		return capacidade;
	}
	
	/**
	 * Obtem o numero atual de ingressos restantes.
	 * @return A quantidade de ingressos (inteiro) restantes.
	 */
	public int getIngressosDisponiveis() {
		return ingressosDisponiveis;
	}
	
	/**
	 * Obtem o nome do evento
	 * @return O nome (String) do evento.
	 */
	public String getNomeEvento() {
		return nomeEvento;
	}
	
	/**
	 * Obtem o local onde o evento ocorrera.
	 * @return O local (String) do evento.
	 */
	public String getLocal() {
		return local;
	}
	
	/**
	 * Obtem a categoria do evento.
	 * @return A categoria (String) do evento.
	 */
	public String getCategoria() {
		return categoria;
	}
	
	/**
	 * Obtem o organizador associado a este evento.
	 * @return O objeto {@link Organizador} responsável pelo evento.
	 */
	public Organizador getOrganizador() {
		return organizador;
	}
	
	/**
	 * Obtem a descrição detalhada do evento.
	 * @return A descrição (String) do evento.
	 */
	public String getDescrição() {
		return descrição;
	}
	
	/**
	 * Obtem o preco base do ingresso do evento.
	 * @return O preco (double) do ingresso.
	 */
	public double getPreco() {
		return preco;
	}
	
	/**
	 * Obtem a data e hora do evento.
	 * @return Um objeto {@link LocalDateTime} com a data e hora do evento.
	 */
	public LocalDateTime getDataEvento() {
		return dataEvento;
	}
	
	/**
     * Obtém a URL da imagem de divulgação do evento.
     * @return A URL (String) da imagem.
     */
	public String getImageUrl() {
		return imageUrl;
	}
	
	/**
     * Obtém o código do cupom de desconto.
     * @return O codigo de cupom (String) do evento.
     */
	public String getCupomCode() {
		return cupomCode;
	}

	/**
     * Obtém o valor do desconto associado ao cupom.
     * @return O valor de desconto (double) do cupom.
     */
	public double getCupomDiscountValue() {
		return cupomDiscountValue;
	}

    public String getStatus() {
        return status;
    }
    
    public int getOrganizadorId() {
        return organizadorId;
    }
    
    
	//Setters	

    public void setOrganizadorId(int organizadorId) {
        this.organizadorId = organizadorId;
    }

    public void setStatus(String status) {
        this.status = status;
    }	

	/**
     * Define o código do cupom de desconto.
     * @param cupomCode (String) O novo código de cupom para o evento.
     */
	public void setCupomCode(String cupomCode) {
		this.cupomCode = cupomCode;
	}

	/**
     * Define o valor do desconto do cupom.
     * @param cupomDiscountValue (double) O novo valor de desconto para o cupom.
     */
	public void setCupomDiscountValue(double cupomDiscountValue) {
		this.cupomDiscountValue = cupomDiscountValue;
	}
	
	/**
     * Define a URL da imagem de divulgação do evento.
     * @param imageUrl (String) A nova URL da imagem.
     */
	public void setImageUrl(String imageUrl) {
	        this.imageUrl = imageUrl;
	}
	
	/**
     * Define o identificador único do evento.
     * @param idEvento (inteiro) O novo ID para o evento.
     */
	public void setIdEvento(int idEvento) {
		this.idEvento = idEvento;
	}
	
	/**
     * Define a capacidade máxima do evento.
     * @param capacidade (inteiro) A nova capacidade total de participantes.
     */
	public void setCapacidade(int capacidade) {
		this.capacidade = capacidade;
	}
	
	/**
     * Define o número de ingressos disponíveis.
     * @param ingressosDisponiveis (inteiro) A nova quantidade de ingressos disponíveis.
     */
	public void setIngressosDisponiveis(int ingressosDisponiveis) {
		this.ingressosDisponiveis = ingressosDisponiveis;
	}
	
	/**
     * Define o nome do evento.
     * @param nomeEvento (String) O novo nome para o evento.
     */
	public void setNomeEvento(String nomeEvento) {
		this.nomeEvento = nomeEvento;
	}
	
	/**
     * Define o local onde o evento ocorrerá.
     * @param local (String) O novo endereço ou local do evento.
     */
	public void setLocal(String local) {
		this.local = local;
	}
	
	/**
     * Define a categoria do evento.
     * @param categoria (String) A nova categoria para o evento.
     */
	public void setCategoria(String categoria) {
		this.categoria = categoria;
	}
	
	/**
     * Define o organizador responsável pelo evento.
     * @param organizador ({@link Organizador}) O objeto a ser associado ao evento.
     */
	public void setOrganizador(Organizador organizador) {
		this.organizador = organizador;
	}
	
	/**
     * Define a descrição do evento.
     * @param descrição (String) O novo texto descritivo para o evento.
     */
	public void setDescrição(String descrição) {
		this.descrição = descrição;
	}
	
	/**
     * Define o preço do ingresso do evento.
     * @param preco (double) O novo preço para o ingresso.
     */
	public void setPreco(double preco) {
		this.preco = preco;
	}
	
	/**
     * Define a data e hora em que o evento ocorrerá.
     * @param dataEvento ({@link LocalDateTime}) O novo objeto de data e local para o evento.
     */
	public void setDataEvento(LocalDateTime dataEvento) {
		this.dataEvento = dataEvento;
	}


}
