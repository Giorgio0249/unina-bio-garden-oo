package it.unina.biogarden.service;


import java.util.List;

import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.ReportLottoDao;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.ReportLotto;

public class ReportService {
	
	private final ReportLottoDao reportLottoDao;
	
	private final LottoService lottoService;
	
	public ReportService(ReportLottoDao reportLottoDao, LottoService lottoService) {
		this.reportLottoDao=reportLottoDao;
		this.lottoService=lottoService;
	}
	
	public ReportService() {
		this(DaoFactory.createReportLottoDao(), new LottoService());
	}
	
	public List<ReportLotto> getReportPerLotto(Proprietario proprietario, int id_lotto)throws Exception{
		
		Lotto lotto=lottoService.findById(id_lotto);
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi vedere il report di un lotto che non è tuo.");
		}
		
		return reportLottoDao.getStatisticheRaccoltaPerLotto(id_lotto);
	}

}
