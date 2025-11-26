package it.unina.biogarden.dao;

import java.util.List;

import it.unina.biogarden.model.Notifica;
import it.unina.biogarden.model.Proprietario;

public interface NotificaDao {
	
	public int create(Notifica n)throws Exception;
	
	public Notifica findById(int id_notifica)throws Exception;
	
	public List<Notifica> findByColtivatore(String email_colt)throws Exception;
	
	public List<Notifica> findByProprietario(String email_prop)throws Exception;
	
	public List<Notifica> findByProgetto(int id_progetto)throws Exception;
	
	public int delete(int id_notifica)throws Exception;

}
