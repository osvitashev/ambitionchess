package util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class BitwiseVectorTransposeTest {

	@Test
	void testOperations() {
		BitwiseVectorTranspose v = new BitwiseVectorTranspose(20);
		
		for(int i=0;i<v.size();++i)
			assertEquals(0l, v.get(i));
		
		v.set(1, 0x1800000000000000l);
		v.set(2, 0x18l);
		v.set(3, 0xa000000000000005l);
		v.set(8, 0xa00000000000050l);
		
		assertEquals(0x1800000000000000l, v.get(1));
		assertEquals(0x18l, v.get(2));
		assertEquals(0xa000000000000005l, v.get(3));
		assertEquals(0xa00000000000050l, v.get(8));
		
		v.leftShiftWhere(0xff00000000000000l, 5);
		
		assertEquals(0x0, v.get(1));
		assertEquals(0x18l, v.get(2));
		assertEquals(0x5, v.get(3));
		assertEquals(0x1800000000000000l, v.get(6));
		assertEquals(0x0l, v.get(7));
		assertEquals(0xa000000000000050l, v.get(8));
		assertEquals(0xa00000000000000l, v.get(13));
		
		v.resetTo0s();
		
		assertEquals(0x0l, v.get(1));
		assertEquals(0x0l, v.get(2));
		assertEquals(0x0l, v.get(3));
		assertEquals(0x0l, v.get(6));
		assertEquals(0x0l, v.get(7));
		assertEquals(0x0l, v.get(8));
		assertEquals(0x0l, v.get(13));
		
		v.resetTo1s();
		
		assertEquals(~0x0l, v.get(1));
		assertEquals(~0x0l, v.get(2));
		assertEquals(~0x0l, v.get(3));
		assertEquals(~0x0l, v.get(6));
		assertEquals(~0x0l, v.get(7));
		assertEquals(~0x0l, v.get(8));
		assertEquals(~0x0l, v.get(13));
		
		v.setWhere(13, 0x5500l, 0xf000);
		assertEquals(0xffffffffffff5fffl, v.get(13));
	}

}
