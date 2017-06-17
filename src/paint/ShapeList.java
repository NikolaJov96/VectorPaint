package paint;

public class ShapeList {
	
	private static class Node {
		Shape shape;
		Node next;
		Node(Shape shape) { this.shape = shape; }
	}
	
	private Node first, last, iterator;
	
	public void addShape(Shape shape) {
		if (first == null) first = last = new Node(shape);
		else last = last.next = new Node(shape);
	}
	
	public void removeShape(Shape shape) {
		Node prev = null, curr = first;
		while (curr != null && curr.shape != shape) {
			prev = curr;
			curr = curr.next;
		}
		if (curr == null) return;
		
		if (last == curr) last = prev;
		if (prev != null) prev.next = curr.next;
		else first = curr.next;
		
		if (iterator == curr) iterator = first;
	}
	
	public boolean isEmpty() {
		if (first == null) return true;
		return false;
	}
	
	public void removeAll() {
		iterator = first = last = null;
	}
	
	public boolean contains(Shape shape) {
		for (Node curr = first; curr != null; curr = curr.next) {
			if (curr.shape == shape) return true;
		}
		return false;
	}
	
	public void itReset() {
		iterator = first;
	}
	
	public Shape next() {
		if (iterator == null) return null;
		Shape shape = iterator.shape;
		iterator = iterator.next;
		return shape;
	}

}
