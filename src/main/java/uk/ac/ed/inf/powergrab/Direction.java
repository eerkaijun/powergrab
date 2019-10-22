package uk.ac.ed.inf.powergrab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Direction {
	
	public final double angle;
	
	public static final Direction N = new Direction(0);
	public static final Direction NNE = new Direction(22.5);
	public static final Direction NE = new Direction(45);
	public static final Direction ENE = new Direction(67.5);
	public static final Direction E = new Direction(90);
	public static final Direction ESE = new Direction(112.5);
	public static final Direction SE = new Direction(135);
	public static final Direction SSE = new Direction(157.5);
	public static final Direction S = new Direction(180);
	public static final Direction SSW = new Direction(202.5);
	public static final Direction SW = new Direction(225);
	public static final Direction WSW = new Direction(247.5);
	public static final Direction W = new Direction(270);
	public static final Direction WNW = new Direction(292.5);
	public static final Direction NW = new Direction(315);
	public static final Direction NNW = new Direction(337.5);
	
	static Direction[] directions = {N, NNE, NE, ENE, E, ESE, SE, SSE, S, SSW, SW, WSW, W, WNW, NW, NNW};
	static List<Direction> compass = new ArrayList<Direction>(Arrays.asList(directions));
	
	public Direction(double angle) {
		this.angle = Math.toRadians(angle);
	}

}
