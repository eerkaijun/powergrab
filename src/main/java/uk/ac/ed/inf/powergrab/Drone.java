package uk.ac.ed.inf.powergrab;

public class Drone extends Position {
	
	public double coin;
	public double power;
	
	public Drone(double coin, double power, double latitude, double longitude) {
		super(latitude, longitude);
		this.coin = coin;
		this.power = power;
	}
    
	public void updateCoin (double coinIn) {
		this.coin = this.coin + coinIn;
		if (this.coin < 0) this.coin = 0; 
	}
	
	public void updatePower (double powerIn) {
		this.power = this.power + powerIn;
		if (this.power < 0) this.power = 0;
	}
	
	public double calculateDistance(double latitudeIn, double longitudeIn) {
		double a = Math.pow((this.latitude - latitudeIn),2);
		double b = Math.pow((this.longitude - longitudeIn),2);
		return (Math.sqrt(a+b));
	}
	
	public boolean withinRange(double distance) {
		if (distance <= 0.00025) {
			return true;
		} else {
			return false;
		}
	}
}
