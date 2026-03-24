package it.unina.biogarden.model;

import java.math.BigDecimal;

public class Lotto {
	
	private final int id_lotto;
	private final String posizione;
	private final BigDecimal superficie;
	private final String fk_proprietario;
	private String nomeProprietario;		//aggiunti dopo, non modifico costruttore perche dovrei modificare tutte le classi che invocano questa
	private String cognomeProprietario;		//creo solo setters per questi due
	
	public Lotto(int id_lotto, String posizione, BigDecimal superficie, String fk_proprietario) {
		this.id_lotto=id_lotto;
		this.posizione=posizione;
		this.superficie=superficie;
		this.fk_proprietario=fk_proprietario;
	}
	
	//getters
	public int getId_lotto() {return id_lotto;}
	public String getPosizione() {return posizione;}
	public BigDecimal getSuperficie() {return superficie;}
	public String getFk_proprietario() {return fk_proprietario;}
	
	public void setNomeProprietario(String nome) { this.nomeProprietario = nome; }
	public void setCognomeProprietario(String cognome) { this.cognomeProprietario = cognome; }
	
	//se nome prop == null ritorna l' email
	public String getNomeCompleto() { 
	    return (nomeProprietario != null) ? nomeProprietario + " " + cognomeProprietario : fk_proprietario; 
	}

	@Override
	public String toString() {
	    // Se i nomi sono stati caricati, usa quelli, altrimenti usa l'email
	    return "#" + id_lotto + " " + posizione + " - Proprietario: " + getNomeCompleto();
	}

}
