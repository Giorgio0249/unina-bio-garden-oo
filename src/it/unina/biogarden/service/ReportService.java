package it.unina.biogarden.service;

import java.util.List;
import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.ReportLottoDao;
import it.unina.biogarden.model.ProgettoStagionale;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.ReportLotto;

public class ReportService {
	
	private final ReportLottoDao reportLottoDao;
	private final ProgettoService progettoService;
	private final LottoService lottoService;

	public ReportService(ReportLottoDao reportLottoDao, ProgettoService progettoService, LottoService lottoService) {
		this.reportLottoDao = reportLottoDao;
		this.progettoService = progettoService;
		this.lottoService = lottoService;
	}
	
	public ReportService() {
		this(DaoFactory.createReportLottoDao(), new ProgettoService(), new LottoService());
	}
	
	public List<ReportLotto> getReportPerLotto(Proprietario proprietario, int id_progetto) throws Exception {
		
		// 1. Recuperiamo il progetto
		ProgettoStagionale progetto = progettoService.findById(id_progetto);
		
		// 2. Recuperiamo il lotto associato al progetto per controllare il proprietario
		Lotto lotto = lottoService.findById(progetto.getFk_lotto());
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi vedere il report di un progetto su un lotto che non è tuo.");
		}
		
		// 3. Chiamiamo il DAO passando l'ID Progetto
		return reportLottoDao.getStatisticheRaccoltaPerLotto(id_progetto);
	}
}
