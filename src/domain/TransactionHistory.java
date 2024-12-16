	package domain;

import java.time.LocalDate;



public class TransactionHistory {
	
	private final int id;
	private final int idClient;
	private final int idSeller;
	private final LocalDate date;
	private int dancingShoes;
	private int runningShoes;
	private int footballShoes;
	private double costOfCart;
	private final int idCart;
	
	public TransactionHistory(int id, int idCliente, int idVenditore, LocalDate data, int scarpeDaBallo,
			int scarpeDaCorsa, int scarpeDaCalcio, double costoCarrello, int idCarrello) {
		this.id = id;
		this.idClient = idCliente;
		this.idSeller = idVenditore;
		this.date = data;
		this.dancingShoes = scarpeDaBallo;
		this.runningShoes = scarpeDaCorsa;
		this.footballShoes = scarpeDaCalcio;
		this.costOfCart = costoCarrello;
		this.idCart = idCarrello;
	}

	public int getId() {
		return id;
	}

	public LocalDate getDate() {
		return date;	
	}

	public int getDancingShoes() {
		return dancingShoes;
	}

	public int getRunningShoes() {
		return runningShoes;
	}

	public int getFootbalShoes() {
		return footballShoes;
	}

	public double getCostOfCart() {
		return costOfCart;
	}

	public int getidCart() {
		return idCart;
	}
	
	public int getIdClient() {
		return idClient;
	}
	
	public int getIdSeller() {
		return idSeller;
	}
}
