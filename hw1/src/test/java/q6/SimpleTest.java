package q6;

import org.junit.Test;
import static org.junit.Assert.*;

public class SimpleTest {

	@Test
	public void TestBakery() {
		int res = q6.Bakery.PIncrement.parallelIncrement(0, 8);
		assertTrue("Result is " + res + ", expected result is 1200000.", res == 1200000);
	}

//	@Test
//	public void TestAtomicInteger() {
//    	int res = q6.AtomicInteger.PIncrement.parallelIncrement(0, 8);
//    	assertTrue("Result is " + res + ", expected result is 1200000.", res == 1200000);
//	}
//
//	@Test
//	public void TestSynchronized() {
//    	int res = q6.Synchronized.PIncrement.parallelIncrement(0, 8);
//    	assertTrue("Result is " + res + ", expected result is 1200000.", res == 1200000);
//	}

//	@Test
//	public void TestReentrantLock() {
//		int res = q6.ReentrantLock.PIncrement.parallelIncrement(0, 8);
//		assertTrue("Result is " + res + ", expected result is 1200000.", res == 1200000);
//	}
}
