package searcher;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SearchResultTest {

	@Test
	void testCRUD() {
		long rez, neg;
		
		rez=0;
		rez = SearchResult.setScore(rez, -10000);
		rez = SearchResult.setDepth(rez, 122);
		rez = SearchResult.setCheckmate(rez);
		rez = SearchResult.setOtherDraw(rez);
		assertEquals(-10000, SearchResult.getScore(rez));
		assertEquals(122, SearchResult.getDepth(rez));
		assertEquals(true, SearchResult.isCheckmate(rez));
		assertEquals(false, SearchResult.isStalemate(rez));
		assertEquals(true, SearchResult.isOtherDraw(rez));
		
		neg = SearchResult.negateScore(rez);
		assertEquals(10000, SearchResult.getScore(neg));
		assertEquals(122, SearchResult.getDepth(neg));
		assertEquals(true, SearchResult.isCheckmate(neg));
		assertEquals(false, SearchResult.isStalemate(neg));
		assertEquals(true, SearchResult.isOtherDraw(neg));
		
		assertTrue(SearchResult.isScoreLess(rez, neg));
		assertFalse(SearchResult.isScoreLess(neg, rez));
		
		rez=0;
		rez = SearchResult.setScore(rez, 1500);
		rez = SearchResult.setDepth(rez, 78);
		rez = SearchResult.setStalemate(rez);
		assertEquals(1500, SearchResult.getScore(rez));
		assertEquals(78, SearchResult.getDepth(rez));
		assertEquals(false, SearchResult.isCheckmate(rez));
		assertEquals(true, SearchResult.isStalemate(rez));
		assertEquals(false, SearchResult.isOtherDraw(rez));
		
		neg = SearchResult.negateScore(rez);
		assertEquals(-1500, SearchResult.getScore(neg));
		assertEquals(78, SearchResult.getDepth(neg));
		assertEquals(false, SearchResult.isCheckmate(neg));
		assertEquals(true, SearchResult.isStalemate(neg));
		assertEquals(false, SearchResult.isOtherDraw(neg));
		
		assertFalse(SearchResult.isScoreLess(rez, neg));
		assertTrue(SearchResult.isScoreLess(neg, rez));
	}
	
	@Test
	void testConstructors() {
		long rez =0;
		
		rez=SearchResult.createCheckmate(3);
		assertEquals(SearchResult.LOSS+3, SearchResult.getScore(rez));
		assertEquals(3, SearchResult.getDepth(rez));
		assertEquals(true, SearchResult.isCheckmate(rez));
		assertEquals(false, SearchResult.isStalemate(rez));
		assertEquals(false, SearchResult.isOtherDraw(rez));
		
		rez=SearchResult.negateScore(rez);
		assertEquals(SearchResult.WIN-3, SearchResult.getScore(rez));
		assertEquals(3, SearchResult.getDepth(rez));
		assertEquals(true, SearchResult.isCheckmate(rez));
		assertEquals(false, SearchResult.isStalemate(rez));
		assertEquals(false, SearchResult.isOtherDraw(rez));
		
		rez=SearchResult.negateScore(rez);
		assertEquals(SearchResult.LOSS+3, SearchResult.getScore(rez));
		assertEquals(3, SearchResult.getDepth(rez));
		assertEquals(true, SearchResult.isCheckmate(rez));
		assertEquals(false, SearchResult.isStalemate(rez));
		assertEquals(false, SearchResult.isOtherDraw(rez));
		
	}
}
