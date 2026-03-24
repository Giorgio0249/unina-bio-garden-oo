package it.unina.biogarden.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.unina.biogarden.dao.AttivitaDao;
import it.unina.biogarden.dao.ColturaDao;
import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.LottoDao;
import it.unina.biogarden.dao.ProgettoDao;
import it.unina.biogarden.dao.RaccoltaDao;
import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.Coltura;
import it.unina.biogarden.model.Coltivatore;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.Raccolta;
import it.unina.biogarden.model.StatoAttivita;
import it.unina.biogarden.model.TipoAttivita;


public class AttivitaService {
	
	private final AttivitaDao attivitaDao;
	private final RaccoltaDao raccoltaDao;
	
	private final ColturaService colturaService;
	private final ProgettoService progettoService;
	private final LottoService lottoService;

	public AttivitaService(AttivitaDao attivitaDao, RaccoltaDao raccoltaDao, ColturaService colturaService, ProgettoService progettoService, LottoService lottoService) {
		this.attivitaDao=attivitaDao;
		this.raccoltaDao=raccoltaDao;
		this.colturaService=colturaService;
		this.progettoService=progettoService;
		this.lottoService=lottoService;
	}
	
	public AttivitaService() {
		this(DaoFactory.createAttivitaDao(), DaoFactory.createRaccoltaDao(), new ColturaService(), new ProgettoService(), new LottoService());
	}
	
	
	public int createAttivitaPerProprietario(Proprietario proprietario, Attivita attivita)throws Exception {
		
		Coltura coltura=colturaService.findById(attivita.getFk_coltura());
		
		ProgettoStagionale progetto=progettoService.findById(coltura.getFk_progetto());
		Lotto lotto=lottoService.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi creare attività su colture che non appartengono ai tuoi lotti.");
		}
		
