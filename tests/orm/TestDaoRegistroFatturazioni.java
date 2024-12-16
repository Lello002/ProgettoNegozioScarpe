package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import domain.InvoiceRegister;

public class TestDaoRegistroFatturazioni {

	private DBManager dbManager;
	private DaoRegistroFatturazioni daoRegistroFatturazioni;
	private List<InvoiceRegister> listaTransazioni;
	private LocalDate date;
	
	@Before
	public void setUp() throws SQLException {
		dbManager = new DBManager();
		dbManager.connect();
		dbManager.createTables();
		
		daoRegistroFatturazioni = new DaoRegistroFatturazioni();
		date = LocalDate.now();
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
	public void testCorrettoInserimentoNelDBPiuFatture() throws SQLException {
	    
	    daoRegistroFatturazioni.inserisciRegistroFatturazioni(1, date, 10);
	    daoRegistroFatturazioni.inserisciRegistroFatturazioni(1, date.minusDays(1), 15);
	    
	    listaTransazioni = daoRegistroFatturazioni.stampaRegistroFatturazioni(1);
	    
	    assertThat(listaTransazioni.size()).isEqualTo(2);
	    
	    assertThat(listaTransazioni)
	        .extracting("amount") 
	        .containsExactlyInAnyOrder(10.0, 15.0);
	    
	    assertThat(listaTransazioni)
	        .extracting("date")
	        .containsExactlyInAnyOrder(date, date.minusDays(1));
	    
	    assertThat(listaTransazioni)
	        .allMatch(transazione -> transazione.getidSeller() == 1);
	}
	
	@Test
	public void testScorrettoInserimento() {
		assertThatThrownBy(() -> daoRegistroFatturazioni.stampaRegistroFatturazioni(1))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Nessuna fatturazione trovata per il venditore con ID: 1");
	}

	
	
	
	
}
