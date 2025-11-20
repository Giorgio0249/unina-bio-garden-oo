package it.unina.biogarden.dao;

import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.Coltura;
import it.unina.biogarden.model.StatoAttivita;
import it.unina.biogarden.model.TipoAttivita;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

 class AttivitaDaoPg implements AttivitaDao{
	
	@Override 
	public List<Attivita> findByColtura(int id_coltura) throws Exception{
		String sql="""
				SELECT a.id_attivita, 
						a.tipoAttivita::text, 
						a.stato::text, 
						a.dataPianificata, 
						a.dataEffettiva, 
						a.descrizione, 
						a.fk_coltura, 
						a.fk_coltivatore
				FROM Attivita a
				WHERE a.fk_coltura=?
				ORDER BY dataPianificata
				""";
		
		try (Connection conn=ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_coltura);
			
			try (ResultSet rs = ps.executeQuery()){
				List<Attivita> out=new ArrayList<>();
				while(rs.next()) {
					TipoAttivita tipo=TipoAttivita.valueOf(rs.getString("tipoAttivita").toUpperCase());
					StatoAttivita stato=StatoAttivita.valueOf(rs.getString("stato").toUpperCase());
					
					out.add(new Attivita(
							rs.getInt("id_attivita"),
							tipo,
							stato,
							rs.getObject("dataPianificata", LocalDate.class),
							rs.getObject("dataEffettiva", LocalDate.class),
							rs.getString("descrizione"),
							rs.getInt("fk_coltura"),
							rs.getString("fk_coltivatore")
							));
				}
				return out;
			}
			
			
		}
		
		
	}
	
	@Override
	public int create(Attivita a) throws Exception{
		//questa query oltre a inserire una nuova riga nella tabella Attivita ritorna l'id della riga appena creata
		String sql="""
				INSERT INTO Attivita
				(tipoAttivita, stato, dataPianificata, dataEffettiva, descrizione, fk_coltura, fk_coltivatore)
				VALUES
				(?::TipoAttivita, ?::StatoAttivita, ?, ?, ?, ?, ?)
				RETURNING id_attivita
				""";
		
		try (Connection conn=ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setString(1, a.getTipoAttivita().toString());
			ps.setString(2, a.getStato().toString());
			ps.setObject(3, a.getDataPianificata());
			ps.setObject(4, a.getDataEffettiva());
			ps.setString(5, a.getDescrizione());
			ps.setInt(6, a.getFk_coltura());
			ps.setString(7, a.getFk_coltivatore());
			
			try (ResultSet rs=ps.executeQuery()){//il ResultSet serve perchè il db manda in output dei dati(id_attivita) che vengono salvati lì.
				rs.next();
				return rs.getInt("id_attivita");
			}
			
		catch(SQLException e) {
			//mostra messaggi di trigger e constraint
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
			
		}
	}
	
	
	@Override
	public int updateStato(int id_attivita, StatoAttivita nuovoStato)throws Exception {
		String sql="""
				UPDATE attivita
				SET stato = ?::StatoAttivita
				WHERE id_attivita=?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			ps.setString(1, nuovoStato.toString());
			ps.setInt(2, id_attivita);
			
			return ps.executeUpdate();
			
			
		}
		
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
	}
	
	@Override
	public int update(Attivita a)throws Exception{
		String sql="""
				UPDATE Attivita
				SET tipoAttivita = ?::TipoAttivita,
					stato = ?::StatoAttivita,
					dataPianificazione = ?,
					dataEffettiva = ?,
					descrizione = ?,
					fk_coltura = ?,
					fk_coltivatore = ?
				WHERE id_attivita = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setString(1, a.getTipoAttivita().toString());
			ps.setString(2, a.getStato().toString());
			ps.setObject(3, a.getDataPianificata());
			ps.setObject(4, a.getDataEffettiva());
			ps.setString(5, a.getDescrizione());
			ps.setInt(6, a.getFk_coltura());
			ps.setString(7, a.getFk_coltivatore());
			ps.setInt(8, a.getId_attivita());
			
			return ps.executeUpdate();
		}
		
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
			
	}
	
	@Override
	public Attivita findById(int id_attivita)throws Exception{
		String sql="""
				SELECT a.id_attivita, 
						a.tipoAttivita::text, 
						a.stato::text, 
						a.dataPianificata, 
						a.dataEffettiva, 
						a.descrizione, 
						a.fk_coltura, 
						a.fk_coltivatore
				FROM Attivita a
				WHERE a.id_attivita = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
			
			ps.setInt(1, id_attivita);
			
			try(ResultSet rs=ps.executeQuery()){
				
				if(rs.next()) {
					String strTipo=rs.getString("tipoAttivita").toUpperCase();
					String strStato=rs.getString("stato").toUpperCase();
					TipoAttivita tipo=TipoAttivita.valueOf(strTipo);
					StatoAttivita stato=StatoAttivita.valueOf(strStato);
					
					return new Attivita(
							rs.getInt("id_attivita"),
							tipo,
							stato,
							rs.getObject("dataPianificata", LocalDate.class),
							rs.getObject("dataEffettiva", LocalDate.class),
							rs.getString("descrizione"),
							rs.getInt("fk_coltura"),
							rs.getString("fk_coltivatore")
							);
				}
				return null;
			}
		}
	}
	
	@Override
	public int delete(int id_attivita)throws Exception{
		String sql="""
				DELETE FROM Attivita
				WHERE id_attivita = ?
				""";
		
		try(Connection conn=ConnectionFactory.getInstance().getConnection();
			PreparedStatement ps=conn.prepareStatement(sql)){
				
				ps.setInt(1, id_attivita);
				
				return ps.executeUpdate();
			}
			
			catch(SQLException e) {
				System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
				System.err.println("DB says: "+e.getMessage());
				throw e;
			}
			
	}

}
