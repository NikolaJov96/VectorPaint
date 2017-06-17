package paint;

import java.awt.Color;

public class ColorChangedMod extends Modification {
	
	private Shape shape;
	private Color prevColor, newColor;
	
	public ColorChangedMod(Shape shape, Color prevColor, Color newColor) {
		this.shape = shape;
		this.prevColor = prevColor;
		this.newColor = newColor;
	}

	@Override
	public void undo(Canvas canvas) {
		super.undo(canvas);
		shape.setColor(prevColor);
	}

	@Override
	public void redo(Canvas canvas) {
		super.redo(canvas);
		shape.setColor(newColor);
	}

}
