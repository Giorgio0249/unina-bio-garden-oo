package it.unina.biogarden.model;

import java.math.BigDecimal;

public class ReportLotto {
	
	private final int id_progetto;
	private final String nome_progetto;
	private final String tipo_coltura;
	
	private final BigDecimal quantita_min;
	private final BigDecimal quantita_med;
	private final BigDecimal quantita_max;
	private final long num_raccolte;
	
	public ReportLotto(int id_progetto,
						String nome_progetto,
						String tipo_coltura,
						BigDecimal quantita_min,
						BigDecimal quantita_med,
						BigDecimal quantita_max,
						long num_raccolte) {
		this.id_progetto=id_progetto;
		this.nome_progetto=nome_progetto;
		this.tipo_coltura=tipo_coltura;
		this.quantita_min=quantita_min;
		this.quantita_med=quantita_med;
		this.quantita_max=quantita_max;
		this.num_raccolte=num_raccolte;
	}
	
	//getters
	public int getId_progetto() {return id_progetto;}
	public String getNome_progetto() {return nome_progetto;}
	public String getTipo_coltura() {return tipo_coltura;}
	public BigDecimal getQuantita_min() {return quantita_min;}
	public BigDecimal getQuantita_med() {return quantita_med;}
	public BigDecimal getQuantita_max() {return quantita_max;}
	public long getNum_racolte() {return num_raccolte;}
	
	@Override
	public String toString() {
		return String.format("Progetto %d (%s), coltura=%s: min=%s, med=%s, max=%s, num_raccolte=%d", id_progetto, nome_progetto, tipo_coltura, quantita_min, quantita_med, quantita_max, num_raccolte);
	}
	

}
