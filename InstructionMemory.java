import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class InstructionMemory {
	
	private Scanner input;
	private String line;
//	private BitSet bs;
	
	public InstructionMemory(File f) throws FileNotFoundException {
		input = new Scanner(f);
	}

	public byte[] fetchNext() {
		if (input.hasNext()) {
			byte[] res = new byte[4];
			line = input.nextLine().trim();
			String tempStr;
			int temp = 0;
			
			for (int i = 0; i < 4; i++) {
				tempStr = line.substring(i*8, (i+1)*8);
				temp = 0;
				for (int j = 0; j < 8; j++) {
					if (tempStr.charAt(j) == '1') temp += Math.pow(2, 7-j); 
				}
				res[i] = (byte) temp;
			}
			return res;
			
//			bs = new BitSet(Processor.WORDSIZE);
//			for (int i = 0; i < Processor.WORDSIZE; i++) {
//				if (line.charAt(i) == '1') bs.set(i);
//			}
//			return bs.toByteArray();
		}
		else return Processor.NAN;
	}
}