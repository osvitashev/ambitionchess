package util;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.ByteBuffer;
import java.util.zip.CRC32;

import org.junit.jupiter.api.Test;

class RollingCheckSumTest {

	@Test
	void test() {
		CRC32 crc = new CRC32();
		
		String str1 = "OpenAI’s messy firing and re-hiring of its powerful chief executive this week shocked the tech world. But the power struggle has implications beyond the company’s boardroom, AI experts said. It throws into relief the greenness of the AI industry and the strong desire in Silicon Valley to be first, and raises urgent questions about the safety of the technology.";
		String str2 = "If the old board judged, for example, that Altman was unfit for the job because he was taking OpenAI down a reckless path, lights-wise, there would plainly be an obligation to speak up. Or, if the fear is unfounded, the architects of the failed boardroom coup could do everybody a favour and say so. Saying nothing useful, especially when your previous stance has been that transparency and safety go hand in hand, is indefensible.";
		String str3 = "Yet the precise reason for sacking Altman still matters. There were only four members of the board apart from him. One was the chief scientist, Ilya Sutskever, who subsequently performed a U-turn that he didn’t explain. Another is Adam D’Angelo, chief executive of the question-and-answer site Quora, who, bizarrely, intends to transition seamlessly from the board that sacked Altman to the one that hires him back. Really?";
		
		byte arg1 [] = str1.getBytes();
		byte arg2 [] = str2.getBytes();
		byte arg3 [] = str3.getBytes();
		
		System.out.println(arg1.length + ", " +arg2.length + ", "+ arg3.length);
		
		crc.reset();
		crc.update(arg1);
		System.out.println("A checksum: " + Long.toHexString(crc.getValue()));
		crc.reset();
		crc.update(arg2);
		System.out.println("B checksum: " + Long.toHexString(crc.getValue()));
		
		crc.reset();
		crc.update(arg1);
		crc.update(arg2);
		System.out.println("A+B checksum: " + Long.toHexString(crc.getValue()));
		
		System.out.println("Second attempt->>>>>>>>>");
		crc.reset();
		crc.update(arg1);
		System.out.println("A checksum: " + Long.toHexString(crc.getValue()));
		crc.reset();
		crc.update(arg2);
		System.out.println("B checksum: " + Long.toHexString(crc.getValue()));
		
		crc.reset();
		crc.update(arg1);
		crc.update(arg2);
		System.out.println("A+B checksum: " + Long.toHexString(crc.getValue()));
		
		crc.reset();
		crc.update(arg1);
		System.out.println("A checksum: " + Long.toHexString(crc.getValue()));
		crc.update(arg1);
		System.out.println("Ax2 checksum: " + Long.toHexString(crc.getValue()));
		
		//fail("Not yet implemented");
	}

}
