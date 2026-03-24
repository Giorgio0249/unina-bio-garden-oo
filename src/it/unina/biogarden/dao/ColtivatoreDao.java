package it.unina.biogarden.dao;

import it.unina.biogarden.model.Coltivatore;
import java.util.List;

public interface ColtivatoreDao {
	
	public Coltivatore Authenticate(String email, String password) throws Exception; 

	public Coltivatore findByEmail(String email)throws Exception;
	
	public List<Coltivatore> findAll()throws Exception;
	
	public List<Coltivatore> findColtivatoriPerProprietario(String emailProprietario) throws Exception;

}
