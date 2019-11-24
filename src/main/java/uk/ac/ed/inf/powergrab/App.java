package uk.ac.ed.inf.powergrab;

import java.io.IOException;

public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	
    	/*
    	Maps map = new Maps("http://homepages.inf.ed.ac.uk/stg/powergrab/2019/03/05/powergrabmap.geojson");
    	List<Feature> features = map.readMap();
    	System.out.println(features);
    	double total = 0.0;
    	for (int i=0; i<50; i++) {
			Feature f = features.get(i);
			Station s = new Station();
			s.getInfo(f);
			if (s.coins > 0) total = total + s.coins;
		}
    	System.out.println("The total coins available is " + total);*/
    	
    	//StatelessSim sim = new StatelessSim(55.944425,-3.188396);
    	//sim.simulation("txt.txt","http://homepages.inf.ed.ac.uk/stg/powergrab/2019/09/15/powergrabmap.geojson", 36);
    	
    	//StatefulSim sim = new StatefulSim( 55.944425,-3.188396);
    	//sim.simulation("test.txt", "http://homepages.inf.ed.ac.uk/stg/powergrab/2019/03/05/powergrabmap.geojson", 2643);
    	
    	
    	
    	String day="";
    	String month="";
    	String year="";
    	double initial_latitude=0;
    	double initial_longitude=0;
    	int seed=0;
    	String drone_type="";
    	
    	String[] day_list = {"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
    	String[] month_list = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    	String[] year_list = {"2019","2020"};
    	String[] type = {"stateless","stateful"};
    	int index = -1;
    	try {
    		if (args.length != 7) throw new IllegalArgumentException();
    		int temp = 0;
    		day = args[0];
    		for (int i=0; i<day_list.length; i++) {
    			if (day_list[i].equals(day)) {
    				temp = 1;
    				break;
    			}
    		}
    		if (temp == 0) throw new IllegalArgumentException();
    		temp = 0;
    		month = args[1];
    		for (int i=0; i<month_list.length; i++) {
    			if (month_list[i].equals(month)) {
    				temp = 1;
    				break;
    			}
    		}
    		if (temp == 0) throw new IllegalArgumentException();
    		temp = 0;
    		year = args[2];
    		for (int i=0; i<year_list.length; i++) {
    			if (year_list[i].equals(year)) {
    				temp = 1;
    				break;
    			}
    		}
    		if (temp == 0) throw new IllegalArgumentException();
    		
    		if (Double.parseDouble(args[3]) < 55.942617  || Double.parseDouble(args[3]) > 55.946233) throw new IllegalArgumentException();
    		initial_latitude = Double.parseDouble(args[3]);
    		
    		if (Double.parseDouble(args[4]) < -3.19247  || Double.parseDouble(args[4]) > -3.184319) throw new IllegalArgumentException();
    		initial_longitude = Double.parseDouble(args[4]);
    		
    		seed = Integer.parseInt(args[5]);
    		
    		temp = 0;
    		drone_type = args[6];
    		for (int i=0; i<type.length; i++) {
    			if (type[i].equals(drone_type)) {
    				temp = 1;
    				index = i;
    				break;
    			}
    		}
    		if (temp == 0) throw new IllegalArgumentException();
    		
    	} catch(IllegalArgumentException e) {
    		e.printStackTrace();
    	}
    	
    	System.out.println(day);
    	System.out.println(month);
    	System.out.println(year);
    	System.out.println(initial_latitude);
    	System.out.println(initial_longitude);
    	System.out.println(seed);
    	System.out.println(drone_type);
    	
    	
    	String url = "http://homepages.inf.ed.ac.uk/stg/powergrab/"+year+"/"+month+"/"+day+"/powergrabmap.geojson";
    	String filename = drone_type+"-"+day+"-"+month+"-"+year;

    	Strategy[] algorithms = {new StatelessSim(initial_latitude,initial_longitude), new StatefulSim(initial_latitude,initial_longitude)};
        algorithms[index].simulation(filename, url, seed);
    }
  
}
