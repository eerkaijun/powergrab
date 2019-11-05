package uk.ac.ed.inf.powergrab;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import com.mapbox.geojson.FeatureCollection;

public class File {
	
	public static void writeTextFile(String filename, String content) {
        
        try {
            FileWriter fileWriter = new FileWriter(filename, true);
            BufferedWriter bufWriter = new BufferedWriter(fileWriter);
            bufWriter.write(content);
            bufWriter.newLine();
            bufWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
	}
	
	public static void writeGeoJSONFile(String filename, FeatureCollection fc) {
		
		try (FileWriter file = new FileWriter(filename)) {
            file.write(fc.toJson());
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
		
	}

}
