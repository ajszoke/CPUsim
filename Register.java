public class Register {
	
	byte[] data = new byte[4];
	
	public Register() {
		this.data = Processor.NAN;
	}
	
	public Register(byte[] _data) {
		this.data = _data;
	}
	
	public byte[] get() {
		return this.data;
	}
	
	public void set(byte[] _data) {
		this.data = _data;
	}
}
