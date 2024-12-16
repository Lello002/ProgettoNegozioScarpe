package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import domain.Cart;
import domain.Seller;

public class TestDaoCarrello {

    private DBManager dbManager;
    private DaoCarrello daoCarrello;
    private Cart carrello;
    private DaoVenditore daoVenditore;
    private Seller venditore;

    @Before
    public void setUp() throws SQLException {
        dbManager = new DBManager();
        dbManager.connect();
        dbManager.createTables();

        daoCarrello = new DaoCarrello();
        daoVenditore = new DaoVenditore();
        venditore = new Seller(1, "Franco", "Franco@gmail.com", "Password!", 2);

        daoVenditore.inserisciVenditore(
            venditore.getName(), 
            venditore.getemailSeller(), 
            venditore.getPassword(), 
            venditore.getShoesPrice()
        );
        
        daoCarrello.inserisciCarrello(1); 
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
    public void testCorrettoInserimentoCarrelloNelDatabase() throws SQLException {
        carrello = daoCarrello.ritornaCarrello(daoCarrello.ritornaID(1));

        assertThat(carrello)
            .extracting("idClient", "dancingShoes", "runningShoes", "footballShoes", "costCart")
            .containsExactly(1, 0, 0, 0, 0.0);
    }

    @Test
    public void testInserimentoCarrelloConIDClienteErrato() {
        assertThatThrownBy(() -> daoCarrello.ritornaID(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nessun carrello trovato per idCliente: 0");
    }

    @Test
    public void testAggiornamentoContenutoCarrello() throws SQLException {
        daoCarrello.aggiornaCarrello(1, 3, 4, 3, 1);
        carrello = daoCarrello.ritornaCarrello(daoCarrello.ritornaID(1));

        assertThat(carrello)
            .extracting("dancingShoes", "runningShoes", "footballShoes", "costCart")
            .containsExactly(3, 4, 3, 20.0);
    }

    @Test
    public void testAggiornamentoConIDClienteErrato() {
        assertThatThrownBy(() -> daoCarrello.aggiornaCarrello(0, 3, 4, 3, 1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nessun carrello trovato per idCliente: 0");
    }

    @Test
    public void testCostoCarrelloDopoAggiornamento() throws SQLException {
        daoCarrello.aggiornaCarrello(1, 5, 6, 5, 1);

        double costoCarrello = daoCarrello.getCostoCarrello(1);
        assertThat(costoCarrello).isEqualTo(32.0);
    }

    @Test
    public void testEliminazioneCarrello() throws SQLException {
        daoCarrello.eliminaCarrello(1);

        assertThatThrownBy(() -> daoCarrello.getCostoCarrello(1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Carrello con ID 1 non trovato.");
    }

    @Test
    public void testEliminazioneCarrelloInesistente() {
        assertThatThrownBy(() -> daoCarrello.eliminaCarrello(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Carrello con ID 0 non trovato.");
    }
    
    @Test
    public void testGetCostoCarrelloMaConIDCarrelloSbagliato() {
        assertThatThrownBy(() -> daoCarrello.getCostoCarrello(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Carrello con ID 0 non trovato.");
    }

    @Test
    public void testCostoCarrelloDopoModificaContenuto() throws SQLException {
        daoCarrello.aggiornaCarrello(1, 2, 5, 4, 1);
        assertThat(daoCarrello.getCostoCarrello(1)).isEqualTo(22.0);

        daoCarrello.aggiornaCarrello(1, 5, 5, 0, 1);
        assertThat(daoCarrello.getCostoCarrello(1)).isEqualTo(20.0);
    }

}
