package gamestate.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import gamestate.MovePool;

class MovePoolTest {

	@Test
	void test() {
		MovePool pool = new MovePool();
		
		pool.add(1);
		pool.add(2);
		pool.add(3);
		pool.add(4);
		pool.add(5);
		pool.add(6);
		pool.add(7);
		pool.add(8);
		pool.add(9);
		pool.add(10);
		pool.add(11);
		pool.add(12);
		pool.add(13);
		pool.add(14);
		pool.add(15);
		pool.add(16);
		
		assertEquals(16, pool.size());
		assertEquals(1, pool.get(0));
		assertEquals(15, pool.get(14));
		
		//sort range
		
		pool.resize(10);
		assertEquals(10, pool.size());
		assertEquals(10, pool.get(9));
		pool.add(100);
		assertEquals(11, pool.size());	
		
		pool.clear();
		assertEquals(0, pool.size());
		pool.add(77);
		assertEquals(1, pool.size());	
	}

}
