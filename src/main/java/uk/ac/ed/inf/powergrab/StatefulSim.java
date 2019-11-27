package uk.ac.ed.inf.powergrab;

import java.util.Random;

public class StatefulSim extends Stateful{
	
	public StatefulSim(double latitude, double longitude) {
		super(latitude, longitude);
	}
	
	//Determine the angle between the drone's current position and the nearest positive charging station
	protected double angleNearestPositive() {
		double [] distance_pos = new double[this.positive.size()];
		for (int i=0; i<this.positive.size(); i++) {
			Station s = this.positive.get(i);
			distance_pos[i] = Distance.calculateDistance(this.drone.latitude, this.drone.longitude, s.coordinates[0], s.coordinates[1]);	
		}
		//Find the angle between current position and nearest positive charging station
		int index_pos = Distance.minIndex(distance_pos);
		Station s_pos = this.positive.get(index_pos);
		System.out.println("The nearest positive charging station is " + s_pos.id);
		double angle = Math.atan((this.drone.longitude-s_pos.coordinates[1])/(this.drone.latitude-s_pos.coordinates[0]));
		double adjusted_angle;
		if(drone.longitude > s_pos.coordinates[1]) {
			if (angle <= 0) {
				adjusted_angle = (2 * Math.PI + angle);
			} else {
				adjusted_angle = (Math.PI + angle);
			}
		} else {
			if (angle >= 0) {
				adjusted_angle = angle;
			} else {
				adjusted_angle = (Math.PI + angle);
			}
		}
		System.out.println("Adjusted angle is " + Math.toDegrees(adjusted_angle));
		return adjusted_angle;
	}
	
	protected void determineAvailableMoves() {
		for (int i=0; i<16; i++) {
			//Test for valid directions within the next 16 possible moves
			Drone drone_test = this.drone;
			drone_test = drone_test.nextPosition(Direction.compass.get(i));
			if (drone_test.inPlayArea()) {
				this.valid_directions.add(i);
				double [] distance_neg = new double[negative.size()];
				for (int j=0; j<negative.size(); j++) {
					Station s = negative.get(j);
					distance_neg[j] = Distance.calculateDistance(drone_test.latitude, drone_test.longitude, s.coordinates[0], s.coordinates[1]);
				}
				//Test for valid directions without negative charging station within the next 16 possible moves
				if (Distance.minDist(distance_neg) > 0.00025) {
					this.preferred_directions.add(i);
				}
			}
		}
	}
	
	//Determine the two preferred directions to move towards the next nearest positive charging station
	protected void determinePreferredDirection(double adjusted_angle) {
		for (int i=1; i<17; i++) {
			if (adjusted_angle - Direction.directions_angle[i] < 0) {
				if (Math.abs(adjusted_angle - Direction.directions_angle[i]) < adjusted_angle - Direction.directions_angle[i-1]) {
					this.first_preferred_direction = i;
					this.second_preferred_direction = i-1;
				} else {
					this.first_preferred_direction = i-1;
					this.second_preferred_direction = i;
				}
				break;
			}
		}
		if (this.first_preferred_direction == 16) this.first_preferred_direction = 0;
		if (this.second_preferred_direction == 16) this.second_preferred_direction = 0;
	}
	
	//Move towards the preferred directions if there are no negative charging stations
	//Else choose a random direction that avoids negative charging stations
	protected String meaningfulMoves(Random rnd) {
		String direction;
		int move;
		if (this.preferred_directions.size() > 0) {
			if (this.preferred_directions.contains(this.first_preferred_direction)) {
				move = this.first_preferred_direction;
			} else if (this.preferred_directions.contains(this.second_preferred_direction)) {
				move = this.second_preferred_direction;
			} else {
				int select = rnd.nextInt(this.preferred_directions.size());
				move = this.preferred_directions.get(select);
			}
		} else {
			int select = rnd.nextInt(this.valid_directions.size());
			move = this.valid_directions.get(select);
		}
		this.drone = this.drone.nextPosition(Direction.compass.get(move));
		direction = Direction.directions_str[move]; 
		return direction;
	}
	
	//Move towards a random direction that avoids negative charging stations
	protected String randomMoves(Random rnd) {
		String direction;
		int move;
		if (this.preferred_directions.size() > 0) {
			int select = rnd.nextInt(this.preferred_directions.size());
			move = this.preferred_directions.get(select);
		} else {
			int select = rnd.nextInt(this.valid_directions.size());
			move = this.valid_directions.get(select);
		}
		this.drone = this.drone.nextPosition(Direction.compass.get(move));
		direction = Direction.directions_str[move]; 
		return direction;
	}
	
	//Update drone's fields when a charging station is within range
	protected int connectToChargingStation(int count) {
		count++;
		double[] distance = new double[50];
		for (int i=0; i<50; i++) {
			Station s = this.stations.get(i);
			distance[i] = Distance.calculateDistance(this.drone.latitude, this.drone.longitude, s.coordinates[0], s.coordinates[1]);
	    }
	    if (Distance.minDist(distance) <= 0.00025) {
	    	System.out.println("A charging station is within range!");
			int index = Distance.minIndex(distance);
			Station s = this.stations.get(index);
			this.drone.updateCoin(s.coins);
			this.drone.updatePower(s.power);
			s.coins = 0;
			s.power = 0;
			this.stations.set(index, s);
			for (int i=0; i<this.positive.size(); i++) {
				Station s_pos = this.positive.get(i);
				if (s.id.equals(s_pos.id)) {
					this.positive.remove(i);
					count = 0;
					break;
				}
			}
			for (int i=0; i<this.negative.size(); i++) {
				Station s_neg = this.negative.get(i);
				if (s.id.equals(s_neg.id)) {
					this.negative.remove(i);
					break;
				}
			}
		} 
	    return count;
	}
}
