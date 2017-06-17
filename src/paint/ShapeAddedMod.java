package paint;

public class ShapeAddedMod extends Modification {
	
	private Shape shape;
	
	public ShapeAddedMod(Shape shape) {
		this.shape = shape;
	}

	@Override
	public void undo(Canvas canvas) {
		super.undo(canvas);
		canvas.shapeList.removeShape(shape);
	}

	@Override
	public void redo(Canvas canvas) {
		super.redo(canvas);
		canvas.shapeList.addShape(shape);
	}

}
