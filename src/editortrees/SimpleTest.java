package editortrees;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.AfterClass;
import org.junit.Test;
import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

public class SimpleTest {
	/*@Test
	public void testAdd() {
		EditTree t = new EditTree();
		t.add('a');
		t.add('b', 1);
		t.add('c', 0);
		t.add('d', 0);
		System.out.println(t);
		System.out.println(t.getRoot().toTestString());
		System.out.println("L=" + t.getRoot().left.toTestString());
		System.out.println("R=" + t.getRoot().right.toTestString());
		if (t.getRoot().left != Node.NULL_NODE) {
			System.out.println("LL=" + t.getRoot().left.left.toTestString());
			System.out.println("LR=" + t.getRoot().left.right.toTestString());
		}
		if (t.getRoot().right != Node.NULL_NODE) {
			System.out.println("RL=" + t.getRoot().right.left.toTestString());
			System.out.println("RR=" + t.getRoot().right.right.toTestString());
		}
		//System.out.println(t.getRoot().add('a', 0));
	}*/

	@Test
	public void testBalance() {
		EditTree t = new EditTree();
		//System.out.println("a: " + t.add('a'));
		//System.out.println("b: " + t.add('b'));
		//System.out.println("c: " + t.add('c'));*/
		t.add('b');
		t.add('a');
		t.add('c');
		/*t.add('d',0);
		t.add('e',2);
		t.add('f',4);
		t.add('g',6);*/
		t.add('h',0);
		t.add('i',0);
		t.add('j');
		t.add('k',4);
		System.out.println(t.toDebugString());
		/*System.out.println(t.getRoot().toTestString());
		System.out.println(t.get(0));
		System.out.println(t.get(1));
		System.out.println(t.get(2));
		System.out.println(t.get(3));
		System.out.println(t.get(4));
		System.out.println(t.get(5));*/
		//System.out.println("d: " + t.add('d', 0));
		//System.out.println("e: " + t.add('e', 0));
		/*System.out.println(t.getRoot().toTestString());
		System.out.println("L=" + t.getRoot().left.toTestString());
		System.out.println("R=" + t.getRoot().right.toTestString());
		if (t.getRoot().left != Node.NULL_NODE) {
			System.out.println("LL=" + t.getRoot().left.left.toTestString());
			System.out.println("LR=" + t.getRoot().left.right.toTestString());
		}
		if (t.getRoot().right != Node.NULL_NODE) {
			System.out.println("RL=" + t.getRoot().right.left.toTestString());
			System.out.println("RR=" + t.getRoot().right.right.toTestString());
		}*/
	}
}
