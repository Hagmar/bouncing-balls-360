import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferStrategy;

import javax.swing.JApplet;

/**
 * A basis for animated applets. The animation loop is controlled by a desired
 * framerate.
 * 
 * The framerate is automatically adjusted to accommodate time consuming
 * animation.
 * 
 * This class can be left unmodified for the bouncing balls lab. :)
 * 
 * @author Oscar Soderlund
 * 
 */
@SuppressWarnings("serial")
public abstract class Animator extends JApplet {

	private static final double MAX_FRAMERATE = 60;
	private static final double MIN_FRAMERATE = 10;

	private double fps;
	private double timePerFrame;
	private volatile Thread animationThread;
	protected int canvasWidth;
	protected int canvasHeight;
	private Canvas canvas;
	private BufferStrategy buffer;

	protected abstract void drawFrame(Graphics2D g);

	@Override
	public void init() {
		Dimension appletDimension = getSize();
		canvasWidth = appletDimension.width;
		canvasHeight = appletDimension.height;
		setFrameRate(MAX_FRAMERATE);
		// Set up animation tools
		canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		canvas.setSize(canvasWidth, canvasHeight);
		add(canvas);
		// Set up double buffer
		canvas.createBufferStrategy(2);
		buffer = canvas.getBufferStrategy();
	}

	@Override
	public void start() {
		// As soon as the applet starts or restarts, a separate thread is
		// spawned to handle the animation
		animationThread = new Thread() {
			@Override
			public void run() {
				animationLoop();
			}
		};
		// animationThread.setPriority(Thread.MAX_PRIORITY);
		animationThread.start();
	}

	@Override
	public void stop() {
		animationThread.interrupt();
		animationThread = null;
	}

	protected void setFrameRate(double fps) {
		this.fps = fps;
		timePerFrame = (1000000000 / fps);
	}

	private void animationLoop() {
		while (Thread.currentThread() == animationThread) {
			try {
				double frameStartTime = System.nanoTime();
				animateNextFrame();
				double currentFrameTime = System.nanoTime() - frameStartTime;
				int timeToNextFrame = (int) ((timePerFrame - currentFrameTime) / 1000000);
				if (timeToNextFrame > 0) {
					// Adjust frame rate to minimize sleep time
					if (fps < MAX_FRAMERATE && timeToNextFrame > 1) {
						setFrameRate(calculateOptimalFrameRate(currentFrameTime));
					}
					Thread.sleep(timeToNextFrame);
				} else if (fps > MIN_FRAMERATE) {
					// Inadequate time left, decrease the frame rate
					setFrameRate(calculateOptimalFrameRate(currentFrameTime));
				}
			} catch (InterruptedException e) {
			} catch (IllegalStateException e) {
			}
		}
	}

	private void animateNextFrame() {
		// Animate next frame on buffer
		Graphics2D g = (Graphics2D) buffer.getDrawGraphics();
		drawFrame(g);
		// Display the buffer
		if (!buffer.contentsLost()) {
			buffer.show();
		}
		g.dispose();
		// Repainting solves the problem of the applet not updating properly
		// on the Red Hat Linux computers.
		repaint();
	}

	private double calculateOptimalFrameRate(double currentFrameTime) {
		double optimalFrameRate = 1000000000 / (currentFrameTime + 1000000);
		return Math.min(Math.max(optimalFrameRate, MIN_FRAMERATE),
				MAX_FRAMERATE);
	}
}
