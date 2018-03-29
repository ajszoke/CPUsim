public class RegisterBlock {
	
	private Register[] registers;
	
	public RegisterBlock() {
		registers = new Register[Processor.NUM_REGISTERS];
		for (int i = 0; i < Processor.NUM_REGISTERS; i++) registers[i] = new Register();
	}
	
	//	OUTPUT: READ DATA 1
	public byte[] readReg1(int regNum) {
		return registers[regNum].get();
	}
	
	//	OUTPUT: READ DATA 2
	public byte[] readReg2(int regNum) {
		return registers[regNum].get();
	}
	
	public void writeReg(int regNum, byte[] data) {
		registers[regNum].set(java.util.Arrays.copyOf(data, data.length));
	}
	
	public Register getReg(int regNum) {
		return registers[regNum];
	}
}