package it.unina.biogarden.service;

import java.util.List;

import it.unina.biogarden.dao.AttivitaDao;
import it.unina.biogarden.dao.ColturaDao;
import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.LottoDao;
import it.unina.biogarden.dao.ProgettoDao;
import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.Coltura;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.StatoAttivita;


public class AttivitaService {
	
	private final AttivitaDao attivitaDao;
	private final ColturaDao colturaDao;
	private final LottoDao lottoDao;
	private final ProgettoDao progettoDao;

	public AttivitaService(AttivitaDao attivitaDao, ColturaDao colturaDao, LottoDao lottoDao, ProgettoDao progettoDao) {
		this.attivitaDao=attivitaDao;
		this.colturaDao=colturaDao;
		this.lottoDao=lottoDao;
		this.progettoDao=progettoDao;
	}
	
	public AttivitaService() {
		this(DaoFactory.createAttivitaDao(), DaoFactory.createColturaDao(), DaoFactory.createLottoDao(), DaoFactory.createProgettoDao());
	}
	
	
	public int createAttivitaPerProprietario(Proprietario proprietario, Attivita attivita)throws Exception {
		
		Coltura coltura=colturaDao.findById(attivita.getFk_coltura());
		if(coltura==null) {
			throw new IllegalArgumentException("La coltura ("+attivita.getFk_coltura()+") non esiste.");
		}
		
		ProgettoStagionale progetto=progettoDao.findById(coltura.getFk_progetto());
		Lotto lotto=lottoDao.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi creare attività su colture che non appartengono ai tuoi lotti.");
		}
		
		int id_creata = attivitaDao.create(attivita);
		System.out.println("Attività ("+id_creata+") creata con successo.");
		return id_creata;
		
	}
	
	
	public void updateAttivitaPerProprietario(Proprietario proprietario, Attivita modificata)throws Exception{
		
		Attivita esistente=attivitaDao.findById(modificata.getId_attivita());
		if(esistente==null) {
			throw new IllegalArgumentException("L'attività ("+modificata.getId_attivita()+") non esiste.");
		}
		
		Coltura colturaAttuale=colturaDao.findById(esistente.getFk_coltura());
		ProgettoStagionale progettoAttuale=progettoDao.findById(colturaAttuale.getFk_progetto());
		Lotto lottoAttuale=lottoDao.findById(progettoAttuale.getFk_lotto());
		
		if(!lottoAttuale.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi modificare un'attività che non appartiene ai tuoi lotti.");
		}
		
		
		if(modificata.getFk_coltura()!=esistente.getFk_coltura()) {
			Coltura colturaNuova=colturaDao.findById(modificata.getFk_coltura());
			if(colturaNuova==null) {
				throw new IllegalArgumentException("La nuova coltura ("+modificata.getFk_coltivatore()+") non esiste.");
			}
			
			ProgettoStagionale progettoNuovo=progettoDao.findById(colturaNuova.getFk_progetto());
			Lotto lottoNuovo=lottoDao.findById(progettoNuovo.getFk_lotto());
			
			if(!lottoNuovo.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
				throw new IllegalArgumentException("Non puoi spostare un'attività su una coltura che non è su un tuo lotto.");
			}
		}
		
		
		attivitaDao.update(modificata);
		System.out.println("Attività ("+modificata.getId_attivita()+") modificata con successo.");
	}
	
	public void deleteAttivitaPerProprietario(Proprietario proprietario, int id_attivita)throws Exception {
		
		Attivita attivita=attivitaDao.findById(id_attivita);
		if(attivita==null) {
			throw new IllegalArgumentException("L'attività ("+id_attivita+") non esiste.");
		}
		
		Coltura coltura=colturaDao.findById(attivita.getFk_coltura());
		ProgettoStagionale progetto=progettoDao.findById(coltura.getFk_progetto());
		Lotto lotto=lottoDao.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi cancellare un'attività che non appartiene ai tuoi lotti.");
		}
		
		attivitaDao.delete(id_attivita);
		System.out.println("Attività ("+id_attivita+") eliminata con successo.");
		
	}
	
	public void updateStatoAttivitaPerProprietario(Proprietario proprietario, int id_attivita, StatoAttivita stato)throws Exception{
		
		Attivita esistente=attivitaDao.findById(id_attivita);
		if(esistente==null) {
			throw new IllegalArgumentException("L'attività ("+id_attivita+") non esiste.");
		}
		
		Coltura colturaAttuale=colturaDao.findById(esistente.getFk_coltura());
		ProgettoStagionale progettoAttuale=progettoDao.findById(colturaAttuale.getFk_progetto());
		Lotto lottoAttuale=lottoDao.findById(progettoAttuale.getFk_lotto());
		
		if(!lottoAttuale.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi modificare lo stato di un'attività che non appartiene ai tuoi lotti.");
		}
		
		
		attivitaDao.updateStato(id_attivita, stato);
		System.out.format("Stato attività (%d) modificato con successo (%s --> %s).", id_attivita, esistente.getStato().toString(), stato.toString());
	}
	
	//metodi di lettura
	public List<Attivita> findByColtura(int id_coltura)throws Exception{
		return attivitaDao.findByColtura(id_coltura);
	}
	
	public Attivita findById(int id_attivita)throws Exception{
		return attivitaDao.findById(id_attivita);
	}
	
	
}
