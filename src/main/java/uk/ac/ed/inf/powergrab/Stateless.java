package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Point;

public abstract class Stateless implements Strategy{
	
	protected Drone drone;
	protected List<Station> stations;
	protected HashMap<Integer, Station> positive; 
	protected HashMap<Integer, Station> neutral; 
	protected HashMap<Integer, Station> negative; 
	protected List<Integer> invalid_directions;
	
	public Stateless(double latitudeInitial, double longitudeInitial, String url) {
		this.drone = new Drone(0.0, 250.0, 250, latitudeInitial, longitudeInitial);
		initialiseStations(url);
		this.positive = new HashMap<Integer, Station>(); 
		this.neutral = new HashMap<Integer, Station>(); 
		this.negative = new HashMap<Integer, Station>(); 
		this.invalid_directions = new ArrayList<Integer>();
	}
	
	public void simulation(String filename, String url, int seed) {
			
		//Initialise random seed
		Random rnd = new Random(seed);
		
		//Initialise a list of Point indicating the drone's flight path
		List<Point> points = new ArrayList<Point>();
		Point p0 = Point.fromLngLat(this.drone.longitude, this.drone.latitude);
		points.add(p0);
		
		while(this.drone.moves>0 && this.drone.power>0) {
			
			//Drone coordinates in String before move
			String pre_latitude = Double.toString(this.drone.latitude);
			String pre_longitude = Double.toString(this.drone.longitude);
			String direction;
			
			this.positive.clear();
			this.neutral.clear();
			this.negative.clear();
			this.invalid_directions.clear();
			
			checkNextPossibleMove();
			
			System.out.println("Positive hashmap " + positive);
			System.out.println("Neutral hashmap " + neutral);
			System.out.println("Negative hashmap " + negative);
			
			if (this.positive.size() > 0) {
				direction = moveTowardsPositive(rnd);
			} else if (this.negative.size() > 0 && this.negative.size() < (16-this.invalid_directions.size())) {
				direction = moveAwayFromNegative(rnd);
			} else if (this.negative.size() == (16-this.invalid_directions.size())) {
				direction = moveLeastNegative(rnd);
			} else {
				direction = moveRandomly(rnd);
			}
			
			this.drone.updatePower(-1.25);
			this.drone.moves = this.drone.moves - 1;
			System.out.println("Total moves left: " + this.drone.moves);
			System.out.println("Coin values after " + this.drone.moves + " is: " + this.drone.coin);
			System.out.println("Power values after " + this.drone.moves + " is: " + this.drone.power);
			System.out.println("Latitude values after " + this.drone.moves + " is: " + this.drone.latitude);
			System.out.println("Longitude values after " + this.drone.moves + " is: " + this.drone.longitude);
			System.out.println(" ");
			
			//Drone values after move
			
			Point p = Point.fromLngLat(drone.longitude, drone.latitude);
			points.add(p);
			
			String post_latitude = Double.toString(drone.latitude);
			String post_longitude = Double.toString(drone.longitude);
			String post_coin = Double.toString(drone.coin);
			String post_power = Double.toString(drone.power);
			String content = pre_latitude + "," + pre_longitude + "," + direction + "," + post_latitude + "," + post_longitude + "," + post_coin + "," + post_power;
			File.writeTextFile(filename, content);
			
		}
		
		Maps map = new Maps(url);
		List<Feature> features = map.readMap();
		FeatureCollection fc = map.writeMap(points, features);
		File.writeGeoJSONFile("test.geojson", fc);
		
		for (int i=0; i<50; i++) {
			System.out.println(i + " station has coin value of " + this.stations.get(i).coins);
		}
		
	}
	
	private void initialiseStations(String url) {
		
		Maps map = new Maps(url);
		List<Feature> features = map.readMap();
		this.stations = new ArrayList<Station>();
		for (int i=0; i<50; i++) {
			Feature f = features.get(i);
			Station s = new Station();
			s.getInfo(f);
			this.stations.add(s);
		}
		
	}
	
	abstract void checkNextPossibleMove();
	
	abstract String moveTowardsPositive(Random rnd);
	
	abstract String moveAwayFromNegative(Random rnd);
	
	abstract String moveLeastNegative(Random rnd);
	
	abstract String moveRandomly(Random rnd);
	
}
