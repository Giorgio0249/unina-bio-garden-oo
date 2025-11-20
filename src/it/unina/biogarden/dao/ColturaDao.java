package it.unina.biogarden.dao;

import it.unina.biogarden.model.Coltura;
import java.util.List;

public interface ColturaDao {
	
	public List<Coltura> findByProgetto(int id_progetto)throws Exception;
	
	public Coltura findById(int id_coltura)throws Exception;
	
	public int create(Coltura c)throws Exception;
	
	public int update(Coltura c)throws Exception;
	
	public int delete(int id_coltura)throws Exception;
}
