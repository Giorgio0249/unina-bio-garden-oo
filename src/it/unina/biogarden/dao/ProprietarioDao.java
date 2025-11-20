package it.unina.biogarden.dao;

import it.unina.biogarden.model.Proprietario;

public interface ProprietarioDao {
	
	public Proprietario authenticate(String email, String password)throws Exception;
	
	public Proprietario findByEmail(String email)throws Exception;

}
