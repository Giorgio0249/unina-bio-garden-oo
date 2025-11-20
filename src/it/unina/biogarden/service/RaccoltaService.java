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
	private final ColturaDao colturaDao;
	private final ProgettoDao progettoDao;
	private final LottoDao lottoDao;
	
	public RaccoltaService(RaccoltaDao raccoltaDao, ColturaDao colturaDao, ProgettoDao progettoDao, LottoDao lottoDao) {
		this.raccoltaDao=raccoltaDao;
		this.colturaDao=colturaDao;
		this.progettoDao=progettoDao;
		this.lottoDao=lottoDao;
	}
	
	public RaccoltaService() {
		this(DaoFactory.createRaccoltaDao(), DaoFactory.createColturaDao(), DaoFactory.createProgettoDao(), DaoFactory.createLottoDao());
	}
	
	
	public int createRaccoltaPerProprietario(Proprietario proprietario, Raccolta raccolta)throws Exception{
		
		Coltura coltura=colturaDao.findById(raccolta.getFk_coltura());
		if(coltura==null) {
			throw new IllegalArgumentException("La coltura ("+raccolta.getFk_coltura()+") non esiste.");
		}
		
		ProgettoStagionale progetto=progettoDao.findById(coltura.getFk_progetto());
		Lotto lotto=lottoDao.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi registrare raccolte su colture che non appartengono ai tuoi lotti.");
		}
		
		int id_creata=raccoltaDao.create(raccolta);
		System.out.println("Raccolta ("+id_creata+") registrata con successo.");
		return id_creata;

	}
	
	public void deleteRaccoltaPerProprietario(Proprietario proprietario, int id_raccolta)throws Exception{
		
		Raccolta esistente=raccoltaDao.findById(id_raccolta);
		if(esistente==null) {
			throw new IllegalArgumentException("La raccolta ("+id_raccolta+") non esiste.");
		}
		
		Coltura coltura=colturaDao.findById(esistente.getFk_coltura());
		ProgettoStagionale progetto=progettoDao.findById(coltura.getFk_progetto());
		Lotto lotto=lottoDao.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi cancellare una raccolta che non appartiene ai tuoi lotti.");
		}
		
		raccoltaDao.delete(id_raccolta);
		System.out.println("Raccolta ("+id_raccolta+") eliminata con successo.");
	}
	
	
	//metodi di lettura
	public List<Raccolta> findByColtura(int id_coltura) throws Exception{
		return raccoltaDao.findByColtura(id_coltura);
	}
	
	public Raccolta findById(int id_raccolta)throws Exception{
		return raccoltaDao.findById(id_raccolta);
	}

}
