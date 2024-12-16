package businessLogic;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import domain.Cart;
import domain.Client;
import domain.TransactionHistory;
import orm.DBManager;
import orm.DaoCarrello;
import orm.DaoCliente;
import orm.DaoMagazzino;
import orm.DaoRegistroFatturazioni;
import orm.DaoStoricoDelleTransazioni;
import orm.DaoVenditore;

public class TestClient {

	private DBManager dbManager;
	private ClientApplication clientApplication;
	private Client client;
	private LocalDate date = LocalDate.now();

	
	@Before
    public void setUp() throws SQLException {
        dbManager = new DBManager();
        dbManager.connect();
        dbManager.createTables();

        clientApplication = new ClientApplication(new DaoCliente(), new DaoCarrello(), new DaoStoricoDelleTransazioni(), new DaoRegistroFatturazioni());
    	clientApplication.register("Mario", "mario@gmail.com", "Elefante!");

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
    public void testRegistrazioneEdAccessoDelCliente() throws SQLException {
    	
    	clientApplication.login("Mario", "Elefante!");
    	
    	client = clientApplication.getClient(clientApplication.getIdClient());
    	
    	assertThat(client)
        	.extracting("name", "email", "password")
        	.containsExactly("Mario", "mario@gmail.com", "Elefante!");
    }
    
    @Test
    public void testAccediConDatiSbagliati() throws SQLException {
    	
    	assertThatThrownBy(() -> clientApplication.login("Mario", "Elefante"))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("Nome o password errati");
    }
    
    @Test
    public void testRicaricaSaldo() throws SQLException {
    	
    	DaoCliente daoCliente = new DaoCliente();
    	
    	clientApplication.login("Mario", "Elefante!");

    	assertThat(daoCliente.getSaldo(clientApplication.getIdClient())).isEqualTo(0);
    	
    	clientApplication.login("Mario", "Elefante!");
    	clientApplication.refillBalance(10);
    	
    	assertThat(daoCliente.getSaldo(clientApplication.getIdClient())).isEqualTo(10);
    }
    
    @Test
    public void testRicaricaConSaldoNegativo() throws SQLException {
    	
    	clientApplication.login("Mario", "Elefante!");
    	
    	assertThatThrownBy(() -> clientApplication.refillBalance(0))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("La ricarica deve essere maggiore di zero");
    }
    
    @Test
    public void testModificaCarrello() throws SQLException {
    	
    	Cart cart;
    	SellerApplication sellerApplication = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	sellerApplication.register("Tommaso", "tommaso@gmail,com", "Ronaldo!", 100, 15);
    	sellerApplication.login("Tommaso", "Ronaldo!");
    	
    	clientApplication.login("Mario", "Elefante!");

    	clientApplication.editCart(10, 10, 10, sellerApplication.getIdSeller());
    	
    	cart = clientApplication.getCart(clientApplication.getIdCart());

    	assertThat(cart)
    		.extracting("dancingShoes", "runningShoes", "costCart")
    		.containsExactly(10, 10, 450.0);
    }
    
    @Test
    public void testModificaCarrelloConQuantitaNonValide() throws SQLException {

    	SellerApplication venditoreGestione = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	venditoreGestione.register("Tommaso", "tommaso@gmail,com", "Ronaldo!", 100, 15);
    	venditoreGestione.login("Tommaso", "Ronaldo!");
    	
    	clientApplication.login("Mario", "Elefante!");

    	assertThatThrownBy(() -> clientApplication.editCart(15, -10, 15, venditoreGestione.getIdSeller()))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("Modifica non valida");
    	
    }
    
    @Test
    public void testCompraCarrello() throws SQLException {
    	
    	Cart cart;
    	SellerApplication venditoreGestione = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	venditoreGestione.register("Tommaso", "tommaso@gmail,com", "Ronaldo!", 100, 15);
    	venditoreGestione.login("Tommaso", "Ronaldo!");
    	venditoreGestione.refillWarehouse(20, 20, 10);
    	
    	clientApplication.login("Mario", "Elefante!");

    	clientApplication.editCart(2, 3, 0, venditoreGestione.getIdSeller());
    	clientApplication.refillBalance(75);
    	
    	cart = clientApplication.getCart(clientApplication.getIdCart());
    	
    	assertThat(cart)
			.extracting("dancingShoes", "runningShoes", "costCart")
			.containsExactly(2, 3, 75.0);
    	
    	clientApplication.buyCart();

    	cart = clientApplication.getCart(clientApplication.getIdCart());
    	
    	assertThat(cart)
			.extracting("dancingShoes", "runningShoes", "costCart")
			.containsExactly(0, 0, 0.0);
    	
    }
    
    @Test
    public void testSaldoInsufficiente() throws SQLException {
    	
    	SellerApplication venditoreGestione = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	venditoreGestione.register("Tommaso", "tommaso@gmail,com", "Ronaldo!", 100, 15);
    	venditoreGestione.login("Tommaso", "Ronaldo!");
    	venditoreGestione.refillWarehouse(20, 20, 10);
    	
    	clientApplication.login("Mario", "Elefante!");
    	clientApplication.editCart(2, 3, 0, venditoreGestione.getIdSeller());
    	
    	assertThatThrownBy(() -> clientApplication.buyCart())
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("Saldo insufficiente");
    }
    
    @Test
    public void testCambiaVenditore() throws SQLException {
    	
    	Cart cart;
    	clientApplication.login("Mario", "Elefante!");

    	SellerApplication venditoreGestione = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	venditoreGestione.register("Tommaso", "tommaso@gmail,com", "Ronaldo!", 100, 15);
    	venditoreGestione.login("Tommaso", "Ronaldo!");
    	venditoreGestione.refillWarehouse(10, 12, 15);
    	
    	clientApplication.editCart(5, 5, 5, venditoreGestione.getIdSeller());
    	cart = clientApplication.getCart(clientApplication.getIdCart());
    	assertThat(cart.getCostCart()).isEqualTo(225.0);
    	
    	venditoreGestione.register("Francesco", "francesco@gmail,com", "Cristiano!", 200, 10);
    	venditoreGestione.login("Francesco", "Cristiano!");
    	venditoreGestione.refillWarehouse(20, 50, 30);
    	
    	clientApplication.changeSeller(venditoreGestione.getIdSeller());
    	cart = clientApplication.getCart(clientApplication.getIdCart());
    	assertThat(cart.getCostCart()).isEqualTo(150.0);
    	
    }
    	
    
    @Test
    public void testCambiaVenditoreInesistente() throws SQLException {
    	
    	clientApplication.login("Mario", "Elefante!");

    	assertThatThrownBy(() -> clientApplication.changeSeller(0))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("idVenditore invalido");
    	
    }
    
    @Test
    public void testMostraStoricoCarrelli() throws SQLException {
    	    	
    	clientApplication.login("Mario", "Elefante!");
    	SellerApplication venditoreGestione = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	venditoreGestione.register("Tommaso", "tommaso@gmail,com", "Ronaldo!", 100, 15);
    	venditoreGestione.login("Tommaso", "Ronaldo!");
    	venditoreGestione.refillWarehouse(20, 32, 45);
    	
    	clientApplication.refillBalance(1000);
    	clientApplication.editCart(3, 5, 10, venditoreGestione.getIdSeller());
    	
    	clientApplication.buyCart();
    	
    	clientApplication.editCart(5, 5, 5, venditoreGestione.getIdSeller());
    	clientApplication.buyCart();
    	clientApplication.showTransactionHistory(date.minusDays(10), date);
    	
    	List<TransactionHistory> lista = clientApplication.getTransactionHistory();
    	
    	 assertThat(lista)
         .hasSize(2)
         .extracting("dancingShoes", "costOfCart")
         .containsExactlyInAnyOrder(
             tuple(3, 270.0),
             tuple(5, 225.0));
    }
    
    @Test
    public void testMostraStoricoVuoto() throws SQLException {
    	
    	LocalDate date = LocalDate.now();
    	
    	clientApplication.login("Mario", "Elefante!");
    	SellerApplication venditoreGestione = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	venditoreGestione.register("Tommaso", "tommaso@gmail,com", "Ronaldo!", 100, 15);
    	venditoreGestione.login("Tommaso", "Ronaldo!");
    	venditoreGestione.refillWarehouse(20, 32, 45);
    	
    	clientApplication.refillBalance(1000);
    	clientApplication.editCart(3, 5, 10, venditoreGestione.getIdSeller());
    	
    	clientApplication.buyCart();
    	
    	clientApplication.editCart(5, 5, 5, venditoreGestione.getIdSeller());
    	clientApplication.buyCart();

    	assertThatThrownBy(() -> clientApplication.showTransactionHistory(date.minusDays(10), date.minusDays(3)))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("Nessuna transazione trovata per il cliente con ID: 1 nel periodo da " + date.minusDays(10) + " a " + date.minusDays(3));
    }
    
    @Test
    public void testMostraStoricoDataFinePrecedenteDataInizio() throws SQLException {
    	LocalDate date = LocalDate.now();
    	
    	clientApplication.login("Mario", "Elefante!");
    	SellerApplication venditoreGestione = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	venditoreGestione.register("Tommaso", "tommaso@gmail,com", "Ronaldo!", 100, 15);
    	venditoreGestione.login("Tommaso", "Ronaldo!");
    	venditoreGestione.refillWarehouse(20, 32, 45);
    	
    	clientApplication.refillBalance(1000);
    	clientApplication.editCart(3, 5, 10, venditoreGestione.getIdSeller());
    	
    	clientApplication.buyCart();
    	
    	clientApplication.editCart(5, 5, 5, venditoreGestione.getIdSeller());
    	clientApplication.buyCart();

    	assertThatThrownBy(() -> clientApplication.showTransactionHistory(date.minusDays(3), date.minusDays(10)))
    						.isInstanceOf(IllegalArgumentException.class)
    						.hasMessage("La data di inizio deve essere precedente o uguale alla data di fine.");
    }
    
    @Test
    public void testUsoCorrettoDelApplicazione() throws SQLException {
    	
    	SellerApplication venditoreGestione = new SellerApplication(new DaoVenditore(), new DaoMagazzino(), new DaoRegistroFatturazioni());
    	venditoreGestione.register("Mario", "mario@gmail,com", "Messi!", 100, 15);
    	venditoreGestione.login("Mario", "Messi!");
    	venditoreGestione.refillWarehouse(20, 10, 50);
    	
    	clientApplication.register("Antonio", "antonio@gmail.com", "Antonio!");
    	clientApplication.login("Antonio", "Antonio!");
    	
    	clientApplication.refillBalance(100);
    	
    	clientApplication.editCart(3, 0, 1, venditoreGestione.getIdSeller());
    	
    	clientApplication.buyCart();
    	
    	venditoreGestione.register("Franco", "franco@gmail,com", "Franchino!", 1000, 10);
    	venditoreGestione.login("Franco", "Franchino!");
    	venditoreGestione.refillWarehouse(50, 30, 50);
    	
    	clientApplication.changeSeller(venditoreGestione.getIdSeller());
    	clientApplication.editCart(1, 0, 0, venditoreGestione.getIdSeller());
    	clientApplication.buyCart();
    	
    	clientApplication.showTransactionHistory(date.minusDays(5), date);
    	
    	client = clientApplication.getClient(clientApplication.getIdClient());
    	
    	List<TransactionHistory> list = clientApplication.getTransactionHistory();
    	
    	assertThat(client)
    		.extracting("name", "email", "password", "balance")
    		.containsExactly("Antonio", "antonio@gmail.com", "Antonio!", 30.0);
    	
    	assertThat(list)
        .hasSize(2)
        .extracting("dancingShoes", "footbalShoes", "costOfCart")
        .containsExactlyInAnyOrder(
            tuple(3, 1, 60.0),
            tuple(1, 0, 10.0));
    }
    
    
}
