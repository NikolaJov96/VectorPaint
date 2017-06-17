package paint;

import java.awt.*;

public class BoundingBox implements Drawable {

	public static final double BB_SPACING = 10;
	
	private static final Color COLOR = new Color(0, 0, 255);
	
	private Point position;
	private double width, height;
	
	public BoundingBox(Point position, double width, double height) {
		this.position = new Point(position);
		this.width = width;
		this.height = height;
	}
	
	public BoundingBox(Point topLeft, Point bottomRight) {
		this (new Point((bottomRight.getX() + topLeft.getX()) / 2, (topLeft.getY() + bottomRight.getY()) / 2),
				bottomRight.getX() - topLeft.getX(), 
				topLeft.getY() - bottomRight.getY());
	}

	@Override
	public void paint(Graphics2D g) {
		Point topLeft = ContextState.getAdjustedPosition(
				new Point(position.getX() - width / 2, position.getY() - height / 2));
		Point bottomRight= ContextState.getAdjustedPosition(
				new Point(position.getX() + width / 2, position.getY() + height / 2));
		
		g.setColor(COLOR);
		g.setStroke(new BasicStroke(1));
		g.drawRect((int)( topLeft.getX() ), 
				(int)( topLeft.getY() ), 
				(int)( bottomRight.getX() - topLeft.getX() ),
				(int)( bottomRight.getY() - topLeft.getY()));
		g.fillOval((int)( topLeft.getX() - BB_SPACING / 2), 
				(int)( topLeft.getY() - BB_SPACING / 2), 
				(int)(BB_SPACING), (int)(BB_SPACING));
		g.fillOval((int)( topLeft.getX() - BB_SPACING / 2), 
				(int)( bottomRight.getY() - BB_SPACING / 2), 
				(int)(BB_SPACING), (int)(BB_SPACING));
		g.fillOval((int)( bottomRight.getX() - BB_SPACING / 2), 
				(int)( topLeft.getY() - BB_SPACING / 2), 
				(int)(BB_SPACING), (int)(BB_SPACING));
		g.fillOval((int)( bottomRight.getX() - BB_SPACING / 2), 
				(int)( bottomRight.getY() - BB_SPACING / 2), 
				(int)(BB_SPACING), (int)(BB_SPACING));
	}
	
}
