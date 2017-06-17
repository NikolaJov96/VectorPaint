package paint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Scanner;
import java.util.Vector;

import javax.swing.*;

import paint.shapes.CompositeLine;
import paint.shapes.Rectangle;

@SuppressWarnings("serial")
public class Canvas extends JPanel {
	
	public enum Mode { SELECT, DELETE, LINE, MULTI_LINE, CLOSED_LINE, RECTANGLE };

	public static int CANVAS_WIDTH = 0;
	public static int CANVAS_HEIGHT = 0;
	
	private Mode selectedMode = Mode.SELECT;
	
	ShapeList shapeList = new ShapeList();
	
	Shape selectedShape, tempSelectedShape;
	
	private Vector<Point> refPoints = new Vector<Point>();
	private Shape tempShape;
	private Point mousePressedPoint;
	private Point oldShapePosition;
	private Point prevClicked; 
	private volatile boolean mouseDown;
	
	{	
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				handleClick(e);
			}
			@Override
			public void mousePressed(MouseEvent e) {
				handlePress(new Point(ContextState.getWorldPosition(new Point(e.getX(), e.getY()))));
			}
			@Override
			public void mouseReleased(MouseEvent e) {
				handleRelease(new Point(ContextState.getWorldPosition(new Point(e.getX(), e.getY()))));
			}
		});
		
		addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				if (e.getWheelRotation() > 0) ContextState.scaleWorld(0.9);
				else if (e.getWheelRotation() < 0) ContextState.scaleWorld(1.1);
			}
		});
	}
	
	public synchronized void setMode(Mode mode) {
		if (mode == Mode.DELETE && selectedShape != null) {
			shapeList.removeShape(selectedShape);
			MainWindow.mainWindow.addModification(new ShapeDeletedMod(selectedShape));
		}
		selectedMode = mode;
		selectedShape = null;
		tempShape = null;
		refPoints.removeAllElements();
	}
	
	public synchronized Mode getMode() {
		return selectedMode;
	}
	
	public synchronized void addShape(Shape shape) {
		shapeList.addShape(shape);
		MainWindow.mainWindow.addModification(new ShapeAddedMod(shape));
	}
	
	public synchronized void reset() {
		selectedMode = Mode.SELECT;
		selectedShape = null;
		refPoints.removeAllElements();
		shapeList.removeAll();
	}

	public void updateColor() {
		if (selectedShape != null) {
			MainWindow.mainWindow.addModification(new ColorChangedMod(selectedShape, 
					selectedShape.getColor(), ContextState.selectedColor));
			selectedShape.setColor(ContextState.selectedColor);
		}
	}

	public void updateLineWidth() {
		if (selectedShape != null) {
			MainWindow.mainWindow.addModification(new LineWidthChangedMod(selectedShape,
					selectedShape.getLineWidthId(), ContextState.lineWidthId));
			selectedShape.setLineWidthId(ContextState.lineWidthId);
		}
	}
	
	private synchronized void handleClick(MouseEvent e) {
		Point point = new Point(ContextState.getWorldPosition(new Point(e.getX(), e.getY())));
		Shape foundShape = null;
		switch (selectedMode) {
		case SELECT:
			if (prevClicked != null && prevClicked.equals(point)) {
				shapeList.itReset();
				for (Shape shape = shapeList.next(); shape != null; shape = shapeList.next()) {
					if (shape.containsPoint(point)) {
						if (shape == selectedShape && foundShape != null) break;
						foundShape = shape;
					}
				}
				selectedShape = foundShape;
			} else prevClicked = point;
			break;
		case DELETE:
			tempSelectedShape = selectedShape = null;
			shapeList.itReset();
			for (Shape shape = shapeList.next(); shape != null; shape = shapeList.next()) {
				if (shape.containsPoint(point)) foundShape = shape;
			}
			if (foundShape != null) {
				shapeList.removeShape(foundShape);
				MainWindow.mainWindow.addModification(new ShapeDeletedMod(foundShape));
			}
			break;
		case MULTI_LINE:
			refPoints.addElement(point);
			if (e.getClickCount() == 2) {
				selectedShape = generateLineFromRefPoints(); 
				addShape(selectedShape);
				refPoints.removeAllElements();
			}
			break;
		case CLOSED_LINE: 
			refPoints.addElement(point);
			if (e.getClickCount() == 2) {
				refPoints.addElement(new Point(refPoints.get(0)));
				selectedShape = generateLineFromRefPoints(); 
				addShape(selectedShape);
				refPoints.removeAllElements();
			}
			break;
		default:
			break;
		}
	}
	
	private synchronized void handlePress(Point point) {
		switch (selectedMode) {
		case SELECT:
			Shape foundShape = null;
			if (selectedShape == null || !selectedShape.containsPoint(point)) {
				shapeList.itReset();
				for (Shape shape = shapeList.next(); shape != null; shape = shapeList.next())
					if (shape.containsPoint(point)) foundShape = shape;
				selectedShape = foundShape;
			}
			if (selectedShape != null) oldShapePosition = new Point(selectedShape.getPosition());
			break;
		case LINE:
			selectedShape = null;
			refPoints.addElement(point);
			break;
		case RECTANGLE:
			selectedShape = null;
			refPoints.add(point);
			break;
		case MULTI_LINE: case CLOSED_LINE: 
			selectedShape = null;
			break;
		default:
			break;
		}
		mousePressedPoint = point;
		mouseDown = true;
	}
	
	private synchronized void handleRelease(Point point) {
		mouseDown = false;
		tempShape = null;
		switch (selectedMode) {
		case SELECT:
			if (selectedShape != null && selectedShape.containsPoint(mousePressedPoint) && !point.equals(oldShapePosition))
				MainWindow.mainWindow.addModification(new ShapeMovedMod(selectedShape, 
						oldShapePosition, selectedShape.getPosition()));
			break;
		case LINE:
			if (!refPoints.get(0).equals(point)) {
				refPoints.addElement(point);
				selectedShape = generateLineFromRefPoints();
				addShape(selectedShape);
			} else selectedShape = null;
			refPoints.removeAllElements();
			break;
		case RECTANGLE:
			if (!refPoints.get(0).equals(point)) {
				refPoints.addElement(point);
				Point p1 = refPoints.get(0);
				Point p2 = refPoints.get(1);
				selectedShape = new Rectangle(new Point((p1.getX() + p2.getX()) / 2, ((p1.getY() + p2.getY()) / 2)), 
						Math.abs(p1.getX() - p2.getX()), Math.abs(p1.getY() - p2.getY())); 
				addShape(selectedShape);
			} else selectedShape = null;
			refPoints.removeAllElements();
			break;
		default:
			break;
		}
		mousePressedPoint = null;
	}
	
	public synchronized void handleMouseMotion(Point point) {
		switch (selectedMode) {
		case SELECT:
			if (mouseDown && !point.equals(mousePressedPoint)) {
				if (selectedShape != null && selectedShape.containsPoint(mousePressedPoint)) {
					selectedShape.move(point.getX() - mousePressedPoint.getX(), 
							point.getY() - mousePressedPoint.getY());
					mousePressedPoint = point;
				} else {
					Point adjPos = ContextState.getAdjustedPosition(point);
					ContextState.moveWorld(point.getX() - mousePressedPoint.getX(), 
							point.getY() - mousePressedPoint.getY());
					mousePressedPoint = ContextState.getWorldPosition(adjPos);
				}
			}
			break;
		case DELETE:
			Shape foundShape = null;
			shapeList.itReset();
			for (Shape shape = shapeList.next(); shape != null; shape = shapeList.next())
				if (shape.containsPoint(point)) foundShape = shape;
			tempSelectedShape = foundShape;
			break;
		case LINE: 
			if (mouseDown) {
				refPoints.addElement(point);
				tempShape = generateLineFromRefPoints();
				refPoints.remove(point);
			}
			break;
		case MULTI_LINE: case CLOSED_LINE:
			if (refPoints.size() > 0) {
				refPoints.addElement(point);
				tempShape = generateLineFromRefPoints();
				refPoints.remove(point);
			}
			break;
		case RECTANGLE:
			if (mouseDown) {
				Point p1 = refPoints.get(0);
				tempShape = new Rectangle(new Point((p1.getX() + point.getX()) / 2, ((p1.getY() + point.getY()) / 2)), 
						Math.abs(p1.getX() - point.getX()), Math.abs(p1.getY() - point.getY()));
			}
			break;
		default:
			break;
		}
		if (tempShape != null) {
			Color color = tempShape.getColor();
			tempShape.setColor(new Color(
					(int)(color.getRed()),
					(int)(color.getGreen()), 
					(int)(color.getBlue())));
		}
	}
	
	private synchronized Shape generateLineFromRefPoints() {
		Vector<Point> adjPoints = new Vector<Point>();
		for (Point point : refPoints) adjPoints.add(ContextState.getWorldPosition(point));
		
		double minX = refPoints.get(0).getX();
		double maxX = refPoints.get(0).getX();
		double minY = refPoints.get(0).getY();
		double maxY = refPoints.get(0).getY();
		for (Point point : refPoints) {
			if (point.getX() < minX) minX = point.getX();
			if (point.getX() > maxX) maxX = point.getX();
			if (point.getY() < minY) minY = point.getY();
			if (point.getY() > maxY) maxY = point.getY();
		}
		Point position = new Point(minX + (maxX - minX) / 2, minY + (maxY - minY) / 2);
		Vector<Point> linePoints = new Vector<Point>();
		for (Point point : refPoints) {
			linePoints.add(new Point(point.getX() - position.getX(), point.getY() - position.getY()));
		}
		return new CompositeLine(position, linePoints);
	}
	
	@Override
	public synchronized void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		CANVAS_WIDTH = getWidth();
		CANVAS_HEIGHT = getHeight();
		
		g2.setColor(Color.black);
		g2.setStroke(new BasicStroke(1));
		Point a = ContextState.getAdjustedPosition(new Point(0, 10));
		Point b = ContextState.getAdjustedPosition(new Point(0, -10));
		g2.drawLine((int)(a.getX()), (int)(a.getY()), (int)(b.getX()), (int)(b.getY()));
		a = ContextState.getAdjustedPosition(new Point(10, 0));
		b = ContextState.getAdjustedPosition(new Point(-10, 0));
		g2.drawLine((int)(a.getX()), (int)(a.getY()), (int)(b.getX()), (int)(b.getY()));
		
		shapeList.itReset();
		for (Shape shape = shapeList.next(); shape != null; shape = shapeList.next())
			shape.paint(g2);
		
		if (selectedShape != null) selectedShape.getBoundingBox().paint(g2);
		if (tempSelectedShape != null) tempSelectedShape.getBoundingBox().paint(g2);
		for (Point p : refPoints) p.paint(g2);
		
		if (tempShape != null) tempShape.paint(g2);
	}
	
	public String packImage() {
		StringBuilder sb = new StringBuilder();
		shapeList.itReset();
		for (Shape shape = shapeList.next(); shape != null; shape = shapeList.next())
			sb.append(shape.packShape()).append("\n");
		return sb.toString();
	}
	
	public void unpackImage(Scanner scanner) {
		while (scanner.hasNext()) {
			switch (scanner.nextInt()) {
			case 1: shapeList.addShape(new CompositeLine(scanner)); break;
			case 2: shapeList.addShape(new Rectangle(scanner)); break;
			}
		}
	}

}
