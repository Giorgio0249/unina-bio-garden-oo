package it.unina.biogarden.dao;

import it.unina.biogarden.model.Attivita;
import it.unina.biogarden.model.Coltura;
import it.unina.biogarden.model.StatoAttivita;
import it.unina.biogarden.model.TipoAttivita;

import java.sql.*;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

class AttivitaDaoPg implements AttivitaDao {

    @Override
    public List<Attivita> findByColtivatore(String emailColtivatore) throws Exception {
        String sql = """
                SELECT a.*, 
                       tc.nomeTipo as nome_tipo_coltura
                FROM Attivita a
                JOIN Coltura c ON a.fk_coltura = c.id_coltura
                JOIN TipoColtura tc ON c.fk_tipo_coltura = tc.nomeTipo
                WHERE a.fk_coltivatore = ?
                ORDER BY 
                    CASE a.stato
                        WHEN 'IN_CORSO' THEN 1
                        WHEN 'PIANIFICATA' THEN 2
                        WHEN 'COMPLETATA' THEN 3
                        WHEN 'ANNULLATA' THEN 4
                        ELSE 5
                    END, 
                    a.dataPianificata ASC;
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, emailColtivatore);

            try (ResultSet rs = ps.executeQuery()) {
                List<Attivita> lista = new ArrayList<>();
                while (rs.next()) {
                    Attivita att = new Attivita(
                        rs.getInt("id_attivita"),
                        TipoAttivita.valueOf(rs.getString("tipoAttivita").toUpperCase()),
                        StatoAttivita.valueOf(rs.getString("stato").toUpperCase()),
                        rs.getObject("dataPianificata", LocalDate.class),
                        rs.getObject("dataEffettiva", LocalDate.class),
                        rs.getString("descrizione"),
                        rs.getInt("fk_coltura"),
                        rs.getString("fk_coltivatore")
                    );
                    att.setNomeTipoColtura(rs.getString("nome_tipo_coltura"));
                    lista.add(att);
                }
                return lista;
            }
        }
    }

    @Override
    public List<Attivita> findByColtura(int id_coltura) throws Exception {
        String sql = """
                SELECT a.*, 
                		tc.nomeTipo as nome_tipo_coltura
                FROM Attivita a
                JOIN Coltura c ON a.fk_coltura = c.id_coltura
                JOIN TipoColtura tc ON c.fk_tipo_coltura = tc.nomeTipo
                WHERE a.fk_coltura = ?
                ORDER BY 
                    CASE a.stato
                        WHEN 'IN_CORSO' THEN 1
                        WHEN 'PIANIFICATA' THEN 2
                        WHEN 'COMPLETATA' THEN 3
                        WHEN 'ANNULLATA' THEN 4
                        ELSE 5
                    END, 
                    a.dataPianificata ASC;
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id_coltura);

