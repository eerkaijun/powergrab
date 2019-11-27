package uk.ac.ed.inf.powergrab;

import java.io.IOException;

public class App 
{
    public static void main( String[] args ) throws IOException
    {
    	String day="";
    	String month="";
    	String year="";
    	int day_int = 0;
    	int month_int = 0;
    	int year_int = 0;
    	double initial_latitude=0;
    	double initial_longitude=0;
    	int seed=0;
    	String drone_type="";
    	
    	String[] day_list = {"01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25","26","27","28","29","30","31"};
    	String[] month_list = {"01","02","03","04","05","06","07","08","09","10","11","12"};
    	String[] year_list = {"2019","2020"};
    	String[] type = {"stateless","stateful"}; //Only two types of drone are present
    	int index = -1;
    	try {
    		if (args.length != 7) throw new IllegalArgumentException();
    		
    		int temp = 0;
    		day = args[0];
    		for (int i=0; i<day_list.length; i++) {
    			if (day_list[i].equals(day)) {
    				day_int = Integer.parseInt(day);
    				temp = 1;
    				break;
    			}
    		}
    		if (temp == 0) throw new IllegalArgumentException();
    		temp = 0;
    		month = args[1];
    		for (int i=0; i<month_list.length; i++) {
    			if (month_list[i].equals(month)) {
    				month_int = Integer.parseInt(month);
    				temp = 1;
    				break;
    			}
    		}
    		if (temp == 0) throw new IllegalArgumentException();
    		temp = 0;
    		year = args[2];
    		for (int i=0; i<year_list.length; i++) {
    			if (year_list[i].equals(year)) {
    				year_int = Integer.parseInt(year);
    				temp = 1;
    				break;
    			}
    		}
    		if (temp == 0) throw new IllegalArgumentException();
    		
    		if (month_int == 2 && day_int > 29) throw new IllegalArgumentException();
    		if (year_int == 2019 && month_int == 2 && day_int == 29) throw new IllegalArgumentException();
    		if ((month_int == 4 || month_int == 6 || month_int == 9 || month_int == 11) && day_int == 31) throw new IllegalArgumentException();
    		
    		//Throw NumberFormatException if latitude, longitude or random seed do not conform to the correct format
    		try {
    			if (Double.parseDouble(args[3]) < 55.942617  || Double.parseDouble(args[3]) > 55.946233) throw new IllegalArgumentException();
    			initial_latitude = Double.parseDouble(args[3]);
    		
    			if (Double.parseDouble(args[4]) < -3.19247  || Double.parseDouble(args[4]) > -3.184319) throw new IllegalArgumentException();
    			initial_longitude = Double.parseDouble(args[4]);
    		
    			seed = Integer.parseInt(args[5]); 
    		} catch(NumberFormatException t) {
    			throw new IllegalArgumentException();
    		}
    		
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
    		
    		String url = "http://homepages.inf.ed.ac.uk/stg/powergrab/"+year+"/"+month+"/"+day+"/powergrabmap.geojson";
        	String filename = drone_type+"-"+day+"-"+month+"-"+year;

        	Strategy[] algorithms = {new StatelessSim(initial_latitude,initial_longitude), new StatefulSim(initial_latitude,initial_longitude)};
            algorithms[index].simulation(filename, url, seed);
    		
    	} catch(IllegalArgumentException e) {
    		e.printStackTrace();
    	} 
    }
  
}
