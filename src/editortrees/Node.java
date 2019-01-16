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
	Code balance; 
	// Node parent;  // You may want this field.
	// Feel free to add other fields that you find useful

	public Node() {
//		this.element = null;
		this.left = null;
		this.right = null;
//		this.rank = null;
		this.balance = null;
	}

	public Node(char element) {
		this.element = element;
		this.left = NULL_NODE;
		this.right = NULL_NODE;
		this.balance = Code.SAME;
	}

	public int height() {
		return this == NULL_NODE ? 0 : 1 + Math.max(this.left.height(), this.right.height());
	}

	public int size() {
		return -1;
	public Result<Node> add(char ch, int pos) {
		Result<Node> result;
		if (pos <= this.rank && pos != -1) {
			if (this.left != NULL_NODE) {
				result = this.left.add(ch, pos);
				if (result.getSuccess()) {
					this.rank += 1;
					// TODO: Check balance
				}
			} else {
				result = new Result<>();
				Node node = new Node(ch);
				result.setResult(node);
				result.setSuccess(true);
				this.rank += 1;
				this.left = node;
			}
		} else {
			if (this.right != NULL_NODE) {
				result = this.right.add(ch, pos - this.rank - 1);
				if (result.getSuccess()) {
					// TODO: Check balance
				}
			} else {
				result = new Result<>();
				Node node = new Node(ch);
				result.setResult(node);
				result.setSuccess(true);
				this.right = node;
			}
		}
		return result;
	}

	public String toString() {
		if(this==NULL_NODE) {
			return "";
		}else {
			return left.toString()+element+right.toString();
		}
	}

	public void toDebugString(StringBuilder build) {
		if(this!=NULL_NODE) {
			build.append(element+rank+balance.toString()+", ");
			left.toDebugString(build);
			right.toDebugString(build);
		}
	}
}