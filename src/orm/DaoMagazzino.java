package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.WareHouse;

public class DaoMagazzino {

	public void inserisciMagazzino(int idVenditore, int capienzaMagazzino) throws SQLException {
		
		String insertSQL = "INSERT INTO magazzino (idVenditore, capienzaMagazzino) VALUES ( ?, ?)";
		try (PreparedStatement pstmt = DBManager.conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, idVenditore);
            pstmt.setInt(2, capienzaMagazzino);
            pstmt.executeUpdate();
		}
	}
	

	public void ricaricaMagazzino(int idVenditore, int scarpeDaBallo, int scarpeDaCorsa, int scarpeDaCalcio, int idMagazzino) throws SQLException {
	    String querySelezionaMagazzino = "SELECT * FROM magazzino WHERE id = ?";
	    String queryAggiornaMagazzino = "UPDATE magazzino SET scarpeDaBallo = ?, scarpeDaCorsa = ?, scarpeDaCalcio = ? WHERE id = ?";

	    try (PreparedStatement pstmtSeleziona = DBManager.conn.prepareStatement(querySelezionaMagazzino)) {
	        pstmtSeleziona.setInt(1, idMagazzino);

	        try (ResultSet rs = pstmtSeleziona.executeQuery()) {
	            if (rs.next()) {
	                int attualiScarpeDaBallo = rs.getInt("scarpeDaBallo");
	                int attualiScarpeDaCorsa = rs.getInt("scarpeDaCorsa");
	                int attualiScarpeDaCalcio = rs.getInt("scarpeDaCalcio");
	                int capienzaMagazzino = rs.getInt("capienzaMagazzino");

	                int nuoveScarpeDaBallo = attualiScarpeDaBallo + scarpeDaBallo;
	                int nuoveScarpeDaCorsa = attualiScarpeDaCorsa + scarpeDaCorsa;
	                int nuoveScarpeDaCalcio = attualiScarpeDaCalcio + scarpeDaCalcio;

	                int totaleScarpe = nuoveScarpeDaBallo + nuoveScarpeDaCorsa + nuoveScarpeDaCalcio;
	                if (totaleScarpe > capienzaMagazzino) {
	                    throw new IllegalArgumentException("Errore: la somma delle scarpe ricaricate supera la capienza del magazzino.");
	                }

	                try (PreparedStatement pstmtAggiorna = DBManager.conn.prepareStatement(queryAggiornaMagazzino)) {
	                    pstmtAggiorna.setInt(1, nuoveScarpeDaBallo);
	                    pstmtAggiorna.setInt(2, nuoveScarpeDaCorsa);
	                    pstmtAggiorna.setInt(3, nuoveScarpeDaCalcio);
	                    pstmtAggiorna.setInt(4, idMagazzino);
	                    pstmtAggiorna.executeUpdate();
	                }
	            } else {
	                throw new IllegalArgumentException("Nessun magazzino trovato per il venditore con ID: " + idVenditore);
	            }
	        }
	    }
	}


	public int ritornaID(int idVenditore) throws SQLException {
	    String query = "SELECT * FROM magazzino WHERE idVenditore = ? LIMIT 1";
	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(query)) {
	        pstmt.setInt(1, idVenditore);
	        
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("id");

	            } else {
	            	throw new IllegalArgumentException("Nessun magazzino trovato per idVenditore: " + idVenditore);
	            }
	        }
	    }
	}
	
	public int trovaAltroIDMagazzino(int idVenditore, int idMagazzino) throws SQLException {
	    String query = "SELECT id FROM magazzino WHERE idVenditore = ? AND id != ? LIMIT 1";
	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(query)) {
	        pstmt.setInt(1, idVenditore);
	        pstmt.setInt(2, idMagazzino);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("id");
	            } else {
	                throw new IllegalArgumentException("Nessun altro magazzino trovato per idVenditore: " + idVenditore);
	            }
	        }
	    }
	}

	
	
	public WareHouse creaMagazzino(int idMagazzino) throws SQLException {
	    String queryMagazzino = "SELECT * FROM magazzino WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryMagazzino)) {
	        pstmt.setInt(1, idMagazzino);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                int id = rs.getInt("id");
	                int idVenditore = rs.getInt("idVenditore");
	                int scarpeDaBallo = rs.getInt("scarpeDaBallo");
	                int scarpeDaCorsa = rs.getInt("scarpeDaCorsa");
	                int scarpeDaCalcio = rs.getInt("scarpeDaCalcio");
	                int capienzaMagazzino = rs.getInt("capienzaMagazzino");

	                // Creazione e ritorno dell'oggetto Magazzino
	                return new WareHouse(id, idVenditore, scarpeDaBallo, scarpeDaCorsa, scarpeDaCalcio, capienzaMagazzino);
	            } else {
	                throw new IllegalArgumentException("Magazzino con ID " + idMagazzino + " non trovato.");
	            }
	        }
	    }
	}

	public int getScarpeDaBallo(int id) throws SQLException {
	    String queryScarpeDaBallo = "SELECT scarpeDaBallo FROM magazzino WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryScarpeDaBallo)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("scarpeDaBallo");
	            } else {
	                throw new IllegalArgumentException("Magazzino non trovato per il cliente con ID " + id);
	            }
	        }
	    }
	}

	public int getScarpeDaCorsa(int id) throws SQLException {
	    String queryScarpeDaCorsa = "SELECT scarpeDaCorsa FROM magazzino WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryScarpeDaCorsa)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("scarpeDaCorsa");
	            } else {
	                throw new IllegalArgumentException("Magazzino non trovato per il cliente con ID " + id);
	            }
	        }
	    }
	}
	
	public int getScarpeDaCalcio(int id) throws SQLException {
	    String queryScarpeDaCalcio = "SELECT scarpeDaCalcio FROM magazzino WHERE id = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryScarpeDaCalcio)) {
	        pstmt.setInt(1, id);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("scarpeDaCalcio");
	            } else {
	                throw new IllegalArgumentException("Magazzino non trovato per il cliente con ID " + id);
	            }
	        }
	    }
	}
	
	public int contaMagazziniPerVenditore(int idVenditore) throws SQLException {
	    String queryContaMagazzini = "SELECT COUNT(*) AS totale FROM magazzino WHERE idVenditore = ?";

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryContaMagazzini)) {
	        pstmt.setInt(1, idVenditore);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return rs.getInt("totale");
	            } else {
	                throw new IllegalArgumentException("Errore durante il conteggio dei magazzini per idVenditore: " + idVenditore);
	            }
	        }
	    }
	}

	
}

