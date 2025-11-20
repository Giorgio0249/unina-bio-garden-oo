package it.unina.biogarden.model;

import java.time.LocalDate;

//Coltivatore serve solo classe model(il coltivatore non deve loggarsi)
public class Coltivatore {

	private final String email;
	private final String nome;
	private final String cognome;
	private final LocalDate dataN;
	
	public Coltivatore(String email, String nome, String cognome, LocalDate dataN) {
		this.email=email;
		this.nome=nome;
		this.cognome=cognome;
		this.dataN=dataN;
	}
	
	//getters
	public String getEmail() {return email;}
	public String getNome() {return nome;}
	public String getCognome() {return cognome;}
	public LocalDate getDataN() {return dataN;}
	
	@Override
	public String toString() {
		return String.format("%s %s <%s> (%s)", nome, cognome, email, dataN);
	}
}
