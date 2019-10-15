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
	
	public boolean withinRange(double latitudeIn, double longitudeIn, double radius){
		double x = Math.pow((this.longitude - longitudeIn),2);
		double y = Math.pow((this.latitude - latitudeIn),2);
		double distance = Math.sqrt(x+y);
		if (distance <= radius) {
			return true;
		} else {
			return false;
		}
	}
}
