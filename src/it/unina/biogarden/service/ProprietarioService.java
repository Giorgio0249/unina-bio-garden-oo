package it.unina.biogarden.service;

import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.ProprietarioDao;
import it.unina.biogarden.model.Proprietario;

public class ProprietarioService {
	
	private final ProprietarioDao proprietarioDao;
	
	public ProprietarioService(ProprietarioDao proprietarioDao) {
		this.proprietarioDao=proprietarioDao;
	}
	
	public ProprietarioService() {
		this(DaoFactory.createProprietarioDao());
	}
	
	
	public Proprietario authenticate(String email, String password)throws Exception{
		Proprietario proprietario=proprietarioDao.authenticate(email, password);
		
		if(proprietario==null) {
			throw new IllegalArgumentException("Email o password errati!");
		}
		return proprietario;
	}
	
	
	public Proprietario findByEmail(String email)throws Exception{
		Proprietario proprietario=proprietarioDao.findByEmail(email);
		
		if(proprietario==null) {
			throw new IllegalArgumentException("Nessun proprietario trovato associato all'email inserita.");
		}
		return proprietario;
	}

}
