package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domain.TransactionHistory;



public class DaoStoricoDelleTransazioni {
	
	
	public void inserisciStoricoDelleTransazioni(LocalDate data, int scarpeDaBallo, int scarpeDaCorsa, int scarpeDaCalcio, double costoCarrello, int idCarrello, int idCliente, int idVenditore) throws SQLException {
        String insertSQL = "INSERT INTO storicoDelleTransazioni (idCliente, idCarrello, idVenditore, data, scarpeDaBallo, scarpeDaCorsa, scarpeDaCalcio, costoCarrello) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DBManager.conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, idCliente);
            pstmt.setInt(2, idCarrello);
            pstmt.setInt(3, idVenditore);
            pstmt.setString(4, data.toString());
            pstmt.setInt(5, scarpeDaBallo);
            pstmt.setInt(6, scarpeDaCorsa);
            pstmt.setInt(7, scarpeDaCalcio);
            pstmt.setDouble(8, costoCarrello);                        
            pstmt.executeUpdate();
        }
    }
	
	public List<TransactionHistory> mostraStorico(int idCliente, LocalDate da, LocalDate finoAl) throws SQLException {
	    String query = "SELECT * FROM storicoDelleTransazioni WHERE idCliente = ? AND data BETWEEN ? AND ?";
	    List<TransactionHistory> storicoTransazioni = new ArrayList<>();

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(query)) {
	        pstmt.setInt(1, idCliente);
	        pstmt.setString(2, da.toString());
	        pstmt.setString(3, finoAl.toString());

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                TransactionHistory transazione = new TransactionHistory(
	                    rs.getInt("id"),
	                    idCliente,
	                    rs.getInt("idVenditore"),
	                    LocalDate.parse(rs.getString("data")),
	                    rs.getInt("scarpeDaBallo"),
	                    rs.getInt("scarpeDaCorsa"),
	                    rs.getInt("scarpeDaCalcio"),
	                    rs.getDouble("costoCarrello"),
	                    rs.getInt("idCarrello")
	                );
	                storicoTransazioni.add(transazione);  
	            }
	        }
	    }

	    if (storicoTransazioni.isEmpty()) {
	        throw new IllegalArgumentException("Nessuna transazione trovata per il cliente con ID: " + idCliente + " nel periodo da " + da + " a " + finoAl);
	    }

	    return storicoTransazioni;
	}
}