package uk.ac.ed.inf.powergrab;

public interface Strategy {
	
	//Declare simulation method that must be implemented by subclasses
	void simulation(String filename, String url, int seed);

}
