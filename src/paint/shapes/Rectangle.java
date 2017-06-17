package paint.shapes;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.Scanner;

import paint.*;

public class Rectangle extends paint.Shape {
	
	public static final double SELECTION_SPACING = 7;
	
	private double width, height;

	public Rectangle(Point position, double width, double height) {
		super (position);
		this.width = width;
		this.height = height;
	}
	
	public Rectangle(Scanner scanner) { // Unpack constructor
		super (scanner);
		width = scanner.nextDouble();
		height = scanner.nextDouble();
	}

	@Override
	public void paint(Graphics2D g) {
		Point topLeft = ContextState.getAdjustedPosition(
				new Point(position.getX() - width / 2, position.getY() - height / 2));
		Point bottomRight = ContextState.getAdjustedPosition(
				new Point(position.getX() + width / 2, position.getY() + height / 2));
		
		g.setColor(color);
		g.setStroke(new BasicStroke(lineWidthId * STROKE_MUL));
		
		g.drawRect((int)( topLeft.getX() ),
				(int)( topLeft.getY() ),  
				(int)( bottomRight.getX() - topLeft.getX() ),
				(int)( bottomRight.getY() - topLeft.getY()));		
	}

	@Override
	public BoundingBox getBoundingBox() {
		return new BoundingBox(position, width + 2 * BoundingBox.BB_SPACING, height + 2 * BoundingBox.BB_SPACING);
	}

	@Override
	public boolean containsPoint(Point point) {
		if (point.getX() < position.getX() - width / 2 - SELECTION_SPACING) return false;
		if (point.getX() > position.getX() + width / 2 + SELECTION_SPACING) return false;
		if (point.getY() < position.getY() - height / 2 - SELECTION_SPACING) return false;
		if (point.getY() > position.getY() + height / 2 + SELECTION_SPACING) return false;

		if (point.getX() > position.getX() - width / 2 + SELECTION_SPACING &&
				point.getX() < position.getX() + width / 2 - SELECTION_SPACING &&
				point.getY() > position.getY() - height / 2 + SELECTION_SPACING &&
				point.getY() < position.getY() + height / 2 - SELECTION_SPACING) return false;
		
		return true;
	}
	
	@Override
	public String packShape() {
		StringBuilder sb = new StringBuilder("2 ");
		sb.append(super.packShape()).append(" ").append(width).append(" ").append(height);
		return sb.toString();
	}

}
