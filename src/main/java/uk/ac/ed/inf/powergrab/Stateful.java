package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public class Stateful {
	
	public int moves;
	public int seed;
	
	public Stateful(int seed) {
		this.moves = 250;
		this.seed = seed;
	}
	
	public void simulation(String url, double latitudeInitial, double longitudeInitial) {
		
		//Initialise random seed
		Random rnd = new Random(this.seed);
		
		//Initialise drone
		Drone drone = new Drone(0.0, 250.0, latitudeInitial, longitudeInitial);
		
		Maps map = new Maps(url);
		List<Feature> features = map.readMap();
		
		//Initialise a list of charging stations on the map
		List<Station> stations = new ArrayList<Station>();
		
		//List of positive, negative and neutral charging stations respectively
		List<Station> positive = new ArrayList<Station>();
	    List<Station> negative = new ArrayList<Station>();
		
		//Initialise a list of Point indicating the drone's flight path
		List<Point> points = new ArrayList<Point>();
		Point p0 = Point.fromLngLat(longitudeInitial, latitudeInitial);
		points.add(p0);
		
		//Convert features into station instances
		for (int i=0; i<50; i++) {
			Feature f = features.get(i);
			Station s = new Station();
			s.getInfo(f);
			stations.add(s);
		}
		
		for (int i=0; i<50; i++) {
			Station s = stations.get(i);
			if (s.marker_symbol.equals("lighthouse")) {
				positive.add(s);
			} else if (s.marker_symbol.equals("danger")) {
				negative.add(s);
			}
		}
		
		while(this.moves>0 && drone.power>0) {
			
			System.out.println("Positive size is now " + positive.size());
			
			//Drone coordinates in String before move
			String pre_latitude = Double.toString(drone.latitude);
			String pre_longitude = Double.toString(drone.longitude);
			String direction;
			
			if (positive.size() != 0) {
				
				//Compute distance between drone and all positive stations available
				double [] distance_pos = new double[positive.size()];
				for (int i=0; i<positive.size(); i++) {
					Station s = positive.get(i);
					distance_pos[i] = Distance.calculateDistance(drone.latitude, drone.longitude, s.coordinates[0], s.coordinates[1]);	
				}
				
				//Debugging statement
				for (int j=0; j<distance_pos.length; j++) {
					System.out.println("Number " + j + "is " + distance_pos[j]);
				}
				
				//Move towards the direction of the nearest positive charging station
				int index_pos = Distance.minIndex(distance_pos);
				Station s_pos = positive.get(index_pos);
				System.out.println("The nearest positive charging station is " + s_pos.id);
				double angle = Math.atan((drone.longitude-s_pos.coordinates[1])/(drone.latitude-s_pos.coordinates[0]));
				System.out.println("Angle is " + Math.toDegrees(angle));
				double adjusted_angle;
				if(drone.longitude > s_pos.coordinates[1]) {
					if (angle <= 0) {
						adjusted_angle = (2 * Math.PI + angle);
					} else {
						adjusted_angle = (Math.PI + angle);
					}
				} else {
					if (angle >= 0) {
						adjusted_angle = angle;
					} else {
						adjusted_angle = (Math.PI + angle);
					}
				}
				System.out.println("Adjusted angle is " + Math.toDegrees(adjusted_angle));
		    
				//Drone move in the direction towards the nearest positive charging station
				int preferred_direction = -1;
				int second_preferred_direction = -1;
				List<Integer> valid_directions = new ArrayList<Integer>();
				List<Integer> valid_non_negative_directions = new ArrayList<Integer>();
				
				for (int i=0; i<16; i++) {
					//Test for valid directions within the next 16 possible moves
					Drone drone_test = drone;
					drone_test = drone_test.nextPosition(Direction.compass.get(i));
					if (drone_test.inPlayArea()) {
						valid_directions.add(i);
						double [] distance_neg = new double[negative.size()];
						for (int j=0; j<negative.size(); j++) {
							Station s = negative.get(j);
							distance_neg[j] = Distance.calculateDistance(drone_test.latitude, drone_test.longitude, s.coordinates[0], s.coordinates[1]);
						}
						if (Distance.minDist(distance_neg) > 0.00025) {
							valid_non_negative_directions.add(i);
						}
					}
				}
				
				for (int i=1; i<17; i++) {
					if (adjusted_angle - Direction.directions_angle[i] < 0) {
						if (Math.abs(adjusted_angle - Direction.directions_angle[i]) < adjusted_angle - Direction.directions_angle[i-1]) {
							preferred_direction = i;
							second_preferred_direction = i-1;
						} else {
							preferred_direction = i-1;
							second_preferred_direction = i;
						}
						break;
					}
				}
				
				if (preferred_direction == 16) preferred_direction = 0;
				if (second_preferred_direction == 16) second_preferred_direction = 0;
				int move = -1;
				if (valid_non_negative_directions.size() > 0) {
					if (valid_non_negative_directions.contains(preferred_direction)) {
						move = preferred_direction;
					} else if (valid_non_negative_directions.contains(second_preferred_direction)) {
						move = second_preferred_direction;
					} else {
						int select = rnd.nextInt(valid_non_negative_directions.size());
						move = valid_non_negative_directions.get(select);
					}
				} else {
					int select = rnd.nextInt(valid_directions.size());
					move = valid_directions.get(select);
				}
				
				drone = drone.nextPosition(Direction.compass.get(move));				
				
			} else {
				
				//Initialise ArrayLists to store preferred directions and valid directions to move
				List<Integer> preferred_directions = new ArrayList<Integer>();
				List<Integer> valid_directions = new ArrayList<Integer>();
				
				for (int i=0; i<16; i++) {
					Drone drone_test = drone;
					drone_test = drone_test.nextPosition(Direction.compass.get(i));
					if (drone_test.inPlayArea()) {
						double [] distance_neg = new double[negative.size()];
						for (int j=0; j<negative.size(); j++) {
							Station s = negative.get(j);
							distance_neg[j] = Distance.calculateDistance(drone_test.latitude, drone_test.longitude, s.coordinates[0], s.coordinates[1]);
						}
						if (Distance.minDist(distance_neg) > 0.00025) {
							preferred_directions.add(i);
						}
						valid_directions.add(i);
					}
				}
				
				if (preferred_directions.size() > 0) {
					int select = rnd.nextInt(preferred_directions.size());
					int move = preferred_directions.get(select);
					drone = drone.nextPosition(Direction.compass.get(move));
				} else {
					int select = rnd.nextInt(valid_directions.size());
					int move = valid_directions.get(select);
					drone = drone.nextPosition(Direction.compass.get(move));
				}
				
			}
			
			double[] distance = new double[50];
			for (int i=0; i<50; i++) {
				Station s = stations.get(i);
				distance[i] = Distance.calculateDistance(drone.latitude, drone.longitude, s.coordinates[0], s.coordinates[1]);
		    }

		    if (Distance.minDist(distance) <= 0.00025) {
		    	System.out.println("A charging station is within range!");
				int index = Distance.minIndex(distance);
				Station s = stations.get(index);
				drone.updateCoin(s.coins);
				drone.updatePower(s.power);
				s.coins = 0;
				s.power = 0;
				stations.set(index, s);
				for (int i=0; i<positive.size(); i++) {
					Station s_pos = positive.get(i);
					if (s.id.equals(s_pos.id)) {
						positive.remove(i);
						break;
					}
				}
				for (int i=0; i<negative.size(); i++) {
					Station s_neg = negative.get(i);
					if (s.id.equals(s_neg.id)) {
						negative.remove(i);
						break;
					}
				}
			} 
			
			drone.updatePower(-1.25);
			this.moves--;
			System.out.println("Total moves left: " + this.moves);
			System.out.println("Coin values after " + this.moves + " is: " + drone.coin);
			System.out.println("Power values after " + this.moves + " is: " + drone.power);
			System.out.println("Latitude values after " + this.moves + " is: " + drone.latitude);
			System.out.println("Longitude values after " + this.moves + " is: " + drone.longitude);
			System.out.println(" ");
			
			
			Point p = Point.fromLngLat(drone.longitude, drone.latitude);
			points.add(p);
			
			//String post_latitude = Double.toString(drone.latitude);
			//String post_longitude = Double.toString(drone.longitude);
			//String post_coin = Double.toString(drone.coin);
			//String post_power = Double.toString(drone.power);
			//String content = pre_latitude + "," + pre_longitude + "," + direction + "," + post_latitude + "," + post_longitude + "," + post_coin + "," + post_power;
			
		}
		
		FeatureCollection fc = map.writeMap(points, features);
		File.writeGeoJSONFile("testing.geojson", fc);
		
	}

}
