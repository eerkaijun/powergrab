package uk.ac.ed.inf.powergrab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;

public class Maps {
	
	public String mapString;
	public String mapSource;
	
	public Maps(String mapString) {
		this.mapString = mapString;
		this.mapSource = null;
	}
	
	public void readMap() {
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
	}
	
	public List<Feature> parseMap() {
		FeatureCollection fc = FeatureCollection.fromJson(mapSource);
        List<Feature> f = fc.features();
        return f;
	}

}
