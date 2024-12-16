package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import domain.WareHouse;


public class TestDaoMagazzino {
	
	private DBManager dbManager;
	private DaoMagazzino daoMagazzino;
	private WareHouse magazzino;
	private int idMagazzino;
	
	@Before
	public void setUp() throws SQLException {
		dbManager = new DBManager();
		dbManager.connect();
		dbManager.createTables();
		
		daoMagazzino = new DaoMagazzino();
		
		daoMagazzino.inserisciMagazzino(1, 100);
	}
	
	@After
	public void rollback() throws SQLException {
		DBManager.rollback();
	}
	
	@AfterClass
	public static void closeDB() throws SQLException {
		DBManager.close();
	}
	
	@Test
	public void testCorrettoInserimentoMagazzinoNelDB() throws SQLException {

		idMagazzino = daoMagazzino.ritornaID(1);
		
		magazzino = daoMagazzino.creaMagazzino(idMagazzino);
		
		assertThat(magazzino)
			.extracting("idSeller", "dancingShoes", "runningShoes", "capacity")
			.containsExactly(1, 0, 0, 100);
	}
	
	@Test
	public void testCreaMagazzino() throws SQLException {
		
		idMagazzino = daoMagazzino.ritornaID(1);
		
		assertThat(magazzino).isNull();
		
		magazzino = daoMagazzino.creaMagazzino(idMagazzino);
		
		assertThat(magazzino).isNotNull();
		
	}
	
	@Test
	public void testCreazioneMagazzinoIDErrato() {
		
		assertThatThrownBy(() -> daoMagazzino.creaMagazzino(0))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Magazzino con ID 0 non trovato.");
	}

	@Test
	public void testRicaricaMagazzino() throws SQLException {

		idMagazzino = daoMagazzino.ritornaID(1);
		
		daoMagazzino.ricaricaMagazzino(1, 5, 6, 8, idMagazzino);
		
		magazzino = daoMagazzino.creaMagazzino(idMagazzino);
		
		assertThat(magazzino)
			.extracting("idSeller", "dancingShoes", "runningShoes", "capacity")
			.containsExactly(1, 5, 6, 100);
	}
	

	@Test
	public void testSuperaSogliaMassimaCapienzaMagazzino() throws SQLException {
		
		idMagazzino = daoMagazzino.ritornaID(1);
		
		assertThatThrownBy(() -> daoMagazzino.ricaricaMagazzino(1, 40, 40, 30, idMagazzino))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Errore: la somma delle scarpe ricaricate supera la capienza del magazzino.");
		
	}
	
	
	@Test
	public void testRicaricaMagazzinoIDNonEsistente() {

		assertThatThrownBy(() -> daoMagazzino.ricaricaMagazzino(1, 3, 4, 5, 0))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Nessun magazzino trovato per il venditore con ID: 1");
	}
	
	
}
