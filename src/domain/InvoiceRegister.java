package domain;

import java.time.LocalDate;


public class InvoiceRegister {
	
	private final int id;
	private final int idSeller;
	private final LocalDate data;
	private final double amount;
	
	public InvoiceRegister(int id, LocalDate data, double amount, int idSeller) {
		this.id = id;
		this.idSeller = idSeller;
		this.data = data;
		this.amount = amount;
		
	}

	public int getId() {
		return id;
	}
	
	public int getidSeller() {
		return idSeller;
	}

	public LocalDate getDate() {
		return data;
	}

	public double getAmount() {
		return amount;
	}
	
	
	
	
	

}
