import java.awt.geom.Ellipse2D;
import java.util.HashMap;
import java.util.Random;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.text.StyledEditorKit.BoldAction;

public class PhysicsModel implements IBouncingBallsModel {

	private static final double G_CONST = 0.01;
	private static final double TIME_CONST = 1;

	private final double areaWidth;
	private final double areaHeight;

	List<PhysicsBall> balls;
	HashMap<PhysicsBall, HashMap<PhysicsBall, Boolean>> collisions;

	public PhysicsModel(double width, double height) {
		this.areaWidth = width;
		this.areaHeight = height;

		balls = new LinkedList<PhysicsBall>();
		collisions = new HashMap<PhysicsBall, HashMap<PhysicsBall, Boolean>>();

		Random rand = new Random();
		for (int i = 0; i < 1; i++) {
			addBall(10000, areaWidth / 2, areaHeight / 2, 0, 0, 1, 0);
			addBall(1, areaWidth/2, 1, -10, 0, 1, 0);
		}
	}
	
	private void addBall(){
		Random rand = new Random();
		addBall(1, rand.nextInt((int) areaWidth - 2) + 1,
				rand.nextInt((int) areaHeight - 2) + 1, rand.nextInt(20),
				rand.nextInt(20), 0.5, 9.81);
	}
	
	private void addBall(double m, double x, double y, double vx, double vy, double r, double g){
		PhysicsBall b = new PhysicsBall(m, x, y, vx, vy, r, g);
		HashMap<PhysicsBall, Boolean> hm = new HashMap<PhysicsBall, Boolean>();
		collisions.put(b, hm);
		for (PhysicsBall ball : balls) {
			hm.put(ball, new Boolean(false));
		}
		balls.add(b);
	}

	private class Tuple<X, Y> {
		private final X first;
		private final Y second;

		public Tuple(X x, Y y) {
			this.first = x;
			this.second = y;
		}

		public X getFirst() {
			return first;
		}

		public Y getSecond() {
			return second;
		}

		public String toString() {
			return first.toString() + " " + second.toString();
		}
	}

	private void calculateBounces() {
		for (int i = 0; i < balls.size() - 1; i++) {
			for (int j = i + 1; j < balls.size(); j++) {
				calculateBounce(balls.get(i), balls.get(j));
			}
		}
	}

	private void calculateBounce(PhysicsBall a, PhysicsBall b) {
		if (a.collidesWith(b)) {
			if (!collisions.get(b).get(a)) {
				collisions.get(b).put(a, new Boolean(true));
				PolarCoordinate aSpeed = rektToPolar(a.getVx(), a.getVy());
				PolarCoordinate bSpeed = rektToPolar(b.getVx(), b.getVy());
				double axisRotation = axisRotation(a.getX(), a.getY(),
						b.getX(), b.getY());
				aSpeed.rotate(axisRotation);
				bSpeed.rotate(axisRotation);
				Tuple<PolarCoordinate, PolarCoordinate> newSpeeds = performBounce(
						a.getM(), aSpeed, b.getM(), bSpeed);
				aSpeed = newSpeeds.getFirst();
				bSpeed = newSpeeds.getSecond();
				aSpeed.rotate(-axisRotation);
				bSpeed.rotate(-axisRotation);
				Tuple<Double, Double> aRektSpeed = getRekt(aSpeed);
				Tuple<Double, Double> bRektSpeed = getRekt(bSpeed);
				a.setVx(aRektSpeed.getFirst());
				a.setVy(aRektSpeed.getSecond());
				b.setVx(bRektSpeed.getFirst());
				b.setVy(bRektSpeed.getSecond());
			}
		} else {
			collisions.get(b).put(a, new Boolean(false));
		}
	}

	private Tuple<PolarCoordinate, PolarCoordinate> performBounce(double ma,
			PolarCoordinate a, double mb, PolarCoordinate b) {
		Tuple<Double, Double> aSpeedRekt = getRekt(a);
		Tuple<Double, Double> bSpeedRekt = getRekt(b);
		double aX = aSpeedRekt.getFirst();
		double aY = aSpeedRekt.getSecond();
		double bX = bSpeedRekt.getFirst();
		double bY = bSpeedRekt.getSecond();

		double R = aY - bY;
		double I = ma * aY + mb * bY;

		double aSpeedY = (I - mb * R) / (ma + mb);
		double bSpeedY = R + aSpeedY;

		PolarCoordinate aSpeed = rektToPolar(aX, aSpeedY);
		PolarCoordinate bSpeed = rektToPolar(bX, bSpeedY);
		return new Tuple<PolarCoordinate, PolarCoordinate>(aSpeed, bSpeed);
	}

