import java.io.File;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class Processor {

	// CONSTANTS
	protected static final int WORDSIZE = 32;
	protected static final int NUM_REGISTERS = 16;
	protected static final int BIAS = 127;
	protected static final byte[] NAN = javax.xml.bind.DatatypeConverter.parseHexBinary("80000000");
	protected static final byte[] POS_INFINITY = javax.xml.bind.DatatypeConverter.parseHexBinary("7F800000");
	protected static final byte[] NEG_INFINITY = javax.xml.bind.DatatypeConverter.parseHexBinary("FF800000");
	
	// BLOCKS
	protected RegisterBlock regBlk = new RegisterBlock();
	protected Control ctrl = new Control(this);
	protected MemoryBlock memBlk = new MemoryBlock(this);
	protected InstructionMemory im;
	protected ALU alu = new ALU(this);
	
	// FLAGS
 	protected boolean Z = false;	// zero
	protected boolean N = false;	// negative
	protected boolean V = false;	// overflow
	protected boolean U = false;	// underflow
	protected boolean X = false;	// exception
	
	// OTHER
	protected int regCode = -1; // encoding for which registers get used in a given instruction
	
	/*
	 * This is the function that drives the processor through the execution of the program. It assumes that the
	 * program supplied is already formatted into binary. 
	 */
	public void runProgram(File f) throws FileNotFoundException {
		im = new InstructionMemory(f);
		int writeReg = -1;
		int readReg1 = -1;
		int readReg2 = -1;
		byte[] instBytes;
		byte[] readData1 = null;
		byte[] readData2 = null;
		float dataFlt = Float.NaN;
		float getVal;
		String partialFloat;
		String inst;
		String opcode;
		
		while (true) {
			instBytes = im.fetchNext(); // fetches next instruction to execute
			if (Arrays.equals(instBytes, NAN)) break;
			inst = bytes2String(instBytes);
			opcode = inst.substring(0, 5);
			ctrl.process(opcode);
			writeReg = Integer.parseInt(inst.substring(5, 9), 2);
			readReg1 = Integer.parseInt(inst.substring(9, 13), 2);
			readReg2 = Integer.parseInt(inst.substring(13, 17), 2);
			memBlk.addr = writeReg;
			
			// Sign extension
			if (opcode.equals("00001") || opcode.equals("11000") || opcode.equals("11001") ||
				opcode.equals("11010") || opcode.equals("11011")) {
					partialFloat = inst.substring(9, 32);
					dataFlt = convertPartialToFloat(partialFloat);
					readData2 = ByteBuffer.allocate(4).putFloat(dataFlt).array();
			} else if (opcode.equals("10000")) {
					partialFloat = inst.substring(13, 32);
					dataFlt = convertPartialToFloat(partialFloat);
					readData2 = ByteBuffer.allocate(4).putFloat(dataFlt).array();
			}
			
			// output raw instruction machine code to the console
			printInst(inst, opcode, writeReg, readReg1, readReg2, dataFlt);
			
			// control flow for non-ALU ops
			if (!ctrl.regWrite) { // get
				getVal = convertPartialToFloat(bytes2String(regBlk.readReg1(writeReg)));
				System.out.println("Register " + writeReg + " decimal value: " + getVal);
			} else if (opcode.equals("00001")) { // set
				memBlk.data = readData2;
				memBlk.writeData();
				if (ByteBuffer.wrap(readData2).order(ByteOrder.BIG_ENDIAN).getFloat() < 0) N = true;
				if (ByteBuffer.wrap(readData2).order(ByteOrder.BIG_ENDIAN).getFloat() == 0) Z = true;
			} else if (opcode.equals("00011")) { // move
				readData1 = regBlk.readReg1(readReg1);
				memBlk.data = readData1;
				memBlk.writeData();
				if (ByteBuffer.wrap(readData2).order(ByteOrder.BIG_ENDIAN).getFloat() < 0) N = true;
				if (ByteBuffer.wrap(readData2).order(ByteOrder.BIG_ENDIAN).getFloat() == 0) Z = true;
			} else if (ctrl.regWrite && ctrl.memWrite && !ctrl.memRead && ctrl.reg2Loc) { // nop
				continue;
			} else { // ALU ops
				readData1 = regBlk.readReg1(readReg1);
				readData2 = (readData2 == null)? regBlk.readReg1(readReg2) : readData2;
				
				alu.arg1 = readData1;
				alu.arg2 = readData2;
				alu.process();
			}
			
			// print out used registers and all flags after instruction executes
			if (regCode != 0) System.out.println("R" + writeReg + ": " +
												formatFloatStr(bytes2String(regBlk.readReg1(writeReg))));
			if (regCode == 2 || regCode == 3) System.out.println("R" + readReg1 + ": " +
												formatFloatStr(bytes2String(regBlk.readReg1(readReg1))));
			if (regCode == 3 || regCode == 4) System.out.println("R" + readReg2 + ": " +
												formatFloatStr(bytes2String(regBlk.readReg1(readReg2))));
			System.out.println("ZNVUX\n" + isFlagged(Z) + isFlagged(N) + isFlagged(V) + isFlagged(U) + isFlagged(X)
								+ "\n-------------------------------");
			
			// reset for the next instruction
			Z = N = V = U = X = false;
			readData1 = readData2 = null;
			readReg1 = readReg2 = writeReg = -1;
			dataFlt = Float.NaN;
		}
	}
	
	/* 
	 * ************************************************************************
	 * ************************* UTILITY FUNCTIONS ****************************
	 * ************************************************************************
	 */
	
	/*
	 * This function converts a truncated float, supplied by an instruction's operand, into a standard IEEE-754 float.
	 */
	public float convertPartialToFloat(String partial) {
		float result;
		int sign;
		int exp;
		float tempRes = 0;
		String manStr;
		
		if (partial.substring(0, 1).equals("1")) sign = -1;
		else sign = 1;
		
		exp = Integer.parseInt(partial.substring(1, 9), 2) - BIAS;
		
		manStr = partial.substring(9, partial.length());
		tempRes += Math.pow(2, exp);
		for (int i = 0; i < manStr.length(); i++) {
			if (manStr.charAt(i) == '1') {
				tempRes += Math.pow(2, (exp - i - 1));
			}
		}
		
		result = (float) tempRes * sign;
		return result;
	}
	
	/*
	 * This function converts a byte array into a readable (and print-friendly) String of data.
	 */
	public String bytes2String(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		StringBuilder temp = null;
		int mask;
		
		for (int i : bytes) {
			temp = new StringBuilder();
			mask = 0x80;
			for (int j = 0; j < 8; j++) {
				if ((i & mask) == 0) temp.append("0");
				else temp.append("1");
				mask = mask >> 1;
			}
			sb.append(temp);
		}
		
		return sb.toString();
	}
	
	/*
	 * This function inserts spaces into a modified (as per the README) float, to better distinguish its components.
	 */
	public String formatFloatStr(String flt) {
		StringBuilder sb = new StringBuilder(flt);
		sb.insert(9, " ");
		sb.insert(1, " ");
		
		return sb.toString();
	}
	
	/*
	 * Converts a boolean flag into a "1" or "0" string for ease of printing to the console.
	 */
	public String isFlagged(boolean flag) {
		if (flag) return "1";
		else return "0";
	}
	
	/*
	 * Prints the "English" translation of the binary instruction
	 */
	public void printInst(String inst, String opcode, int Ri, int Rj, int Rk, float dataFlt) {
		StringBuilder sb = new StringBuilder(inst);
		String opcodeEnglish = null;
				
		System.out.println(inst);
		sb.insert(5, " ");
		switch (opcode) {
		case "00001":
			opcodeEnglish = "SET";
			sb.insert(10, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", #" + dataFlt);
			regCode = 1;
			break;
		case "00010":
			opcodeEnglish = "GET";
			sb.insert(10, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri);
			regCode = 1;
			break;
		case "00011":
			opcodeEnglish = "MOV";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "00100":
			opcodeEnglish = "ADD";
			sb.insert(10, " ");
			sb.insert(15, " ");
			sb.insert(20, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", R" + Rk);
			if ((Ri != Rj) && ((Ri == Rk) || (Rj == Rk))) regCode = 2;
			else if ((Ri != Rj) && (Ri != Rk) && (Rj != Rk)) regCode = 3;
			else if ((Ri != Rk) && ((Ri == Rj) || (Rj == Rk))) regCode = 4;
			break;
		case "00101":
			opcodeEnglish = "SUB";
			sb.insert(10, " ");
			sb.insert(15, " ");
			sb.insert(20, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", R" + Rk);
			if ((Ri != Rj) && ((Ri == Rk) || (Rj == Rk))) regCode = 2;
			else if ((Ri != Rj) && (Ri != Rk) && (Rj != Rk)) regCode = 3;
			else if ((Ri != Rk) && ((Ri == Rj) || (Rj == Rk))) regCode = 4;
			break;
		case "00110":
			opcodeEnglish = "NEG";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "00111":
			opcodeEnglish = "MULT";
			sb.insert(10, " ");
			sb.insert(15, " ");
			sb.insert(20, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", R" + Rk);
			if ((Ri != Rj) && ((Ri == Rk) || (Rj == Rk))) regCode = 2;
			else if ((Ri != Rj) && (Ri != Rk) && (Rj != Rk)) regCode = 3;
			else if ((Ri != Rk) && ((Ri == Rj) || (Rj == Rk))) regCode = 4;
			break;
		case "01000":
			opcodeEnglish = "DIV";
			sb.insert(10, " ");
			sb.insert(15, " ");
			sb.insert(20, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", R" + Rk);
			if ((Ri != Rj) && ((Ri == Rk) || (Rj == Rk))) regCode = 2;
			else if ((Ri != Rj) && (Ri != Rk) && (Rj != Rk)) regCode = 3;
			else if ((Ri != Rk) && ((Ri == Rj) || (Rj == Rk))) regCode = 4;
			break;
		case "01001":
			opcodeEnglish = "FLR";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "01010":
			opcodeEnglish = "CEIL";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "01011":
			opcodeEnglish = "RND";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "01100":
			opcodeEnglish = "ABS";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "01101":
			opcodeEnglish = "INV";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "01110":
			opcodeEnglish = "MIN";
			sb.insert(10, " ");
			sb.insert(15, " ");
			sb.insert(20, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", R" + Rk);
			if ((Ri != Rj) && ((Ri == Rk) || (Rj == Rk))) regCode = 2;
			else if ((Ri != Rj) && (Ri != Rk) && (Rj != Rk)) regCode = 3;
			else if ((Ri != Rk) && ((Ri == Rj) || (Rj == Rk))) regCode = 4;
			break;
		case "01111":
			opcodeEnglish = "MAX";
			sb.insert(10, " ");
			sb.insert(15, " ");
			sb.insert(20, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", R" + Rk);
			if ((Ri != Rj) && ((Ri == Rk) || (Rj == Rk))) regCode = 2;
			else if ((Ri != Rj) && (Ri != Rk) && (Rj != Rk)) regCode = 3;
			else if ((Ri != Rk) && ((Ri == Rj) || (Rj == Rk))) regCode = 4;
			break;
		case "10000":
			opcodeEnglish = "PWR";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", #" + dataFlt);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "10001":
			opcodeEnglish = "SIN";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "10010":
			opcodeEnglish = "COS";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "10011":
			opcodeEnglish = "TAN";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "10100":
			opcodeEnglish = "EXP";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "10101":
			opcodeEnglish = "LOG";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "10110":
			opcodeEnglish = "SQRT";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "10111":
			opcodeEnglish = "NOP";
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish);
			regCode = 0;
			break;
		case "11000":
			opcodeEnglish = "ADDI";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", #" + dataFlt);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "11001":
			opcodeEnglish = "SUBI";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", #" + dataFlt);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "11010":
			opcodeEnglish = "MULTI";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", #" + dataFlt);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		case "11011":
			opcodeEnglish = "DIVI";
			sb.insert(10, " ");
			sb.insert(15, " ");
			System.out.println(sb.toString());
			System.out.println(opcodeEnglish + " R" + Ri + ", R" + Rj + ", #" + dataFlt);
			regCode = (Ri == Rj) ? 1 : 2;
			break;
		}
	}
}