		int id_creata = attivitaDao.create(attivita);
		System.out.println("Attività ("+id_creata+") creata con successo.");
		return id_creata;
		
	}
	
	
	public void updateAttivitaPerProprietario(Proprietario proprietario, Attivita modificata)throws Exception{
		
		Attivita esistente=this.findById(modificata.getId_attivita());
		
		Coltura colturaAttuale=colturaService.findById(esistente.getFk_coltura());
		ProgettoStagionale progettoAttuale=progettoService.findById(colturaAttuale.getFk_progetto());
		Lotto lottoAttuale=lottoService.findById(progettoAttuale.getFk_lotto());
		
		if(!lottoAttuale.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi modificare un'attività che non appartiene ai tuoi lotti.");
		}
		
		
		if(modificata.getFk_coltura()!=esistente.getFk_coltura()) {
			
			Coltura colturaNuova=colturaService.findById(modificata.getFk_coltura());
			
			ProgettoStagionale progettoNuovo=progettoService.findById(colturaNuova.getFk_progetto());
			Lotto lottoNuovo=lottoService.findById(progettoNuovo.getFk_lotto());
			
			if(!lottoNuovo.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
				throw new IllegalArgumentException("Non puoi spostare un'attività su una coltura che non è su un tuo lotto.");
			}
		}
		
		attivitaDao.update(modificata);
		System.out.println("Attività ("+modificata.getId_attivita()+") modificata con successo.");
	}
	
	public void deleteAttivitaPerProprietario(Proprietario proprietario, int id_attivita)throws Exception {
		
		Attivita attivita=this.findById(id_attivita);
		
		Coltura coltura=colturaService.findById(attivita.getFk_coltura());
		ProgettoStagionale progetto=progettoService.findById(coltura.getFk_progetto());
		Lotto lotto=lottoService.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi cancellare un'attività che non appartiene ai tuoi lotti.");
		}
		
		attivitaDao.delete(id_attivita);
		System.out.println("Attività ("+id_attivita+") eliminata con successo.");
		
	}
	
	public void updateStatoAttivitaPerProprietario(Proprietario proprietario, int id_attivita, StatoAttivita stato)throws Exception{
		
		Attivita esistente=this.findById(id_attivita);
		
		Coltura colturaAttuale=colturaService.findById(esistente.getFk_coltura());
		ProgettoStagionale progettoAttuale=progettoService.findById(colturaAttuale.getFk_progetto());
		Lotto lottoAttuale=lottoService.findById(progettoAttuale.getFk_lotto());
		
		if(!lottoAttuale.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi modificare lo stato di un'attività che non appartiene ai tuoi lotti.");
		}
		
		attivitaDao.updateStato(id_attivita, stato);
		System.out.format("Stato attività (%d) modificato con successo (%s --> %s).", id_attivita, esistente.getStato().toString(), stato.toString());
	}
	
	public void updateStatoPerColtivatore(Coltivatore coltivatore, int id_attivita, StatoAttivita nuovoStato) throws Exception {
	    Attivita esistente = attivitaDao.findById(id_attivita);
	    if (esistente == null) {
	        throw new IllegalArgumentException("Attività non trovata.");
	    }

	    if (!esistente.getFk_coltivatore().equalsIgnoreCase(coltivatore.getEmail())) {
	        throw new IllegalArgumentException("Non puoi modificare un'attività assegnata a un altro coltivatore.");
	    }

	    if (esistente.getTipoAttivita() != TipoAttivita.SEMINA && 
	       (nuovoStato == StatoAttivita.IN_CORSO || nuovoStato == StatoAttivita.COMPLETATA)) {
	        
	        List<Attivita> attivitaColtura = attivitaDao.findByColtura(esistente.getFk_coltura());
	        
	        boolean seminaCompletata = attivitaColtura.stream()
	            .anyMatch(a -> a.getTipoAttivita() == TipoAttivita.SEMINA && a.getStato() == StatoAttivita.COMPLETATA);
	        
	        if (!seminaCompletata) {
	            throw new Exception("Operazione negata: devi prima completare l'attività di SEMINA per questa coltura!");
	        }
	    }

	    attivitaDao.updateStato(id_attivita, nuovoStato);
	    
	    System.out.format("Coltivatore %s ha modificato stato attività %d in %s%n", 
	                      coltivatore.getEmail(), id_attivita, nuovoStato);
	}
	
	public void completaAttivitaConRaccolta(Coltivatore coltivatore, int id_attivita, BigDecimal quantita) throws Exception {
	    Attivita esistente = attivitaDao.findById(id_attivita);
	    if (esistente == null) 
	    	throw new IllegalArgumentException("Attività non trovata.");

	    // AGGIUNTA: CONTROLLO STATO SEMINA
	    if (esistente.getTipoAttivita() == TipoAttivita.RACCOLTA) {
	        // Verifichiamo se esiste una semina completata per questa coltura
	    }

	    // Prova a creare la raccolta
	    if (esistente.getTipoAttivita() == TipoAttivita.RACCOLTA) {
	        Raccolta nuova = new Raccolta(
	            0, 
	            LocalDate.now(), 
	            quantita, 
	            esistente.getFk_coltura()
	        );
	        
	        try {
	            raccoltaDao.create(nuova); 
	        } catch (Exception e) {
	            // Se l'errore contiene il nome del vincolo UNIQUE
	            if (e.getMessage().contains("unq_raccolta")) {
	                throw new Exception("Hai già registrato una raccolta per questa coltura oggi!");
	            }
	            // Se l'errore viene dal trigger della semina
	            if (e.getMessage().contains("dataSemina")) {
	                throw new Exception("Impossibile raccogliere: l'attività di SEMINA deve essere completata prima della raccolta!");
	            }
	            throw e; // Rilancia gli altri errori
	        }
	    }

	    // Solo se la raccolta va a buon fine, completiamo l'attività
	    attivitaDao.updateStato(id_attivita, StatoAttivita.COMPLETATA);
	}
	
	//metodi di lettura

	
	public List<Attivita> findByColtivatore(String email) throws Exception {
	    if (email == null || email.isEmpty()) {
	        throw new IllegalArgumentException("Email coltivatore non valida.");
	    }
	    return attivitaDao.findByColtivatore(email);
	}
	
	
	
	public List<Attivita> findByColtura(int id_coltura)throws Exception{
		colturaService.findById(id_coltura);
		
		List<Attivita> out=attivitaDao.findByColtura(id_coltura);
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessuna attività trovata sulla coltura ("+id_coltura+")");
		}
		return out;
	}
	
	public List<Attivita> findByProgetto(int id_progetto) throws Exception {
	    progettoService.findById(id_progetto);
	    
	    List<Attivita> out = attivitaDao.findByProgetto(id_progetto);
	    if (out.isEmpty()) {
	        throw new IllegalArgumentException("Nessuna attività pianificata per questo progetto.");
	    }
	    return out;
	}
	
	public Attivita findById(int id_attivita)throws Exception{
		Attivita attivita=attivitaDao.findById(id_attivita);
		
		if(attivita==null) {
			throw new IllegalArgumentException("Nessuna attività con id ("+id_attivita+") trovata.");
		}
		return attivita;
	}
	

}
