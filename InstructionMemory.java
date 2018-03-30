/*
 * This class is responsible for iterating the processor through the program. It reads in text lines from the test file one at a
 * time, then computes and returns their byte array equivalents to simulate a processor's structure. As the test files have (supposedly)
 * already been examined to ensure accuracy and validity, it provides no handling for malformed instructions.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InstructionMemory {
	
	private Scanner input;
	private String line;
	
	public InstructionMemory(File f) throws FileNotFoundException {
		input = new Scanner(f);
	}

	public byte[] fetchNext() {
		if (input.hasNext()) {
			byte[] res = new byte[4];
			line = input.nextLine().trim();
			String tempStr;
			int temp = 0;
			
			// first compute the unsigned int equivalent of the 32-bit instruction, then cast it to a byte array
			for (int i = 0; i < 4; i++) {
				tempStr = line.substring(i*8, (i+1)*8);
				temp = 0;
				for (int j = 0; j < 8; j++) {
					if (tempStr.charAt(j) == '1') temp += Math.pow(2, 7-j); 
				}
				res[i] = (byte) temp;
			}
			return res;
		}
		else return Processor.NAN;
	}
}
