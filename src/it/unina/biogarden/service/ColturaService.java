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
	
	private final ProgettoService progettoService;
	private final LottoService lottoService;
	
	public ColturaService(ColturaDao colturaDao, ProgettoService progettoService, LottoService lottoService) {
		this.colturaDao=colturaDao;
		this.progettoService=progettoService;
		this.lottoService=lottoService;
	}
	
	public ColturaService() {
		this(DaoFactory.createColturaDao(), new ProgettoService(), new LottoService());
	}
	
	
	
	//metodi CREATE/INSERT/UPDATE 
	
	public int createColturaPerProprietario(Proprietario proprietario, Coltura coltura)throws Exception {
		
		ProgettoStagionale progetto=progettoService.findById(coltura.getFk_progetto());
		
		Lotto lotto=lottoService.findById(progetto.getFk_lotto());
		
		//controllo ownership
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi creare colture su un progetto che non è su un tuo lotto.");
		}
		
		int id_creata=colturaDao.create(coltura);
		System.out.println("Coltura ("+id_creata+") creata con successo.");
		return id_creata;
	}
	
	
	public void updateColturaPerProprietario(Proprietario proprietario, Coltura modificata)throws Exception{
		
		Coltura esistente=this.findById(modificata.getId_coltura());
		
		//verifico se il proprietario possiede la coltura che vuole modificare
		ProgettoStagionale progettoAttuale=progettoService.findById(esistente.getFk_progetto());
		Lotto lottoAttuale=lottoService.findById(progettoAttuale.getFk_lotto());
		
		if(!lottoAttuale.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi modificare una coltura che non appartiene ai tuoi lotti.");
		}
	
		//verifico se la nuova versione di quella coltura appartiene ad un progetto del proprietario
		
		if(modificata.getFk_progetto()!=esistente.getFk_progetto()) {
			
			ProgettoStagionale progettoNuovo=progettoService.findById(modificata.getFk_progetto());
			
			Lotto lottoNuovo=lottoService.findById(progettoNuovo.getFk_lotto());
			
			if(!lottoNuovo.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
				throw new IllegalArgumentException("Non puoi spostare una coltura su un progetto che non è su un tuo lotto.");
			}
		}
		
		colturaDao.update(modificata);
		System.out.println("Coltura ("+modificata.getId_coltura()+") modificata con successo.");
		
	}
	
	
	public void deleteColturaPerProprietario(Proprietario proprietario, int id_coltura)throws Exception{
		
		Coltura esistente=this.findById(id_coltura);
		
		ProgettoStagionale progetto=progettoService.findById(esistente.getFk_progetto());
		Lotto lotto=lottoService.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi cancellare una coltura che non e su un tuo lotto.");
		}
		
		colturaDao.delete(id_coltura);
		System.out.println("Coltura ("+id_coltura+") eliminata con successo.");
	}
	
	//metodi di lettura
	public List<Coltura> findByProgetto(int id_progetto)throws Exception{
		
		progettoService.findById(id_progetto);
		
		List<Coltura> out=colturaDao.findByProgetto(id_progetto);
		
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessuna coltura sul progetto ("+id_progetto+") trovata.");
		}
		return out;
	}
	
	public Coltura findById(int id_coltura)throws Exception{
		Coltura coltura=colturaDao.findById(id_coltura);
		
		if(coltura==null) {
			throw new IllegalArgumentException("Nessuna coltura con id ("+id_coltura+") trovata.");
		}
		return coltura;
	}
}
