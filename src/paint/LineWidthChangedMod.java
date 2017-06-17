package paint;

public class LineWidthChangedMod extends Modification {

	private Shape shape;
	private int oldLineWidthId, newLineWidthId;
	
	public LineWidthChangedMod(Shape shape, int oldLineWidthId, int newLineWidthId) {
		this.shape = shape;
		this.oldLineWidthId = oldLineWidthId;
		this.newLineWidthId = newLineWidthId;
	}
	
	@Override
	public void undo(Canvas canvas) {
		super.undo(canvas);
		shape.setLineWidthId(oldLineWidthId);
	}

	@Override
	public void redo(Canvas canvas) {
		super.redo(canvas);
		shape.setLineWidthId(newLineWidthId);
	}

}
