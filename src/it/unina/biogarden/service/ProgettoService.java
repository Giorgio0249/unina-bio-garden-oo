package it.unina.biogarden.service;

import java.util.List;

import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.LottoDao;
import it.unina.biogarden.dao.ProgettoDao;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Proprietario;

public class ProgettoService {
	
	private final ProgettoDao progettoDao;
	private final LottoDao lottoDao;
	
	public ProgettoService(ProgettoDao progettoDao, LottoDao lottoDao) {
		this.progettoDao=progettoDao;
		this.lottoDao=lottoDao;
	}
	
	public ProgettoService() {
		this(DaoFactory.createProgettoDao(), DaoFactory.createLottoDao());
	}
	
	
	/*questo metodo serve a creare un progetto sul lotto scelto, ma solo se il lotto appartiene al proprietario loggato.
	 * ecco perche usiamo il service (per aggiungere logica di buisness che il metodo ProgettoDaoPg.create non implementa)
	 */
	public int createProgettoPerProprietario(Proprietario proprietario, ProgettoStagionale progetto)throws Exception{
		
		//(1)recupero il lotto dal DB
		Lotto lotto=lottoDao.findById(progetto.getFk_lotto());
		if(lotto==null) {
			throw new IllegalArgumentException("Il lotto ("+progetto.getFk_lotto()+") non esiste.");			
		}
		
		//(2)controllo che il lotto su cui voglio creare il progetto appartenga al proprietario
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Il lotto ("+lotto.getId_lotto()+") non appartiene al proprietario: "+proprietario.getEmail());
		}
		
		//(3)se tutto ok creo il progetto
		int id_creato=progettoDao.create(progetto);
		System.out.println("Progetto ("+id_creato+") creato con successo.");
		return id_creato;
		
	}
	
	
	public void updateProgettoPerProprietario(Proprietario proprietario, ProgettoStagionale modificato)throws Exception{
		
		//carico il progetto esistente corrispondente all'id della nuova versione passata come paramentro
		ProgettoStagionale esistente=progettoDao.findById(modificato.getId_progetto());
		if(esistente==null) {
			throw new IllegalArgumentException("Il progetto ("+modificato.getId_progetto()+") non esiste.");
		}
		
		Lotto lottoAttuale=lottoDao.findById(esistente.getFk_lotto());
		if(lottoAttuale==null) {
			throw new IllegalArgumentException("il lotto associato al progetto non esiste più");
		}
		
		//verifico che il progetto che si vuole modificare appartenga al proprietario loggato
		if(!lottoAttuale.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi modificare un progetto non tuo.\nIl progetto ("+esistente.getId_progetto()+") non appartiene al proprietario: "+proprietario.getEmail());
		}
		
		
		//i controlli sottostanti servono a capire, nel caso in cui l'update cambiasse il lotto al quale si riferisce il progetto, 
		//se il nuovo lotto appartiene al proprietario
		
		if(esistente.getFk_lotto()!=modificato.getFk_lotto()) {
			Lotto nuovoLotto=lottoDao.findById(modificato.getFk_lotto());
			
			if(nuovoLotto==null) {
				throw new IllegalArgumentException("il nuovo lotto ("+modificato.getFk_lotto()+") non esiste.");
			}
			
			if(!nuovoLotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
				throw new IllegalArgumentException("Non puoi spostare il progetto su un lotto che non è tuo.");
			}
		}
		
		
		//se tutto ok, faccio eseguire al dao la modifica
		progettoDao.update(modificato);
		System.out.println("Progetto ("+modificato.getId_progetto()+") aggiornato con successo.");
		
	}
	
	
	public void deleteProgettoPerProprietario(Proprietario proprietario, int id_progetto)throws Exception{
		
		ProgettoStagionale esistente=progettoDao.findById(id_progetto);
		
		if(esistente==null) {
			throw new IllegalArgumentException("Il progetto ("+id_progetto+") non esiste.");
		}
		
		Lotto lotto=lottoDao.findById(esistente.getFk_lotto());
		
		if(lotto==null) {
			throw new IllegalArgumentException("il lotto associato al progetto non esiste più.");
		}
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi cancellare un progetto non tuo.");
		}
		
		progettoDao.delete(id_progetto);
		System.out.println("Progetto ("+id_progetto+") eliminato con successo.");
	}
	
	
	//metodi di lettura
	public List<ProgettoStagionale> findAll() throws Exception{
		return progettoDao.findAll();
	}
	
	public ProgettoStagionale findById(int id_progetto) throws Exception{
		return progettoDao.findById(id_progetto);
	}
	
	public List<ProgettoStagionale> findAllByProprietario(String emailProprietario)throws Exception{
		return progettoDao.findAllByProprietario(emailProprietario);
	}
	
	public List<ProgettoStagionale> findByLotto(int id_lotto)throws Exception{
		return progettoDao.findByLotto(id_lotto);
	}
	

}
