package it.unina.biogarden.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Notifica {
	
	private final int id;
	private final String creatore_proprietario_email;
	private final String destinatario_coltivatore_email;// se null -> a tutti
	private final String titolo;
	private final String messaggio;
	private final TipoNotifica tipo;
	private final LocalDateTime data_creazione;
	
	public static final DateTimeFormatter formatter=DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
	
	private final Integer fk_progetto;
	private final Integer fk_attivita;
	
	public Notifica(int id, String creatore_proprietario_email, String destinatario_coltivatore_email, String titolo, String messaggio, TipoNotifica tipo, LocalDateTime data_creazione, Integer fk_progetto, Integer fk_attivita) {
		this.id=id;
		this.creatore_proprietario_email=creatore_proprietario_email;
		this.destinatario_coltivatore_email=destinatario_coltivatore_email;
		this.titolo=titolo;
		this.messaggio=messaggio;
		this.tipo=tipo;
		this.data_creazione=data_creazione;
		this.fk_progetto=fk_progetto;
		this.fk_attivita=fk_attivita;
	}
	
	
	//getters
	public int getId() {return id;}
	public String getCreatore_proprietario_email() {return creatore_proprietario_email;}
	public String getDestinatario_coltivatore_email() {return destinatario_coltivatore_email;}
	public String getTitolo() {return titolo;}
	public String getMessaggio() {return messaggio;}
	public TipoNotifica getTipo() {return tipo;}
	public LocalDateTime getData_creazione() {return data_creazione;}
	public Integer getFk_progetto() {return fk_progetto;}
	public Integer getFk_attivita() {return fk_attivita;}
	
	@Override
	public String toString() {
	    
	    String destinatarioDisplay;
	    
	    if (destinatario_coltivatore_email == null) {
	        destinatarioDisplay = "(a: TUTTI)";
	    } else {
	        destinatarioDisplay = "(a: " + destinatario_coltivatore_email + ")";
	    }
	    
	    String dataFormattata=this.data_creazione.format(formatter);
	    
	    return String.format("[%s] %s %s - %s [Creata il: %s]", this.tipo, this.titolo, destinatarioDisplay, this.messaggio, dataFormattata);
	}

}
