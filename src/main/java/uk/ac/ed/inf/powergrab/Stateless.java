package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.mapbox.geojson.Feature;

public class Stateless {
	
	public int moves;
	public int seed;
	
	public Stateless(int seed) {
		this.moves = 250;
		this.seed = seed;
	}
	
	public void simulation(String url, double latitudeInitial, double longitudeInitial) {
			
		//Initialise random seed
		Random rnd = new Random(this.seed);
		
		//Initialise a stateless drone
		Drone drone = new Drone(0.0, 250.0, latitudeInitial, longitudeInitial);
		
		//Initialise the map based on the given URL
		Maps map = new Maps(url);
		List<Feature> features = map.readMap();
		
		//Initialise a list of charging stations on the map
		List<Station> stations = new ArrayList<Station>();
		
		//Convert features into station instances
		for (int i=0; i<50; i++) {
			Feature f = features.get(i);
			Station s = new Station();
			s.getInfo(f);
			stations.add(s);
		}
		
		while(this.moves>0 && drone.power>0) {
			
			//Initialise lists of positive and negative charging stations within range of next move
			List<Station> stations_positive = new ArrayList<Station>();
			List<Station> stations_negative = new ArrayList<Station>();
			
			//Initialise list of valid stations after move by drone
			List<Station> stations_valid = new ArrayList<Station>(); 
			
			//Rough estimation on the positive charging stations within the next move of the drone
			for (int i=0; i<50; i++) {
				Station s = stations.get(i);
				if(drone.withinRange(s.coordinates[0], s.coordinates[1], 0.00055)){
					if(s.marker_symbol == "lighthouse") {
						stations_positive.add(s);
					} else {
						stations_negative.add(s);
					}	       
				}
			}
			
			if (stations_positive.size() == 0) {
				int temp = 1;  
				while(temp == 1) {
					Drone drone_test = drone;
					int move = rnd.nextInt(16);
					System.out.println(move);
					drone_test = drone_test.nextPosition(Direction.compass.get(move));
					if(drone_test.inPlayArea()) {
						drone = drone.nextPosition(Direction.compass.get(move));
						temp = 0;
					}
				}
			} else {		
				//Initialise a test drone and choose a random direction to move
				do {
					stations_valid.clear();
					Drone drone_test = drone;
					int move = rnd.nextInt(16);
					System.out.println(move);
					drone_test = drone_test.nextPosition(Direction.compass.get(move));
					System.out.println(drone_test.latitude);
					System.out.println(drone_test.longitude);
					if(drone_test.inPlayArea()) {
						for (int i=0; i<stations_positive.size(); i++) {
							Station s = stations_positive.get(i);
							if(drone_test.withinRange(s.coordinates[0], s.coordinates[1], 0.00025)) {
								stations_valid.add(s);
							}
						}
					} else {
						System.out.println("Drone_test is out of play area");
					}
		
					if(stations_valid.size() > 0) {
						drone = drone.nextPosition(Direction.compass.get(move));
						double[] distance = new double[stations_valid.size()];
						for (int i=0; i<stations_valid.size(); i++) {
							Station s = stations_valid.get(i);
							distance[i] = Drone.calculateDistance(drone.latitude, drone.longitude, s.coordinates[0], s.coordinates[1]);
						}
						//Return the feature with the nearest distance
						int index = Drone.minDist(distance);
						//Update coin and power
						Station s = stations_valid.get(index);
						drone.updateCoin(s.coins);
						drone.updatePower(s.power);
						s.coins = 0;
						s.power = 0;
					} 
				} while(stations_valid.size() == 0);
			}
			
			drone.updatePower(-1.25);
			this.moves--;
			System.out.println("Number of moves: " + this.moves);
			System.out.println("Coin values after " + this.moves + " is: " + drone.coin);
			System.out.println("Power values after " + this.moves + " is: " + drone.power);
			System.out.println("Latitude values after " + this.moves + " is: " + drone.latitude);
			System.out.println("Longitude values after " + this.moves + " is: " + drone.longitude);
		}
	}	
}
