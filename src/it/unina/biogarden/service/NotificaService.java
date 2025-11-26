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
		
		List<Notifica> notificheDaCreare=notificaDao.genereAutomaticheDaView(proprietario.getEmail());
		
		if(notificheDaCreare.isEmpty()) {
			System.out.println("Nessuna notifica automatica da generare.");
			return;
		}
		
		int count=0;
		
		for(Notifica n:notificheDaCreare) {
			
			this.createNotificaPerTutti(proprietario,
										n.getTitolo(), 
										n.getMessaggio(), 
										n.getTipo(), 
										n.getFk_progetto(), 
										n.getFk_attivita());
			
			count++;
		}
		
		System.out.println("Generazione notifiche automatiche completata. Inserite "+count+" notifiche nel DB.");
		
	}

}
