package paint;

import java.awt.Color;
import java.util.Scanner;

public abstract class Shape implements Drawable {
	
	protected Point position;
	protected Color color;
	protected int lineWidthId;
	
	protected Shape(Point position) {
		this.position = position;
		this.color = ContextState.selectedColor;
		this.lineWidthId = ContextState.lineWidthId;
	}
	
	protected Shape(Scanner scanner) { // Unpack constructor
		position = new Point(scanner.nextDouble(), scanner.nextDouble());
		color = new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
		lineWidthId = scanner.nextInt();
	}
	
	public void move(double dX, double dY) {
		position.setX(position.getX() + dX);
		position.setY(position.getY() + dY);
	}
	
	public void setPosition(Point point) {
		position.setX(point.getX());
		position.setY(point.getY());
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setLineWidthId(int id) {
		this.lineWidthId = id;
	}
	
	public Point getPosition() {
		return position;
	}
	
	public Color getColor() {
		return color;
	}
	
	public int getLineWidthId() {
		return lineWidthId;
	}
	
	public String packShape() {
		return position.packShape() + " " + ContextState.packColor(color) + " " + lineWidthId;
	}

	public abstract BoundingBox getBoundingBox();
	
	public abstract boolean containsPoint(Point point);

}
