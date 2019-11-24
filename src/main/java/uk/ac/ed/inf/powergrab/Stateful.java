package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public abstract class Stateful implements Strategy {
	
	protected Drone drone;
	protected List<Station> stations;
	protected List<Station> positive; 
	protected List<Station> negative; 
	protected List<Integer> valid_directions;
	protected List<Integer> preferred_directions;
	protected int first_preferred_direction;
	protected int second_preferred_direction;
	
	public Stateful(double latitudeInitial, double longitudeInitial) {
		this.drone = new Drone(0.0, 250.0, 250, latitudeInitial, longitudeInitial);
		this.stations = new ArrayList<Station>();
		this.positive = new ArrayList<Station>(); 
		this.negative = new ArrayList<Station>(); 
		this.valid_directions = new ArrayList<Integer>();
		this.preferred_directions = new ArrayList<Integer>();
	}
	
	public void simulation(String filename, String url, int seed) {
		
		//Initialise random seed
		Random rnd = new Random(seed);
		
		//Initialise a list of Features representing charging stations on the map
		Maps map = new Maps(url);
		List<Feature> features = map.readMap();
		
		//Initialise a list of Point indicating the drone's flight path
		List<Point> points = new ArrayList<Point>();
		Point p0 = Point.fromLngLat(this.drone.longitude, this.drone.latitude);
		points.add(p0);
		
		//Initialise the stations list which contains all the charging stations as Station instances
		this.stations = Station.initialiseStations(features);
		
		//Put the positive charging stations into the positive arraylist
		//Put the negative charging stations into the negative arraylist
		for (int i=0; i<50; i++) {
			Station s = stations.get(i);
			if (s.marker_symbol.equals("lighthouse")) {
				this.positive.add(s);
			} else if (s.marker_symbol.equals("danger")) {
				this.negative.add(s);
			}
		}
		
		int count = 0;
		
		while(this.drone.moves>0 && this.drone.power>0) {
			
			this.valid_directions.clear();
			this.preferred_directions.clear();
			
			System.out.println("Positive size is now " + positive.size());
			
			//Drone coordinates in String before move
			String pre_latitude = Double.toString(this.drone.latitude);
			String pre_longitude = Double.toString(this.drone.longitude);
			String direction;
			
			if (this.positive.size() != 0) {
				
				this.first_preferred_direction = -1;
				this.second_preferred_direction = -1;
				
				double adjusted_angle = angleNearestPositive();

				determineAvailableMoves();
				
				determinePreferredDirection(adjusted_angle);
				
				direction = meaningfulMoves(rnd);
				
			} else {
				
				determineAvailableMoves();
				
				direction = randomMoves(rnd);
				
			}
			
			count = connectToChargingStation(count);

			//If drone is stuck on a nearest positive charging station for more than 30 moves, ignore that station
			if (count == 30) {
				if (this.positive.size() > 0) {
					double [] distance_pos = new double[this.positive.size()];
					for (int i=0; i<this.positive.size(); i++) {
						Station s = this.positive.get(i);
						distance_pos[i] = Distance.calculateDistance(this.drone.latitude, this.drone.longitude, s.coordinates[0], s.coordinates[1]);	
					}
					int index_min = Distance.minIndex(distance_pos);
					this.positive.remove(index_min);
					count = 0;
				} 
			}
			
			//Update the power and move values of the drone
			this.drone.updatePower(-1.25);
			this.drone.moves--;
			
			Point p = Point.fromLngLat(this.drone.longitude, this.drone.latitude);
			points.add(p);
			
			String post_latitude = Double.toString(this.drone.latitude);
			String post_longitude = Double.toString(this.drone.longitude);
			String post_coin = Double.toString(this.drone.coin);
			String post_power = Double.toString(this.drone.power);
			String content = pre_latitude + "," + pre_longitude + "," + direction + "," + post_latitude + "," + post_longitude + "," + post_coin + "," + post_power;
			File.writeTextFile(filename+".txt", content);
			
			//Debugging statement
			System.out.println("Total moves left: " + this.drone.moves);
			System.out.println("Coin values after is: " + this.drone.coin);
			System.out.println("Power values after is: " + this.drone.power);
			System.out.println("Latitude values after is: " + this.drone.latitude);
			System.out.println("Longitude values after is: " + this.drone.longitude);
			System.out.println(" ");
			
		}
		
		//Write the drone's path to a new GeoJSON file 
		FeatureCollection fc = map.writeMap(points, features);
		File.writeGeoJSONFile(filename+".geojson", fc);
		
	}
	
	
	abstract double angleNearestPositive();
	
	abstract void determineAvailableMoves();
	
	abstract void determinePreferredDirection(double adjusted_angle);
	
	abstract String meaningfulMoves(Random rnd);
	
	abstract String randomMoves(Random rnd);
	
	abstract int connectToChargingStation(int count);


}
