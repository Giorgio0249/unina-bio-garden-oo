package it.unina.biogarden.dao;

import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.StatoAttivita;

import java.util.List;

public interface AttivitaDao {
	
	public List<Attivita> findByColtura(int id_coltura)throws Exception;
	
	public int create(Attivita a)throws Exception;
	
	public int updateStato(int id_attivita, StatoAttivita nuovoStato)throws Exception;
	
	public int update(Attivita a)throws Exception;
	
	public Attivita findById(int id_attivita)throws Exception;
	
	public int delete(int id_attivita)throws Exception;
}
