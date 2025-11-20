package it.unina.biogarden.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import it.unina.biogarden.model.ReportLotto;

 class ReportLottoDaoPg implements ReportLottoDao{
	
	@Override
	public List<ReportLotto> getStatisticheRaccoltaPerLotto(int id_lotto)throws Exception{
		String sql="""
				SELECT l.id_lotto, l.posizione AS nome_lotto, tc.nomeTipo AS tipo_coltura,
						MIN(r.quantitaEffettiva) AS minimo,
						AVG(r.quantitaEffettiva) AS media,
						MAX(r.quantitaEffettiva) AS massimo,
						COUNT(*) AS num_raccolte
				FROM Lotto l
				JOIN ProgettoStagionale p ON l.id_lotto=p.fk_lotto
				JOIN Coltura c ON p.id_progetto = c.fk_progetto
				JOIN TipoColtura tc ON c.fk_tipo_coltura = tc.nomeTipo
				LEFT JOIN Raccolta r ON c.id_coltura = r.fk_coltura		--prende anche colture con 0 raccolte
				WHERE l.id_lotto = ?
				GROUP BY l.id_lotto, l.posizione, tc.nomeTipo
				ORDER BY tc.nomeTipo
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_lotto);
			
			try(ResultSet rs=ps.executeQuery()){
				List<ReportLotto> out=new ArrayList<>();
				while(rs.next()) {
					
					BigDecimal min = rs.getBigDecimal("minimo");
					BigDecimal avg = rs.getBigDecimal("media");
					BigDecimal max = rs.getBigDecimal("massimo");

					BigDecimal minimo  = (min  == null ? BigDecimal.ZERO : min.setScale(2, RoundingMode.HALF_UP));
					BigDecimal media   = (avg   == null ? BigDecimal.ZERO : avg.setScale(2, RoundingMode.HALF_UP));
					BigDecimal massimo = (max == null ? BigDecimal.ZERO : max.setScale(2, RoundingMode.HALF_UP));
					
					out.add(new ReportLotto(
							rs.getInt("id_lotto"),
							rs.getString("nome_lotto"),
							rs.getString("tipo_coltura"),
							minimo,
							media,
							massimo,
							rs.getLong("num_raccolte")));
				}
				
				return out;
			}
		}
	}

}
