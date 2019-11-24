package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class StatelessSim extends Stateless{
	
	public StatelessSim(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	protected void checkNextPossibleMove() {
		for (int i=0; i<16; i++) {
			Drone drone_test = this.drone;
			drone_test = drone_test.nextPosition(Direction.compass.get(i));
			if (drone_test.inPlayArea()) {
				double[] distance = new double[50];
				for (int j=0; j<50; j++) {
					Station s = this.stations.get(j);
					distance[j] = Distance.calculateDistance(drone_test.latitude, drone_test.longitude, s.coordinates[0], s.coordinates[1]);
				}
				if (Distance.minDist(distance) <= 0.00025) {
					int index = Distance.minIndex(distance);
					System.out.println("The charging station with the minimum distance is " + index);
					Station s = this.stations.get(index);
					if (s.coins>0 || s.power>0) this.positive.put(i, s);
					else if (s.coins<0 || s.power<0) this.negative.put(i, s);
					else this.neutral.put(i, s);
				} 
			} else {
				this.invalid_directions.add(i);
			}
		}
	}
	
	protected String moveTowardsPositive(Random rnd) {
		Set<Integer> key_set = this.positive.keySet();
		Integer[] keys = key_set.toArray(new Integer[0]);
		int select = rnd.nextInt(keys.length);
		int move = keys[select];
		this.drone = this.drone.nextPosition(Direction.compass.get(move));
		String direction = Direction.directions_str[move]; 
		Station s = this.positive.get(move);
		this.drone.updateCoin(s.coins);
		this.drone.updatePower(s.power);
		s.coins = 0;
		s.power = 0;
		for (int i=0; i<50; i++) {
			if (this.stations.get(i).id.equals(s.id)) {
				this.stations.set(i, s);
				break;
			}
		}
		return direction;
	}
	
	protected String moveAwayFromNegative(Random rnd) {
		Set<Integer> key_set = this.negative.keySet();
		List<Integer> key_list = new ArrayList<Integer>(key_set);
		Integer[] directions = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		List<Integer> valid = new ArrayList<Integer>(Arrays.asList(directions));
		valid.removeAll(key_list);
		valid.removeAll(invalid_directions);
		int select = rnd.nextInt(valid.size());
		int move = valid.get(select);
		this.drone = this.drone.nextPosition(Direction.compass.get(move));
		String direction = Direction.directions_str[move]; 
		return direction;
	}
	
	protected String moveLeastNegative(Random rnd) {
		Set<Integer> key_set = this.negative.keySet();
		Integer[] keys = key_set.toArray(new Integer[0]);
		int select = rnd.nextInt(keys.length);
		int move = keys[select];
		this.drone = this.drone.nextPosition(Direction.compass.get(move));
		String direction = Direction.directions_str[move]; 
		Station s = this.negative.get(move);
		this.drone.updateCoin(s.coins);
		this.drone.updatePower(s.power);
		s.coins = 0;
		s.power = 0;
		for (int i=0; i<50; i++) {
			if (this.stations.get(i).id.equals(s.id)) {
				this.stations.set(i, s);
				break;
			}
		}
		return direction;
	}
	
	protected String moveRandomly(Random rnd) {
		Integer[] directions = {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		List<Integer> valid = new ArrayList<Integer>(Arrays.asList(directions));
		valid.removeAll(this.invalid_directions);
		int select = rnd.nextInt(valid.size());
		int move = valid.get(select);
		this.drone = this.drone.nextPosition(Direction.compass.get(move));
		String direction = Direction.directions_str[move]; 
		return direction;
	}

}

