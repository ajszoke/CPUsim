import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ALU {
	
	byte[] arg1 = null;
	byte[] arg2 = null;
	byte[] res = null;
	String aluOp = null;
	Processor p;
	
	public ALU(Processor _p) {
		this.p = _p;
	}
	
	public void process() {
		switch (aluOp) {
		case "00100":
			res = add(arg1, arg2);
			break;
		case "00101":
			res = subtract(arg1, arg2);
			break;
		case "00110":
			res = negate(arg1);
			break;
		case "00111":
			res = multiply(arg1, arg2);
			break;
		case "01000":
			res = divide(arg1, arg2);
			break;
		case "01001":
			res = floor(arg1);
			break;
		case "01010":
			res = ceiling(arg1);
			break;
		case "01011":
			res = round(arg1);
			break;
		case "01100":
			res = absoluteValue(arg1);
			break;
		case "01101":
			res = inverse(arg1);
			break;
		case "01110":
			res = minimum(arg1, arg2);
			break;
		case "01111":
			res = maximum(arg1, arg2);
			break;
		case "10000":
			res = power(arg1, arg2);
			break;
		case "10001":
			res = sine(arg1);
			break;
		case "10010":
			res = cosine(arg1);
			break;
		case "10011":
			res = tangent(arg1);
			break;
		case "10100":
			res = exponent(arg1);
			break;
		case "10101":
			res = logarithm(arg1);
			break;
		case "10110":
			res = squareRoot(arg1);
			break;
		case "11000":
			res = addI(arg1, arg2);
			break;
		case "11001":
			res = subtractI(arg1, arg2);
			break;
		case "11010":
			res = multiplyI(arg1, arg2);
			break;
		case "11011":
			res = divideI(arg1, arg2);
			break;
		}
		
		if(res.equals(Processor.NAN) || res.equals(Processor.POS_INFINITY) || res.equals(Processor.NEG_INFINITY)) {
			p.X = true;
		}
		p.memBlk.data = res;
		p.memBlk.writeData();
	}
	
	public byte[] add(byte[] Rj, byte[] Rk) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		float b = ByteBuffer.wrap(Rk).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(a + b).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(Rk, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if ((Math.abs(a) != Math.abs(b)) && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] subtract(byte[] Rj, byte[] Rk) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		float b = ByteBuffer.wrap(Rk).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(a - b).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(Rk, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if ((Math.abs(a) != Math.abs(b)) && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] negate(byte[] Rj) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(-a).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] multiply(byte[] Rj, byte[] Rk) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		float b = ByteBuffer.wrap(Rk).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(a * b).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(Rk, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && b != 0 && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] divide(byte[] Rj, byte[] Rk) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		float b = ByteBuffer.wrap(Rk).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		if (b == 0) {
			p.X = true;
			return Processor.NAN;
		}
		
		result = ByteBuffer.allocate(4).putFloat(a / b).array();
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(Rk, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && fltRes == 0) p.U = true;
		
		return result;
	}

	public byte[] floor(byte[] Rj) {
		byte[] result = new byte[4];
		double tempDbl;
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempDbl = Math.floor(a);
		tempFlt = (float) tempDbl;
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] ceiling(byte[] Rj) {
		byte[] result = new byte[4];
		double tempDbl;
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempDbl = Math.ceil(a);
		tempFlt = (float) tempDbl;
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] round(byte[] Rj) {
		byte[] result = new byte[4];
		int tempInt;
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempInt = Math.round(a);
		tempFlt = (float) tempInt;
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] absoluteValue(byte[] Rj) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(Math.abs(a)).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] inverse(byte[] Rj) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (a == 0 || Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		result = ByteBuffer.allocate(4).putFloat(1 / a).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		
		return result;
	}
	
	public byte[] minimum(byte[] Rj, byte[] Rk) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		float b = ByteBuffer.wrap(Rk).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(Math.min(a, b)).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(Rk, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] maximum(byte[] Rj, byte[] Rk) {
		byte[] result = new byte[4];
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		float b = ByteBuffer.wrap(Rk).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(Math.max(a, b)).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(Rk, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] power(byte[] Rj, byte[] pow) {
		byte[] result = new byte[4];
		float expFlt = ByteBuffer.wrap(pow).order(ByteOrder.BIG_ENDIAN).getFloat();
		double b = (double) expFlt;
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempFlt = (float) Math.pow(a, b);
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] sine(byte[] Rj) {
		byte[] result = new byte[4];
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempFlt = (float) Math.sin(a);
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] cosine(byte[] Rj) {
		byte[] result = new byte[4];
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempFlt = (float) Math.cos(a);
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] tangent(byte[] Rj) {
		byte[] result = new byte[4];
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempFlt = (float) Math.tan(a);
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] exponent(byte[] Rj) {
		byte[] result = new byte[4];
		float tempFlt;
		double tempDbl;
		
		tempFlt = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempDbl = (double) tempFlt;
		tempFlt = (float) Math.exp(tempDbl);
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] logarithm(byte[] Rj) {
		byte[] result = new byte[4];
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempFlt = (float) Math.log(a);
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		
		return result;
	}
	
	public byte[] squareRoot(byte[] Rj) {
		byte[] result = new byte[4];
		float tempFlt;
		
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		tempFlt = (float) Math.sqrt(a);
		result = ByteBuffer.allocate(4).putFloat(tempFlt).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] addI(byte[] Rj, byte[] imm) {
		byte[] result = new byte[4];
		float i = ByteBuffer.wrap(imm).order(ByteOrder.BIG_ENDIAN).getFloat();	
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(a + i).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(imm, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (Math.abs(a) != Math.abs(i) && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] subtractI(byte[] Rj, byte[] imm) {
		byte[] result = new byte[4];
		float i = ByteBuffer.wrap(imm).order(ByteOrder.BIG_ENDIAN).getFloat();	
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(a - i).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(imm, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (Math.abs(a) != Math.abs(i) && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] divideI(byte[] Rj, byte[] imm) {
		byte[] result = new byte[4];
		float i = ByteBuffer.wrap(imm).order(ByteOrder.BIG_ENDIAN).getFloat();	
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		
		if (i == 0 || Rj.equals(Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		result = ByteBuffer.allocate(4).putFloat(a / i).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && fltRes == 0) p.U = true;
		
		return result;
	}
	
	public byte[] multiplyI(byte[] Rj, byte[] imm) {
		byte[] result = new byte[4];
		float i = ByteBuffer.wrap(imm).order(ByteOrder.BIG_ENDIAN).getFloat();	
		float a = ByteBuffer.wrap(Rj).order(ByteOrder.BIG_ENDIAN).getFloat();
		result = ByteBuffer.allocate(4).putFloat(a * i).array();
		
		float fltRes = ByteBuffer.wrap(result).order(ByteOrder.BIG_ENDIAN).getFloat();
		if (Arrays.equals(Rj, Processor.NAN) || Arrays.equals(imm, Processor.NAN)) {
			p.X = true;
			return Processor.NAN;
		}
		if (fltRes < 0) p.N = true;
		if (fltRes == 0) p.Z = true;
		if (fltRes == Float.POSITIVE_INFINITY || fltRes == Float.NEGATIVE_INFINITY) p.V = true;
		if (a != 0 && i != 0 && fltRes == 0) p.U = true;
		
		return result;
	}
}
