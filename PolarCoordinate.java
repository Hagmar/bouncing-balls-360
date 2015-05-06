public class PolarCoordinate {
	private double phi, r;

	public PolarCoordinate(double phi, double r) {
		this.phi = phi;
		this.r = r;
	}

	public double getPhi() {
		return phi;
	}

	public double getR() {
		return r;
	}

	public void setPhi(double phi) {
		this.phi = phi;
	}

	public void setR(double r) {
		this.r = r;
	}
	
	public void rotate(double r){
		this.r = (this.r + r) % (2*Math.PI);
	}
	
	@Override
	public String toString() {
		return "phi: " + Double.toString(phi) + " r: " + Double.toString(r);
	}
}
