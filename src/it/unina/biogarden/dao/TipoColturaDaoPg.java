package it.unina.biogarden.dao;

import it.unina.biogarden.model.TipoColtura;
import java.util.*;
import java.sql.*;

 class TipoColturaDaoPg implements TipoColturaDao{
	
	@Override
	public List<TipoColtura> findAll() throws Exception{
		List<TipoColtura> lista=new ArrayList<>();
		
		String sql="""
				SELECT nomeTipo, tempoMaturazioneGiorni
				FROM TipoColtura
				""";
		
		try(Connection conn = ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps=conn.prepareStatement(sql);
				ResultSet rs=ps.executeQuery()){
			
			while(rs.next()) {
				lista.add(new TipoColtura(
						rs.getString("nomeTipo"),
						rs.getInt("tempoMaturazioneGiorni")
						));
			}
		}
		
		return lista;
	
	}

}
