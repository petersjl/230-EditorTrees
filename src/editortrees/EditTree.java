package editortrees;

import java.util.LinkedList;
import java.util.Stack;

import editortrees.Node.Code;
import editortrees.Result.ResultDelete;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	private Node root;
	private int rotationCount = 0;

	/**
	 * MILESTONE 1
	 * Construct an empty tree
	 */
	public EditTree() {
		this.root = Node.NULL_NODE;
	}

	/**
	 * MILESTONE 1
	 * Construct a single-node tree whose element is ch
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		this.root = new Node(ch);
	}

	/**
	 * MILESTONE 2
	 * Make this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {
		this.root = Node.NULL_NODE;
		if (e.root != Node.NULL_NODE) {
			this.root = Node.copy(e.root);
		}
	}

	/**
	 * MILESTONE 3
	 * Create an EditTree whose toString is s. This can be done in O(N) time,
	 * where N is the size of the tree (note that repeatedly calling insert() would be
	 * O(N log N), so you need to find a more efficient way to do this.
	 * 
	 * @param s
	 */
	public EditTree(String s) {
		if(s.equals("")) this.root = Node.NULL_NODE;
		else {
			this.root = Node.createTree(s);
		}
	}
	
	/**
	 * MILESTONE 1
	 * returns the total number of rotations done in this tree since it was
	 * created. A double rotation counts as two.
	 *
	 * @return number of rotations since this tree was created.
	 */
	public int totalRotationCount() {
		return this.rotationCount; // replace by a real calculation.
	}

	/**
	 * MILESTONE 1
	 * return the string produced by an inorder traversal of this tree
	 */
	@Override
	public String toString() {
		return root.toString();
	}

	/**
	 * MILESTONE 1
	 * This one asks for more info from each node. You can write it like 
	 * the arraylist-based toString() method from the
	 * BinarySearchTree assignment. However, the output isn't just the elements, 
	 * but the elements, ranks, and balance codes. Former CSSE230 students recommended
	 * that this method, while making it harder to pass tests initially, saves
	 * them time later since it catches weird errors that occur when you don't
	 * update ranks and balance codes correctly.
	 * For the tree with root b and children a and c, it should return the string:
	 * [b1=, a0=, c0=]
	 * There are many more examples in the unit tests.
	 * 
	 * @return The string of elements, ranks, and balance codes, given in
	 *         a pre-order traversal of the tree.
	 */
	public String toDebugString() {
		StringBuilder build = new StringBuilder();
		build.append("[");
		root.toDebugString(build);
		if (!build.toString().equals("[")) {
			build.deleteCharAt(build.length() - 2);
			build.deleteCharAt(build.length() - 1);
		}
		build.append("]");
		return build.toString();
	}

	/**
	 * MILESTONE 1
	 * @param ch
	 *            character to add to the end of this tree.
	 */
	public void add(char ch) {
		this.add(ch, Math.max(this.size(), 0));
	}

	/**
	 * MILESTONE 1
	 * @param ch
	 *            character to add
	 * @param pos
	 *            character added in this inorder position
	 * @throws IndexOutOfBoundsException
	 *            if pos is negative or too large for this tree
	 */
	public void add(char ch, int pos) throws IndexOutOfBoundsException {
		if (pos < 0) throw new IndexOutOfBoundsException();
		if (this.root == Node.NULL_NODE)
			if (pos == 0) this.root = new Node(ch);
			else throw new IndexOutOfBoundsException();
		else {
			// Get result data from Node add()
			Result.ResultAdd result = this.root.add(ch, pos);
			// check if successful and if there needs to be a rotation
			if (result.success && result.rotation != null) {
				Node node = Node.NULL_NODE;
				switch (result.rotation) {
					case LEFT_SINGLE:
						node = this.rotationLeftSingle(result.node);
						break;
					case LEFT_DOUBLE:
						node = this.rotationLeftDouble(result.node);
						break;
					case RIGHT_SINGLE:
						node = this.rotationRightSingle(result.node);
						break;
					case RIGHT_DOUBLE:
						node = this.rotationRightDouble(result.node);
						break;
				}
				if (result.parent == Node.NULL_NODE) this.root = node;
				else {
					// Perform re-add operation on rebalanced node
					switch (result.rotation) {
						case LEFT_SINGLE:
						case LEFT_DOUBLE: {
							if (result.parent.left == node.left) result.parent.left = node;
							else result.parent.right = node;
							break;
						}
						case RIGHT_SINGLE:
						case RIGHT_DOUBLE: {
							if (result.parent.right == node.right) result.parent.right = node;
							else result.parent.left = node;
							break;
						}
					}
				}
			}
		}
	}

	public Node rotationLeftSingle(Node parent) {
		Node child = parent.right;
		parent.right = child.left;
		child.left = parent;
		parent.balance = Code.SAME;
		child.balance = Code.SAME;
		child.rank += parent.rank + 1;
		this.rotationCount++;
		return child;
	}

	public Node rotationRightSingle(Node parent) {
		Node child = parent.left;
		parent.left = child.right;
		child.right = parent;
		parent.balance = Code.SAME;
		child.balance = Code.SAME;
		parent.rank -= child.rank + 1;
		this.rotationCount++;
		return child;
	}

	public Node rotationLeftDouble(Node parent) {
		Code balance = parent.right.left.balance;
		parent.right = this.rotationRightSingle(parent.right);
		Node node = this.rotationLeftSingle(parent);
		updateDoubleBalances(node, balance);
		return node;
	}

	public Node rotationRightDouble(Node parent) {
		Code balance = parent.left.right.balance;
		parent.left = this.rotationLeftSingle(parent.left);
		Node node = this.rotationRightSingle(parent);
		updateDoubleBalances(node, balance);
		return node;
	}

	public void updateDoubleBalances(Node grandChild, Code balance) {
		switch (balance) {
			case LEFT:
				grandChild.right.balance = Code.RIGHT;
				break;
			case RIGHT:
				grandChild.left.balance = Code.LEFT;
				break;
		}
	}
	
	
	/**
	 * MILESTONE 1
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		if (this.root == Node.NULL_NODE) throw new IndexOutOfBoundsException();
		Result result = this.root.get(pos);
		if (result.getSuccess()) return (char) result.getResult();
		throw new IndexOutOfBoundsException();
	}

	public Node getNode(int pos) throws IndexOutOfBoundsException {
		if (this.root == Node.NULL_NODE) throw new IndexOutOfBoundsException();
		Result r = this.root.getNode(pos);
		if (r.getSuccess()) return (Node) r.getResult();
		throw new IndexOutOfBoundsException();
	}

	/**
	 * MILESTONE 1
	 * @return the height of this tree
	 */
	public int height() {
		return root.height();
	}

	/**
	 * MILESTONE 2
	 * @return the number of nodes in this tree, 
	 *         not counting the NULL_NODE if you have one.
	 */
	public int size() {
		return (this.root != Node.NULL_NODE) ? this.root.size() : 0;
	}
	
	
	/**
	 * MILESTONE 2
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 * Milestone 2 Before 11:55 commit
	 * 
	 */
	
	public char delete(int pos) throws IndexOutOfBoundsException {
		if (pos < 0) throw new IndexOutOfBoundsException();
		if (this.root == Node.NULL_NODE) throw new IndexOutOfBoundsException();
		if(this.height()==0&&pos==0) {
			char ele = root.element;
			root=Node.NULL_NODE;
			return ele;
		}
		if(pos==root.rank) {
			char ele = root.element;
			rootDelete();
			return ele;
		}
		ResultDelete result = new ResultDelete();
		this.root.delete(pos, this, result);
		rebalanceTreeNodeStack(result.nodeStack);
		return result.deleteNode.element;
	}

	private void rootDelete() {
		if(root.right==Node.NULL_NODE) {
			root=root.left;
			return;
		}else {
			Stack<Node> nodeStack = new Stack<Node>();
			nodeStack.add(root);
			Node newRoot = root.right;
			//nodeStack.add(newRoot);
			if(newRoot.left==Node.NULL_NODE) {
				root.element=root.right.element;
				root.right=root.right.right;
			}else {
				nodeStack.add(newRoot);
				while(newRoot.left.left!=Node.NULL_NODE) {
					newRoot=newRoot.left;
					nodeStack.add(newRoot);
				}
				Node deleteNode = newRoot.left;
				Node deleteParent = newRoot;
				root.element=deleteNode.element;
				deleteParent.rank--;
				if (deleteNode.left == Node.NULL_NODE && deleteNode.right == Node.NULL_NODE) { // leaf
					deleteParent.left = Node.NULL_NODE;

				} else if (deleteNode.left == Node.NULL_NODE && deleteNode.right != Node.NULL_NODE) { // single R child
					deleteParent.left = deleteNode.right;

				} else if (deleteNode.right == Node.NULL_NODE && deleteNode.left!=Node.NULL_NODE) { // single L child
					deleteParent.left = deleteNode.left;

				} else if (deleteNode.right.left == Node.NULL_NODE) { // Double children R leaf
					deleteNode.right.left = deleteNode.left;
					deleteParent.left = deleteNode.right;
					deleteParent.left.rank++;
					if (deleteParent.left.balance == Code.SAME) deleteParent.left.balance = Code.LEFT;
					else if (deleteParent.left.balance == Code.RIGHT) deleteParent.left.balance = Code.SAME;
				} else {
					deleteParent.rank++;
					LinkedList<Node> nodeList = new LinkedList<>();
					deleteParent.left = Node.getSuccessor(deleteNode, nodeList);
					nodeStack.push(deleteParent.left);
					while (nodeList.size() > 0) nodeStack.push(nodeList.removeFirst());
				}
				
			}	
			rebalanceTreeNodeStack(nodeStack);
		}
		
	}
	
	public void rebalanceTreeNodeStack(Stack<Node> nodeStack){
		Stack<Node> passedNodes = new Stack<Node>();
		Code nextDirection = null;
		while (nodeStack.size() > 0) {
			Node current = nodeStack.pop();
			
			if(!passedNodes.isEmpty()) {
				if(nextDirection.equals(Code.LEFT)) {
					current.left=passedNodes.pop();
				}else if(nextDirection.equals(Code.RIGHT)){
					current.right=passedNodes.pop();
				}
			}
			if(!nodeStack.isEmpty()) {
				Node next = nodeStack.peek();
				if(current.equals(next.left)) {
					next.rank--;
					nextDirection=Code.LEFT;
				}else if(current.equals(next.right)) {
					nextDirection=Code.RIGHT;
				}
				
			}else {
				nextDirection=Code.SAME;
			}
			//if(!passedNodes.isEmpty()&&passedNodes.peek().equals(current.left)) {
				//current.rank--;
			//}
			if(current.left.height()>current.right.height()) {
				current.balance=Code.LEFT;
			}else if(current.left.height()<current.right.height()) {
				current.balance=Code.RIGHT;
			}else {
				current.balance=Code.SAME;
			}
			if(Math.abs(current.left.height()-current.right.height())>1) {
				
				if(current.left.height()>current.right.height()) {
					if(current.left.left.height()>=current.left.right.height()) {
						current = rotationRightSingle(current);
					}else if(current.left.left.height()<current.left.right.height()) {
						current = rotationRightDouble(current);
					}
				}else if(current.right.height()>current.left.height()) {
					if(current.right.right.height()>=current.right.left.height()) {
						current = rotationLeftSingle(current);
					}else if(current.right.right.height()<current.right.left.height()) {
						current = rotationLeftDouble(current);
					}
				}
				
			}
			passedNodes.push(current);
		}
		root=passedNodes.pop();
	}

	/**
	 * MILESTONE 3, EASY
	 * This method operates in O(length*log N), where N is the size of this
	 * tree.
	 * 
	 * @param pos
	 *            location of the beginning of the string to retrieve
	 * @param length
	 *            length of the string to retrieve
	 * @return string of length that starts in position pos
	 * @throws IndexOutOfBoundsException
	 *             unless both pos and pos+length-1 are legitimate indexes
	 *             within this tree.
	 */
	public String get(int pos, int length) throws IndexOutOfBoundsException {
		if(pos + length > this.size()) throw new IndexOutOfBoundsException();
		String str = "";
		for(int i = 0; i < length; i++) {
			 str += this.root.get(pos + i).getResult();
		}
		return str;
	}

	/**
	 * MILESTONE 3, MEDIUM - SEE THE PAPER REFERENCED IN THE SPEC FOR ALGORITHM!
	 * Append (in time proportional to the log of the size of the larger tree)
	 * the contents of the other tree to this one. Other should be made empty
	 * after this operation.
	 * 
	 * @param other
	 * @throws IllegalArgumentException
	 *             if this == other
	 */
	public void concatenate(EditTree other) throws IllegalArgumentException {
		if(this == other) throw new IllegalArgumentException();
		if(other.root == Node.NULL_NODE) return;
		if(this.root == Node.NULL_NODE) {
			this.root = other.root;
			other.root = Node.NULL_NODE;
			return;
		}
		if(this.size() == 1) {
			other.add(this.root.element, 0);
			this.root = other.root;
			other.root = Node.NULL_NODE;
		}
		else if(other.size() == 1) {
			this.add(other.root.element, this.size());
			other.root = Node.NULL_NODE;
		}
		else if(this.size() > other.size()) {
			this.ctRight(other);
		}
		else {
			this.ctLeft(other);
		}
	}

	public void ctRight(EditTree other) {
		EditTree T, V;
		int hV, hp;
		Node q, p;
		Stack<Node> s = new Stack<Node>();
		T = this;
		V = other;
		q = other.getNode(0);
		V.delete(0);
		hp = T.height();
		hV = V.height();
		p = T.root;
		while(hp - hV > 1) {
			s.push(p);
			if(p.balance == Code.LEFT) hp -= 2;
			else hp -= 1;
			p = p.right;
		}
		q.left = p;
		q.right = V.root;
		if(hp == hV) q.balance = Code.SAME;
		else q.balance = Code.LEFT;
		s.push(q);
		this.root = ctrebalance(s, true);
		other.root = Node.NULL_NODE;
	}

	public void ctLeft(EditTree other) {
		EditTree T, V;
		int hV, hp;
		Node q, p;
		Stack<Node> s = new Stack<Node>();
		T = other;
		V = this;
		q = this.getNode(this.size()-1);
		V.delete(this.size()-1);
		hp = T.height();
		hV = V.height();
		p = T.root;
		while(hp - hV > 1) {
			s.push(p);
			if(p.balance == Code.RIGHT) hp -= 2;
			else hp -= 1;
			p = p.left;
		}
		q.right = p;
		q.left = V.root;
		if(hp == hV) q.balance = Code.SAME;
		else q.balance = Code.RIGHT;
		s.push(q);
		this.root = ctrebalance(s, false);
		other.root = Node.NULL_NODE;
	}

	public Node ctrebalance(Stack<Node> s, boolean right) {
		Node current = s.pop();
		/*Node next;
		while(!s.isEmpty()) {
			if(Math.abs(current.left.height - current.right.height) > 1) {
				current = right ? rotationLeftSingle(current) : rotationRightSingle(current);
				current.left.setHeight();
				current.right.setHeight();
				current.left.setSize();
				current.right.setSize();
			}
			next = s.pop();
			next.right = current;
			current = next;
		}
		if(current == Node.NULL_NODE) return current;
		current = rotationRightSingle(current);
		try {
			current.left.setHeight();
			current.right.setHeight();
			current.left.setSize();
			current.right.setSize();
		}catch (NullPointerException e){
//			System.out.println("Got Null Pointer while setting heights/sizes");
		}*/
		return current;
	}

	/**
	 * MILESTONE 3: DIFFICULT
	 * This operation must be done in time proportional to the height of this
	 * tree.
	 * 
	 * @param pos
	 *            where to split this tree
	 * @return a new tree containing all of the elements of this tree whose
	 *         positions are >= position. Their nodes are removed from this
	 *         tree.
	 * 
	 *         IMPORTANT NOTE: Figure 7.20 in "Data Structures In Pascal" shows that
	 *         the new tree gets all the elements in positions > pos, not >= pos.
	 *         The JUnit tests expect >=, so add the element identified as 'd' (in
	 *         Figure 7.20) to the new tree, do not add it to the original tree that
	 *         is being split
	 *
	 * @throws IndexOutOfBoundsException
	 */
	public EditTree split(int pos) throws IndexOutOfBoundsException {
		return null; // replace by a real calculation.
	}

	/**
	 * MILESTONE 3: JUST READ IT FOR USE OF SPLIT/CONCATENATE
	 * This method is provided for you, and should not need to be changed. If
	 * split() and concatenate() are O(log N) operations as required, delete
	 * should also be O(log N)
	 * 
	 * @param start
	 *            position of beginning of string to delete
	 * 
	 * @param length
	 *            length of string to delete
	 * @return an EditTree containing the deleted string
	 * @throws IndexOutOfBoundsException
	 *             unless both start and start+length-1 are in range for this
	 *             tree.
	 */
	public EditTree delete(int start, int length)
			throws IndexOutOfBoundsException {
		if (start < 0 || start + length >= this.size())
			throw new IndexOutOfBoundsException(
					(start < 0) ? "negative first argument to delete"
							: "delete range extends past end of string");
		EditTree t2 = this.split(start);
		EditTree t3 = t2.split(length);
		this.concatenate(t3);
		return t2;
	}

	/**
	 * MILESTONE 3
	 * Don't worry if you can't do this one efficiently.
	 * 
	 * @param s
	 *            the string to look for
	 * @return the position in this tree of the first occurrence of s; -1 if s
	 *         does not occur
	 */
	public int find(String s) {
		return this.find(s, 0);
	}

	/**
	 * MILESTONE 3
	 * @param s
	 *            the string to search for
	 * @param pos
	 *            the position in the tree to begin the search
	 * @return the position in this tree of the first occurrence of s that does
	 *         not occur before position pos; -1 if s does not occur
	 */
	public int find(String s, int pos) {
		if(s.equals("")) return 0;
		int index = pos;
		int size = this.size();
		while(index < size - s.length() + 1) {
			if(this.get(index) == s.charAt(0)) {
				String check = this.get(index, s.length());
				if(this.get(index, s.length()).equals(s)) return index;
			}
			index++;
		}
		return -1;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}
}
