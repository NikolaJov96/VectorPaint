package paint;

public abstract class Modification {
	
	public void undo(Canvas canvas) {
		canvas.selectedShape = null;
	}
	
	public void redo(Canvas canvas) {
		canvas.selectedShape = null;
	}

}
