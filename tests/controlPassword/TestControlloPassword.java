package controlPassword;

import static org.assertj.core.api.Assertions.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import businessLogic.ClientApplication;
import orm.DBManager;
import orm.DaoCarrello;
import orm.DaoCliente;
import orm.DaoRegistroFatturazioni;
import orm.DaoStoricoDelleTransazioni;


public class TestControlloPassword {
	
	private DBManager dbManager;
	private ClientApplication client;
	
	@Before
    public void setUp() throws SQLException {
        dbManager = new DBManager();
        dbManager.connect();
        dbManager.createTables();

        client = new ClientApplication(new DaoCliente(), new DaoCarrello(), new DaoStoricoDelleTransazioni(), new DaoRegistroFatturazioni());
        client.register("Franco", "franco@gmail.com", "Season!");
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
	public void testCaratteriSpeciali() {

		ControlPassword controlloPassword = new ControllerContainsSpecialCharacters(null);
		
		assertTrue(controlloPassword.control("aa"));
		assertFalse(controlloPassword.control("a!a"));		
	}

	@Test
	public void testControllaPresenzaSpazio(){

		ControlPassword controlloPassword = new ControllerContainsWhitespaces(null);

		assertTrue(controlloPassword.control("ci ao"));
		assertFalse(controlloPassword.control("ciao"));
	}

	@Test
	public void testControllaLetteraMaiuscola() {

		ControlPassword controlloPassword = new ControllerContainsCapitalLetters(null);

		assertTrue(controlloPassword.control("ciao"));
		assertFalse(controlloPassword.control("Ciao"));
	}

	@Test
	public void testChainOfResposibility() {

		ControlPassword controlloPassword = new ControllerContainsCapitalLetters(new ControllerContainsWhitespaces(new ControllerContainsSpecialCharacters(null)));

		assertTrue(controlloPassword.control("Ciao"));		
		assertTrue(controlloPassword.control("ciao"));
		assertTrue(controlloPassword.control("ciao?"));
		assertTrue(controlloPassword.control("Ci ao?"));
		assertFalse(controlloPassword.control("Ciao?"));

	}
	
	@Test
	public void testAccediCorrettamente() throws SQLException {
        client.register("Franco", "franco@gmail.com", "Season!");

		client.login("Franco", "Season!");
		
		assertThat(client.getIdClient()).isEqualTo(1);
		assertThat(client.getIdCart()).isEqualTo(1);
	}
	
	@Test
	public void testPasswordDebole(){

		assertThatThrownBy(() -> client.register("Franco", "franco@gmail.com", "Se ason!"))
							.isInstanceOf(IllegalArgumentException.class)
							.hasMessage("Password debole");
	}
	
	
	

}
