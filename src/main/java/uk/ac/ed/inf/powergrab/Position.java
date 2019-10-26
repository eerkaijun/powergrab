package uk.ac.ed.inf.powergrab;

public class Position {
	
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public boolean inPlayArea() {
		if (this.latitude <= 55.942617 || this.latitude >= 55.946233) return false;
		if (this.longitude <= -3.192473 || this.longitude >= -3.184319) return false;
		return true;
	}
     
}
