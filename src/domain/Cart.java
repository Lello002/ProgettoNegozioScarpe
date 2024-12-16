package domain;

public class Cart {
	
	private final int id;
	private final int idClient;
	private int dancingShoes;
	private int runningShoes;
	private int footballShoes;
	private double costCart;
	
	public Cart(int id, int dancingShoes, int runningShoes, int footballShoes, int idCliente, double costCart) {
		this.id = id;
		this.dancingShoes = dancingShoes;
		this.runningShoes = runningShoes;
		this.footballShoes = footballShoes;
		this.idClient = idCliente;
		this.costCart = costCart;
	}

	public int getDancingShoes() {
		return dancingShoes;
	}

	public int getrunningShoes() {
		return runningShoes;
	}

	public int getfootballShoes() {
		return footballShoes;
	}

	public int getId() {
		return id;
	}
	
	public int getIdClient() {
		return idClient;
	}
	
	public double getCostCart() {
		return costCart;
	}
}
