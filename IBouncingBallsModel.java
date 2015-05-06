import java.awt.geom.Ellipse2D;
import java.util.List;

/**
 * Your bouncing balls model must adhere to this interface in order to make use
 * of the pre-written classes for drawing the balls.
 * 
 * @author Oscar Soderlund
 * 
 */
public interface IBouncingBallsModel {
	/**
	 * Returns a list of shape representations of the balls. Used by the
	 * BouncingBalls class to draw the balls.
	 * 
	 * @return the balls as shape objects
	 */
	public List<Ellipse2D> getBalls();

	/**
	 * Changes the state of the model using the Euler method by simulating
	 * deltaT units of time.
	 * 
	 * @param deltaT
	 *            the amount of time to simulate
	 */
	public void tick(double deltaT);
}
