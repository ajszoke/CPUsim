public class Control {
	
	boolean regWrite;
	boolean memWrite;
	boolean memRead;
	boolean memToReg;
	boolean reg2Loc;
	Processor p;
	
	public Control(Processor _p) {
		regWrite = memWrite = memRead = memToReg = reg2Loc = false;
		this.p = _p;
	}

	public void process(String opcode) {
		p.alu.aluOp = opcode;
		switch (opcode) {
			case "00001": // SET
				regWrite = true;
				memWrite = true;
				memRead = false;
				reg2Loc = false;
				memToReg = true;
				break;
			case "00010": // GET
				regWrite = false;
				memWrite = true;
				memRead = false;
				reg2Loc = true;
				memToReg = true;
				break;
			case "00011": // MOVE
				regWrite = true;
				memWrite = true;
				memRead = false;
				reg2Loc = false;
				memToReg = true;
				break;
			case "00100":
			case "00101":
			case "00110":
			case "00111":
			case "01000":
			case "01001":
			case "01010":
			case "01011":
			case "01100":
			case "01101":
			case "01110":
			case "01111":
			case "10000":
			case "10001":
			case "10010":
			case "10011":
			case "10100":
			case "10101":
			case "10110":
			case "11000":
			case "11001":
			case "11010":
			case "11011":
				regWrite = true;
				memWrite = false;
				memRead = true;
				reg2Loc = false;
				memToReg = false;
				break;
			case "10111": // NOP
				regWrite = true;
				memWrite = true;
				memRead = false;
				reg2Loc = true;
				memToReg = false;
				break;
		}
	}
}
