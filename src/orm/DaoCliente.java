package orm;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import domain.Client;


public class DaoCliente{
		
    public int inserisciCliente(String nome, String email, String password) throws SQLException {
        String insertSQL = "INSERT INTO cliente (nome, email, password) VALUES ( ?, ?, ?)";
        try (PreparedStatement pstmt = DBManager.conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, email);
            pstmt.setString(3, password);
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
        String querySQL = "SELECT * FROM cliente WHERE nome = ? AND password = ?";
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

    public void ricaricaAccount(int id, double soldiInseriti) throws SQLException {
        String queryRecuperaSaldo = "SELECT saldo FROM cliente WHERE id = ?";
        String queryAggiornaSaldo = "UPDATE cliente SET saldo = ? WHERE id = ?";
        
        try (PreparedStatement pstmtRecupera = DBManager.conn.prepareStatement(queryRecuperaSaldo)) {
            pstmtRecupera.setInt(1, id);
            
            try (ResultSet rs = pstmtRecupera.executeQuery()) {
                if (rs.next()) {
                    double saldoAttuale = rs.getDouble("saldo");
                    double nuovoSaldo = saldoAttuale + soldiInseriti;
                    
                    try (PreparedStatement pstmtAggiorna = DBManager.conn.prepareStatement(queryAggiornaSaldo)) {
                        pstmtAggiorna.setDouble(1, nuovoSaldo);
                        pstmtAggiorna.setInt(2, id);
                        pstmtAggiorna.executeUpdate();
                    }
                } else {
                    throw new IllegalArgumentException("Cliente con ID " + id + " non trovato.");
                }
            }
        }
    }

    public void paga(int id, double soldiDaSottrarre) throws SQLException {
        String queryRecuperaSaldo = "SELECT saldo FROM cliente WHERE id = ?";
        String queryAggiornaSaldo = "UPDATE cliente SET saldo = ? WHERE id = ?";
        
        try (PreparedStatement pstmtRecupera = DBManager.conn.prepareStatement(queryRecuperaSaldo)) {
            pstmtRecupera.setInt(1, id);
            
            try (ResultSet rs = pstmtRecupera.executeQuery()) {
                if (rs.next()) {
                    double saldoAttuale = rs.getDouble("saldo");
                    double nuovoSaldo = saldoAttuale - soldiDaSottrarre;

                    if(nuovoSaldo>0) {
                        try (PreparedStatement pstmtAggiorna = DBManager.conn.prepareStatement(queryAggiornaSaldo)) {
                            pstmtAggiorna.setDouble(1, nuovoSaldo);
                            pstmtAggiorna.setInt(2, id);
                            pstmtAggiorna.executeUpdate();
                        }
                    }
                } else {
                    throw new IllegalArgumentException("Cliente con ID " + id + " non trovato.");
                }
            }
        }
    }
    
    public void togliDalMagazzinoLaMerceVenduta(int idVenditore, int scarpeDaBallo, int scarpeDaCorsa, int scarpeDaCalcio) throws SQLException {
	    String querySelezionaMagazzino = "SELECT * FROM magazzino WHERE idVenditore = ? LIMIT 1";
	    String queryAggiornaMagazzino = "UPDATE magazzino SET scarpeDaBallo = ?, scarpeDaCorsa = ?, scarpeDaCalcio = ? WHERE id = ?";

	    try (PreparedStatement pstmtSeleziona = DBManager.conn.prepareStatement(querySelezionaMagazzino)) {
	        pstmtSeleziona.setInt(1, idVenditore);

	        try (ResultSet rs = pstmtSeleziona.executeQuery()) {
	            if (rs.next()) {
	                int idMagazzino = rs.getInt("id");
	                int attualiScarpeDaBallo = rs.getInt("scarpeDaBallo");
	                int attualiScarpeDaCorsa = rs.getInt("scarpeDaCorsa");
	                int attualiScarpeDaCalcio = rs.getInt("scarpeDaCalcio");

	                int nuoveScarpeDaBallo = attualiScarpeDaBallo - scarpeDaBallo;
	                int nuoveScarpeDaCorsa = attualiScarpeDaCorsa - scarpeDaCorsa;
	                int nuoveScarpeDaCalcio = attualiScarpeDaCalcio - scarpeDaCalcio;

	                if (nuoveScarpeDaBallo < 0 || nuoveScarpeDaCorsa < 0 || nuoveScarpeDaCalcio < 0) {
	                	throw new IllegalArgumentException("Errore: quantità insufficienti nel magazzino per completare la vendita.");
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
    
    public double getSaldo(int id) throws SQLException {
        String queryRecuperaSaldo = "SELECT saldo FROM cliente WHERE id = ?";

        try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryRecuperaSaldo)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("saldo");
                } else {
                    throw new IllegalArgumentException("Cliente con ID " + id + " non trovato.");
                }
            }
        }
    }

    public Client creaCliente(int idCliente) throws SQLException {
        String queryCliente = "SELECT * FROM cliente WHERE id = ?";

        try (PreparedStatement pstmt = DBManager.conn.prepareStatement(queryCliente)) {
            pstmt.setInt(1, idCliente);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String nome = rs.getString("nome");
                    String email = rs.getString("email");
                    String password = rs.getString("password");
                    double saldo = rs.getDouble("saldo");

                    return new Client(id, nome, email, password, saldo);
                } else {
                    throw new IllegalArgumentException("Cliente con ID " + idCliente + " non trovato.");
                }
            }
        }
    }
}