package uk.ac.ed.inf.powergrab;

public class Drone extends Position {
	
	public double coin;
	public double power;
	public int moves;
	
	public Drone(double coin, double power, int moves, double latitude, double longitude) {
		super(latitude, longitude);
		this.coin = coin;
		this.power = power;
		this.moves = moves;
	}
	
	public Drone nextPosition(Direction direction) {
		
		double x_move, y_move;
		x_move = 0.0003 * Math.sin(direction.angle);
		y_move = 0.0003 * Math.cos(direction.angle);
		Drone temp = new Drone(coin, power, moves, latitude, longitude);
		temp.longitude = temp.longitude + x_move;
		temp.latitude = temp.latitude + y_move;
		
		return temp;
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
