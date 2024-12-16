package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import domain.InvoiceRegister;


public class DaoRegistroFatturazioni {
	
	public void inserisciRegistroFatturazioni(int idVenditore, LocalDate data, double importo) throws SQLException {
		String insertSQL = "INSERT INTO registroFatturazioni ( idVenditore, data, importo) VALUES ( ?, ?, ?)";
        try (PreparedStatement pstmt = DBManager.conn.prepareStatement(insertSQL)) {
            pstmt.setInt(1, idVenditore);
            pstmt.setString(2, data.toString());
            pstmt.setDouble(3, importo);
            pstmt.executeUpdate();
        }
	}
	
	public List<InvoiceRegister> stampaRegistroFatturazioni(int idVenditore) throws SQLException {
	    String query = "SELECT * FROM registroFatturazioni WHERE idVenditore = ?";
	    List<InvoiceRegister> fatturazioni = new ArrayList<>();

	    try (PreparedStatement pstmt = DBManager.conn.prepareStatement(query)) {
	        pstmt.setInt(1, idVenditore);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                InvoiceRegister registro = new InvoiceRegister(
	                    rs.getInt("id"),
	                    LocalDate.parse(rs.getString("data")),  
	                    rs.getDouble("importo"),
	                    rs.getInt("idVenditore")
	                );
	                fatturazioni.add(registro);
	            }
	        }
	    }

	    if (fatturazioni.isEmpty()) {
	        throw new IllegalArgumentException("Nessuna fatturazione trovata per il venditore con ID: " + idVenditore);
	    }

	    return fatturazioni;
	}
}