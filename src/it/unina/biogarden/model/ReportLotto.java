package it.unina.biogarden.model;

import java.math.BigDecimal;

public class ReportLotto {
	
	private final int id_lotto;
	private final String nome_lotto;
	private final String tipo_coltura;
	
	private final BigDecimal quantita_min;
	private final BigDecimal quantita_med;
	private final BigDecimal quantita_max;
	private final long num_raccolte;
	
	public ReportLotto(int id_lotto,
						String nome_lotto,
						String tipo_coltura,
						BigDecimal quantita_min,
						BigDecimal quantita_med,
						BigDecimal quantita_max,
						long num_raccolte) {
		this.id_lotto=id_lotto;
		this.nome_lotto=nome_lotto;
		this.tipo_coltura=tipo_coltura;
		this.quantita_min=quantita_min;
		this.quantita_med=quantita_med;
		this.quantita_max=quantita_max;
		this.num_raccolte=num_raccolte;
	}
	
	//getters
	public int getId_lotto() {return id_lotto;}
	public String getNome_lotto() {return nome_lotto;}
	public String getTipo_coltura() {return tipo_coltura;}
	public BigDecimal getQuantita_min() {return quantita_min;}
	public BigDecimal getQuantita_med() {return quantita_med;}
	public BigDecimal getQuantita_max() {return quantita_max;}
	public long getNum_racolte() {return num_raccolte;}
	
	@Override
	public String toString() {
		return String.format("Lotto %d (%s), coltura=%s: min=%s, med=%s, max=%s, num_raccolte=%d", id_lotto, nome_lotto, tipo_coltura, quantita_min, quantita_med, quantita_max, num_raccolte);
	}
	

}
