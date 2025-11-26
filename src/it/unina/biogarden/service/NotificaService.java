package it.unina.biogarden.service;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import it.unina.biogarden.dao.AttivitaDao;
import it.unina.biogarden.dao.ConnectionFactory;
import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.NotificaDao;
import it.unina.biogarden.dao.ProgettoDao;
import it.unina.biogarden.model.Notifica;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.TipoNotifica;

public class NotificaService {
	
	private final NotificaDao notificaDao;
	private final AttivitaDao attivitaDao;
	private final ProgettoDao progettoDao;
	
	private final ProgettoService progettoService;
	private final ColtivatoreService coltivatoreService;
	private final AttivitaService attivitaService;
	
	public NotificaService(NotificaDao notificaDao, AttivitaDao attivitaDao, ProgettoDao progettoDao, ProgettoService progettoService, ColtivatoreService coltivatoreService, AttivitaService attivitaService) {
		this.notificaDao=notificaDao;
		this.attivitaDao=attivitaDao;
		this.progettoDao=progettoDao;
		this.progettoService=progettoService;
		this.coltivatoreService=coltivatoreService;
		this.attivitaService=attivitaService;
	}
	
	public NotificaService() {
		this(DaoFactory.createNotificaDao(), DaoFactory.createAttivitaDao(), DaoFactory.createProgettoDao(), new ProgettoService(), new ColtivatoreService(), new AttivitaService());
	}
	
	
	public int createNotificaPerTutti(Proprietario proprietario,
										String titolo, String messaggio,
										TipoNotifica tipo, Integer fk_progetto,
										Integer fk_attivita)throws Exception{
		
		if(fk_progetto != null) {
			progettoService.findById(fk_progetto);
			if(!progettoDao.existsProgettoPerProprietario(fk_progetto, proprietario.getEmail())) {
			throw new IllegalArgumentException("Il progetto indicato non appartiene al proprietario "+proprietario.getEmail());
			}
		}
		
		if(fk_attivita != null) {
			attivitaService.findById(fk_attivita);
			if(!attivitaDao.existsAttivitaPerProprietario(fk_attivita, proprietario.getEmail()))
			throw new IllegalArgumentException("L'attività indicata non appartiene al proprietario "+proprietario.getEmail());			
		}
		
		if(fk_progetto != null && fk_attivita != null) {
			if(!attivitaDao.existsAttivitaInProgetto(fk_attivita, fk_progetto)) {
				throw new IllegalArgumentException("L'attività ("+fk_attivita+") non appartiene al progetto ("+fk_progetto+")");							
			}	
		}
		
		Notifica n=new Notifica(0, proprietario.getEmail(), null, titolo, messaggio, tipo, LocalDateTime.now(), fk_progetto, fk_attivita);
		
		int id_creata=notificaDao.create(n);
		
		System.out.println("Notifica ("+id_creata+") rivolta a tutti inserita con successo.");
		
		return id_creata;
		}
	
	
	public int createNotificaPerColtivatore(Proprietario proprietario, String destinatario_coltivatore_email, 
												String titolo, String messaggio,
												TipoNotifica tipo, Integer fk_progetto,
												Integer fk_attivita)throws Exception{
		
		coltivatoreService.findByEmail(destinatario_coltivatore_email);
				
		if(!attivitaDao.existsColtivatorePerProprietario(destinatario_coltivatore_email, proprietario.getEmail())) {
			throw new IllegalArgumentException("Il coltivatore "+destinatario_coltivatore_email+" non è coinvolto in progetti del proprietario "+proprietario.getEmail());
		}
		
		if(fk_progetto != null) {
			progettoService.findById(fk_progetto);
			if(!progettoDao.existsProgettoPerProprietario(fk_progetto, proprietario.getEmail())) {
			throw new IllegalArgumentException("Il progetto indicato non appartiene al proprietario "+proprietario.getEmail());
			}
		}
		
		if(fk_attivita != null) {
			attivitaService.findById(fk_attivita);
			if(!attivitaDao.existsAttivitaPerProprietario(fk_attivita, proprietario.getEmail()))
			throw new IllegalArgumentException("L'attività indicata non appartiene al proprietario "+proprietario.getEmail());			
		}
		
		//controllo per vedere che attività appartenga al progetto indicato.
		if(fk_progetto != null && fk_attivita != null) {
			if(!attivitaDao.existsAttivitaInProgetto(fk_attivita, fk_progetto)) {
				throw new IllegalArgumentException("L'attività ("+fk_attivita+") non appartiene al progetto ("+fk_progetto+")");							
			}	
		}
		
		Notifica n=new Notifica(0, proprietario.getEmail(), destinatario_coltivatore_email, 
								titolo, messaggio, tipo, LocalDateTime.now(), fk_progetto, fk_attivita);
		
		int id_creata=notificaDao.create(n);
		
		System.out.println("Notifica ("+id_creata+") rivolta a "+destinatario_coltivatore_email+" inserita con successo.");
		
		return id_creata;
	}
	
	
	public void deleteNotificaPerProprietario(Proprietario proprietario, int id_notifica)throws Exception{
		
		Notifica notifica= this.findById(id_notifica);
		
		if(!notifica.getCreatore_proprietario_email().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi eliminare notifiche create da un altro proprietario.");
		}
		
		notificaDao.delete(id_notifica);
		System.out.println("Notifica ("+id_notifica+") eliminata con successo.");
	}
	
	
	//metodi di lettura
	public List<Notifica> findByColtivatore(String email_colt)throws Exception{
		coltivatoreService.findByEmail(email_colt);
		
		List<Notifica> out=notificaDao.findByColtivatore(email_colt);
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessuna notifica trovata per il coltivatore: "+email_colt);					
		}
		return out;
	}
	
	
	
	public List<Notifica> findByProprietario(Proprietario proprietario)throws Exception{
		
		List<Notifica> out=notificaDao.findByProprietario(proprietario.getEmail());
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessuna notifica trovata per il proprietario: "+proprietario.getEmail());			
		}
		return out;
	}
	
	
	
	public List<Notifica> findByProgetto(int id_progetto)throws Exception{
		progettoService.findById(id_progetto);
		
		List<Notifica> out=notificaDao.findByProgetto(id_progetto);
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessuna notifica trovata per il progetto ("+id_progetto+")");
		}
			return out;
	}
	
	
	
	public Notifica findById(int id_notifica)throws Exception{
		Notifica notifica=notificaDao.findById(id_notifica);
		if(notifica==null) {
			throw new IllegalArgumentException("Nessuna notifica con id ("+id_notifica+") trovata.");
		}
			return notifica;
	}
	
	
	//metodo per generazione automatica notifiche
	
	public void generaAutomaticheDaView(Proprietario proprietario)throws Exception{
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection()){
			
			//attività in ritardo
			String sqlRitardo="""
					SELECT
						   v.id_attivita,
						   v.tipoAttivita,
						   v.dataPianificata,
						   v.id_coltura,
						   p.id_progetto,
						   p.nome AS progetto_nome
					FROM vw_attivita_in_ritardo v
					JOIN Coltura c ON v.id_coltura = c.id_coltura
					JOIN ProgettoStagionale p ON c.fk_progetto = p.id_progetto
					JOIN Lotto l ON p.fk_lotto = l.id_lotto
					WHERE l.fk_proprietario = ?
					""";
			
			try(PreparedStatement ps=conn.prepareStatement(sqlRitardo)){
				ps.setString(1, proprietario.getEmail());
				
				try(ResultSet rs=ps.executeQuery()){
					while(rs.next()) {
						
						String titolo="Attività in ritardo: "+rs.getString("tipoAttivita");
						String messaggio="L'attività era prevista per "+rs.getObject("dataPianificata", LocalDate.class)+
											" nel progetto "+rs.getString("progetto_nome");
						
						this.createNotificaPerTutti(proprietario, 
													titolo, 
													messaggio, 
													TipoNotifica.ATTIVITA_IN_RITARDO, 
													rs.getInt("id_progetto"), 
													rs.getInt("id_attivita"));
					}
				}
			}
			
			//attività imminenti
			String sqlImminenti="""
					SELECT
						   v.id_attivita,
						   v.tipoAttivita,
						   v.dataPianificata,
						   v.id_coltura,
						   p.id_progetto,
						   p.nome AS progetto_nome
					FROM vw_attivita_prossime v
					JOIN Coltura c ON v.id_coltura = c.id_coltura
					JOIN ProgettoStagionale p ON c.fk_progetto = p.id_progetto
					JOIN Lotto l ON p.fk_lotto = l.id_lotto
					WHERE l.fk_proprietario = ?
					""";
			
			try(PreparedStatement ps=conn.prepareStatement(sqlImminenti)){
				
				ps.setString(1, proprietario.getEmail());
				
				try(ResultSet rs=ps.executeQuery()){
					
					while(rs.next()) {
						
						String titolo="Attività imminente: "+rs.getString("tipoAttivita");
						String messaggio="Attività prevista per "+rs.getObject("dataPianificata", LocalDate.class)+
											" nel progetto "+rs.getString("progetto_nome");	
						
						this.createNotificaPerTutti(proprietario,
													titolo, 
													messaggio, 
													TipoNotifica.ATTIVITA_IMMINENTE, 
													rs.getInt("id_progetto"), 
													rs.getInt("id_attivita"));
					}
				}
			}
		}
	}

}
