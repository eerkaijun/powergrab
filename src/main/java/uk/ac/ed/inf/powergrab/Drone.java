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
	
	public static double calculateDistance(double latitude, double longitude, double latitudeIn, double longitudeIn) {
		double x = Math.pow((longitude - longitudeIn),2);
		double y = Math.pow((latitude - latitudeIn),2);
		double distance = Math.sqrt(x+y);
		return distance;
	}
	
	public boolean withinRange(double latitudeIn, double longitudeIn, double radius){
		double distance = calculateDistance(this.latitude, this.longitude, latitudeIn, longitudeIn);
		if (distance <= radius) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int minDist(double[] array) {
		double min = array[0];
		int index = 0;
		for(int i=0; i<array.length; i++) {
			if(array[i] < min) {
				min = array[i];
				index = i;
		    }
		}
		return index;
	}
}
