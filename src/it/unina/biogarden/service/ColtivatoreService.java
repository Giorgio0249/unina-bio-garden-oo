package it.unina.biogarden.service;

import java.util.ArrayList;
import java.util.List;
import it.unina.biogarden.dao.ColtivatoreDao;
import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.model.Coltivatore;

 public class ColtivatoreService {
	
	private final ColtivatoreDao coltivatoreDao;
	
	public ColtivatoreService(ColtivatoreDao coltivatoreDao) {
		this.coltivatoreDao=coltivatoreDao;
	}
	
	public ColtivatoreService() {
		this(DaoFactory.createColtivatoreDao());
	}
	
	public Coltivatore authenticate(String email, String password) throws Exception {
	    Coltivatore coltivatore = coltivatoreDao.Authenticate(email, password);
	    
	    if (coltivatore == null) {
	        throw new IllegalArgumentException("Email o password errati!");
	    }
	    
	    return coltivatore;
	}
	
	public Coltivatore findByEmail(String email)throws Exception{
		Coltivatore coltivatore=coltivatoreDao.findByEmail(email);
		
		if(coltivatore==null) {
			throw new IllegalArgumentException("Nessun coltivatore trovato associato all'email inserita.");
		}
		return coltivatore;
	}
	
	public List<Coltivatore> findAll()throws Exception{
		List<Coltivatore> lista=coltivatoreDao.findAll();
		
		if(lista.isEmpty()) {
			throw new IllegalArgumentException("Non sono presenti coltivatori nel db.");
		}
		return lista;
	}
	
	public List<Coltivatore> findColtivatoriPerProprietario(String emailProprietario) throws Exception {
	    if (emailProprietario == null || emailProprietario.isEmpty()) 
	    	return new ArrayList<>();
	    return coltivatoreDao.findColtivatoriPerProprietario(emailProprietario);
	}
}
