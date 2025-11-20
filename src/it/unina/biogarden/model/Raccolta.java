package it.unina.biogarden.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Raccolta {
	
	private final int id_raccolta;
	private final LocalDate dataRaccolta;
	private final BigDecimal quantitaEffettiva;
	private final int fk_coltura;
	
	public Raccolta(int id_raccolta, LocalDate dataRaccolta, BigDecimal quantitaEffettiva, int fk_coltura) {
		this.id_raccolta=id_raccolta;
		this.dataRaccolta=dataRaccolta;
		this.quantitaEffettiva=quantitaEffettiva;
		this.fk_coltura=fk_coltura;
	}
	
	//getters
	public int getId_raccolta() {return id_raccolta;}
	public LocalDate getDataRaccolta() {return dataRaccolta;}
	public BigDecimal getQuantitaEffettiva() {return quantitaEffettiva;}
	public int getFk_coltura() {return fk_coltura;}
	
	@Override
	public String toString() {
		return String.format("#%d %s qe=%s colt=%d", id_raccolta, dataRaccolta, quantitaEffettiva, fk_coltura);
	}

}
