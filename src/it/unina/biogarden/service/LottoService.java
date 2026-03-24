package it.unina.biogarden.service;

import java.util.List;

import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.LottoDao;
import it.unina.biogarden.model.Lotto;

 public class LottoService {
	
	private final LottoDao lottoDao;
	
	private final ProprietarioService proprietarioService;
	
	public LottoService(LottoDao lottoDao, ProprietarioService proprietarioService) {
		this.lottoDao=lottoDao;
		this.proprietarioService=proprietarioService;
	}
	
	public LottoService() {
		this(DaoFactory.createLottoDao(), new ProprietarioService());
	}
	
	
	
	public List<Lotto> findByProprietario(String emailProprietario)throws Exception{
		proprietarioService.findByEmail(emailProprietario);
		
		List<Lotto> out=lottoDao.findByProprietario(emailProprietario);
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessun lotto trovato associato al proprietario: "+emailProprietario);
		}
		return out;
	}
	
	
	public Lotto findById(int id_lotto)throws Exception{
		Lotto lotto=lottoDao.findById(id_lotto);
		
		if(lotto==null) {
			throw new IllegalArgumentException("Nessun lotto con id ("+id_lotto+") trovato.");			
		}
		return lotto;
	}
	
	
	public List<Lotto> findAll()throws Exception{
		List<Lotto> out=lottoDao.findAll();
		
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessun lotto presente nel sistema.");
		}
		return out;
	}
	

}
