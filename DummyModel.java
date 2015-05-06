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
		balls.add(new Ball("two", 1, 1, 1, 4, 5, 1, 9.81));
		balls.add(new Ball("two", 1, 1, 1, 4, 10, 1, 9.81));
	}

	private class Tuple<X, Y> {
		private final X x;
		private final Y y;

		public Tuple(X x, Y y) {
			this.x = x;
			this.y = y;
		}

		public X getX() {
			return x;
		}

		public Y getY() {
			return y;
		}
		public String toString() {
			return x.toString() + " " + y.toString();
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
		}
	}

	private PolarCoordinate rectToPolar(Tuple<Double, Double> t){
		return rectToPolar(t.getX(), t.getY());
	}
	
	private PolarCoordinate rectToPolar(double x, double y) {
		double length = Math.sqrt(x * x + y * y);
		double phi = Math.acos(x / length);
		if (y < 0) {
			phi += Math.PI;
		}
		return new PolarCoordinate(phi, length);
	}
	
	private Tuple<Double, Double> getRekt(PolarCoordinate p){
		double x = Math.cos(p.getPhi())*p.getR();
		double y = Math.sin(p.getPhi())*p.getR();
		if (p.getPhi() > Math.PI && p.getPhi() < 3*Math.PI/4){
			y = -y;
		}
		return new Tuple<Double, Double>(x, y);
	}

	private double axisRotation(double x1, double y1, double x2, double y2){
		double length = Math.sqrt(Math.pow(x1-x2, 2)+Math.pow(y1+y2, 2));
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
