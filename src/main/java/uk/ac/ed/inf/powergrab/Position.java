package uk.ac.ed.inf.powergrab;

public class Position {
	
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) {
		
	}
	
	/*
	public Position nextPostion(Direction direction) {

	}
	*/
	
	
	public boolean inPlayArea(Position P) {
		if (P.latitude < 5.942617 || P.latitude > 55.946233) return false;
		if (P.longitude < -3.192473 || P.longitude > -3.19247) return false;
		return true;
	}
     
}
