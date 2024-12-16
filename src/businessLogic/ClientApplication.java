package businessLogic;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

import controlPassword.ControlPassword;
import controlPassword.ControllerContainsCapitalLetters;
import controlPassword.ControllerContainsSpecialCharacters;
import controlPassword.ControllerContainsWhitespaces;
import domain.Cart;
import domain.Client;
import domain.TransactionHistory;
import orm.DaoCarrello;
import orm.DaoCliente;
import orm.DaoRegistroFatturazioni;
import orm.DaoStoricoDelleTransazioni;

public class ClientApplication{
	
	private DaoCliente daoClient;
	private int idClient;
	private DaoCarrello daoCart;
	private int idCart;
	private DaoStoricoDelleTransazioni daoTransactionHistory;
	private DaoRegistroFatturazioni daoInvoiceRegister;
	
	private LocalDate date = LocalDate.now();
	private int idSeller;
    private List<TransactionHistory> transtactionHistoryList;
    
    private ControlPassword controlPassword  = new ControllerContainsCapitalLetters(new ControllerContainsSpecialCharacters(new ControllerContainsWhitespaces(null)));
    
	public ClientApplication(DaoCliente daoCliente,DaoCarrello daoCarrello, DaoStoricoDelleTransazioni daoStoricoDelleTransazioni, DaoRegistroFatturazioni daoRegistroFatturazioni) {
		this.daoClient = daoCliente;
		this.daoCart = daoCarrello;
		this.daoTransactionHistory = daoStoricoDelleTransazioni;
		this.daoInvoiceRegister = daoRegistroFatturazioni;
	}
	
	public void register(String nome, String email, String password) throws SQLException {
		
		if(controlPassword.control(password))
			throw new IllegalArgumentException("Password debole");
		
		int idClient = daoClient.inserisciCliente(nome, email, password);
		
		daoCart.inserisciCarrello(idClient);
		
	}
	
	
	public void login(String nome, String password) throws SQLException {
		
		idClient = daoClient.accediAccount(nome, password);
		idCart = daoCart.ritornaID(idClient);
	}
	
	
	public void refillBalance(double amount) throws SQLException {
		
		if(amount<=0)
			throw new IllegalArgumentException("La ricarica deve essere maggiore di zero");
		
		daoClient.ricaricaAccount(idClient, amount);
	}
	
	
	public void editCart(int qtaScarpeDaBallo,int qtaScarpeDaCorsa, int qtaScarpeDaCalcio, int idVenditore ) throws SQLException {
		
		if(qtaScarpeDaBallo<0 || qtaScarpeDaCorsa<0 || qtaScarpeDaCalcio<0) 
			throw new IllegalArgumentException("Modifica non valida");
		
		this.idSeller = idVenditore;
		daoCart.aggiornaCarrello(idClient, qtaScarpeDaBallo, qtaScarpeDaCorsa, qtaScarpeDaCalcio, idVenditore);
	}
	
	public void buyCart() throws SQLException {
		double cost = daoCart.getCostoCarrello(idCart);
		
		if(daoClient.getSaldo(idClient)<cost)
			throw new IllegalArgumentException("Saldo insufficiente");
		
		daoTransactionHistory.inserisciStoricoDelleTransazioni(date, daoCart.getScarpeDaBallo(idCart), daoCart.getScarpeDaCorsa(idCart), daoCart.getScarpeDaCalcio(idCart), cost, idCart, idClient, idSeller);
		daoClient.paga(idClient, cost);
		
		daoClient.togliDalMagazzinoLaMerceVenduta(idSeller, daoCart.getScarpeDaBallo(idCart), daoCart.getScarpeDaCorsa(idCart), daoCart.getScarpeDaCalcio(idCart));
		
		daoInvoiceRegister.inserisciRegistroFatturazioni(idSeller, date, cost);
		daoCart.eliminaCarrello(idCart);
		daoCart.inserisciCarrello(idClient);
		idCart = daoCart.ritornaID(idClient);
	}
	
	public void changeSeller(int idNewSeller) throws SQLException {
		
		if(idNewSeller <= 0)
			throw new IllegalArgumentException("idVenditore invalido");
		
		daoCart.aggiornaCarrello(idClient, daoCart.getScarpeDaBallo(idCart), daoCart.getScarpeDaCorsa(idCart), daoCart.getScarpeDaCalcio(idCart), idNewSeller);
	}
	
	
	public void showTransactionHistory(LocalDate da, LocalDate finoAl) throws SQLException {
	    if (da.isAfter(finoAl))
	        throw new IllegalArgumentException("La data di inizio deve essere precedente o uguale alla data di fine.");
	    
	    
	    transtactionHistoryList = daoTransactionHistory.mostraStorico(idClient, da, finoAl);
	}

	
	public Client getClient(int idClient) throws SQLException {
		return daoClient.creaCliente(idClient);
	}
	
	public Cart getCart(int idCart) throws SQLException {
		return daoCart.ritornaCarrello(idCart);
	}
	
	public List<TransactionHistory> getTransactionHistory() {
		return transtactionHistoryList;
	}
	
	public int getIdClient() {
		return idClient;
	}
	
	public int getIdCart() {
		return idCart;
	}
}