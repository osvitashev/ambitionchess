package util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class ResettableArray32Test {

	@Test
	void testOperations() {
		ResettableArray32 ar = new ResettableArray32(5);
		ar.add(-13);
		ar.add(3);
		
		assertEquals(-13, ar.get(0));
		assertEquals(3, ar.get(1));
		assertEquals(2, ar.size());
		
		ar.reset();
		ar.add(5);
		assertEquals(5, ar.get(0));
		assertEquals(1, ar.size());
		
	}

}
