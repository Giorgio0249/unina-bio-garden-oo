package it.unina.biogarden.dao;

import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.StatoAttivita;

import java.util.List;

public interface AttivitaDao {
	
	public List<Attivita> findByColtivatore(String emailoltivatore)throws Exception;
	
	public List<Attivita> findByColtura(int id_coltura)throws Exception;
	
	public List<Attivita> findByProgetto(int id_progetto) throws Exception;
	
	public int create(Attivita a)throws Exception;
	
	public int updateStato(int id_attivita, StatoAttivita nuovoStato)throws Exception;
	
	public int update(Attivita a)throws Exception;
	
	public Attivita findById(int id_attivita)throws Exception;
	
	public int delete(int id_attivita)throws Exception;
	
	//questo metodo serve a verificare che il coltivatore passato lavori per il proprietario passato
	//(serve in NotificaService per mandare notifiche solo a coltivatori sottoposti a un proprietario)
	public boolean existsColtivatorePerProprietario(String email_colt, String email_prop)throws Exception;
	
	public boolean existsAttivitaPerProprietario(int id_attivita, String email_prop)throws Exception;
	
	public boolean existsAttivitaInProgetto(int id_attivita, int id_progetto)throws Exception;
}
