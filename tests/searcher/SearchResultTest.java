package searcher;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SearchResultTest {

	@Test
	void testCRUD() {
		long rez, neg;
		
		rez=0;
		rez = SearchOutcome.setScore(rez, -10000);
		rez = SearchOutcome.setDepth(rez, 122);
		rez = SearchOutcome.setQuiescenceDepth(rez, 30);
		rez = SearchOutcome.setCheckmate(rez);
		rez = SearchOutcome.setOtherDraw(rez);
		assertEquals(-10000, SearchOutcome.getScore(rez));
		assertEquals(122, SearchOutcome.getDepth(rez));
		assertEquals(30, SearchOutcome.getQuiescenceDepth(rez));
		assertEquals(true, SearchOutcome.isCheckmate(rez));
		assertEquals(false, SearchOutcome.isStalemate(rez));
		assertEquals(true, SearchOutcome.isOtherDraw(rez));
		
		neg = SearchOutcome.negateScore(rez);
		assertEquals(10000, SearchOutcome.getScore(neg));
		assertEquals(122, SearchOutcome.getDepth(neg));
		assertEquals(30, SearchOutcome.getQuiescenceDepth(rez));
		assertEquals(true, SearchOutcome.isCheckmate(neg));
		assertEquals(false, SearchOutcome.isStalemate(neg));
		assertEquals(true, SearchOutcome.isOtherDraw(neg));
		
		assertTrue(SearchOutcome.compare(rez, SearchOutcome.LT, neg));
		assertFalse(SearchOutcome.compare(neg, SearchOutcome.LT, rez));
		
		rez=0;
		rez = SearchOutcome.setScore(rez, 1500);
		rez = SearchOutcome.setDepth(rez, 78);
		rez = SearchOutcome.setQuiescenceDepth(rez, 26);
		rez = SearchOutcome.setStalemate(rez);
		assertEquals(1500, SearchOutcome.getScore(rez));
		assertEquals(78, SearchOutcome.getDepth(rez));
		assertEquals(26, SearchOutcome.getQuiescenceDepth(rez));
		assertEquals(false, SearchOutcome.isCheckmate(rez));
		assertEquals(true, SearchOutcome.isStalemate(rez));
		assertEquals(false, SearchOutcome.isOtherDraw(rez));
		
		neg = SearchOutcome.negateScore(rez);
		assertEquals(-1500, SearchOutcome.getScore(neg));
		assertEquals(78, SearchOutcome.getDepth(neg));
		assertEquals(26, SearchOutcome.getQuiescenceDepth(rez));
		assertEquals(false, SearchOutcome.isCheckmate(neg));
		assertEquals(true, SearchOutcome.isStalemate(neg));
		assertEquals(false, SearchOutcome.isOtherDraw(neg));
		
		assertFalse(SearchOutcome.compare(rez, SearchOutcome.LT, neg));
		assertTrue(SearchOutcome.compare(neg, SearchOutcome.LT, rez));
	}
	
	@Test
	void testConstructors() {
		long rez =0;
		
		rez=SearchOutcome.createCheckmate(3);
		assertEquals(SearchOutcome.LOSS+4, SearchOutcome.getScore(rez));
		assertEquals(3, SearchOutcome.getDepth(rez));
		assertEquals(true, SearchOutcome.isCheckmate(rez));
		assertEquals(false, SearchOutcome.isStalemate(rez));
		assertEquals(false, SearchOutcome.isOtherDraw(rez));
		
		rez=SearchOutcome.negateScore(rez);
		assertEquals(SearchOutcome.WIN-4, SearchOutcome.getScore(rez));
		assertEquals(3, SearchOutcome.getDepth(rez));
		assertEquals(true, SearchOutcome.isCheckmate(rez));
		assertEquals(false, SearchOutcome.isStalemate(rez));
		assertEquals(false, SearchOutcome.isOtherDraw(rez));
		
		rez=SearchOutcome.negateScore(rez);
		assertEquals(SearchOutcome.LOSS+4, SearchOutcome.getScore(rez));
		assertEquals(3, SearchOutcome.getDepth(rez));
		assertEquals(true, SearchOutcome.isCheckmate(rez));
		assertEquals(false, SearchOutcome.isStalemate(rez));
		assertEquals(false, SearchOutcome.isOtherDraw(rez));
		
		assertTrue(
			SearchOutcome.compare(
				SearchOutcome.createCheckmate(1),
				SearchOutcome.GTE,
				SearchOutcome.createLowerBound(SearchOutcome.LOSS)
			)
		);
		
		assertTrue(
			SearchOutcome.compare(
				SearchOutcome.createCheckmate(1),
				SearchOutcome.GT,
				SearchOutcome.createLowerBound(SearchOutcome.LOSS)
			)
		);
		
		assertTrue(
			SearchOutcome.compare(
				SearchOutcome.createCheckmate(1),
				SearchOutcome.LT,
				SearchOutcome.createLowerBound(SearchOutcome.WIN)
			)
		);
		
		assertTrue(
			SearchOutcome.compare(
				SearchOutcome.createCheckmate(1),
				SearchOutcome.LTE,
				SearchOutcome.createLowerBound(SearchOutcome.WIN)
			)
		);
		
	}
}
