package domain;

public class Client {

	private final int id;
	private final String name;
	private final String email;
	private final String password;
	private double balance;
	
	public Client(int id, String name, String email, String password, double balance) {
		this.balance = balance;
		this.name = name;
		this.id = id;
		this.password = password;
		this.email = email;
	}

	public double getbalance() {
		return balance;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPassword() {
		return password;
	}
}
