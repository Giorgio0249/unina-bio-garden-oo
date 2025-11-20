package it.unina.biogarden.model;

import java.math.BigDecimal; 
import java.time.LocalDate;

public class Coltura {
	
	private final int id_coltura;
	private final BigDecimal quantitaPrevista;
	private final LocalDate dataSemina;
	private final StatoColtura stato;
	private final int fk_progetto;
	private final String fk_tipo_coltura;
	
	public Coltura(int id_coltura, BigDecimal quantitaPrevista, LocalDate dataSemina, StatoColtura stato, int fk_progetto, String fk_tipo_coltura) {
		this.id_coltura=id_coltura;
		this.quantitaPrevista=quantitaPrevista;
		this.dataSemina=dataSemina;
		this.stato=stato;
		this.fk_progetto=fk_progetto;
		this.fk_tipo_coltura=fk_tipo_coltura;
	}
	
	
	//getters
	public int getId_coltura() {return id_coltura;}
	public BigDecimal getQuantitaPrevista() {return quantitaPrevista;}
	public LocalDate getDataSemina() {return dataSemina;}
	public StatoColtura getStato() {return stato;}
	public int getFk_progetto() {return fk_progetto;}
	public String getFk_tipo_coltura() {return fk_tipo_coltura;}
	
	
	@Override
	public String toString() {
		return String.format("#%d %s qp=%s semina=%s stato=%s proj=%d", id_coltura, fk_tipo_coltura, quantitaPrevista, dataSemina, stato, fk_progetto);
	}
	

}
