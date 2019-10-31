package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.mapbox.geojson.Feature;

public class Stateless {
	
	public int moves;
	public int seed;
	
	public Stateless(int seed) {
		this.moves = 250;
		this.seed = seed;
	}
	
	public void simulation(String url, String filename, double latitudeInitial, double longitudeInitial) {
			
		//Initialise random seed
		Random rnd = new Random(this.seed);
		
		//Initialise a stateless drone
		Drone drone = new Drone(0.0, 250.0, latitudeInitial, longitudeInitial);
		System.out.println(drone.latitude);
		System.out.println(drone.longitude);
		
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
			
			//Drone coordinates in String before move
			String pre_latitude = Double.toString(drone.latitude);
			String pre_longitude = Double.toString(drone.longitude);
			String direction;
			
			//Initialise HashMap -- Integer for directions and Station for nearest charging station in that particular direction
			HashMap<Integer, Station> positive = new HashMap<Integer, Station>(); 
			HashMap<Integer, Station> neutral = new HashMap<Integer, Station>(); 
			HashMap<Integer, Station> negative = new HashMap<Integer, Station>(); 
			List<Integer> invalid_directions = new ArrayList<Integer>();
			
			for (int i=0; i<16; i++) {
				System.out.println("The drone is now in direction " + i);
				Drone drone_test = drone;
				drone_test = drone_test.nextPosition(Direction.compass.get(i));
				if (drone_test.inPlayArea()) {
					double[] distance = new double[50];;
					for (int j=0; j<50; j++) {
						Station s = stations.get(j);
						distance[j] = Distance.calculateDistance(drone_test.latitude, drone_test.longitude, s.coordinates[0], s.coordinates[1]);
					}
					System.out.println("The distance size is " + distance.length);
					if (Distance.minDist(distance) <= 0.00025) {
						int index = Distance.minIndex(distance);
						System.out.println("The charging station with the minimum distance is " + index);
						Station s = stations.get(index);
						if (s.coins>0 || s.power>0) positive.put(i, s);
						else if (s.coins<0 || s.power<0) negative.put(i, s);
						else neutral.put(i, s);
					} 
				} else {
					System.out.println("Drone_test in the direction of " + i + " is out of playing area.");
					invalid_directions.add(i);
				}
			}
			
			System.out.println("Positive hashmap " + positive);
			System.out.println("Neutral hashmap " + neutral);
			System.out.println("Negative hashmap " + negative);
			
			if (positive.size() > 0) {
				Set<Integer> key_set = positive.keySet();
				Integer[] keys = key_set.toArray(new Integer[0]);
				int select = rnd.nextInt(keys.length);
				int move = keys[select];
				drone = drone.nextPosition(Direction.compass.get(move));
				direction = Direction.directions_str[move]; 
				Station s = positive.get(move);
				drone.updateCoin(s.coins);
				drone.updatePower(s.power);
				s.coins = 0;
				s.power = 0;
				for (int i=0; i<50; i++) {
					if (stations.get(i).id.equals(s.id)) {
						stations.set(i, s);
						break;
					}
				}
			} else if (negative.size() > 0 && negative.size() < (16-invalid_directions.size())) {
				Set<Integer> key_set = negative.keySet();
				List<Integer> key_list = new ArrayList<Integer>(key_set);
				Integer[] directions = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
				List<Integer> valid = new ArrayList<Integer>(Arrays.asList(directions));
				valid.removeAll(key_list);
				valid.removeAll(invalid_directions);
				int select = rnd.nextInt(valid.size());
				int move = valid.get(select);
				drone = drone.nextPosition(Direction.compass.get(move));
				direction = Direction.directions_str[move]; 
			} else if (negative.size() == (16-invalid_directions.size())) {
				Set<Integer> key_set = negative.keySet();
				Integer[] keys = key_set.toArray(new Integer[0]);
				int select = rnd.nextInt(keys.length);
				int move = keys[select];
				drone = drone.nextPosition(Direction.compass.get(move));
				direction = Direction.directions_str[move]; 
				Station s = negative.get(move);
				drone.updateCoin(s.coins);
				drone.updatePower(s.power);
				s.coins = 0;
				s.power = 0;
				for (int i=0; i<50; i++) {
					if (stations.get(i).id.equals(s.id)) {
						stations.set(i, s);
						break;
					}
				}
			} else {
				Integer[] directions = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
				List<Integer> valid = new ArrayList<Integer>(Arrays.asList(directions));
				valid.removeAll(invalid_directions);
				int select = rnd.nextInt(valid.size());
				int move = valid.get(select);
				drone = drone.nextPosition(Direction.compass.get(move));
				direction = Direction.directions_str[move]; 
			}
			
			drone.updatePower(-1.25);
			this.moves--;
			System.out.println("Total moves left: " + this.moves);
			System.out.println("Coin values after " + this.moves + " is: " + drone.coin);
			System.out.println("Power values after " + this.moves + " is: " + drone.power);
			System.out.println("Latitude values after " + this.moves + " is: " + drone.latitude);
			System.out.println("Longitude values after " + this.moves + " is: " + drone.longitude);
			System.out.println(" ");
			
			//Drone values after move
			
			String post_latitude = Double.toString(drone.latitude);
			String post_longitude = Double.toString(drone.longitude);
			String post_coin = Double.toString(drone.coin);
			String post_power = Double.toString(drone.power);
			String content = pre_latitude + "," + pre_longitude + "," + direction + "," + post_latitude + "," + post_longitude + "," + post_coin + "," + post_power;
			File.writeTextFile(filename, content);
			
		}
		
