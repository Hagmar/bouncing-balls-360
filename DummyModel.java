import java.util.Random;
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
		balls.add(new Ball("two", 1, 5, 1, 24, 5, 0.1, 9.81));
		balls.add(new Ball("two", 6, 5, 2, 23, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 3, 22, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 4, 21, 1, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 5, 20, 14, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 6, 19, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 7, 18, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 8, 17, 9, 0.1, 9.81));
		balls.add(new Ball("two", 1, 5, 9, 16, 5, 0.1, 9.81));
		balls.add(new Ball("two", 6, 5, 10, 15, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 11, 14, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 12, 13, 1, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 13, 12, 14, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 14, 11, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 15, 10, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 16, 9, 9, 0.1, 9.81));
		balls.add(new Ball("two", 1, 5, 17, 8.3, 5, 0.1, 9.81));
		balls.add(new Ball("two", 6, 5, 18, 7.3, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 19, 6, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 20, 5, 1, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 21, 4, 14, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 22, 3, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 23, 2, 5, 0.1, 9.81));
		balls.add(new Ball("one", 1, 1, 24, 1, 9, 0.1, 9.81));

	}

	private void calculateBounce(Ball a, Ball b){
		if (a.collidesWith(b)){
			a.setX(1);
			b.setX(10);
		}
	}
	
	private void calculateBounces(){
		for(int i=0; i<balls.size()-1; i++){
			for(int j=i+1; j<balls.size(); j++){
				calculateBounce(balls.get(i), balls.get(j));
			}
		}
	}

	@Override
	public void tick(double deltaT) {
		for(Ball ball : balls){
			ball.tick(deltaT, areaWidth, areaHeight);
		}
		
		calculateBounces();
	}
	


	@Override
	public List<Ellipse2D> getBalls() {
		List<Ellipse2D> myBalls = new LinkedList<Ellipse2D>();
		for (Ball ball : balls){
			myBalls.add(ball.getEllipse2D());
		}
		return myBalls;
	}
}
