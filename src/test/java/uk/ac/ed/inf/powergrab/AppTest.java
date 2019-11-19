package uk.ac.ed.inf.powergrab;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for PowerGrab App.
 */
public class AppTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName name of the test case
	 */
	public AppTest(String testName) {
		super( testName );
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(AppTest.class);
	}

	public void testPositionConstructor() {
		assertTrue( new Position(55.944425, -3.188396) != null );
	}

	final Position p0 = new Position(55.944425, -3.188396);

	public void testPositionLatitude() {
		assertTrue(p0.latitude == 55.944425);
	}

	public void testPositionLongitude() {
		assertTrue(p0.longitude == -3.188396);
	}
	
	public void testCentreInPlayArea() {
		assertTrue(p0.inPlayArea());
	}
	
	public void testBoundaryOutOfPlayArea() {
		Position pos = new Position(55.946233, -3.192473);
		assertFalse(pos.inPlayArea());
	}
	
	public void testPositionOutOfPlayArea() {
		Position pos = new Position(55.942000, -3.192000);
		assertFalse(pos.inPlayArea());
	}
	
	boolean approxEq(double d0, double d1) {
		final double epsilon = 1.0E-12d;
		return Math.abs(d0 - d1) < epsilon;
	}
	
	boolean approxEq(Position p0, Position p1) {
		return approxEq(p0.latitude, p1.latitude) && approxEq(p0.longitude, p1.longitude); 
	}
	
}