		for (int i=0; i<50; i++) {
			System.out.println(i + " station has coin value of " + stations.get(i).coins);
		}
	
		
		/*while(this.moves>0 && drone.power>0) {
			
			//Initialise lists of positive and negative charging stations within range of next move
			List<Station> stations_positive = new ArrayList<Station>();
			List<Station> stations_negative = new ArrayList<Station>();
			
			//Initialise list of positive valid stations after move by drone
			List<Station> stations_valid = new ArrayList<Station>(); 
			
			//Initialise list of negative stations after move by drone
			List<Station> stations_invalid = new ArrayList<Station>();
			
			//Rough estimation on the positive charging stations within the next move of the drone
			for (int i=0; i<50; i++) {
				Station s = stations.get(i);
				if(drone.withinRange(s.coordinates[0], s.coordinates[1], 0.00055)){
					if(s.marker_symbol.equals("lighthouse")) {
						stations_positive.add(s);
					} else if (s.marker_symbol.equals("danger")){
						stations_negative.add(s);
					}	       
				}
			}
			System.out.println(drone.latitude);
			System.out.println(drone.longitude);
			System.out.println("Positive charging stations size are: " + stations_positive.size());
			System.out.println("Negative charging stations size are: " + stations_negative.size());
			
			if (stations_positive.size() == 0) {
				int temp = 1;  
				System.out.println("Testing");
				while(temp == 1) {
					Drone drone_test = drone;
					stations_invalid.clear();
					int move = rnd.nextInt(16);
					System.out.println(move);
					drone_test = drone_test.nextPosition(Direction.compass.get(move));
					System.out.println(drone_test.latitude);
					System.out.println(drone_test.longitude);
					if(drone_test.inPlayArea()) {
						System.out.println("Testing2");
						for (int i=0; i<stations_negative.size(); i++) {
							Station s = stations_negative.get(i);
							if(drone_test.withinRange(s.coordinates[0], s.coordinates[1], 0.00025)) {
								stations_invalid.add(s);
							}	
						}
						System.out.println("Stations size are: " + stations_invalid.size());
						if (stations_invalid.size() == 0) {
							drone = drone.nextPosition(Direction.compass.get(move));
							temp = 0;
							System.out.println("Testing3");
						} 
					}
				}
			} else {		
				int temp = 0;
				//Initialise a test drone and choose a random direction to move
				do {
					String id ="";
					stations_valid.clear();
					stations_invalid.clear();
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
						double[] distance_pos = new double[stations_valid.size()];
						for (int i=0; i<stations_valid.size(); i++) {
							Station s = stations_valid.get(i);
							distance_pos[i] = Drone.calculateDistance(drone.latitude, drone.longitude, s.coordinates[0], s.coordinates[1]);
						}
						
						for (int i=0; i<stations_negative.size(); i++) {
							Station s = stations_negative.get(i);
							if(drone_test.withinRange(s.coordinates[0], s.coordinates[1], 0.00025)) {
								stations_invalid.add(s);
							}
						}
						double[] distance_neg = new double[stations_valid.size()];
						for (int i=0; i<stations_invalid.size(); i++) {
							Station s = stations_invalid.get(i);
							distance_neg[i] = Drone.calculateDistance(drone.latitude, drone.longitude, s.coordinates[0], s.coordinates[1]);
						}
						
						double dist1 = Drone.minDist(distance_pos);
						int index = Drone.minIndex(distance_pos);
						id = stations_valid.get(index).id;
						double dist2 = Drone.minDist(distance_neg);
						if (stations_valid.size() > 0 && (dist1<dist2 || stations_invalid.size()==0)) {temp = 1;}
					} 
					if(temp == 1) {
						drone = drone.nextPosition(Direction.compass.get(move));
						for (int i=0; i<50; i++) {
							if (stations.get(i).id.equals(id)) {
								Station s = stations.get(i);
								drone.updateCoin(s.coins);
								drone.updatePower(s.power);
								s.coins = 0.0;
								s.power = 0.0;
								s.marker_symbol = "neutral";
								stations.set(i, s);
								System.out.println(stations.get(i).coins);
								System.out.println(stations.get(i).id);
								break;
							}
						}
					} 
				} while(temp == 0);
			}
			
			
			drone.updatePower(-1.25);
			this.moves--;
			System.out.println("Number of moves: " + this.moves);
			System.out.println("Coin values after " + this.moves + " is: " + drone.coin);
			System.out.println("Power values after " + this.moves + " is: " + drone.power);
			System.out.println("Latitude values after " + this.moves + " is: " + drone.latitude);
			System.out.println("Longitude values after " + this.moves + " is: " + drone.longitude);
		}*/
		
	}	
}
