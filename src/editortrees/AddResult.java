package editortrees;

public class AddResult {
	public enum Rotation {LEFT_SINGLE, LEFT_DOUBLE, RIGHT_SINGLE, RIGHT_DOUBLE}

	public Node.Code[] directions = new Node.Code[] {Node.Code.SAME, Node.Code.SAME};
	public Rotation rotation;
	public Node node = Node.NULL_NODE;
	public Node parent = Node.NULL_NODE;
	public boolean success;
	public boolean rotate;

	public String toString() {
		return "<[" + this.directions[0].toString() + this.directions[1].toString() + "] " + this.rotation + " " + this.node.element + " " + this.parent.element + " " + (this.success ? "true" : "false") + ">";
	}

	public void setValues(Node node) {
		this.directions[1] = this.directions[0];
		this.directions[0] = node.balance;
		this.node = node;
		if (this.directions[0] == Node.Code.LEFT && this.directions[1] == Node.Code.LEFT) this.rotation = Rotation.RIGHT_SINGLE;
		if (this.directions[0] == Node.Code.LEFT && this.directions[1] == Node.Code.RIGHT) this.rotation = Rotation.RIGHT_DOUBLE;
		if (this.directions[0] == Node.Code.RIGHT && this.directions[1] == Node.Code.LEFT) this.rotation = Rotation.LEFT_DOUBLE;
		if (this.directions[0] == Node.Code.RIGHT && this.directions[1] == Node.Code.RIGHT) this.rotation = Rotation.LEFT_SINGLE;
		this.rotate = true;
	}
}
