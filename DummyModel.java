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
		balls.add(new Ball("two", 1, 1, 1, 4, 5, 1, 9.81));
		balls.add(new Ball("two", 1, 1, 1, 4, 10, 1, 9.81));
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
