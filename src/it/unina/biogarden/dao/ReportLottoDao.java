package it.unina.biogarden.dao;

import java.util.List;

import it.unina.biogarden.model.ReportLotto;

public interface ReportLottoDao {
	
	public List<ReportLotto> getStatisticheRaccoltaPerLotto(int id_progetto)throws Exception;

}
