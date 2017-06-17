package paint;

import java.awt.Color;
import java.util.Scanner;

public class ContextState {
	
	public static Color selectedColor = new Color(0, 0, 0);
	public static int lineWidthId = 1;
	
	public static double scale = 1.0; 
	public static Point worldCenter = new Point(0, 0);
	
	public static void resetContext() {
		selectedColor = new Color(0, 0, 0);
		lineWidthId = 1;
		scale = 1.0;
		worldCenter = new Point(0, 0);
	}
	
	public static Point getAdjustedPosition(Point point) {
		return new Point(point.getX() * scale + worldCenter.getX() + Canvas.CANVAS_WIDTH / 2.0,
				point.getY() * scale + worldCenter.getY() + Canvas.CANVAS_HEIGHT / 2.0);
	}
	
	public static Point getWorldPosition(Point point) {
		return new Point((point.getX() - worldCenter.getX() - Canvas.CANVAS_WIDTH / 2.0) / scale,
				(point.getY() - worldCenter.getY() - Canvas.CANVAS_HEIGHT / 2.0) / scale);
	}
	
	public static void moveWorld(double dX, double dY) {
		worldCenter = new Point(worldCenter.getX() + dX * scale, worldCenter.getY() + dY * scale);
	}
	
	public static void scaleWorld(double ratio) {
		scale *= ratio;
	}
	
	public static String packColor(Color color) {
		return color.getRed() + " " + color.getGreen() + " " + color.getBlue();
	}
	
	public static String packContext() {
		return packColor(selectedColor) + " " + lineWidthId + " " + scale + " " + worldCenter.packShape(); 
	}
	
	public static void unpackContext(Scanner scanner) {
		selectedColor = new Color(scanner.nextInt(), scanner.nextInt(), scanner.nextInt());
		lineWidthId = scanner.nextInt();
		scale = scanner.nextDouble();
		worldCenter.unpackShape(scanner);
	}
	
}
