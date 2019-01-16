package editortrees;

import editortrees.Node.Code;

// A height-balanced binary tree with rank that could be the basis for a text editor.

public class EditTree {

	private Node root = Node.NULL_NODE;
	private int rotationCount = 0;

	/**
	 * MILESTONE 1
	 * Construct an empty tree
	 */
	public EditTree() {
		root=Node.NULL_NODE;
	}

	/**
	 * MILESTONE 1
	 * Construct a single-node tree whose element is ch
	 * 
	 * @param ch
	 */
	public EditTree(char ch) {
		root=new Node(ch);
	}

	/**
	 * MILESTONE 2
	 * Make this tree be a copy of e, with all new nodes, but the same shape and
	 * contents.
	 * 
	 * @param e
	 */
	public EditTree(EditTree e) {

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
		if(!build.toString().equals("[")) {
			build.deleteCharAt(build.length()-2);
			build.deleteCharAt(build.length()-1);
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
		this.add(ch, -1);
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
		if (this.root == Node.NULL_NODE)
			this.root = new Node(ch);
		else {
			AddResult result = this.root.add(ch, pos);
			if (result.success && result.rotation != null) {
				Node node = Node.NULL_NODE;
				switch (result.rotation) {
					case LEFT_SINGLE:
						node = this.rotationLeftSingle(result.node);
						break;
					case LEFT_DOUBLE:
						//node = this.rotationLeftDouble(result.node);
						node = result.node;
						break;
					case RIGHT_SINGLE:
						node = this.rotationRightSingle(result.node);
						break;
					case RIGHT_DOUBLE:
						//node = this.rotationRightDouble(result.node);
						node = result.node;
						break;
				}
				if (result.parent == Node.NULL_NODE) this.root = node;
				else if (result.directions[0] == Code.LEFT) result.parent.left = node;
				else if (result.directions[0] == Code.RIGHT) result.parent.right = node;
			}
		}
	}

	public Node rotationLeftSingle(Node parent) {
		Node child = parent.right;
		parent.right = child.left;
		child.left = parent;
		parent.balance = Code.SAME;
		child.balance = Code.SAME;
		this.rotationCount++;
		return child;
	}

	public Node rotationRightSingle(Node parent) {
		Node child = parent.left;
		parent.left = child.right;
		child.right = parent;
		parent.balance = Code.SAME;
		child.balance = Code.SAME;
		this.rotationCount++;
		return child;
	}
	
	public Node rotationLeftDouble(Node rotationRoot) {
		Node newRoot = new Node(rotationRoot.right.left.element);
		newRoot.left = rotationRoot;
		newRoot.right = rotationRoot.right;
		newRoot.right.left=newRoot.right.left.right;
		rotationRoot.right=rotationRoot.right.left.left;
		newRoot.balance=Code.SAME;
		newRoot.left.balance=Code.SAME;
		this.rotationCount += 2;
		return newRoot;	
	}

	public Node rotationRightDouble(Node rotationRoot) {
		Node newRoot = new Node(rotationRoot.left.right.element);
		newRoot.right = rotationRoot;
		newRoot.left = rotationRoot.left;
		newRoot.left.right=newRoot.left.right.left;
		rotationRoot.left=rotationRoot.left.right.right;
		newRoot.balance=Code.SAME;
		newRoot.right.balance=Code.SAME;
		this.rotationCount += 2;
		return newRoot;
	}
	
	
	/**
	 * MILESTONE 1
	 * @param pos
	 *            position in the tree
	 * @return the character at that position
	 * @throws IndexOutOfBoundsException
	 */
	public char get(int pos) throws IndexOutOfBoundsException {
		return '%';
	}

	/**
	 * MILESTONE 1
	 * @return the height of this tree
	 */
	public int height() {
		return this.root.height() - 1;
	}

	/**
	 * MILESTONE 2
	 * @return the number of nodes in this tree, 
	 *         not counting the NULL_NODE if you have one.
	 */
	public int size() {
		return -1; // replace by a real calculation.
	}
	
	
	/**
	 * MILESTONE 2
	 * @param pos
	 *            position of character to delete from this tree
	 * @return the character that is deleted
	 * @throws IndexOutOfBoundsException
	 */
	public char delete(int pos) throws IndexOutOfBoundsException {
		// Implementation requirement:
		// When deleting a node with two children, you normally replace the
		// node to be deleted with either its in-order successor or predecessor.
		// The tests assume assume that you will replace it with the
		// *successor*.
		return '#'; // replace by a real calculation.
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
		return "";
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
		return -2;
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
		return -2;
	}

	/**
	 * @return The root of this tree.
	 */
	public Node getRoot() {
		return this.root;
	}
}
