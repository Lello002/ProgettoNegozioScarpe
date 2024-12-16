package businessLogic;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import domain.InvoiceRegister;
import domain.Seller;
import domain.WareHouse;
import orm.DBManager;
import orm.DaoCarrello;
import orm.DaoCliente;
import orm.DaoMagazzino;
import orm.DaoRegistroFatturazioni;
import orm.DaoStoricoDelleTransazioni;
import orm.DaoVenditore;

public class TestVenditore {
	
	private DBManager dbManager;
	private SellerApplication sellerApplication;
	private Seller seller;
	private WareHouse warehouse;
	private LocalDate date = LocalDate.now();
	
	@Before
    public void setUp() throws SQLException {
        dbManager = new DBManager();
        dbManager.connect();
        dbManager.createTables();

        sellerApplication = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
        sellerApplication.register("Pippo", "pippo@gmail.com", "Pippo!", 100, 15);
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
    public void testRegistrazioneEdAccessoDelVenditore() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");
    	
    	seller = sellerApplication.getSeller(sellerApplication.getIdSeller());
    	
    	assertThat(seller)
        	.extracting("name", "email", "password")
        	.containsExactly("Pippo", "pippo@gmail.com", "Pippo!");
    }
    
    @Test
    public void testAccediConDatiSbagliati() throws SQLException {
    	
    	assertThatThrownBy(() -> sellerApplication.login("Pippo", "Pippo"))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("Nome o password errati");
    }
    
    @Test
    public void testRicaricaMagazzino() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");

    	warehouse = sellerApplication.getWarehouse(sellerApplication.getIdWarehouse());
    	
    	assertThat(warehouse)
    		.extracting("dancingShoes", "runningShoes", "footballShoes")
    		.containsExactly(0, 0, 0);
    	
    	sellerApplication.refillWarehouse(10, 5, 15);
    	
    	warehouse = sellerApplication.getWarehouse(sellerApplication.getIdWarehouse());
    	
    	assertThat(warehouse)
			.extracting("dancingShoes", "runningShoes", "footballShoes")
			.containsExactly(10, 5, 15);
    }
    
    @Test
    public void testRicaricaMagazzinoConDatiErrati() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");

    	assertThatThrownBy(() -> sellerApplication.refillWarehouse(0, 0, -3))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("Errore nei dati");
    }
    
    @Test
    public void testCambiaPrezzoScarpa() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");

    	seller = sellerApplication.getSeller(sellerApplication.getIdSeller());
    	
    	assertThat(seller)
    		.extracting("name", "shoesPrice")
    		.containsExactly("Pippo", 15.0);
    	
    	sellerApplication.changeShoesPrice(20);
    	
    	seller = sellerApplication.getSeller(sellerApplication.getIdSeller());
    	
    	assertThat(seller)
		.extracting("name", "shoesPrice")
		.containsExactly("Pippo", 20.0);
    }
    
    @Test
    public void testCambiaPrezzoMinoreDiZero() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");

    	assertThatThrownBy(() -> sellerApplication.changeShoesPrice(-4))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("Il prezzo deve essere positivo");
    }
    
    @Test
    public void testAggiungiUnMagazzino() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");

    	assertThat(sellerApplication.getNumWarehouseOwn(sellerApplication.getIdSeller())).isEqualTo(1);
    	
    	sellerApplication.addWarehouse(200);
    	
    	assertThat(sellerApplication.getNumWarehouseOwn(sellerApplication.getIdSeller())).isEqualTo(2);
    }
    
    @Test
    public void testAggiungiUnMagazzinoConCapienzaNegativa() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");
    	
    	assertThatThrownBy(() -> sellerApplication.addWarehouse(-400))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("La capienza deve essere positiva");
    }
		
    @Test
    public void testMostraStoricoFatture() throws SQLException {
    	
    	LocalDate date = LocalDate.now();
    	
    	sellerApplication.login("Pippo", "Pippo!");
    	
    	ClientApplication clienteGestione = new ClientApplication(new DaoCliente(), new DaoCarrello(), new DaoStoricoDelleTransazioni(), new DaoRegistroFatturazioni());
    	
    	sellerApplication.refillWarehouse(10, 10, 10);
    	
    	clienteGestione.register("Pluto", "pluto@gmail.com", "Pluto!");
    	clienteGestione.login("Pluto", "Pluto!");
    	clienteGestione.refillBalance(500);
    	clienteGestione.editCart(5, 6, 4, sellerApplication.getIdSeller());
    	clienteGestione.buyCart();
    	
    	clienteGestione.register("Topolino", "topolino@gmail.com", "Topolino!");
    	clienteGestione.login("Topolino", "Topolino!");
    	clienteGestione.refillBalance(500);
    	clienteGestione.editCart(5, 4, 2, sellerApplication.getIdSeller());
    	clienteGestione.buyCart();
    	    	
    	sellerApplication.viewInvoiceRegister(date.minusDays(10), date);
    	
    	List<InvoiceRegister> list = sellerApplication.getList();
    	
    	 assertThat(list)
         .hasSize(3)
         .extracting("idSeller", "amount")
         .containsExactlyInAnyOrder(
             tuple(sellerApplication.getIdSeller(), -225.0),
             tuple(sellerApplication.getIdSeller(), 225.0),
             tuple(sellerApplication.getIdSeller(), 165.0));
    }
    
    @Test
    public void testMostraRegistroFatturazioniConDateInvertite() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");
    	
    	assertThatThrownBy(() -> sellerApplication.viewInvoiceRegister(date, date.minusDays(3)));
    }
    
    @Test
    public void cambiaMagazzinoControllato() throws SQLException {
    	
    	sellerApplication.login("Pippo", "Pippo!");

    	sellerApplication.addWarehouse(140);
    	
    	int id = sellerApplication.getIdWarehouse();
    	
    	assertThat(sellerApplication.getIdWarehouse()).isEqualTo(id);
    	
    	sellerApplication.changeIdWarehouse(sellerApplication.getIdSeller(), id);
    	assertThat(sellerApplication.getIdWarehouse()).isNotEqualTo(id);
    	
    }
}
