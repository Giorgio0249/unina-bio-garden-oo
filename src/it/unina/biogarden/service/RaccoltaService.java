package it.unina.biogarden.service;

import java.util.List;

import it.unina.biogarden.dao.ColturaDao;
import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.LottoDao;
import it.unina.biogarden.dao.ProgettoDao;
import it.unina.biogarden.dao.RaccoltaDao;
import it.unina.biogarden.model.Coltura;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.Raccolta;

public class RaccoltaService {
	
	private final RaccoltaDao raccoltaDao;
	
	private final ColturaService colturaService;
	private final ProgettoService progettoService;
	private final LottoService lottoService;
	
	public RaccoltaService(RaccoltaDao raccoltaDao, ColturaService colturaService, ProgettoService progettoService, LottoService lottoService) {
		this.raccoltaDao=raccoltaDao;
		this.colturaService=colturaService;
		this.progettoService=progettoService;
		this.lottoService=lottoService;
	}
	
	public RaccoltaService() {
		this(DaoFactory.createRaccoltaDao(), new ColturaService(), new ProgettoService(), new LottoService());
	}
	
	
	public int createRaccoltaPerProprietario(Proprietario proprietario, Raccolta raccolta)throws Exception{
		
		Coltura coltura=colturaService.findById(raccolta.getFk_coltura());
		ProgettoStagionale progetto=progettoService.findById(coltura.getFk_progetto());
		Lotto lotto=lottoService.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi registrare raccolte su colture che non appartengono ai tuoi lotti.");
		}
		
		int id_creata=raccoltaDao.create(raccolta);
		System.out.println("Raccolta ("+id_creata+") registrata con successo.");
		return id_creata;

	}
	
	public void deleteRaccoltaPerProprietario(Proprietario proprietario, int id_raccolta)throws Exception{
		
		Raccolta esistente=this.findById(id_raccolta);
		
		Coltura coltura=colturaService.findById(esistente.getFk_coltura());
		ProgettoStagionale progetto=progettoService.findById(coltura.getFk_progetto());
		Lotto lotto=lottoService.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi cancellare una raccolta che non appartiene ai tuoi lotti.");
		}
		
		raccoltaDao.delete(id_raccolta);
		System.out.println("Raccolta ("+id_raccolta+") eliminata con successo.");
	}
	
	
	//metodi di lettura
	public List<Raccolta> findByColtura(int id_coltura) throws Exception{
		colturaService.findById(id_coltura);
		
		List<Raccolta> out=raccoltaDao.findByColtura(id_coltura);
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessuna raccolta trovata sulla coltura ("+id_coltura+")");
		}
		return out;
	}
	
	public Raccolta findById(int id_raccolta)throws Exception{
		Raccolta raccolta=raccoltaDao.findById(id_raccolta);
		if(raccolta==null) {
			throw new IllegalArgumentException("Nessuna raccolta con id ("+id_raccolta+") trovata.");
		}
		return raccolta;
	}

}
