package editortrees;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

public class Node {
	public static final Node NULL_NODE = new Node();

	enum Code {
		SAME, LEFT, RIGHT;
		// Used in the displayer and debug string
		public String toString() {
			switch (this) {
				case LEFT:
					return "/";
				case SAME:
					return "=";
				case RIGHT:
					return "\\";
				default:
					throw new IllegalStateException();
			}
		}
	}
	
	// The fields would normally be private, but for the purposes of this class, 
	// we want to be able to test the results of the algorithms in addition to the
	// "publicly visible" effects
	
	char element;            
	Node left, right; // subtrees
	int rank;         // inorder position of this node within its own subtree.
	Code balance;     // the direction this tree is leaning

	public Node() {
		this.left = null;
		this.right = null;
		this.balance = Code.SAME;
	}

	public Node(char element) {
		this.element = element;
		this.left = NULL_NODE;
		this.right = NULL_NODE;
		this.balance = Code.SAME;
	}

	public int height() {
		return Math.max(this.left != NULL_NODE ? this.left.height() : 0, this.right != NULL_NODE ? this.right.height() : 0) + 1;
	}

	public int size() {
		return 1 + this.rank + (this.right != NULL_NODE ? this.right.size() : 0);
	}

	public Result.ResultAdd add(char ch, int pos) {
		Result.ResultAdd result;
		// Check which subtree to continue down
		if (pos <= this.rank && pos >= 0) {
			if (this.left != NULL_NODE) { // If we have not yet reached the end of the subtree
				result = this.left.add(ch, pos); // Recursion
				if (result.success) this.rank += 1;
				if (result.success && !result.balanced) { // Check if we have not already set up a re-balance
					switch (this.balance) {
						case LEFT:
							if (!result.rotate) result.setValues(this);
							break;
						case RIGHT:
							this.balance = Code.SAME;
							result.balanced = true;
							break;
						default:
							this.balance = Code.LEFT;
							break;
					}
				}
			} else { // Add new node to the tree
				result = new Result.ResultAdd();
				Node node = new Node(ch);
				result.success = true;
				this.rank += 1;
				this.left = node;
				if (this.balance == Code.SAME) this.balance = Code.LEFT;
				else {
					this.balance = Code.SAME;
					result.balanced = true;
				}
			}
		} else { // Go down right subtree
			if (this.right != NULL_NODE) { // If we have not yet reached the end of the subtree
				result = this.right.add(ch, pos - this.rank - 1); // Recursion with new position value
				if (result.success && !result.balanced) { // Check if we have not already set up a re-balance
					switch (this.balance) {
						case LEFT:
							this.balance = Code.SAME;
							result.balanced = true;
							break;
						case RIGHT:
							if (!result.rotate) result.setValues(this);
							break;
						default:
							this.balance = Code.RIGHT;
							break;
					}
				}
			} else if (pos > this.rank + 1) throw new IndexOutOfBoundsException(); // Check if pos is too high
			else { // Add new node to the tree
				result = new Result.ResultAdd();
				Node node = new Node(ch);
				result.success = true;
				this.right = node;
				if (this.balance == Code.SAME) this.balance = Code.RIGHT;
				else {
					this.balance = Code.SAME;
					result.balanced = true;
				}
			}
		}
		if (!result.rotate && !result.balanced) { // Reassign pointers to previous two Code values
			result.directions[1] = result.directions[0];
			result.directions[0] = this.balance;
		}
		if (result.rotate && result.parent == NULL_NODE && result.node != this) result.parent = this; // Set parent if necessary
		return result;
	}

	public Result get(int pos) throws IndexOutOfBoundsException {
		if (pos == this.rank) return new Result().setSuccess(true).setResult(this.element); // Base case
		else if (pos < this.rank) { if (this.left != NULL_NODE) return this.left.get(pos); } // Recurse down left
		else { if (this.right != NULL_NODE) return this.right.get(pos - this.rank - 1); } // Recurse down right
		throw new IndexOutOfBoundsException(); // Throw exception if not found
	}

	public String toString() {
		if (this == NULL_NODE) return "";
		return (this.left != NULL_NODE ? this.left.toString() : "") + this.element + (this.right != NULL_NODE ? this.right.toString() : "");
	}

	public void toDebugString(StringBuilder build) {
		if(this!=NULL_NODE) {
			build.append(""+element+rank+balance.toString()+", ");
			left.toDebugString(build);
			right.toDebugString(build);
		}
	}

	public static Node copy(Node node) {
		Node me = new Node();
		me.element = node.element;
		me.rank = node.rank;
		me.balance = node.balance;
		me.left = node.left != NULL_NODE ? Node.copy(node.left) : NULL_NODE;
		me.right = node.right != NULL_NODE ? Node.copy(node.right) : NULL_NODE;
		return me;
	}
}