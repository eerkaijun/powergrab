package uk.ac.ed.inf.powergrab;

public class Distance {
	
	//Calculate the distance between two sets of coordinates
	public static double calculateDistance(double latitude, double longitude, double latitudeIn, double longitudeIn) {
		double x = Math.pow((longitude - longitudeIn),2);
		double y = Math.pow((latitude - latitudeIn),2);
		double distance = Math.sqrt(x+y);
		return distance;
	}
	
	//Return the minimum distance
	public static double minDist(double[] array) {
		double min = array[0];
		for(int i=0; i<array.length; i++) {
			if(array[i] < min) {
				min = array[i];
		    }
		}
		return min;
	}
	
	//Return the index of the minimum distance
	public static int minIndex(double[] array) {
		double min = array[0];
		int index = 0;
		for(int i=0; i<array.length; i++) {
			if(array[i] < min) {
				min = array[i];
				index = i;
		    }
		}
		return index;
	}

}
