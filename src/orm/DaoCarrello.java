package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.Cart;

public class DaoCarrello {
	
	public void inserisciCarrello(int idCliente) throws SQLException {
	    String insertSQL = "INSERT INTO carrello (idCliente) VALUES (?)";
	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(insertSQL)) {
	        pstmt.setInt(1, idCliente);
	        pstmt.executeUpdate();
	    }
	}
	
	public int ritornaID(int idCliente) throws SQLException {
	    String query = "SELECT * FROM carrello WHERE idCliente = ?";
	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(query)) {
	        pstmt.setInt(1, idCliente);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	            	return rs.getInt("id");
	            } else {
	                throw new IllegalArgumentException("Nessun carrello trovato per idCliente: " + idCliente);
	            }
	        }
	    }
	}
	
	public void eliminaCarrello(int idCarrello) throws SQLException {
	    String queryElimina = "DELETE FROM carrello WHERE id = ?";
	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryElimina)) {
	        pstmt.setInt(1, idCarrello);
	        int rowsAffected = pstmt.executeUpdate();
	        
	        if (rowsAffected <= 0)
	        	throw new IllegalArgumentException("Carrello con ID " + idCarrello + " non trovato.");
	        
	    }
	}
	
	public void aggiornaCarrello(int idCliente, int scarpeDaBallo, int scarpeDaCorsa, int scarpeDaCalcio, int idvenditore) throws SQLException {
	    String queryTrovaCarrello = "SELECT id FROM carrello WHERE idCliente = ?";
	    String queryAggiornaCarrello = "UPDATE carrello SET scarpeDaBallo = ?, scarpeDaCorsa = ?, scarpeDaCalcio = ?, costoCarrello = ? WHERE idCliente = ?";
		    
	    double costoCarrello = (scarpeDaBallo + scarpeDaCorsa + scarpeDaCalcio)*getPrezzoScarpa(idvenditore);
	    
	    try (PreparedStatement pstmtTrova = DBManager.conn.prepareStatement(queryTrovaCarrello)) {
	        pstmtTrova.setInt(1, idCliente);
	        try (ResultSet rs = pstmtTrova.executeQuery()) {
	            if (rs.next()) {

	            	try (PreparedStatement pstmtAggiorna = DBManager.conn.prepareStatement(queryAggiornaCarrello)) {
	                    pstmtAggiorna.setInt(1, scarpeDaBallo);
	                    pstmtAggiorna.setInt(2, scarpeDaCorsa);
	                    pstmtAggiorna.setInt(3, scarpeDaCalcio);
	                    pstmtAggiorna.setDouble(4, costoCarrello);
	                    pstmtAggiorna.setInt(5, idCliente);
	                    pstmtAggiorna.executeUpdate();
	                }
	            } else {
	                throw new IllegalArgumentException("Nessun carrello trovato per idCliente: " + idCliente);
	            }
	        }
	    }
	}
	
	
	public static double getPrezzoScarpa(int idVenditore) throws SQLException {
	    String queryPrezzoScarpa = "SELECT prezzoScarpa FROM venditore WHERE id = ?";
	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryPrezzoScarpa)) {
	        pstmt.setInt(1, idVenditore);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getDouble("prezzoScarpa");
	            } else {
	                throw new IllegalArgumentException("Venditore con ID " + idVenditore + " non trovato.");
	            }
	        }
	    }
	}
	
	public double getCostoCarrello(int idCarrello) throws SQLException {
	    String queryCostoCarrello = "SELECT costoCarrello FROM carrello WHERE id = ?";
	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryCostoCarrello)) {
	        pstmt.setInt(1, idCarrello);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getDouble("costoCarrello");
	            } else {
	                throw new IllegalArgumentException("Carrello con ID " + idCarrello + " non trovato.");
	            }
	        }
	    }
	}
	
	public int getScarpeDaBallo(int id) throws SQLException {
	    String queryScarpeDaBallo = "SELECT scarpeDaBallo FROM carrello WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryScarpeDaBallo)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("scarpeDaBallo");
	            } else {
	                throw new IllegalArgumentException("Carrello non trovato per il cliente con ID " + id);
	            }
	        }
	    }
	}

	public int getScarpeDaCorsa(int id) throws SQLException {
	    String queryScarpeDaCorsa = "SELECT scarpeDaCorsa FROM carrello WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryScarpeDaCorsa)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("scarpeDaCorsa");
	            } else {
	                throw new IllegalArgumentException("Carrello non trovato per il cliente con ID " + id);
	            }
	        }
	    }
	}
	
	public int getScarpeDaCalcio(int id) throws SQLException {
	    String queryScarpeDaCalcio = "SELECT scarpeDaCalcio FROM carrello WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryScarpeDaCalcio)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("scarpeDaCalcio");
	            } else {
	                throw new IllegalArgumentException("Carrello non trovato per il cliente con ID " + id);
	            }
	        }
	    }
	}

	public Cart ritornaCarrello(int idCarrello) throws SQLException {
	    String queryCarrello = "SELECT * FROM carrello WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryCarrello)) {
	        pstmt.setInt(1, idCarrello);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                int id = rs.getInt("id");
	                int qtaScarpeDaBallo = rs.getInt("scarpeDaBallo");
	                int qtaScarpeDaCorsa = rs.getInt("scarpeDaCorsa");
	                int qtaScarpeDaCalcio = rs.getInt("scarpeDaCalcio");
	                int idCliente = rs.getInt("idCliente");
	                double costoCarrello = rs.getDouble("costoCarrello");

	                // Creazione e ritorno dell'oggetto Carrello
	                return new Cart(id, qtaScarpeDaBallo, qtaScarpeDaCorsa, qtaScarpeDaCalcio, idCliente, costoCarrello);
	            } else {
	                throw new IllegalArgumentException("Carrello con ID " + idCarrello + " non trovato.");
	            }
	        }
	    }
	}
}
