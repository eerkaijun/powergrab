package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

public class Stateless {
	
	public int moves;
	public int seed;
	
	public Stateless(int seed) {
		this.moves = 250;
		this.seed = seed;
	}
	
	public void simulation(String url, double latitudeInitial, double longitudeInitial) {
		//Initialise the map based on the given URL
		Maps map = new Maps(url);
		List<Feature> features = map.readMap();
		List<Feature> features_local = new ArrayList<Feature>();
		
		//Initialise a stateless drone
		Drone drone = new Drone(0.0, 250.0, latitudeInitial, longitudeInitial);
		
		for (int i=0; i<50; i++) {
			Feature f = features.get(i);
			Geometry g = f.geometry();
	        Point p = (Point) g;
	        double longitudeIn = p.coordinates().get(0);
	        double latitudeIn = p.coordinates().get(1);
	        if(!drone.withinRange(latitudeIn, longitudeIn, 0.00055)){
	        	features_local.add(f);
	        }
	        //TODO: Minus power after every moves
		}
		
		//update coin and power according to the nearest charging stations
		
	}
	
	
}
