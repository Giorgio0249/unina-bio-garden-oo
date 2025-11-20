package it.unina.biogarden.model;

import java.math.BigDecimal;

public class Lotto {
	
	private final int id_lotto;
	private final String posizione;
	private final BigDecimal superficie;
	private final String fk_proprietario;
	
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
	
	@Override
	public String toString() {
		return String.format("#%d %s (%.2f m²) proprietario = %s", id_lotto, posizione, superficie, fk_proprietario);
	}

}
