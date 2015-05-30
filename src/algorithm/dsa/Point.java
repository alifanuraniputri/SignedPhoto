package algorithm.dsa;

import java.math.BigInteger;

public class Point implements java.io.Serializable {
	public BigInteger x,y;
	public boolean isInfinite;
	
	public Point() {
		x = new BigInteger("0");
		y = new BigInteger("0");
		isInfinite = false;
	}
	
	public Point(BigInteger x, BigInteger y){
		this.x = x;
		this.y = y;
	}
	
	public String toString(){
		return x + " " + y;
	}
}
