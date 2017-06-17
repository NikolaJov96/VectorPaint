package paint;

public class ShapeDeletedMod extends Modification {
	
	private Shape shape;

	public ShapeDeletedMod(Shape shape) {
		this.shape = shape;
	}
	
	@Override
	public void undo(Canvas canvas) {
		super.undo(canvas);
		canvas.shapeList.addShape(shape);
	}

	@Override
	public void redo(Canvas canvas) {
		super.redo(canvas);
		canvas.shapeList.removeShape(shape);
	}

}
