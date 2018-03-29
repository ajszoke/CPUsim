public class MemoryBlock {
	
	int addr = -1;
	byte[] data = null;
	Processor p = null;
	
	public MemoryBlock(Processor _p) {
		this.p = _p;
	}
	
	public void writeData() {
		if (p.ctrl.regWrite) {
			p.regBlk.writeReg(this.addr, this.data);
		}
	}
}
