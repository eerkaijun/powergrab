package uk.ac.ed.inf.powergrab;

public class Position {
	
	public double latitude;
	public double longitude;
	
	public Position(double latitude, double longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	
	public Position nextPosition(Direction direction) {
		
		double x_move, y_move;
		x_move = 0.0003 * Math.sin(direction.angle);
		y_move = 0.0003 * Math.cos(direction.angle);
		Position temp = new Position(latitude, longitude);
		temp.longitude = temp.longitude + x_move;
		temp.latitude = temp.latitude + y_move;
		// check if the drone is still within the playing area
		if (temp.inPlayArea() == false) {
			temp.longitude = temp.longitude - x_move;
			temp.latitude = temp.latitude - y_move;
		}
		return temp;
	}
	

	public boolean inPlayArea() {
		if (this.latitude <= 55.942617 || this.latitude >= 55.946233) return false;
		if (this.longitude <= -3.192473 || this.longitude >= -3.184319) return false;
		return true;
	}
     
}
