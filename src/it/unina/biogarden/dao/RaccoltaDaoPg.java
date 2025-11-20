package it.unina.biogarden.dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.unina.biogarden.model.Raccolta;

 class RaccoltaDaoPg implements RaccoltaDao{
	
	@Override
	public List<Raccolta> findByColtura(int id_coltura) throws Exception{
		
		String sql="""
				SELECT 
					id_raccolta,
					dataRaccolta,
					quantitaEffettiva,
					fk_coltura
				FROM Raccolta
				WHERE fk_coltura=?
				ORDER BY dataRaccolta
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps=conn.prepareStatement(sql)){
			ps.setInt(1, id_coltura);
			
			try(ResultSet rs=ps.executeQuery()){
				List<Raccolta> out=new ArrayList<>();
				
				while(rs.next()) {
				out.add(new Raccolta(
						rs.getInt("id_raccolta"),
						rs.getObject("dataRaccolta", LocalDate.class),
						rs.getBigDecimal("quantitaEffettiva"),
						rs.getInt("fk_coltura")
						));
				
				}
				
				return out;
				
			}
		}
		
	}
	
	
	@Override
	public Raccolta findById(int id_raccolta)throws Exception{
		String sql="""
				SELECT 
					r.id_raccolta,
					r.dataRaccolta,
					r.quantitaEffettiva,
					r.fk_coltura
				FROM Raccolta r
				WHERE r.id_raccolta = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_raccolta);
			
			try(ResultSet rs=ps.executeQuery()){

				if(rs.next()) {
					return new Raccolta(
							rs.getInt("id_raccolta"),
							rs.getObject("dataRaccolta", LocalDate.class),
							rs.getBigDecimal("quantitaEffettiva"),
							rs.getInt("fk_coltura"));
				}
				return null;
			}
		}
	}
	
	@Override
	public int create(Raccolta r)throws Exception{
		String sql="""
				INSERT INTO Raccolta(dataRaccolta, quantitaEffettiva, fk_coltura)
				VALUES
				(?, ?, ?)
				RETURNING id_raccolta
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setObject(1, r.getDataRaccolta());
			ps.setBigDecimal(2, r.getQuantitaEffettiva());
			ps.setInt(3, r.getFk_coltura());
			
			try(ResultSet rs=ps.executeQuery()){
				rs.next();
				return rs.getInt("id_raccolta");
			}
		}
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
	}
	
	@Override
	public int delete(int id_raccolta)throws Exception{
		String sql="""
				DELETE FROM Raccolta
				WHERE id_raccolta = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			ps.setInt(1, id_raccolta);
			
			return ps.executeUpdate();
			
		}
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
	}

}