	private PolarCoordinate rektToPolar(double x, double y) {
		if (x==0 && y==0){
			return new PolarCoordinate(0, 0);
		}
		double length = Math.sqrt(x * x + y * y);
		double phi = Math.acos(x / length);
		if (y < 0) {
			phi *= -1;
			phi += 2 * Math.PI;
		}

		return new PolarCoordinate(phi, length);
	}

	private Tuple<Double, Double> getRekt(PolarCoordinate p) {
		double x = Math.cos(p.getPhi()) * p.getR();
		double y = Math.sin(p.getPhi()) * p.getR();
		return new Tuple<Double, Double>(x, y);
	}

	private double axisRotation(double x1, double y1, double x2, double y2) {
		double length = Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
		double angle = Math.acos(Math.abs(y1 - y2) / length);
		if (((x1 > x2) && (y1 < y2)) || ((x2 > x1) && (y2 < y1))) {
			angle *= -1;
		}
		return angle;
	}

	@Override
	public void tick(double deltaT) {
		for (PhysicsBall ball : balls) {
			ball.tick(deltaT, areaWidth, areaHeight);
		}
		calculateBounces();
		calculatePhysics(deltaT);
	}

	private void calculatePhysics(double deltaT){
		for (PhysicsBall ball : collisions.keySet()){
			PolarCoordinate ballSpeed = rektToPolar(ball.getVx(), ball.getVy());
			for (PhysicsBall other: collisions.get(ball).keySet()){
				PolarCoordinate otherSpeed = rektToPolar(other.getVx(), other.getVy());
				double axisRotation = axisRotation(ball.getX(), ball.getY(),other.getX(), other.getY());
				ballSpeed.rotate(axisRotation);
				otherSpeed.rotate(axisRotation);
				double attraction = physicsAttraction(ball, other);
				PolarCoordinate ballAddSpeed = addAttraction(ball, attraction, deltaT);
				ballSpeed = addPolar(ballSpeed, ballAddSpeed);
				PolarCoordinate otherAddSpeed = addAttraction(other, -attraction, deltaT);
				otherSpeed = addPolar(otherSpeed, otherAddSpeed);
				ballSpeed.rotate(-axisRotation);
				otherSpeed.rotate(-axisRotation);
				Tuple<Double, Double> otherRektSpeed = getRekt(otherSpeed);
				other.setVx(otherRektSpeed.getFirst());
				other.setVy(otherRektSpeed.getSecond());
			}
			Tuple<Double, Double> ballRektSpeed = getRekt(ballSpeed);
			ball.setVx(ballRektSpeed.getFirst());
			ball.setVy(ballRektSpeed.getSecond());
		}
	}
	
	private double physicsAttraction(PhysicsBall a, PhysicsBall b){
		double divisor = Math.sqrt(Math.pow(a.getX()-b.getX(), 2)+Math.pow(a.getY()-b.getY(), 2));
		double attraction = G_CONST*a.getM()*b.getM()/divisor;
		return attraction;
	}
	
	private PolarCoordinate addAttraction(PhysicsBall b, double attraction, double time) {
		double y = attraction/b.getM()*time;

		PolarCoordinate newSpeed = rektToPolar(0, y);
		return newSpeed;
	}
	
	private PolarCoordinate addPolar(PolarCoordinate a, PolarCoordinate b){
		Tuple<Double, Double> aRekt = getRekt(a);
		Tuple<Double, Double> bRekt = getRekt(b);
		
		double x = aRekt.getFirst() + bRekt.getFirst();
		double y = aRekt.getSecond() + bRekt.getSecond();
		
		PolarCoordinate pc = rektToPolar(x, y);
		return pc;
	}
	
	@Override
	public List<Ellipse2D> getBalls() {
		List<Ellipse2D> myBalls = new LinkedList<Ellipse2D>();
		for (PhysicsBall ball : balls) {
			myBalls.add(ball.getEllipse2D());
		}
		return myBalls;
	}
}
