package editortrees;

import org.junit.Test;

public class SimpleTest {
	@Test
	public void test() {
		int height = 3;
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
		}

	}
}
