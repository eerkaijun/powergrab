package uk.ac.ed.inf.powergrab;

import com.google.gson.JsonElement;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

public class Station {
	
	public String id;
	public double coins;
	public double power;
	public String marker_symbol;
	public double[] coordinates;
	
	
	public void getInfo(Feature f) {
		
		JsonElement elm1 = f.getProperty("id");
		String id = elm1.getAsString();
		this.id = id;
		
        JsonElement elm2 = f.getProperty("coins");
    	double coins = elm2.getAsDouble();
    	this.coins = coins;
    	
    	JsonElement elm3 = f.getProperty("power");
    	double power = elm3.getAsDouble();
    	this.power = power;
    	
        JsonElement elm4 = f.getProperty("marker-symbol");
		String marker_symbol = elm4.getAsString();
		this.marker_symbol = marker_symbol;
    	
    	Geometry g = f.geometry();
        Point p = (Point) g;
        double longitude = p.coordinates().get(0);
        double latitude = p.coordinates().get(1);
        double[] coordinates = {latitude, longitude};
        this.coordinates = coordinates;
        
	}

}
