package uk.ac.ed.inf.powergrab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.Point;

public class Maps {
	
	public String mapString;
	
	public Maps(String mapString) {
		this.mapString = mapString;
	}
	
	public List<Feature> readMap() {
		String mapSource = null;
		try {
	        URL mapUrl = new URL(mapString);
	        HttpURLConnection conn = (HttpURLConnection) mapUrl.openConnection();
	        conn.setReadTimeout(10000);
	        conn.setConnectTimeout(15000);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        conn.connect();
        	InputStream file = conn.getInputStream();
        	BufferedReader reader = new BufferedReader(new InputStreamReader(file));
            StringBuilder out = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                out.append(line);
            }
            mapSource = out.toString();
            reader.close();
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
		
		FeatureCollection fc = FeatureCollection.fromJson(mapSource);
        List<Feature> f = fc.features();
        return f;
	}
	
	public double[] getCoordinates(List<Feature> features, int i) {
		Feature f = features.get(i);
		Geometry g = f.geometry();
        Point p = (Point) g;
        double longitude = p.coordinates().get(0);
        double latitude = p.coordinates().get(1);
        double[] coordinates = {latitude, longitude};
        return coordinates;
	}

}
