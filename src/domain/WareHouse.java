package domain;

public class WareHouse {
	
	
	private final int id;
	private final int idSeller;
	private int dancingShoes;
	private int runningShoes;
	private int footballShoes;
	private int capacity;
	
	public WareHouse(int id, int idVenditore, int scarpeDaBallo, int scarpeDaCorsa, int scarpeDaCalcio, int capienzaMagazzino) {
		this.id = id;
		this.idSeller = idVenditore;
		this.dancingShoes = scarpeDaBallo;
		this.runningShoes = scarpeDaCorsa;
		this.footballShoes = scarpeDaCalcio;
		this.capacity = capienzaMagazzino;
	}

	public int getId() {
		return id;
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

	public int getCapacity() {
		return capacity;
	}
	
	public int getiIdSeller() {
		return idSeller;
	}
}
