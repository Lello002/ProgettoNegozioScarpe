package orm;

import java.sql.*;

public class DBManager {

    static Connection conn;

    public void connect() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite: mydatabase.db");
        conn.setAutoCommit(false);
    }

    public void createTables() throws SQLException {
        Statement stmt = conn.createStatement();

        String creaTabellaClienti = "CREATE TABLE IF NOT EXISTS cliente (" +
                                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                "nome TEXT NOT NULL, " +
                                "email TEXT NOT NULL,"+
                                "password TEXT NOT NULL,"+
                                "saldo DOUBLE DEFAULT 0)";
        stmt.executeUpdate(creaTabellaClienti);
        
        String creaTabellaCarrello = "CREATE TABLE IF NOT EXISTS carrello (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "idCliente INTEGER NOT NULL, " +
                "scarpeDaBallo INTEGER DEFAULT 0, " +
                "scarpeDaCorsa INTEGER DEFAULT 0, " +
                "scarpeDaCalcio INTEGER DEFAULT 0, " +
                "costoCarrello DOUBLE DEFAULT 0,"+
                "FOREIGN KEY(idCliente) REFERENCES cliente(id))";
        stmt.executeUpdate(creaTabellaCarrello);

        
        String creaTabellaVenditore = "CREATE TABLE IF NOT EXISTS venditore (" +
        		"id INTEGER PRIMARY KEY AUTOINCREMENT, "+
        		"nome TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "prezzoScarpa DOUBLE NOT NULL) ";
        stmt.executeUpdate(creaTabellaVenditore);
        
        String creaTabellaMagazzino = "CREATE TABLE IF NOT EXISTS magazzino (" +
        		"id INTEGER PRIMARY KEY AUTOINCREMENT, "+
        		"idVenditore INTEGER NOT NULL,"+
        		"scarpeDaBallo INTEGER DEFAULT 0, " +
                "scarpeDaCorsa INTEGER DEFAULT 0, " +
                "scarpeDaCalcio INTEGER DEFAULT 0, "+
                "capienzaMagazzino INTEGER NOT NULL, "+
                "FOREIGN KEY (idVenditore) REFERENCES venditore(id))";
                
        stmt.executeUpdate(creaTabellaMagazzino);
        
        String creaTabellaRegistroFatturazioni = "CREATE TABLE IF NOT EXISTS registroFatturazioni (" +
        		"id INTEGER PRIMARY KEY AUTOINCREMENT, "+
        		"idVenditore INTEGER NOT NULL, "+
        		"data TEXT NOT NULL, "+
        		"importo DOUBLE NOT NULL,"+
        		"FOREIGN KEY(idVenditore) REFERENCES venditore(id))";
        stmt.executeUpdate(creaTabellaRegistroFatturazioni);
        
        String creaTabellaStoricoDelleTransazioni = "CREATE TABLE IF NOT EXISTS storicoDelleTransazioni( "+
        		"id INTEGER PRIMARY KEY AUTOINCREMENT,"+
        		"idCliente INTEGER NOT NULL,"+
        		"idCarrello INTEGER NOT NULL,"+
        		"idVenditore INTEGER NOT NULL,"+
        		"data TEXT NOT NULL, "+
        		"scarpeDaBallo INTEGER DEFAULT 0, "+
        		"scarpeDaCorsa INTEGER DEFAULT 0, " + 
        		"scarpeDaCalcio INTEGER DEFAULT 0, " +
        		"costoCarrello DOUBLE DEFAULT 0, "+
        		"FOREIGN KEY (idCliente) REFERENCES cliente(id), " +
        		"FOREIGN KEY (idCarrello) REFERENCES carrello(id), "+
				"FOREIGN KEY (idVenditore) REFERENCES venditore(id)) " ;
        stmt.executeUpdate(creaTabellaStoricoDelleTransazioni);
    }
    
    public static void rollback() throws SQLException {
        if (conn != null && !conn.getAutoCommit()) {
            conn.rollback();
        }
    }

    public static void close() throws SQLException {
        if (conn != null && !conn.isClosed()) {
            conn.close();
        }
    }
}
