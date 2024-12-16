package businessLogic;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import controlPassword.ControlPassword;
import controlPassword.ControllerContainsCapitalLetters;
import controlPassword.ControllerContainsSpecialCharacters;
import controlPassword.ControllerContainsWhitespaces;
import domain.InvoiceRegister;
import domain.Seller;
import domain.WareHouse;
import orm.DaoCarrello;
import orm.DaoMagazzino;
import orm.DaoRegistroFatturazioni;
import orm.DaoVenditore;

public class SellerApplication{
	
	private DaoVenditore daoSeller;
	private int idSeller;
	private DaoMagazzino daoWarehouse;
	private int idWarehouse;
	private DaoRegistroFatturazioni daoInvoiceRegister;
	
	private List<InvoiceRegister> list;
	
	private LocalDate date = LocalDate.now();

    private ControlPassword controlPassword  = new ControllerContainsCapitalLetters(new ControllerContainsSpecialCharacters(new ControllerContainsWhitespaces(null)));
    
	
	public SellerApplication(DaoVenditore daoVenditore,DaoMagazzino daoMagazzino, DaoRegistroFatturazioni daoRegistroFatturazioni) {
		this.daoSeller = daoVenditore;
		this.daoWarehouse = daoMagazzino;
		this.daoInvoiceRegister = daoRegistroFatturazioni;
	}
	
	public void register(String nome, String email, String password, int capienzaMagazzino, double prezzoScarpa) throws SQLException {
		
		if(controlPassword.control(password))
			throw new IllegalArgumentException("Password debole");
		
		int idVenditore = daoSeller.inserisciVenditore(nome, email, password, prezzoScarpa);
		
		daoWarehouse.inserisciMagazzino(idVenditore, capienzaMagazzino);
		
	}
	
	public void login(String nome, String password) throws SQLException {
		
		idSeller = daoSeller.accediAccount(nome, password);
		idWarehouse = daoWarehouse.ritornaID(idSeller);
	}
	
	public void refillWarehouse(int scarpeDaBallo, int scarpeDaCorsa, int scarpeDaCalcio) throws SQLException {
		
		if(scarpeDaBallo<0 || scarpeDaCorsa<0 || scarpeDaCalcio<0)
			throw new IllegalArgumentException("Errore nei dati");
		
		daoWarehouse.ricaricaMagazzino(idSeller, scarpeDaBallo, scarpeDaCorsa, scarpeDaCalcio, idWarehouse);
				
		double totDaPagare = (scarpeDaBallo + scarpeDaCorsa + scarpeDaCalcio)*(DaoCarrello.getPrezzoScarpa(idSeller)/2);
		
		daoInvoiceRegister.inserisciRegistroFatturazioni(idSeller, date, -totDaPagare);
	}
	
	public void changeShoesPrice(double newPrice) throws SQLException {
		
		if(newPrice <= 0)
			throw new IllegalArgumentException("Il prezzo deve essere positivo");
		
		daoSeller.cambiaPrezzo(idSeller, newPrice);
	}
	
	public void addWarehouse(int warehouseCapacity) throws SQLException {
		
		if(warehouseCapacity <= 0)
			throw new IllegalArgumentException("La capienza deve essere positiva");
		
		daoWarehouse.inserisciMagazzino(idSeller, warehouseCapacity);
	}
	
	public void changeIdWarehouse(int idVenditore, int idMagazzino) throws SQLException {
		
		this.idWarehouse = daoWarehouse.trovaAltroIDMagazzino(idVenditore, idMagazzino);
		
	}
	
	public void viewInvoiceRegister(LocalDate da, LocalDate finoAl) throws SQLException {
	    if (da.isAfter(finoAl)) {
	        throw new IllegalArgumentException("La data di inizio deve essere precedente o uguale alla data di fine.");
	    }
	    
	    list = daoInvoiceRegister.stampaRegistroFatturazioni(idSeller).stream()
	        .filter(f -> !f.getDate().isBefore(da) && !f.getDate().isAfter(finoAl))
	        .collect(Collectors.toList());
	}

	public Seller getSeller(int idVenditore) throws SQLException {
		return daoSeller.creaVenditore(idVenditore);
	}
	
	public WareHouse getWarehouse(int idMagazzino) throws SQLException {
		return daoWarehouse.creaMagazzino(idMagazzino);
	}
	
	public int getNumWarehouseOwn(int idVenditore) throws SQLException {
		return daoWarehouse.contaMagazziniPerVenditore(idVenditore);
	}
	
	public List<InvoiceRegister> getList() {
		return list;
	}
	
	int getIdSeller() {
		return idSeller;
	}
	
	int getIdWarehouse() {
		return idWarehouse;
	}
	
}