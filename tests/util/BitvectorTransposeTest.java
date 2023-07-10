package util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.Bitboard;
import gamestate.GlobalConstants.Square;

class BitvectorTransposeTest {

	@Test
	void testAddAndCompare() {
		int a = Square.A1, b = Square.A2, c = Square.H8;
		long mask_a = Bitboard.initFromSquare(a);
		long mask_b = Bitboard.initFromSquare(b);
		long mask_c = Bitboard.initFromSquare(c);
		
		
		BitvectorTranspose bvt = new BitvectorTranspose();
		assertEquals(0,bvt.indexToScalar(a));
		assertEquals(0,bvt.indexToScalar(b));
		assertEquals(0,bvt.indexToScalar(c));
		
		assertEquals(0l, bvt.maskWhereLessThan(-1));
		assertEquals(0l, bvt.maskWhereLessThan(0));
		assertEquals(~0l, bvt.maskWhereLessThan(1));
		
		bvt.addScalar(0, 5);
		assertEquals(0,bvt.indexToScalar(a));
		assertEquals(0,bvt.indexToScalar(b));
		assertEquals(0,bvt.indexToScalar(c));
		
		assertEquals(0l, bvt.maskWhereLessThan(-10));
		assertEquals(0l, bvt.maskWhereLessThan(0));
		assertEquals(~0l, bvt.maskWhereLessThan(10));
		
		bvt.addScalar(mask_a | mask_c, 5);
		assertEquals(5,bvt.indexToScalar(a));
		assertEquals(0,bvt.indexToScalar(b));
		assertEquals(5,bvt.indexToScalar(c));
		
		
		///adding comparison tests
		assertEquals(0x7ffffffffffffffel, bvt.maskWhereLessThan(4));
		assertEquals(0x7ffffffffffffffel, bvt.maskWhereLessThan(5));
		assertEquals(~0l, bvt.maskWhereLessThan(6));
		assertEquals(0l, bvt.maskWhereLessThan(0));
		assertEquals(0l, bvt.maskWhereLessThan(-6));
		
		bvt.addScalar(mask_a | mask_b, 3);
		assertEquals(8,bvt.indexToScalar(a));
		assertEquals(3,bvt.indexToScalar(b));
		assertEquals(5,bvt.indexToScalar(c));
		
		assertEquals(~mask_a, bvt.maskWhereLessThan(8));
		assertEquals(~0l &~mask_a &~mask_c, bvt.maskWhereLessThan(4));
		
		bvt.addScalar(mask_a | mask_b, -3);
		assertEquals(5,bvt.indexToScalar(a));
		assertEquals(0,bvt.indexToScalar(b));
		assertEquals(5,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_b | mask_c, -3);
		assertEquals(5,bvt.indexToScalar(a));
		assertEquals(-3,bvt.indexToScalar(b));
		assertEquals(2,bvt.indexToScalar(c));
		
		assertEquals(mask_b, bvt.maskWhereLessThan(-2));
		assertEquals(~0l &~mask_a, bvt.maskWhereLessThan(3));
		
		bvt.addScalar(mask_a | mask_c, -6);
		assertEquals(-1,bvt.indexToScalar(a));
		assertEquals(-3,bvt.indexToScalar(b));
		assertEquals(-4,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_c, -22);
		assertEquals(-1,bvt.indexToScalar(a));
		assertEquals(-3,bvt.indexToScalar(b));
		assertEquals(-26,bvt.indexToScalar(c));
		
		assertEquals(mask_b |mask_c, bvt.maskWhereLessThan(-2));
		assertEquals(mask_b |mask_c, bvt.maskWhereLessThan(-1));
		
		bvt.addScalar(mask_b | mask_c, 30);
		assertEquals(-1,bvt.indexToScalar(a));
		assertEquals(27,bvt.indexToScalar(b));
		assertEquals(4,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_a | mask_c, 1);
		assertEquals(0,bvt.indexToScalar(a));
		assertEquals(27,bvt.indexToScalar(b));
		assertEquals(5,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_b, -26);
		assertEquals(0,bvt.indexToScalar(a));
		assertEquals(1,bvt.indexToScalar(b));
		assertEquals(5,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_c, -3);
		assertEquals(0,bvt.indexToScalar(a));
		assertEquals(1,bvt.indexToScalar(b));
		assertEquals(2,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_a | mask_b | mask_c, -2);
		assertEquals(-2,bvt.indexToScalar(a));
		assertEquals(-1,bvt.indexToScalar(b));
		assertEquals(0,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_a | mask_b | mask_c, 1);
		assertEquals(-1,bvt.indexToScalar(a));
		assertEquals(0,bvt.indexToScalar(b));
		assertEquals(1,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_a | mask_b | mask_c, 1);
		assertEquals(0,bvt.indexToScalar(a));
		assertEquals(1,bvt.indexToScalar(b));
		assertEquals(2,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_a | mask_b | mask_c, -1);
		assertEquals(-1,bvt.indexToScalar(a));
		assertEquals(0,bvt.indexToScalar(b));
		assertEquals(1,bvt.indexToScalar(c));
		
		bvt.addScalar(mask_a | mask_b | mask_c, -1);
		assertEquals(-2,bvt.indexToScalar(a));
		assertEquals(-1,bvt.indexToScalar(b));
		assertEquals(0,bvt.indexToScalar(c));
		
	}
	


}
