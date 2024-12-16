package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.Seller;

public class DaoVenditore {
	
	public int inserisciVenditore(String nome, String email, String password, double prezzoScarpa) throws SQLException {
        String insertSQL = "INSERT INTO venditore (nome, email, password, prezzoScarpa) VALUES ( ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DBManager.conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
            pstmt.setDouble(4, prezzoScarpa);
            pstmt.executeUpdate();
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Restituisce l'ID generato
                } else {
                    throw new SQLException("Inserimento fallito, nessun ID ottenuto.");
                }
            }
        }
    }
		
	public int accediAccount(String nome, String password) throws SQLException {
        String querySQL = "SELECT * FROM venditore WHERE nome = ? AND password = ?";
        try (PreparedStatement pstmt = DBManager.conn.prepareStatement(querySQL)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt("id");
                

            } else {
                throw new IllegalArgumentException("Nome o password errati");
            }

        } catch (SQLException e) {
        		throw new SQLException("Errore nel tentativo di accesso: " + e.getMessage());
        }
    }
	
	public void cambiaPrezzo(int idVenditore, double nuovoPrezzo) throws SQLException {
	    String updateSQL = "UPDATE venditore SET prezzoScarpa = ? WHERE id = ?";
	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(updateSQL)) {
	        pstmt.setDouble(1, nuovoPrezzo);
	        pstmt.setInt(2, idVenditore);

	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected == 0) {
	            throw new IllegalArgumentException("Aggiornamento fallito, venditore non trovato con ID: " + idVenditore);
	        }
	    } catch (SQLException e) {
	        throw new SQLException("Errore durante l'aggiornamento del prezzo: " + e.getMessage());
	    }
	}
	
	public Seller creaVenditore(int idVenditore) throws SQLException {
	    String querySeller = "SELECT * FROM venditore WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(querySeller)) {
	        pstmt.setInt(1, idVenditore);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                int id = rs.getInt("id");
	                String name = rs.getString("nome");
	                String email = rs.getString("email");
	                String password = rs.getString("password");
	                double prezzoScarpa = rs.getDouble("prezzoScarpa");

	                return new Seller(id, name, email, password, prezzoScarpa);
	            } else {
	                throw new IllegalArgumentException("Venditore con ID " + idVenditore + " non trovato.");
	            }
	        }
	    }
	}


	
	
	
	
}
