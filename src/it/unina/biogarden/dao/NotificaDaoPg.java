package it.unina.biogarden.dao;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import it.unina.biogarden.model.Notifica;
import it.unina.biogarden.model.Proprietario;
import it.unina.biogarden.model.TipoNotifica;

class NotificaDaoPg implements NotificaDao{
	 
	 @Override
	 public int create(Notifica n)throws Exception{
		 
		 String sql="""
		 		INSERT INTO Notifica(creatore_proprietario_email, destinatario_coltivatore_email, 
		 								titolo, messaggio, tipo, fk_progetto, fk_attivita)
		 		VALUES
		 		(?, ?, ?, ?, ?, ?, ?)
		 		RETURNING id_notifica
		 		""";
		 
		 try(Connection conn=ConnectionFactory.getInstance().getConnection();
			 PreparedStatement ps=conn.prepareStatement(sql)){
			 
			 ps.setString(1, n.getCreatore_proprietario_email());
			 ps.setString(2, n.getDestinatario_coltivatore_email());
			 ps.setString(3, n.getTitolo());
			 ps.setString(4, n.getMessaggio());
			 ps.setString(5, n.getTipo().toString());
			 ps.setObject(6, n.getFk_progetto(), Types.INTEGER);
			 ps.setObject(7, n.getFk_attivita(), Types.INTEGER);
			 
			 try(ResultSet rs=ps.executeQuery()){
				 rs.next();
				 return rs.getInt("id_notifica");
			 }

		 }
		 
		catch(SQLException e) {
			System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
			System.err.println("DB says: "+e.getMessage());
			throw e;
		}
		 
	 }
	 
