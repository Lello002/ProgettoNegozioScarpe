package domain;

public class Seller {
	
	private final String name;
	private int id;
	private final String email;
	private final String password;
	private double shoesPrice;

	public Seller(int id, String name, String email, String password, double shoesPrice) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.shoesPrice = shoesPrice;
	}
	
	public String getName() {
		return name;
	}
	
	public int getId() {
		return id;
	}
	
	public String getemailSeller() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public double getShoesPrice() {
		return shoesPrice;
	}
}