            try (ResultSet rs = ps.executeQuery()) {
                List<Attivita> out = new ArrayList<>();
                while (rs.next()) {
                    Attivita att = new Attivita(
                        rs.getInt("id_attivita"),
                        TipoAttivita.valueOf(rs.getString("tipoAttivita").toUpperCase()),
                        StatoAttivita.valueOf(rs.getString("stato").toUpperCase()),
                        rs.getObject("dataPianificata", LocalDate.class),
                        rs.getObject("dataEffettiva", LocalDate.class),
                        rs.getString("descrizione"),
                        rs.getInt("fk_coltura"),
                        rs.getString("fk_coltivatore")
                    );
                    att.setNomeTipoColtura(rs.getString("nome_tipo_coltura"));
                    out.add(att);
                }
                return out;
            }
        }
    }

    @Override
    public List<Attivita> findByProgetto(int id_progetto) throws Exception {
        String sql = """
                SELECT a.*, 
                		tc.nomeTipo as nome_tipo_coltura
                FROM Attivita a
                JOIN Coltura c ON a.fk_coltura = c.id_coltura
                JOIN TipoColtura tc ON c.fk_tipo_coltura = tc.nomeTipo
                WHERE c.fk_progetto = ?
                ORDER BY 
                    CASE a.stato
                        WHEN 'IN_CORSO' THEN 1
                        WHEN 'PIANIFICATA' THEN 2
                        WHEN 'COMPLETATA' THEN 3
                        WHEN 'ANNULLATA' THEN 4
                        ELSE 5
                    END, 
                    a.dataPianificata ASC;
                """;
        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id_progetto);
            try (ResultSet rs = ps.executeQuery()) {
                List<Attivita> out = new ArrayList<>();
                while (rs.next()) {
                    Attivita att = new Attivita(
                        rs.getInt("id_attivita"),
                        TipoAttivita.valueOf(rs.getString("tipoAttivita").toUpperCase()),
                        StatoAttivita.valueOf(rs.getString("stato").toUpperCase()),
                        rs.getObject("dataPianificata", LocalDate.class),
                        rs.getObject("dataEffettiva", LocalDate.class),
                        rs.getString("descrizione"),
                        rs.getInt("fk_coltura"),
                        rs.getString("fk_coltivatore")
                    );
                    att.setNomeTipoColtura(rs.getString("nome_tipo_coltura"));
                    out.add(att);
                }
                return out;
            }
        }
    }

    @Override
    public int create(Attivita a) throws Exception {
        String sql = """
                INSERT INTO Attivita
                (tipoAttivita, stato, dataPianificata, dataEffettiva, descrizione, fk_coltura, fk_coltivatore)
                VALUES
                (?::TipoAttivita, ?::StatoAttivita, ?, ?, ?, ?, ?)
                RETURNING id_attivita
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getTipoAttivita().toString());
            ps.setString(2, a.getStato().toString());
            ps.setObject(3, a.getDataPianificata());
            ps.setObject(4, a.getDataEffettiva());
            ps.setString(5, a.getDescrizione());
            ps.setInt(6, a.getFk_coltura());
            ps.setString(7, a.getFk_coltivatore());

            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt("id_attivita");
            }
        } catch (SQLException e) {
            System.err.println("SQLState=" + e.getSQLState() + " code=" + e.getErrorCode());
            System.err.println("DB says: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int updateStato(int id_attivita, StatoAttivita nuovoStato) throws Exception {
        String sql = """
                UPDATE Attivita
                SET stato = ?::StatoAttivita
                WHERE id_attivita = ?
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, nuovoStato.toString());
            ps.setInt(2, id_attivita);

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore DB: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public int update(Attivita a) throws Exception {
        String sql = """
                UPDATE Attivita
                SET tipoAttivita = ?::TipoAttivita,
                    stato = ?::StatoAttivita,
                    dataPianificata = ?,
                    dataEffettiva = ?,
                    descrizione = ?,
                    fk_coltura = ?,
                    fk_coltivatore = ?
                WHERE id_attivita = ?
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, a.getTipoAttivita().toString());
            ps.setString(2, a.getStato().toString());
            ps.setObject(3, a.getDataPianificata());
            ps.setObject(4, a.getDataEffettiva());
            ps.setString(5, a.getDescrizione());
            ps.setInt(6, a.getFk_coltura());
            ps.setString(7, a.getFk_coltivatore());
            ps.setInt(8, a.getId_attivita());

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQLState=" + e.getSQLState() + " code=" + e.getErrorCode());
            System.err.println("DB says: " + e.getMessage());
            throw e;
        }

    }

    @Override
    public Attivita findById(int id_attivita) throws Exception {
        String sql = """
                SELECT a.*, 
                		tc.nomeTipo as nome_tipo_coltura
                FROM Attivita a
                JOIN Coltura c ON a.fk_coltura = c.id_coltura
                JOIN TipoColtura tc ON c.fk_tipo_coltura = tc.nomeTipo
                WHERE a.id_attivita = ?
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id_attivita);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    Attivita att = new Attivita(
                        rs.getInt("id_attivita"),
                        TipoAttivita.valueOf(rs.getString("tipoAttivita").toUpperCase()),
                        StatoAttivita.valueOf(rs.getString("stato").toUpperCase()),
                        rs.getObject("dataPianificata", LocalDate.class),
                        rs.getObject("dataEffettiva", LocalDate.class),
                        rs.getString("descrizione"),
                        rs.getInt("fk_coltura"),
                        rs.getString("fk_coltivatore")
                    );
                    att.setNomeTipoColtura(rs.getString("nome_tipo_coltura"));
                    return att;
                }
                return null;
            }
        }
    }

    @Override
    public int delete(int id_attivita) throws Exception {
        String sql = """
                DELETE FROM Attivita
                WHERE id_attivita = ?
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id_attivita);

            return ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQLState=" + e.getSQLState() + " code=" + e.getErrorCode());
            System.err.println("DB says: " + e.getMessage());
            throw e;
        }

    }

    @Override
    public boolean existsColtivatorePerProprietario(String email_colt, String email_prop) throws Exception {
        String sql = """
                SELECT 1
                FROM Attivita a
                JOIN Coltura c ON a.fk_coltura = c.id_coltura
                JOIN ProgettoStagionale p ON c.fk_progetto = p.id_progetto
                JOIN Lotto l ON p.fk_lotto = l.id_lotto
                WHERE a.fk_coltivatore = ?
                AND l.fk_proprietario = ?
                LIMIT 1
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, email_colt);
            ps.setString(2, email_prop);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }

    }

    @Override
    public boolean existsAttivitaPerProprietario(int id_attivita, String email_prop) throws Exception {
        String sql = """
                SELECT 1
                FROM Attivita a
                JOIN Coltura c ON a.fk_coltura = c.id_coltura
                JOIN ProgettoStagionale p ON c.fk_progetto = p.id_progetto
                JOIN Lotto l ON p.fk_lotto = l.id_lotto
                WHERE a.id_attivita = ?
                AND l.fk_proprietario = ?
                LIMIT 1
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id_attivita);
            ps.setString(2, email_prop);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }

    }

    @Override
    public boolean existsAttivitaInProgetto(int id_attivita, int id_progetto) throws Exception {
        String sql = """
                SELECT 1
                FROM Attivita a
                JOIN Coltura c ON a.fk_coltura = c.id_coltura
                WHERE a.id_attivita = ?
                AND c.fk_progetto = ?
                LIMIT 1
                """;

        try (Connection conn = ConnectionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id_attivita);
            ps.setInt(2, id_progetto);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }

    }

}