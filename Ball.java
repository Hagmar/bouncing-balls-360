import java.awt.geom.Ellipse2D;
import java.util.LinkedList;
import java.util.List;

public class Ball {
	private double m, x, y, vx, vy, r, g;
	public String name;	
	public Ball(String name, double m, double x, double y, double vx, double vy, double r, double g){
		this.name = name;
		this.m = m;
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.r = r;
		this.g = g;
	}
	
	public Ball(String name, double m, double x, double y, double vx, double vy, double r){
		this(name, m, x, y, vx, vy, r, 9.81);
	}

	public void tick(double deltaT, double areaWidth, double areaHeight) {
		if (x < r || x > areaWidth - r) {
			vx *= -1;
		}
		if (y < r || y > areaHeight - r) {
			vy *= -1;
		} else {
			vy -= g * deltaT;
		}
		x += vx * deltaT;
		y += vy * deltaT;
	}
	
	public boolean collidesWith(Ball other) {
		double totalR = r + other.getR();
		double distance = Math.sqrt(Math.pow(x-other.getX(), 2) + Math.pow(y-other.getY(), 2));
		return totalR >= distance;// <= distance;
	}
	
	public double getX() {
		return x;
	}
	
	public void setX(double x){
		this.x = x;
	}

	public void setY(double y){
		this.y = y;
	}	
	public double getY() {
		return y;
	}
	public double getR() {
		return r;
	}

	
	public Ellipse2D.Double getEllipse2D(){
		return new Ellipse2D.Double(x - r, y - r, 2 * r, 2 * r);
	}
}