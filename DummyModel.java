import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.List;

public class DummyModel implements IBouncingBallsModel {

	private final double areaWidth;
	private final double areaHeight;

	private double x, y, vx, vy, r, g;

	List<Ball> balls;

	public DummyModel(double width, double height) {
		this.areaWidth = width;
		this.areaHeight = height;

		balls = new LinkedList<Ball>();
		balls.add(new Ball("two", 1, 1, 5, 7, 5, 1, 9.81));
		balls.add(new Ball("two", 1, 3, 1, 4, 10, 1, 9.81));
//		balls.add(new Ball("two", 1, 5, 5, 7, 5, 0.5, 9.81));
//		balls.add(new Ball("two", 1, 7, 1, 4, 10, 0.5, 9.81));
//		balls.add(new Ball("two", 1, 9, 5, 7, 5, 0.5, 9.81));
//		balls.add(new Ball("two", 1, 11, 1, 4, 10, 0.5, 9.81));
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

	private void calculateBounce(Ball a, Ball b) {
		if (a.collidesWith(b)) {
			PolarCoordinate aSpeed = rectToPolar(a.getVx(), a.getVy());
			PolarCoordinate bSpeed = rectToPolar(b.getVx(), b.getVy());
			double axisRotation = axisRotation(a.getX(), a.getY(), b.getX(), b.getY());
			aSpeed.rotate(axisRotation);
			bSpeed.rotate(axisRotation);
			Tuple<PolarCoordinate, PolarCoordinate> newSpeeds = performBounce(a.getM(), aSpeed, b.getM(), bSpeed);
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
	}
	
	private Tuple<PolarCoordinate, PolarCoordinate> performBounce(double ma, PolarCoordinate a, double mb, PolarCoordinate b){
		Tuple<Double, Double> aSpeedRect = getRekt(a);
		Tuple<Double, Double> bSpeedRect = getRekt(b);
		double aX = aSpeedRect.getFirst();
		double aY = aSpeedRect.getSecond();
		double bX = bSpeedRect.getFirst();
		double bY = bSpeedRect.getSecond();
		
		double R = aY-bY;
		double I = ma*aY + mb*bY;
		
		double aSpeedY = (I-mb*R)/(ma+mb);
		double bSpeedY = R+aSpeedY;
		
		PolarCoordinate aSpeed = rectToPolar(aX, aSpeedY);
		PolarCoordinate bSpeed = rectToPolar(bX, bSpeedY);
		return new Tuple<PolarCoordinate, PolarCoordinate>(aSpeed, bSpeed);
	}

	private PolarCoordinate rectToPolar(Tuple<Double, Double> t){
		return rectToPolar(t.getFirst(), t.getSecond());
	}
	
	private PolarCoordinate rectToPolar(double x, double y) {
		double length = Math.sqrt(x * x + y * y);
		double phi = Math.acos(x / length);
		if (y < 0) {
			phi *= -1;
			phi += 2*Math.PI;
		}
		return new PolarCoordinate(phi, length);
	}
	
	private Tuple<Double, Double> getRekt(PolarCoordinate p){
		double x = Math.cos(p.getPhi())*p.getR();
		double y = Math.sin(p.getPhi())*p.getR();
		return new Tuple<Double, Double>(x, y);
	}

	private double axisRotation(double x1, double y1, double x2, double y2){
		double length = Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1-y2, 2));
		double angle = Math.acos(Math.abs(y1-y2)/length);
		return angle;
	}
	
	@Override
	public void tick(double deltaT) {
		for (Ball ball : balls) {
			ball.tick(deltaT, areaWidth, areaHeight);
		}
		calculateBounces();
	}

	@Override
	public List<Ellipse2D> getBalls() {
		List<Ellipse2D> myBalls = new LinkedList<Ellipse2D>();
		for (Ball ball : balls) {
			myBalls.add(ball.getEllipse2D());
		}
		return myBalls;
	}
}
