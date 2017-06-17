package paint;

public class ShapeMovedMod extends Modification {
	
	private Shape shape;
	private Point oldPosition, newPosition;
	
	public ShapeMovedMod(Shape shape, Point oldPosition, Point newPosition) {
		this.shape = shape;
		this.oldPosition = new Point(oldPosition);
		this.newPosition = new Point(newPosition);
	}

	@Override
	public void undo(Canvas canvas) {
		super.undo(canvas);
		shape.setPosition(oldPosition);
	}

	@Override
	public void redo(Canvas canvas) {
		super.redo(canvas);
		shape.setPosition(newPosition);
	}

}
