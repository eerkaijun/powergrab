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
	
	public Stateful(double latitudeInitial, double longitudeInitial, String url) {
		this.drone = new Drone(0.0, 250.0, 250, latitudeInitial, longitudeInitial);
		initialiseStations(url);
		
		this.positive = new ArrayList<Station>(); 
		this.negative = new ArrayList<Station>(); 
		this.valid_directions = new ArrayList<Integer>();
		this.preferred_directions = new ArrayList<Integer>();
	}
	
	public void simulation(String filename, String url, int seed) {
		
		//Initialise random seed
		Random rnd = new Random(seed);
		
		//Initialise a list of Point indicating the drone's flight path
		List<Point> points = new ArrayList<Point>();
		Point p0 = Point.fromLngLat(this.drone.longitude, this.drone.latitude);
		points.add(p0);
		
		for (int i=0; i<50; i++) {
			Station s = stations.get(i);
			if (s.marker_symbol.equals("lighthouse")) {
				this.positive.add(s);
			} else if (s.marker_symbol.equals("danger")) {
				this.negative.add(s);
			}
		}
		
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
			
			connectToChargingStation();
			
			this.drone.updatePower(-1.25);
			this.drone.moves--;
			System.out.println("Total moves left: " + this.drone.moves);
			System.out.println("Coin values after " + this.drone.moves + " is: " + this.drone.coin);
			System.out.println("Power values after " + this.drone.moves + " is: " + this.drone.power);
			System.out.println("Latitude values after " + this.drone.moves + " is: " + this.drone.latitude);
			System.out.println("Longitude values after " + this.drone.moves + " is: " + this.drone.longitude);
			System.out.println(" ");
			
			Point p = Point.fromLngLat(this.drone.longitude, this.drone.latitude);
			points.add(p);
			
			String post_latitude = Double.toString(this.drone.latitude);
			String post_longitude = Double.toString(this.drone.longitude);
			String post_coin = Double.toString(this.drone.coin);
			String post_power = Double.toString(this.drone.power);
			String content = pre_latitude + "," + pre_longitude + "," + direction + "," + post_latitude + "," + post_longitude + "," + post_coin + "," + post_power;
			File.writeTextFile(filename, content);
			
		}
		
		Maps map = new Maps(url);
		List<Feature> features = map.readMap();
		FeatureCollection fc = map.writeMap(points, features);
		File.writeGeoJSONFile("testing1.geojson", fc);
		
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
	
	abstract double angleNearestPositive();
	
	abstract void determineAvailableMoves();
	
	abstract void determinePreferredDirection(double adjusted_angle);
	
	abstract String meaningfulMoves(Random rnd);
	
	abstract String randomMoves(Random rnd);
	
	abstract void connectToChargingStation();


}
