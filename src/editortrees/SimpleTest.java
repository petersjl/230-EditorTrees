package editortrees;

import org.junit.Test;

public class SimpleTest {
	@Test
	public void test() {
		/*int height = 3;
		EditTree t = new EditTree();
		for (int h = 0; h <= height; h++) {
			for (int i = 0; i < Math.pow(2, h); i++) {
				t.add((char) (96 + (Math.pow(2, h) + i)), i * 2);
				//t.add((char) (48 + h), i * 2);
			}
		}
		System.out.println("     " + t);
		EditTree t2;
		for (int i = 0; i < Math.pow(2, height + 1) - 1; i++) {
			t2 = new EditTree(t);
			char ch = t2.get(i);
			t2.delete(i);
			System.out.println(i + (i < 10 ? "  " : " ") + ch + " " + t2);
		}*/

		EditTree t3 = new EditTree();
		t3.add('X');
		t3.add('a', 0);
		t3.add('X');

		t3.add('b', 0);
		t3.add('c', 2);
		t3.add('X', 4);
		t3.add('X', 6);

		t3.add('d', 0);
		t3.add('e', 2);
		t3.add('f', 4);
		t3.add('g', 6);
		t3.add('X', 8);
		t3.add('X', 10);
		t3.add('X', 12);
		t3.add('X', 14);

		t3.add('h', 0);
		t3.add('i', 2);
		t3.add('j', 4);
		t3.add('k', 6);
		t3.add('l', 8);
		t3.add('m', 10);
		t3.add('n', 12);
		t3.add('o', 14);
		t3.add('X', 16);
		t3.add('X', 18);
		t3.add('X', 20);
		t3.add('X', 22);
		t3.add('X', 24);
		t3.add('X', 26);
		t3.add('X', 28);
		t3.add('X', 30);

		EditTree t4 = new EditTree(t3);
		t4.getRoot().mirror();

		System.out.println(t3.getRoot().left);
		System.out.println(t3.toDebugString());
		//System.out.println(t3.getRoot().toDebugString2());
		System.out.println("Deleted: " + t3.delete(7));
		System.out.println(t3.getRoot().left);
		System.out.println(t3.toDebugString());
		System.out.println(t3.getRoot().left.toDebugString2());

		System.out.println("\n\n\n\n----------------------------\n\n\n\n");

		System.out.println(t4.getRoot().right);
		System.out.println(t4.toDebugString());
		//System.out.println(t3.getRoot().toDebugString2());
		System.out.println("Deleted: " + t4.delete(23));
		System.out.println(t4.getRoot().right);
		System.out.println(t4.toDebugString());
		System.out.println(t4.getRoot().right.toDebugString2());
	}
}
