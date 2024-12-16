package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import domain.Seller;

public class TestDaoVenditore {
	
	private DBManager dbManager;
	private DaoVenditore daoVenditore;
	private Seller venditore;
	private int idVenditore = 0;
	
	 @Before
	 public void setUp() throws SQLException {
        dbManager = new DBManager();
        dbManager.connect();
        dbManager.createTables();
	        
        daoVenditore = new DaoVenditore();
        daoVenditore.inserisciVenditore("Paolo", "paolo@gmail.com", "Italia!", 15);
	        
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
    public void testCorrettoInserimentoVenditoreNelDatabase() throws SQLException {
    	idVenditore = daoVenditore.accediAccount("Paolo", "Italia!");
    	venditore = daoVenditore.creaVenditore(idVenditore);
    	
        assertThat(venditore)
            .extracting("name", "email", "password", "shoesPrice")
            .containsExactly("Paolo", "paolo@gmail.com", "Italia!", 15.0);
    }
    
    @Test
    public void testAccediAccount() throws SQLException {
    	
    	assertThat(idVenditore).isEqualTo(0);
    	
    	idVenditore = daoVenditore.accediAccount("Paolo", "Italia!");
    	
    	assertThat(idVenditore).isEqualTo(1);
    }
    
    @Test
    public void testAccediConDatiErrati() {
    	
    	assertThatThrownBy(()->daoVenditore.accediAccount("Paolo", "Italia"))
			.isInstanceOf(IllegalArgumentException.class)
			.hasMessage("Nome o password errati");

    	assertThatThrownBy(()->daoVenditore.accediAccount("Paoo", "Italia!"))
    		.isInstanceOf(IllegalArgumentException.class)
    		.hasMessage("Nome o password errati");
    }
    
    @Test
    public void testChangePrice() throws SQLException {
    	
    	idVenditore = daoVenditore.accediAccount("Paolo", "Italia!");
    	venditore = daoVenditore.creaVenditore(idVenditore);
    	
    	assertThat(venditore.getShoesPrice()).isEqualTo(15);
    	
    	daoVenditore.cambiaPrezzo(idVenditore, 20);
    	venditore = daoVenditore.creaVenditore(idVenditore);

    	assertThat(venditore.getShoesPrice()).isEqualTo(20);
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    
    

}
