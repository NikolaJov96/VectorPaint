package paint.shapes;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.util.Scanner;
import java.util.Vector;

import paint.BoundingBox;
import paint.ContextState;
import paint.Point;
import paint.Shape;

public class CompositeLine extends Shape {
	
	public static final double SELECTION_SPACING = 7;
	
	private Vector<Point> points; // relative to center position
	private double width, height;
	
	public CompositeLine(Point position, Vector<Point> points) {
		super(position);
		this.points = points;
		for (Point point : points) {
			if (point.getX() > width) width = point.getX();
			if (point.getY() > height) height = point.getY();
		}
		width *= 2;
		height *= 2;
	}
	
	public CompositeLine(Scanner scanner) {
		super (scanner);
		width = scanner.nextDouble();
		height = scanner.nextDouble();
		points = new Vector<Point>();
		int pointsCo = scanner.nextInt();
		for (int i = 0; i < pointsCo; i++) {
			points.add(new Point(scanner.nextDouble(), scanner.nextDouble()));
		}
	}

	@Override
	public void paint(Graphics2D g) {
		Point adjPrev = null;
		for (Point curr : points) {
			Point adjCurr = ContextState.getAdjustedPosition(
					new Point(curr.getX() + position.getX(), curr.getY() + position.getY()));
			if (adjPrev != null) { 
				g.setColor(color);
				g.setStroke(new BasicStroke(lineWidthId * STROKE_MUL));
				g.drawLine((int)( adjPrev.getX() ), 
						(int)( adjPrev.getY() ), 
						(int)( adjCurr.getX() ), 
						(int)( adjCurr.getY() ));
			}
			adjPrev = adjCurr;
		}
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
		Point adjPrev = null;
		for (Point curr : points) {
			Point adjCurr = new Point(curr.getX() + position.getX(), curr.getY() + position.getY());
			if (adjPrev != null && point.getDistance(adjPrev, adjCurr) <= SELECTION_SPACING) return true;
			adjPrev = adjCurr;
		}
		return false;
	}

	@Override
	public String packShape() {
		StringBuilder sb = new StringBuilder("1 ");
		sb.append(super.packShape()).append(" ");
		sb.append(width).append(" ").append(height).append(" ").append(points.size());
		for (Point point : points) sb.append(" ").append(point.packShape());
		return sb.toString();
	}
	
}
