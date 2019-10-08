package uk.ac.ed.inf.powergrab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;

public class Maps {
	
	public String mapString;
	
	public Maps(String mapString) {
		this.mapString = mapString;
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
            System.out.println(out.toString());   //Prints the string content read from input stream
            reader.close();
	    } catch (MalformedURLException e) {
	        e.printStackTrace();
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

}
