package paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Scanner;

public class Point implements Drawable {
	
	public static final Color DEFAULT_COLOR = new Color(0, 0, 255);
	public static final double DOT_RADIUS = 5;
	
	private double X, Y;
	
	public Point(double X, double Y) {
		this.X = X;
		this.Y = Y;
	}
	
	public Point(Point point) {
		this (point.X, point.Y);
	}
	
	public Point() {
		this (0, 0);
	}
	
	public double getX() {
		return X;
	}

	public double getY() {
		return Y;
	}
	
	public void setX(double X) {
		this.X = X;
	}
	
	public void setY(double Y) {
		this.Y = Y;
	}
	
	public double getDistanceSquared(Point p) {
		return (X - p.getX()) * (X - p.getX()) + (Y - p.getY()) * (Y - p.getY());   
	}
	
	public double getDistance(Point p1, Point p2) {
		double l2 = p1.getDistanceSquared(p2);
		if (l2 == 0) return Math.sqrt(getDistanceSquared(p1));
		double t = ((X - p1.getX()) * (p2.getX() - p1.getX()) + (Y - p1.getY()) * (p2.getY() - p1.getY())) / l2;
		t = Math.max(0, Math.min(1, t));
		Point p3 = new Point(p1.getX() + t * (p2.getX() - p1.getX()),
				p1.getY() + t * (p2.getY() - p1.getY()));
		return Math.sqrt(getDistanceSquared(p3));
	}

	@Override
	public void paint(Graphics2D g) {
		Point topLeft = ContextState.getAdjustedPosition(new Point(X, Y));
		g.setColor(DEFAULT_COLOR);
		g.setStroke(new BasicStroke(1));
		g.fillOval((int)( topLeft.getX() - DOT_RADIUS ),
				(int)( topLeft.getY() - DOT_RADIUS ),  
				(int)( 2 * DOT_RADIUS ),
				(int)( 2 * DOT_RADIUS ));
	}
	
	public String packShape() {
		return X + " " + Y;
	}
	
	public void unpackShape(Scanner scanner) {
		X = scanner.nextDouble();
		Y = scanner.nextDouble();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point point = (Point) obj;
			if (X != point.X || Y != point.Y) return false;
			return true;
		}
		return super.equals(obj);
	}
	
}