	 @Override
	 public Notifica findById(int id_notifica)throws Exception{
		 
		 String sql="""
		 		SELECT 
		 			n.id_notifica,
		 			n.creatore_proprietario_email,
		 			n.destinatario_coltivatore_email,
		 			n.titolo,
		 			n.messaggio,
		 			n.tipo,
		 			n.data_creazione,
		 			n.fk_progetto,
		 			n.fk_attivita
		 		FROM Notifica n
		 		WHERE n.id_notifica = ?
		 		""";
		 
		 try(Connection conn=ConnectionFactory.getInstance().getConnection();
			  PreparedStatement ps=conn.prepareStatement(sql)){
			 
			 ps.setInt(1, id_notifica);
			 
			 try(ResultSet rs=ps.executeQuery()){
				 if(rs.next()) {
					 
					 String tipoStr=rs.getString("tipo");
					 TipoNotifica tipo=TipoNotifica.valueOf(tipoStr);
					 
					 return new Notifica(rs.getInt("id_notifica"),
							 			  rs.getString("creatore_proprietario_email"),
							 			  rs.getString("destinatario_coltivatore_email"),
							 			  rs.getString("titolo"),
							 			  rs.getString("messaggio"),
							 			  tipo,
							 			  rs.getTimestamp("data_creazione").toLocalDateTime(),
							 			  (Integer) rs.getObject("fk_progetto"),
							 			  (Integer) rs.getObject("fk_attivita"));
							 
				 }
				 return null;
			 }
		 }
	 }
	 
	 
	 @Override
	 public List<Notifica> findByColtivatore(String email_colt)throws Exception{
		 
		 String sql="""
		 		SELECT 
		 			n.id_notifica,
		 			n.creatore_proprietario_email,
		 			n.destinatario_coltivatore_email,
		 			n.titolo,
		 			n.messaggio,
		 			n.tipo,
		 			n.data_creazione,
		 			n.fk_progetto,
		 			n.fk_attivita
		 		FROM Notifica n
		 		WHERE n.destinatario_coltivatore_email = ? -- notifiche individuali
		 		OR(											--subquery per notifiche generiche in cui il coltivatore passato è coinvolto
		 			n.destinatario_coltivatore_email IS NULL 
		 			AND 
		 			EXISTS(
		 					SELECT 1
		 					FROM Attivita a
		 					JOIN Coltura c on a.fk_coltura = c.id_coltura
		 					JOIN ProgettoStagionale p ON c.fk_progetto = p.id_progetto
		 					JOIN Lotto l ON p.fk_lotto = l.id_lotto
		 					WHERE a.fk_coltivatore = ?
		 					AND l.fk_proprietario = n.creatore_proprietario_email
		 					)
		 			)
		 		ORDER BY N.data_creazione DESC
		 		""";
		 
		 try(Connection conn=ConnectionFactory.getInstance().getConnection();
			 PreparedStatement ps=conn.prepareStatement(sql)){
			 
			 ps.setString(1, email_colt);
			 ps.setString(2, email_colt);
			 
			 try(ResultSet rs=ps.executeQuery()){
				 
				 List<Notifica> out=new ArrayList<>();
				 
				 while (rs.next()){
					 
					 String tipoStr=rs.getString("tipo");
					 TipoNotifica tipo=TipoNotifica.valueOf(tipoStr);
					 
					 out.add(new Notifica(
							 rs.getInt("id_notifica"),
							 rs.getString("creatore_proprietario_email"),
							 rs.getString("destinatario_coltivatore_email"),
							 rs.getString("titolo"),
							 rs.getString("messaggio"),
							 tipo,
							 rs.getTimestamp("data_creazione").toLocalDateTime(),
							 (Integer) rs.getObject("fk_progetto"),
							 (Integer) rs.getObject("fk_attivita")));
				 }
				 return out;
			 }
		 }
	 }
	 
	 
	 @Override
	 public List<Notifica> findByProprietario(String email_prop)throws Exception{
		 
		 String sql="""
		 		SELECT 
		 			n.id_notifica,
		 			n.creatore_proprietario_email,
		 			n.destinatario_coltivatore_email,
		 			n.titolo,
		 			n.messaggio,
		 			n.tipo,
		 			n.data_creazione,
		 			n.fk_progetto,
		 			n.fk_attivita
		 		FROM Notifica n
		 		WHERE n.creatore_proprietario_email = ?
		 		ORDER BY n.data_creazione DESC	 		
		 		""";
		 
		 try(Connection conn=ConnectionFactory.getInstance().getConnection();
				 PreparedStatement ps=conn.prepareStatement(sql)){
			 
			 ps.setString(1, email_prop);
			 
			 try(ResultSet rs=ps.executeQuery()){
				 
				 List<Notifica> out=new ArrayList<>();
				 
				 while (rs.next()){
					 
					 String tipoStr=rs.getString("tipo");
					 TipoNotifica tipo=TipoNotifica.valueOf(tipoStr);
					 
					 out.add(new Notifica(
							 rs.getInt("id_notifica"),
							 rs.getString("creatore_proprietario_email"),
							 rs.getString("destinatario_coltivatore_email"),
							 rs.getString("titolo"),
							 rs.getString("messaggio"),
							 tipo,
							 rs.getTimestamp("data_creazione").toLocalDateTime(),
							 (Integer) rs.getObject("fk_progetto"),
							 (Integer) rs.getObject("fk_attivita")));
				 }
				 
				 return out;
				 
			 }
		 }
		 
	 }
	 
	 
	 @Override
	 public List<Notifica> findByProgetto(int id_progetto)throws Exception{

		 String sql="""
			 		SELECT 
			 			n.id_notifica,
			 			n.creatore_proprietario_email,
			 			n.destinatario_coltivatore_email,
			 			n.titolo,
			 			n.messaggio,
			 			n.tipo,
			 			n.data_creazione,
			 			n.fk_progetto,
			 			n.fk_attivita
			 		FROM Notifica n
			 		WHERE n.fk_progetto = ?
			 		ORDER BY n.data_creazione DESC	 		
			 		""";
		 
		 try(Connection conn=ConnectionFactory.getInstance().getConnection();
				 PreparedStatement ps=conn.prepareStatement(sql)){
			 
			 ps.setInt(1, id_progetto);
			 			 
			 try(ResultSet rs=ps.executeQuery()){
				 List<Notifica> out=new ArrayList<>();
				 
				 while (rs.next()){
					 
					 String tipoStr=rs.getString("tipo");
					 TipoNotifica tipo=TipoNotifica.valueOf(tipoStr);
					 
					 out.add(new Notifica(
							 rs.getInt("id_notifica"),
							 rs.getString("creatore_proprietario_email"),
							 rs.getString("destinatario_coltivatore_email"),
							 rs.getString("titolo"),
							 rs.getString("messaggio"),
							 tipo,
							 rs.getTimestamp("data_creazione").toLocalDateTime(),
							 (Integer) rs.getObject("fk_progetto"),
							 (Integer) rs.getObject("fk_attivita")));
				 }
				 
				 return out;
				 
			 }
		 }
		 
	 }
	 
	 
	 @Override
	 public int delete(int id_notifica)throws Exception{
		 
		 String sql="""
		 		DELETE FROM Notifica
		 		WHERE id_notifica = ?
		 		""";
		 
		 try(Connection conn=ConnectionFactory.getInstance().getConnection();
				 PreparedStatement ps=conn.prepareStatement(sql)){
			 
			 ps.setInt(1, id_notifica);
			 
			 return ps.executeUpdate();
			 
		}
		 
		 catch(SQLException e) {
				System.err.println("SQLState="+e.getSQLState()+" code="+e.getErrorCode());
				System.err.println("DB says: "+e.getMessage());
				throw e;
			}
		}
	 
	 
	 @Override
	 public List<Notifica> genereAutomaticheDaView(String email_prop)throws Exception{
		 
		 List<Notifica> out=new ArrayList<>();
		 
			//attività in ritardo
			String sqlRitardo="""
					SELECT
						   v.id_attivita,
						   v.tipoAttivita,
						   v.dataPianificata,
						   v.id_coltura,
						   p.id_progetto,
						   p.nome AS progetto_nome
					FROM vw_attivita_in_ritardo v
					JOIN Coltura c ON v.id_coltura = c.id_coltura
					JOIN ProgettoStagionale p ON c.fk_progetto = p.id_progetto
					JOIN Lotto l ON p.fk_lotto = l.id_lotto
					WHERE l.fk_proprietario = ?
					""";
			
			//attività imminenti
			String sqlImminenti="""
					SELECT
						   v.id_attivita,
						   v.tipoAttivita,
						   v.dataPianificata,
						   v.id_coltura,
						   p.id_progetto,
						   p.nome AS progetto_nome
					FROM vw_attivita_prossime v
					JOIN Coltura c ON v.id_coltura = c.id_coltura
					JOIN ProgettoStagionale p ON c.fk_progetto = p.id_progetto
					JOIN Lotto l ON p.fk_lotto = l.id_lotto
					WHERE l.fk_proprietario = ?
					""";
			
			try(Connection conn=ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps=conn.prepareStatement(sqlRitardo)){
				
				ps.setString(1, email_prop);
				
				try(ResultSet rs=ps.executeQuery()){
					
					while(rs.next()) {
						String titolo="Attività in ritardo: "+rs.getString("tipoAttivita");
						String messaggio="L'attività era prevista per "+rs.getObject("dataPianificata", LocalDate.class)+
											" nel progetto "+rs.getString("progetto_nome");
						
						out.add(new Notifica(0, email_prop, null, titolo, messaggio,
												TipoNotifica.ATTIVITA_IN_RITARDO, LocalDateTime.now(),
												rs.getObject("id_progetto", Integer.class),
												rs.getObject("id_attivita", Integer.class)));
					}
					
				}
			}
			
			try(Connection conn=ConnectionFactory.getInstance().getConnection();
				PreparedStatement ps=conn.prepareStatement(sqlImminenti)){
				
				ps.setString(1, email_prop);
				
				try(ResultSet rs=ps.executeQuery()){
					
					while(rs.next()) {
						String titolo="Attività imminente: "+rs.getString("tipoAttivita");
						String messaggio="Attività prevista per "+rs.getObject("dataPianificata", LocalDate.class)+
											" nel progetto "+rs.getString("progetto_nome");	
	
						out.add(new Notifica(0, email_prop, null, titolo, messaggio,
												TipoNotifica.ATTIVITA_IMMINENTE, LocalDateTime.now(),
												rs.getObject("id_progetto", Integer.class),
												rs.getObject("id_attivita", Integer.class)));
					}
				}
			}
			
			return out;
			
	 }
	 
}
