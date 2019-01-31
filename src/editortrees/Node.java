package editortrees;

// A node in a height-balanced binary tree with rank.
// Except for the NULL_NODE (if you choose to use one), one node cannot
// belong to two different trees.

import java.util.LinkedList;

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
		if(this==NULL_NODE) {
			return -1;
		}else {
			return Math.max(this.left.height(), this.right.height())+1;
		}
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

	public void delete(int pos, EditTree tree, Result.ResultDelete result) throws IndexOutOfBoundsException {
			// Check which subtree to continue down
		    result.nodeStack.push(this);
			if (pos <= this.rank && pos >= 0) {
				if (this.left != NULL_NODE) { // If we have not yet reached the end of the subtree
					if (pos != this.left.rank) {
						this.left.delete(pos, tree, result); // Recursion
					} else { // Remove node from the tree
						result.deleteNode = this.left;
						result.deleteParent = this;
						this.rank--;
						if (result.deleteNode.left == NULL_NODE && result.deleteNode.right == NULL_NODE) { // leaf
							result.deleteParent.left = NULL_NODE;
							result.deleteSwapped = true;
						} else if (result.deleteNode.left == NULL_NODE && result.deleteNode.right != NULL_NODE) { // single R child
							result.deleteParent.left = result.deleteNode.right;
							result.deleteSwapped = true;
						} else if (result.deleteNode.right == NULL_NODE && result.deleteNode.left!=NULL_NODE) { // single L child
							result.deleteParent.left = result.deleteNode.left;
							result.deleteSwapped = true;
						} else if (result.deleteNode.right.left == NULL_NODE) { // Double children R leaf
							result.deleteNode.right.left = result.deleteNode.left;
							result.deleteParent.left = result.deleteNode.right;
							result.deleteParent.left.rank++;
							if (result.deleteParent.left.balance == Code.SAME) result.deleteParent.left.balance = Code.LEFT;
							else if (result.deleteParent.left.balance == Code.RIGHT) result.deleteParent.left.balance = Code.SAME;
							result.deleteSwapped = true;
						} else {
							LinkedList<Node> nodeList = new LinkedList<>();
							result.deleteParent.left = Node.getSuccessor(result.deleteNode, nodeList);
							result.nodeStack.push(result.deleteParent.left);
							while (nodeList.size() > 0) result.nodeStack.push(nodeList.removeFirst());
						}
					}
				}
				
			} else { // Go down right subtree
				if (this.right != NULL_NODE) { // If we have not yet reached the end of the subtree
					if (pos - this.rank - 1 != this.right.rank) {
						this.right.delete(pos - this.rank - 1, tree, result); // Recursion with new position value
					} else { // Remove node from the tree
						result.deleteNode = this.right;
						result.deleteParent = this;
						if (result.deleteNode.left == NULL_NODE && result.deleteNode.right == NULL_NODE) { // leaf
							result.deleteParent.right = NULL_NODE;
							result.deleteSwapped = true;
						} else if (result.deleteNode.left == NULL_NODE && result.deleteNode.right != NULL_NODE) { // single R child
							result.deleteParent.right = result.deleteNode.right;
							result.deleteSwapped = true;
						} else if (result.deleteNode.right == NULL_NODE && result.deleteNode.left!=NULL_NODE) { // single L child
							result.deleteParent.right = result.deleteNode.left;
							result.deleteSwapped = true;
						} else if (result.deleteNode.right.left == NULL_NODE) { // Double children R leaf
							result.deleteNode.right.left = result.deleteNode.left;
							result.deleteParent.right = result.deleteNode.right;
							result.deleteParent.right.rank++;
							if (result.deleteParent.right.balance == Code.SAME) result.deleteParent.right.balance = Code.LEFT;
							else if (result.deleteParent.right.balance == Code.RIGHT) result.deleteParent.right.balance = Code.SAME;
							result.deleteSwapped = true;
						} else {
							LinkedList<Node> nodeList = new LinkedList<>();
							result.deleteParent.right = Node.getSuccessor(result.deleteNode, nodeList);
							result.nodeStack.push(result.deleteParent.right);
							while (nodeList.size() > 0) result.nodeStack.push(nodeList.removeFirst());
						}
					}
				} else if (pos != 0) throw new IndexOutOfBoundsException(); // Check if pos is too high
			}
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

	public String toDebugString2() {
		if (this == NULL_NODE) return "null";
		String output = "";
		output += "[" + this.left.element + ", " + this.element + this.rank + this.balance + ", " + this.right.element + "]";
		output += this.left != NULL_NODE ? "\n" + this.left.toDebugString2() : "";
		output += this.right != NULL_NODE ? "\n" + this.right.toDebugString2() : "";
		return output;
	}

	public void mirror() {
		if (this == NULL_NODE) return;
		this.right.mirror();
		this.left.mirror();
		Node temp = this.left;
		this.left = this.right;
		this.right = temp;
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

	public static Node getSuccessor(Node parent, LinkedList<Node> nodeList) {
		Node node = parent.right;
		Node last = NULL_NODE;
		nodeList.addLast(node);
		while (node.left != NULL_NODE) {
			last = node;
			node = node.left;
			if (node.left != NULL_NODE) nodeList.addLast(node);
		}
		Node movingNode = node;
		last.left = node.right;
		last.rank--;
		movingNode.left = parent.left;
		movingNode.right = parent.right;
		movingNode.rank = parent.rank;
		movingNode.balance = parent.balance;
		return movingNode;
		//if (last.balance == Code.SAME) last.balance = Code.RIGHT;
		//else if (last.balance == Code.LEFT) last.balance = Code.SAME;
	}
}
