package it.unina.biogarden.model;

import java.time.LocalDate;

public class Attivita {
	
	private final int id_attivita;
	private final TipoAttivita tipoAttivita;
	private final StatoAttivita stato;
	private final LocalDate dataPianificata;
	private final LocalDate dataEffettiva;
	private final String descrizione;
	private final int fk_coltura;
	private final String fk_coltivatore;
	
	public Attivita(int id_attivita, TipoAttivita tipoAttivita, StatoAttivita stato, LocalDate dataPianificata, LocalDate dataEffettiva, String descrizione, int fk_coltura, String fk_coltivatore) {
		this.id_attivita=id_attivita;
		this.tipoAttivita=tipoAttivita;
		this.stato=stato;
		this.dataPianificata=dataPianificata;
		this.dataEffettiva=dataEffettiva;
		this.descrizione=descrizione;
		this.fk_coltura=fk_coltura;
		this.fk_coltivatore=fk_coltivatore;
	}

	
	//getters
	public int getId_attivita() {return id_attivita;}
	public TipoAttivita getTipoAttivita() {return tipoAttivita;}
	public StatoAttivita getStato() {return stato;}
	public LocalDate getDataPianificata() {return dataPianificata;}
	public LocalDate getDataEffettiva() {return dataEffettiva;}
	public String getDescrizione() {return descrizione;}
	public int getFk_coltura() {return fk_coltura;}
	public String getFk_coltivatore() {return fk_coltivatore;}
	
	@Override
	public String toString() {
		return String.format("#%d %s [%s] pianificazione:%s effettiva:%s coltura:%d coltivatore:%s descrizione:%s", 
				id_attivita, tipoAttivita, stato, dataPianificata, dataEffettiva, fk_coltura, fk_coltivatore, descrizione);
	}
}
