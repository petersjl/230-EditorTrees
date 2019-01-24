package editortrees;

import java.util.LinkedList;
import java.util.Stack;

public class Result<T> {
	public enum Rotation {LEFT_SINGLE, LEFT_DOUBLE, RIGHT_SINGLE, RIGHT_DOUBLE}

	private T value;
	private boolean success = false;

	public T getResult() {
		return this.value;
	}

	public Result<T> setResult(T value) {
		this.value = value;
		return this;
	}

	public boolean getSuccess() {
		return this.success;
	}

	public Result<T> setSuccess(boolean success) {
		this.success = success;
		return this;
	}

	public static Rotation getRotationType(Node.Code[] directions) {
		if (directions[0] == Node.Code.LEFT && directions[1] == Node.Code.LEFT) return Rotation.RIGHT_SINGLE;
		if (directions[0] == Node.Code.LEFT && directions[1] == Node.Code.RIGHT) return Rotation.RIGHT_DOUBLE;
		if (directions[0] == Node.Code.RIGHT && directions[1] == Node.Code.LEFT) return Rotation.LEFT_DOUBLE;
		if (directions[0] == Node.Code.RIGHT && directions[1] == Node.Code.RIGHT) return Rotation.LEFT_SINGLE;
		return null;
	}

	// This class is used to keep track of the values needed to know how to rotate
	// It allows us to see what kind of rotation by keeping track of the previous two Code values when going out of the recursion
	public static class ResultAdd {
		public Node.Code[] directions = new Node.Code[] {null, null};
		public Rotation rotation;
		public Node node = Node.NULL_NODE;
		public Node parent = Node.NULL_NODE;
		public boolean success;
		public boolean rotate;
		public boolean balanced = false;

		public String toString() {
			return "<[" + (this.directions[0] != null ? this.directions[0].toString() : " ") + (this.directions[1] != null ? this.directions[1].toString() : " ") + "] " + this.rotation + " " + this.node.element + " " + this.parent.element + " " + (this.success ? "true" : "false") + ">";
		}

		public void setValues(Node node) {
			this.directions[1] = this.directions[0];
			this.directions[0] = node.balance;
			this.node = node;
			this.rotation = getRotationType(this.directions);
			this.rotate = true;
			this.balanced = true;
		}
	}

	// Similar to the ResultAdd class, but keeps track of what rotations need to be done inline
	public static class ResultDelete {
		public Node.Code[] directions = new Node.Code[] {null, null};
		public Rotation rotation;
		public Node rotationNode = Node.NULL_NODE;

		public Node deleteNode = Node.NULL_NODE;
		public Node deleteParent = Node.NULL_NODE;
		public Node deletePredecessor = Node.NULL_NODE;
		public boolean deleteSwapped = false;

		public Node lastNode = Node.NULL_NODE;
		public Stack<Node> nodeStack = new Stack<>();
	}

	public static class ResultNodeDirection {
		public enum Direction {LEFT, RIGHT}

		public Node node;
		public Direction direction;

		public ResultNodeDirection(Node node, Direction direction) {
			this.node = node;
			this.direction = direction;
		}
	}
}
