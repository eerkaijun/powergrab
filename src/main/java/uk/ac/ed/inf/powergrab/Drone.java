package uk.ac.ed.inf.powergrab;

public class Drone {
	
	public double coin;
	public double power;
	public Position position;
	
	public Drone(double coin, double power, double latitude, double longitude) {
		this.coin = coin;
		this.power = power;
		this.position = new Position(latitude, longitude);
	}
    
	public void updateCoin (double coinIn) {
		this.coin = this.coin + coinIn;
		if (this.coin < 0) this.coin = 0; 
	}
	
	public void updatePower (double powerIn) {
		this.power = this.power + powerIn;
		if (this.power < 0) this.power = 0;
	}
}
