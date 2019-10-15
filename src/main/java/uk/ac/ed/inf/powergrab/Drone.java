package uk.ac.ed.inf.powergrab;

public class Drone extends Position {
	
	public float coin;
	public float power;
	
	public Drone(float coin, float power, double latitude, double longitude) {
		super(latitude, longitude);
		this.coin = coin;
		this.power = power;
	}
    
	public void updateCoin (float coinIn) {
		this.coin = this.coin + coinIn;
		if (this.coin < 0) this.coin = 0; 
	}
	
	public void updatePower (float powerIn) {
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
