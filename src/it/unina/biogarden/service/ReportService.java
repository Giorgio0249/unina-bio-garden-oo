package it.unina.biogarden.service;


import java.util.List;

import it.unina.biogarden.dao.DaoFactory;
import it.unina.biogarden.dao.LottoDao;
import it.unina.biogarden.dao.ReportLottoDao;
import it.unina.biogarden.model.Lotto;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.ReportLotto;

public class ReportService {
	
	private final ReportLottoDao reportLottoDao;
	private final LottoDao lottoDao;
	
	public ReportService(ReportLottoDao reportLottoDao, LottoDao lottoDao) {
		this.reportLottoDao=reportLottoDao;
		this.lottoDao=lottoDao;
	}
	
	public ReportService() {
		this(DaoFactory.createReportLottoDao(), DaoFactory.createLottoDao());
	}
	
	public List<ReportLotto> getReportPerLotto(Proprietario proprietario, int id_lotto)throws Exception{
		
		Lotto lotto=lottoDao.findById(id_lotto);
		
		if(lotto==null) {
			throw new IllegalArgumentException("IL lotto ("+id_lotto+") non esiste.");
		}
		
		if(!lotto.getFk_proprietario().equalsIgnoreCase(proprietario.getEmail())) {
			throw new IllegalArgumentException("Non puoi vedere il report di un lotto che non è tuo.");
		}
		
		return reportLottoDao.getStatisticheRaccoltaPerLotto(id_lotto);
	}

}
