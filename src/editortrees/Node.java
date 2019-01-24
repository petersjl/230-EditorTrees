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
						if (this.left.left == NULL_NODE && this.left.right == NULL_NODE) { // leaf
							result.deleteParent.left = NULL_NODE;
							result.deleteSwapped = true;
						} else if (this.left.left == NULL_NODE && this.left.right != NULL_NODE) { // single R child
							this.left = this.left.right;
							result.deleteSwapped = true;
						} else if (this.left.right == NULL_NODE && this.left.left!=NULL_NODE) { // single L child
							this.left = this.left.left;
							result.deleteSwapped = true;
						} else if (this.left.left.right == NULL_NODE) { // Double children L leaf
							this.left.right.left = result.deleteNode.left;
							this.left.right.left.rank = 0;
							this.left.right.rank=result.deleteNode.rank;
							if(result.deleteNode.balance.equals(Code.SAME)) {
								this.left.right.balance=Code.LEFT;
							}else if(result.deleteNode.balance.equals(Code.RIGHT)) {
								this.left.right.balance=Code.SAME;
							}
							this.left.right.left.balance = Code.SAME;
							this.left = this.left.right;
							//this.left.right.right = NULL_NODE;
							result.deleteSwapped = true;
						}
					}
				}
				
			} else { // Go down right subtree
				if (this.right != NULL_NODE) { // If we have not yet reached the end of the subtree
					if (pos - this.rank - 1 != this.right.rank) {
						this.right.delete(pos - this.rank - 1, tree, result); // Recursion with new position value
					} else { // Remove node from the tree
						result.deleteNode = this.right;
						System.out.println(result.deleteNode);
						System.out.println(this.right);
						System.out.println(this.right.left.right);
						result.deleteParent = this;
						if (this.right.left == NULL_NODE && this.right.right == NULL_NODE) { // leaf
							this.right = NULL_NODE;
							result.deleteSwapped = true;
						} else if (this.right.left == NULL_NODE && this.right.right != NULL_NODE) { // single R child
							this.right = this.right.right;
							result.deleteSwapped = true;
						} else if (this.right.right == NULL_NODE && this.right.left!=NULL_NODE) { // single L child
							this.right = this.right.left;
							result.deleteSwapped = true;
						} else { // Double children L leaf
							System.out.println(this.right);
							this.right.right.left = result.deleteNode.left;
							//this.right.right.left.rank = 0;
							this.right.right.rank=result.deleteNode.rank;
							if(result.deleteNode.balance.equals(Code.SAME)) {
								this.right.right.balance=Code.LEFT;
							}else if(result.deleteNode.balance.equals(Code.RIGHT)) {
								this.right.right.balance=Code.SAME;
							}
							//this.right.right.left.balance = Code.SAME;
							System.out.println(this.right.right);
							this.right = this.right.right;
							System.out.println(this.right);
							//this.left.right.right = NULL_NODE;
							result.deleteSwapped = true;
						}
					}
				} else if (pos > this.rank) throw new IndexOutOfBoundsException(); // Check if pos is too high
			}
		//System.out.print(result.lastNode.element);
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