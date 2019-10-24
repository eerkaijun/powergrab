package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonElement;
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
		
		while(this.moves>0 && drone.power>0) {
		
			List<Feature> features_positive = new ArrayList<Feature>(); //List of features with positive coin within area of rough estimation
			List<Feature> features_valid = new ArrayList<Feature>(); //List of valid features after move by drone
		
			//Rough estimation on the positive charging stations within the next move of the drone
			//The stations are stored in the variable features_positive
			for (int i=0; i<50; i++) {
				Feature f = features.get(i);
				double[] coordinates = map.getCoordinates(f);
				if(drone.withinRange(coordinates[0], coordinates[1], 0.00055)){
					JsonElement elm = f.getProperty("marker-symbol");
					String symbol = elm.getAsString();
					if(symbol == "lighthouse") {
						features_positive.add(f);
					}	       
				}
			}
		
			//Initialise a test drone and choose a random direction to move
			do {
				features_valid.clear();
				Drone drone_test = drone;
				int move = rnd.nextInt(16);
				drone_test.nextPosition(Direction.compass.get(move));
				if(drone_test.inPlayArea()) {
					for (int i=0; i<features_positive.size(); i++) {
						Feature f = features_positive.get(i);
						double[] coordinates = map.getCoordinates(f);
						if(drone.withinRange(coordinates[0], coordinates[1], 0.00025)) {
							features_valid.add(f);
						}
					}
				}
		
				if(features_valid.size() > 0) {
					drone.nextPosition(Direction.compass.get(move));
					double[] distance = new double[features_valid.size()];
					for (int i=0; i<features_valid.size(); i++) {
						Feature f = features_valid.get(i);
						double[] coordinates = map.getCoordinates(f);
						distance[i] = Drone.calculateDistance(drone.latitude, drone.longitude, coordinates[0], coordinates[1]);
					}
					//Return the feature with the nearest distance
					int index = Drone.minDist(distance);
					//Update coin and power
					Feature f = features_valid.get(index);
					double coinIn = map.getCoin(f);
					drone.updateCoin(coinIn);
					double powerIn = map.getPower(f);
					drone.updatePower(powerIn);
				} 
			} while(features_valid.size() == 0);
		
			drone.updatePower(-1.25);
			this.moves--;
		}
	}	
}
