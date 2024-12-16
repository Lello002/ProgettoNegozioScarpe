package orm;

import static org.assertj.core.api.Assertions.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import domain.TransactionHistory;

public class TestDaoStoricoTransazioni {

    private DBManager dbManager;
    private DaoStoricoDelleTransazioni daoStoricoDelleTransazioni;
    private List<TransactionHistory> storicoDelleTransazioni;
    private LocalDate date;

    @Before
    public void setUp() throws SQLException {
        dbManager = new DBManager();
        dbManager.connect();
        dbManager.createTables();
        
        daoStoricoDelleTransazioni = new DaoStoricoDelleTransazioni();
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
    public void testInserimentoCorrettoDellaTransazioneNelDB() throws SQLException {
        daoStoricoDelleTransazioni.inserisciStoricoDelleTransazioni(date, 5, 5, 5, 20, 1, 1, 1);
        storicoDelleTransazioni = daoStoricoDelleTransazioni.mostraStorico(1, date.minusDays(7), date);

        assertThat(storicoDelleTransazioni)
            .isNotEmpty()
            .hasSize(1)
            .extracting("idCart", "date", "costOfCart")
            .containsExactly(tuple(1, date, 20.0));
    }

    @Test
    public void testInserimentoDiPiuTransazioni() throws SQLException {
        daoStoricoDelleTransazioni.inserisciStoricoDelleTransazioni(date, 5, 5, 5, 20, 4, 1, 4);
        daoStoricoDelleTransazioni.inserisciStoricoDelleTransazioni(date.minusDays(7), 5, 5, 5, 20, 1, 1, 2);

        storicoDelleTransazioni = daoStoricoDelleTransazioni.mostraStorico(1, date.minusDays(5), date);
        assertThat(storicoDelleTransazioni)
            .hasSize(1)
            .extracting("idCart")
            .containsExactly(4);

        storicoDelleTransazioni = daoStoricoDelleTransazioni.mostraStorico(1, date.minusDays(10), date);
        assertThat(storicoDelleTransazioni)
            .hasSize(2)
            .extracting("idCart", "date")
            .containsExactlyInAnyOrder(
                tuple(4, date),
                tuple(1, date.minusDays(7))
            );
    }

    @Test
    public void testStampaVuota() throws SQLException {
        daoStoricoDelleTransazioni.inserisciStoricoDelleTransazioni(date.minusDays(3), 5, 5, 5, 20, 4, 1, 4);
        daoStoricoDelleTransazioni.inserisciStoricoDelleTransazioni(date.minusDays(7), 5, 5, 5, 20, 1, 1, 2);

        assertThatThrownBy(() -> daoStoricoDelleTransazioni.mostraStorico(1, date.minusDays(2), date))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Nessuna transazione trovata per il cliente con ID: 1 nel periodo da " + date.minusDays(2) + " a " + date);
    }
}
