package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import domain.Client;

public class TestDaoCliente {
	
	private DBManager dbManager;
	private DaoCliente daoCliente;
	private Client cliente;
	
	private int idCliente;
	
	@Before
	public void setUp() throws SQLException {
		dbManager = new DBManager();
		dbManager.connect();
		dbManager.createTables();
		
		daoCliente = new DaoCliente();
		daoCliente.inserisciCliente("Andrea", "andrea@gmail.com", "Casa!");
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
	public void testAccediConDatiValidi() throws SQLException {
		
		assertThat(idCliente).isEqualTo(0);
		
		idCliente = daoCliente.accediAccount("Andrea", "Casa!");

		assertThat(idCliente).isNotEqualTo(0);
	}
	
	@Test
	public void testAccediConDatiNonValidi() throws SQLException {

		assertThatThrownBy(()->daoCliente.accediAccount("Andrea", "casa!"))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Nome o password errati");
		
		assertThatThrownBy(()->daoCliente.accediAccount("Andre", "Casa!"))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Nome o password errati");
	}
	
	@Test
	public void testInserimentoCorretto() throws SQLException {
				
		idCliente = daoCliente.accediAccount("Andrea", "Casa!");
		cliente = daoCliente.creaCliente(idCliente);
				
		assertThat(cliente)
        .extracting("name", "email", "password")
        .containsExactly("Andrea", "andrea@gmail.com", "Casa!");

	}
	
	@Test
	public void testRicaricaCorrettamenteAccount() throws SQLException {
		
		idCliente = daoCliente.accediAccount("Andrea", "Casa!");
		assertThat(daoCliente.getSaldo(idCliente)).isEqualTo(0);
		
		daoCliente.ricaricaAccount(1, 10);
		
		assertThat(daoCliente.getSaldo(idCliente)).isEqualTo(10.0);
	}
	
	@Test
	public void testRicaricaIDNonTrovato() throws SQLException {
		
		assertThatThrownBy(()->daoCliente.ricaricaAccount(0, 10))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Cliente con ID 0 non trovato.");
	}
	
	@Test
	public void testPagamento() throws SQLException {
		
		idCliente = daoCliente.accediAccount("Andrea", "Casa!");
		
		daoCliente.ricaricaAccount(idCliente, 20);
		assertThat(daoCliente.getSaldo(idCliente)).isEqualTo(20);
		
		daoCliente.paga(idCliente, 10);
		assertThat(daoCliente.getSaldo(idCliente)).isEqualTo(10);
	}
	
	@Test
	public void testPagamentoIDInesistente() throws SQLException {

		assertThatThrownBy(() -> daoCliente.paga(0, 1))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Cliente con ID 0 non trovato.");
	}
	
	@Test
	public void testTogliLaMerceDalMagazzino() throws SQLException {
		
		DaoMagazzino daoMagazzino = new DaoMagazzino();
		daoMagazzino.inserisciMagazzino(1, 50);
		int idMagazzino = daoMagazzino.ritornaID(1);
		daoMagazzino.ricaricaMagazzino(1 ,10, 10, 10, idMagazzino);
		
		daoCliente.togliDalMagazzinoLaMerceVenduta(1, 2, 3, 5);
		
		assertThat(daoMagazzino.getScarpeDaBallo(idMagazzino)).isEqualTo(8);
		assertThat(daoMagazzino.getScarpeDaCorsa(idMagazzino)).isEqualTo(7);
		assertThat(daoMagazzino.getScarpeDaCalcio(idMagazzino)).isEqualTo(5);

		
	}
	
	@Test
	public void testMerceInsufficienteNelMagazzinoPerCompletareAcquisto() throws SQLException {
		
		DaoMagazzino daoMagazzino = new DaoMagazzino();
		daoMagazzino.inserisciMagazzino(1, 50);
		int idMagazzino = daoMagazzino.ritornaID(1);
		daoMagazzino.ricaricaMagazzino(1 ,0, 10, 10, idMagazzino);
		
		assertThatThrownBy(() -> daoCliente.togliDalMagazzinoLaMerceVenduta(1, 2, 3, 5))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Errore: quantità insufficienti nel magazzino per completare la vendita.");
	}
	
	@Test
	public void testTogliMerceDaMagazzinoInesistente() {
		
		assertThatThrownBy(() -> daoCliente.togliDalMagazzinoLaMerceVenduta(0, 0, 0, 0))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Nessun magazzino trovato per il venditore con ID: 0");
	}
}
