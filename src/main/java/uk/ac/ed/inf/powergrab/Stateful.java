package uk.ac.ed.inf.powergrab;

public class Stateful {
	
	public int moves;
	public int seed;
	
	public Stateful(int seed) {
		this.moves = 250;
		this.seed = seed;
	}
	
	public void simulation(double latitudeInitial, double longitudeInitial) {
		Drone drone = new Drone(0.0, 250.0, latitudeInitial, longitudeInitial);
		
	}

}
