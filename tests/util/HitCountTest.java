package util;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

@SuppressWarnings("deprecation")
class HitCountTest {
	
	private int sumWithSide(int [] arg) {
		int ret=0;
		for(int i : arg) {
			if(i%3==0)
				HitCounter.count("YES");
			if(i%3 != 0)
				HitCounter.count("NO");
			ret+=i;
		}		
		return ret;
	}

	@Test
	void testHitCounts() {
		int[] arr = {1,2,3,4,5,6,7,8,9,10};
		int sum = sumWithSide(arr);
		assertEquals(55, sum);
		assertEquals("NO: 7\nYES: 3\n", HitCounter.dump());
		System.out.println(HitCounter.dump() + sum);
	}

}
