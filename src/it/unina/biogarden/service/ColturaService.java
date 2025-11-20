package it.unina.biogarden.service;

import java.util.List;

import it.unina.biogarden.dao.ColturaDao;
import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.LottoDao;
import it.unina.biogarden.dao.ProgettoDao;
import it.unina.biogarden.model.Coltura;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Proprietario;

public class ColturaService {
	
	private final ColturaDao colturaDao;
	private final ProgettoDao progettoDao;
	private final LottoDao lottoDao;
	
	public ColturaService(ColturaDao colturaDao, ProgettoDao progettoDao, LottoDao lottoDao) {
		this.colturaDao=colturaDao;
		this.progettoDao=progettoDao;
		this.lottoDao=lottoDao;
	}
	
	public ColturaService() {
		this(DaoFactory.createColturaDao(), DaoFactory.createProgettoDao(), DaoFactory.createLottoDao());
	}
	
	
	
	//metodi CREATE/INSERT/UPDATE 
	
	public int createColturaPerProprietario(Proprietario proprietario, Coltura coltura)throws Exception {
		
		ProgettoStagionale progetto=progettoDao.findById(coltura.getFk_progetto());
		
		if(progetto==null) {
			throw new IllegalArgumentException("Il progetto("+coltura.getFk_progetto()+") non esiste.");
		}
		
		Lotto lotto=lottoDao.findById(progetto.getFk_lotto());
		
		//controllo ownership
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi creare colture su un progetto che non è su un tuo lotto.");
		}
		
		int id_creata=colturaDao.create(coltura);
		System.out.println("Coltura ("+id_creata+") creata con successo.");
		return id_creata;
	}
	
	
	public void updateColturaPerProprietario(Proprietario proprietario, Coltura modificata)throws Exception{
		
		Coltura esistente=colturaDao.findById(modificata.getId_coltura());
		if(esistente==null) {
			throw new IllegalArgumentException("La coltura ("+modificata.getId_coltura()+") non esiste.");
		}
		
		//verifico se il proprietario possiede la coltura che vuole modificare
		ProgettoStagionale progettoAttuale=progettoDao.findById(esistente.getFk_progetto());
		Lotto lottoAttuale=lottoDao.findById(progettoAttuale.getFk_lotto());
		
		if(!lottoAttuale.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi modificare una coltura che non appartiene ai tuoi lotti.");
		}
	
		//verifico se la nuova versione di quella coltura appartiene ad un progetto del proprietario
		
		if(modificata.getFk_progetto()!=esistente.getFk_progetto()) {
			
			ProgettoStagionale progettoNuovo=progettoDao.findById(modificata.getFk_progetto());
			
			if(progettoNuovo==null) {
				throw new IllegalArgumentException("Il nuovo progetto ("+modificata.getFk_progetto()+") non esiste.");
			}
			
			Lotto lottoNuovo=lottoDao.findById(progettoNuovo.getFk_lotto());
			
			if(!lottoNuovo.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
				throw new IllegalArgumentException("Non puoi spostare una coltura su un progetto che non è su un tuo lotto.");
			}
		}
		
		colturaDao.update(modificata);
		System.out.println("Coltura ("+modificata.getId_coltura()+") modificata con successo.");
		
	}
	
	
	public void deleteColturaPerProprietario(Proprietario proprietario, int id_coltura)throws Exception{
		
		Coltura esistente=colturaDao.findById(id_coltura);
		if(esistente==null) {
			throw new IllegalArgumentException("La coltura ("+id_coltura+") non esiste.");
		}
		
		ProgettoStagionale progetto=progettoDao.findById(esistente.getFk_progetto());
		Lotto lotto=lottoDao.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi cancellare una coltura che non e su un tuo lotto.");
		}
		
		colturaDao.delete(id_coltura);
		System.out.println("Coltura ("+id_coltura+") eliminata con successo.");
	}
	
	//metodi di lettura
	public List<Coltura> findByProgetto(int id_progetto)throws Exception{
		return colturaDao.findByProgetto(id_progetto);
	}
	
	public Coltura findById(int id_coltura)throws Exception{
		return colturaDao.findById(id_coltura);
	}
}
