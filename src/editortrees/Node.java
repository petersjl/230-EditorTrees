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

	public Result.ResultDelete delete(int pos, EditTree tree, Result.ResultDelete findPredecessor) throws IndexOutOfBoundsException {
		Result.ResultDelete result = new Result.ResultDelete();
		if (findPredecessor != null && findPredecessor.deleteNode != NULL_NODE && !findPredecessor.deleteSwapped) {
			if (this.right.right == NULL_NODE) {
				Node leftChildren = this.right.left;
				this.right.right = findPredecessor.deleteNode.right;
				this.right.left = findPredecessor.deleteNode.left;
				this.right.rank = findPredecessor.deleteNode.rank;
				this.right.balance = findPredecessor.deleteNode.balance;
				if (findPredecessor.deleteParent.left == findPredecessor.deleteNode) findPredecessor.deleteParent.left = this.right;
				if (findPredecessor.deleteParent.right == findPredecessor.deleteNode) findPredecessor.deleteParent.right = this.right;
				this.right = leftChildren;
				findPredecessor.deleteSwapped = true;
				result = findPredecessor;
			}
			if (!findPredecessor.deleteSwapped) {
				findPredecessor.lastNode = this;
				findPredecessor.nodeStack.push(new Result.ResultNodeDirection(this, Result.ResultNodeDirection.Direction.LEFT));
				//System.out.print(result.lastNode.element);
				return this.right.delete(pos, tree, findPredecessor);
			}
		} else if (findPredecessor == null) {
			// Check which subtree to continue down
			if (pos <= this.rank && pos >= 0) {
				if (this.left != NULL_NODE) { // If we have not yet reached the end of the subtree
					if (pos != this.left.rank) {
						result = this.left.delete(pos, tree, null); // Recursion
					} else { // Remove node from the tree
						result.deleteNode = this.left;
						result.deleteParent = this;
						if (this.left.left == NULL_NODE && this.left.right == NULL_NODE) { // leaf
							result.deleteParent.left = NULL_NODE;
							result.deleteSwapped = true;
						} else if (this.left.left == NULL_NODE && this.left.right != NULL_NODE) { // single R child
							result.deleteParent.left = this.left.right;
							result.deleteSwapped = true;
						} else if (this.left.right == NULL_NODE) { // single L child
							result.deleteParent.left = this.left.left;
							result.deleteSwapped = true;
						} else if (this.left.left.right == NULL_NODE) { // Double children L leaf
							this.left.left.right = result.deleteNode.right;
							this.left.left.right.rank = result.deleteNode.rank;
							this.left.left.right.balance = result.deleteNode.balance;
							result.deleteParent.left = this.left.left;
							this.left.left.right = NULL_NODE;
							result.deleteSwapped = true;
						} else if (!result.deleteSwapped) { // double children
							result = this.left.left.delete(pos, tree, result);
							result.deleteSwapped = true;
						}
					}
				}
			} else { // Go down right subtree
				if (this.right != NULL_NODE) { // If we have not yet reached the end of the subtree
					if (pos - this.rank - 1 != this.left.rank) {
						result = this.right.delete(pos - this.rank - 1, tree, null); // Recursion with new position value
					} else { // Remove node from the tree
						result.deleteNode = this.right;
						result.deleteParent = this;
						if (this.right.left == NULL_NODE && this.right.right == NULL_NODE) { // leaf
							result.deleteParent.right = NULL_NODE;
							result.deleteSwapped = true;
						} else if (this.right.left == NULL_NODE && this.right.right != NULL_NODE) { // single R child
							result.deleteParent.right = this.right.right;
							result.deleteSwapped = true;
						} else if (this.right.right == NULL_NODE) { // single L child
							result.deleteParent.right = this.right.left;
							result.deleteSwapped = true;
						} else if (this.right.left.right == NULL_NODE) { // Double children L leaf
							this.right.left.right = result.deleteNode.right;
							this.right.left.right.rank = result.deleteNode.rank;
							this.right.left.right.balance = result.deleteNode.balance;
							result.deleteParent.right = this.right.left;
							this.right.left.right = NULL_NODE;
							result.deleteSwapped = true;
						} else if (!result.deleteSwapped) { // double children
							result = this.right.left.delete(pos, tree, result);
							result.deleteSwapped = true;
						}
					}
				} else if (pos > this.rank + 1) throw new IndexOutOfBoundsException(); // Check if pos is too high
			}
		}
		if (result != null && result.deleteNode != NULL_NODE) {
			if (result.rotation != null) {
				Node node = Node.NULL_NODE;
				switch (result.rotation) {
					case LEFT_SINGLE:
						node = tree.rotationLeftSingle(result.rotationNode);
						break;
					case LEFT_DOUBLE:
						node = tree.rotationLeftDouble(result.rotationNode);
						break;
					case RIGHT_SINGLE:
						node = tree.rotationRightSingle(result.rotationNode);
						break;
					case RIGHT_DOUBLE:
						node = tree.rotationRightDouble(result.rotationNode);
						break;
				}
				if (node != NULL_NODE && result.rotationNode == this.left) this.left = node;
				if (node != NULL_NODE && result.rotationNode == this.right) this.right = node;
			}
			result.directions[1] = result.directions[0]; // Reassign pointers to previous two Code values
			result.directions[0] = this.balance;
			if (result.rotation == null) result.rotation = Result.getRotationType(result.directions);
			if (result.rotation != null) result.rotationNode = this;
		}
		result.lastNode = this;
		result.nodeStack.push(new Result.ResultNodeDirection(this, Result.ResultNodeDirection.Direction.LEFT));
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