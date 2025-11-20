package it.unina.biogarden.model;

import java.time.LocalDate;

public class ProgettoStagionale {
	
	private final int id_progetto;
	private final String nome;
	private final Stagione stagione;
	private final int anno;
	private final LocalDate dataInizio; 
	private final LocalDate dataFine;
	private final int fk_lotto;
	private final String descrizione;
	
	public ProgettoStagionale(int id_progetto, String nome, Stagione stagione, int anno, LocalDate dataInizio, LocalDate dataFine, int fk_lotto, String descrizione) {
		this.id_progetto=id_progetto;
		this.nome=nome;
		this.stagione=stagione;
		this.anno=anno;
		this.dataInizio=dataInizio;
		this.dataFine=dataFine;
		this.fk_lotto=fk_lotto;
		this.descrizione=descrizione;
	}
	
	//Getters
	public int getId_progetto(){return id_progetto;}
	public String getNome() {return nome;}
	public Stagione getStagione() {return stagione;}
	public int getAnno() {return anno;}
	public LocalDate getDataInizio() {return dataInizio;}
	public LocalDate getDataFine() {return dataFine;}
	public int getFk_lotto() {return fk_lotto;}
	public String getDescrizione() {return descrizione;}
	
	@Override
	public String toString() {
		return String.format("#%d %s (%s %d) [%tF -> %tF] lotto=%d", id_progetto, nome, stagione, anno, dataInizio, dataFine, fk_lotto); 
	}

}
