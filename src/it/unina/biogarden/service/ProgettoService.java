package it.unina.biogarden.service;

import java.util.List;

import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.LottoDao;
import it.unina.biogarden.dao.ProgettoDao;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.Notifica;
import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Proprietario;

public class ProgettoService {
	
	private final ProgettoDao progettoDao;
	
	private final LottoService lottoService;
	private final ProprietarioService proprietarioService;
	
	public ProgettoService(ProgettoDao progettoDao, LottoService lottoService, ProprietarioService proprietarioService) {
		this.progettoDao=progettoDao;
		this.lottoService=lottoService;
		this.proprietarioService=proprietarioService;
	}
	
	public ProgettoService() {
		this(DaoFactory.createProgettoDao(), new LottoService(), new ProprietarioService());
	}
	
	
	/*questo metodo serve a creare un progetto sul lotto scelto, ma solo se il lotto appartiene al proprietario loggato.
	 * ecco perche usiamo il service (per aggiungere logica di buisness che il metodo ProgettoDaoPg.create non implementa)
	 */
	public int createProgettoPerProprietario(Proprietario proprietario, ProgettoStagionale progetto)throws Exception{
		
		//(1)recupero il lotto dal DB
		Lotto lotto=lottoService.findById(progetto.getFk_lotto());
		
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
		ProgettoStagionale esistente=this.findById(modificato.getId_progetto());
		
		Lotto lottoAttuale=lottoService.findById(esistente.getFk_lotto());
		
		//verifico che il progetto che si vuole modificare appartenga al proprietario loggato
		if(!lottoAttuale.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi modificare un progetto non tuo.");
		}
		
		
		//i controlli sottostanti servono a capire, nel caso in cui l'update cambiasse il lotto al quale si riferisce il progetto, 
		//se il nuovo lotto appartiene al proprietario
		
		if(esistente.getFk_lotto()!=modificato.getFk_lotto()) {
			Lotto nuovoLotto=lottoService.findById(modificato.getFk_lotto());
			
			if(!nuovoLotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
				throw new IllegalArgumentException("Non puoi spostare il progetto su un lotto che non è tuo.");
			}
		}
		
		//se tutto ok, faccio eseguire al dao la modifica
		progettoDao.update(modificato);
		System.out.println("Progetto ("+modificato.getId_progetto()+") aggiornato con successo.");
	}
	
	
	public void deleteProgettoPerProprietario(Proprietario proprietario, int id_progetto)throws Exception{
		
		ProgettoStagionale esistente=this.findById(id_progetto);
		
		Lotto lotto=lottoService.findById(esistente.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi cancellare un progetto non tuo.");
		}
		
		progettoDao.delete(id_progetto);
		System.out.println("Progetto ("+id_progetto+") eliminato con successo.");
	}
	
	
	//metodi di lettura
	public List<ProgettoStagionale> findAll() throws Exception{
		
		List<ProgettoStagionale> out=progettoDao.findAll();
		
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessun Progetto trovato nel sistema.");
		}
		return out;
	}
	
	public ProgettoStagionale findById(int id_progetto) throws Exception{
		
		ProgettoStagionale progetto=progettoDao.findById(id_progetto);
		
		if(progetto==null) {
			throw new IllegalArgumentException("Nessun progetto con id ("+id_progetto+") trovato.");
		}
			return progetto;
	}
	
	public List<ProgettoStagionale> findAllByProprietario(String emailProprietario)throws Exception{
		
		proprietarioService.findByEmail(emailProprietario);
		
		List<ProgettoStagionale> out=progettoDao.findAllByProprietario(emailProprietario);
		
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessun progetto appartenente a "+emailProprietario+" trovato.");
		}
		return out;
	}
	
	public List<ProgettoStagionale> findByLotto(int id_lotto)throws Exception{
		
		lottoService.findById(id_lotto);
		
		List<ProgettoStagionale> out=progettoDao.findByLotto(id_lotto);
		
		if(out.isEmpty()) {
			throw new IllegalArgumentException("Nessun progetto trovato sul lotto ("+id_lotto+").");
		}
		return out;
	}
	
}
