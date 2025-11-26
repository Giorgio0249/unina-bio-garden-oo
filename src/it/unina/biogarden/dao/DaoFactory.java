package it.unina.biogarden.dao;

public class DaoFactory {
	
	private DaoFactory() {//costruttore privato perche questa classe non si istanzia
		
	}
	
	public static ProgettoDao createProgettoDao() {
		return new ProgettoDaoPg();
	}
	
	public static LottoDao createLottoDao() {
		return new LottoDaoPg();
	}
	
	public static ColturaDao createColturaDao() {
		return new ColturaDaoPg();
	}

	public static AttivitaDao createAttivitaDao() {
		return new AttivitaDaoPg();
	}
	
	public static RaccoltaDao createRaccoltaDao() {
		return new RaccoltaDaoPg();
	}
	
	public static ReportLottoDao createReportLottoDao() {
		return new ReportLottoDaoPg();
	}
	
	public static NotificaDao createNotificaDao() {
		return new NotificaDaoPg();
	}
	
	public static ProprietarioDao createProprietarioDao() {
		return new ProprietarioDaoPg();
	}
	
	public static ColtivatoreDao createColtivatoreDao(){
		return new ColtivatoreDaoPg();
	}
}
