package it.unina.biogarden.dao;

import it.unina.biogarden.model.Coltivatore;

public interface ColtivatoreDao {

	public Coltivatore findByEmail(String email)throws Exception;
}
