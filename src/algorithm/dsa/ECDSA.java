package algorithm.dsa;

import java.math.BigInteger;
import java.util.Random;



public class ECDSA {

	private String signature;
	private String message;
	private Point G; // generator point
	private BigInteger n;
	private BigInteger e; // e=H(m), H is hash function: SHA1
	private BigInteger dA; // private key dA
	private Point QA; // public key QA = dA * G
	private BigInteger r; // r = x1 (mod n), where (x1, y1) = k * G. r !=0
	private BigInteger s; // s = k-1 (e + dAr)(mod n), s != 0
	private BigInteger w;
	private BigInteger u1;
	private BigInteger u2;
	private BigInteger k;

	public ECDSA() {

	}

	public BigInteger getN() {
		return n;
	}

	public BigInteger getE() {
		return e;
	}

	public void setE(BigInteger e) {
		this.e = e;
	}

	public BigInteger getW() {
		return w;
	}

	public void setW(BigInteger w) {
		this.w = w;
	}

	public BigInteger getU1() {
		return u1;
	}

	public void setU1(BigInteger u1) {
		this.u1 = u1;
	}

	public BigInteger getU2() {
		return u2;
	}

	public void setU2(BigInteger u2) {
		this.u2 = u2;
	}

	public BigInteger getK() {
		return k;
	}

	public void setK(BigInteger k) {
		this.k = k;
	}

	public void setN(BigInteger n) {
		this.n = n;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Point getG() {
		return G;
	}

	public void setG(Point g) {
		G = g;
	}

	public BigInteger getdA() {
		return dA;
	}

	public void setdA(BigInteger dA) {
		this.dA = dA;
	}

	public Point getQA() {
		return QA;
	}

	public void setQA(Point qA) {
		QA = qA;
	}

	public BigInteger getR() {
		return r;
	}

	public void setR(BigInteger r) {
		this.r = r;
	}

	public BigInteger getS() {
		return s;
	}

	public void setS(BigInteger s) {
		this.s = s;
	}

	public void cal_e() {
		SHA1 sha1 = new SHA1(message);
		String hashed = sha1.hash();
		e = new BigInteger(hashed, 16);
	}

	public static BigInteger generatePrivateKeyECDSA(BigInteger n) {
		return new BigInteger(256, new Random())
				.mod(n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
	}

	public static Point generatePublicKeyECDSA(BigInteger dA, BigInteger _a, BigInteger _b, BigInteger _p, Point _G) {

		
		ECC.setParam(_a, _b,_p,_G);
		return ECC.times(dA, _G);
	}

	public void signatureGeneration(BigInteger privateK, String _message,
			BigInteger a, BigInteger b, BigInteger p, Point _G, BigInteger _n) {
		
		G = _G;
		ECC.setParam(a, b, p, G);
		dA = privateK;
		message = _message;
	
		cal_e();
		n = _n;
		do {
			k = new BigInteger(256, new Random()).mod(
					n.subtract(BigInteger.ONE)).add(BigInteger.ONE);
			Point kP = ECC.times(k, G);
			r = kP.x.mod(n);
			s = k.modInverse(n).multiply(e.add(dA.multiply(r))).mod(n);
			if (s.compareTo(BigInteger.ZERO) == 0)
				System.out.println("S 0");
		} while (r.compareTo(BigInteger.ZERO) == 0
				|| s.compareTo(BigInteger.ZERO) == 0);

	}

	public boolean verifySignature(Point publicK,  String _message,
			BigInteger a, BigInteger b, BigInteger p, Point _G, BigInteger _n, BigInteger r, BigInteger s) {
		boolean val = false;

		ECC.setParam(a, b, p, _G);
		QA = publicK;
		message=_message;

		if (r.compareTo(_n) != -1
				|| r.compareTo(BigInteger.ZERO) != 1) {
			System.out.println("false r");
			return val;
		}

		if (s.compareTo(_n) != -1
				|| s.compareTo(BigInteger.ZERO) != 1) {
			System.out.println("false s");
			return val;
		}

		cal_e();
		w = s.modInverse(_n);
		u1 = e.multiply(w).mod(_n);
		u2 = r.multiply(w).mod(_n);
		Point P = ECC.add(ECC.times(u1, _G), ECC.times(u2, QA));
		if (P.x.equals(r.mod(_n)))
			val = true;
		
		//System.out.println("test"+_message+"test");
		return val;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		/*
		    // NIST P-192
			BigInteger _p = new BigInteger ("fffffffffffffffffffffffffffffffeffffffffffffffff",16);
			BigInteger _a = new BigInteger ("fffffffffffffffffffffffffffffffefffffffffffffffc",16);
			BigInteger _b = new BigInteger ("64210519e59c80e70fa7e9ab72243049feb8deecc146b9b1",16);
			BigInteger _xG = new BigInteger ("188da80eb03090f67cbf20eb43a18800f4ff0afd82ff1012",16);
			BigInteger _yG = new BigInteger ("07192b95ffc8da78631011ed6b24cdd573f977a11e794811",16);
			BigInteger _n = new BigInteger  ("ffffffffffffffffffffffff99def836146bc9b1b4d22831",16);
			
			// NIST P-256
			BigInteger _p = new BigInteger ("ffffffff00000001000000000000000000000000ffffffffffffffffffffffff",16);
			BigInteger _a = new BigInteger ("ffffffff00000001000000000000000000000000fffffffffffffffffffffffc",16);
			BigInteger _b = new BigInteger ("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b",16);
			BigInteger _xG = new BigInteger ("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16);
			BigInteger _yG = new BigInteger ("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5",16);
			BigInteger _n = new BigInteger ("ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551",16);
		
		BigInteger _p = new BigInteger ("23");
		BigInteger _a = new BigInteger ("1");
		BigInteger _b = new BigInteger ("1");
		BigInteger _xG = new BigInteger ("1");
		BigInteger _yG = new BigInteger ("7");
		BigInteger _n = new BigInteger ("28");
		
		BigInteger _p = new BigInteger ("11");
		BigInteger _a = new BigInteger ("1");
		BigInteger _b = new BigInteger ("6");
		BigInteger _xG = new BigInteger ("2");
		BigInteger _yG = new BigInteger ("7");
		BigInteger _n = new BigInteger ("13");
		
		*/
		
		BigInteger _p = new BigInteger ("ffffffff00000001000000000000000000000000ffffffffffffffffffffffff",16);
		BigInteger _a = new BigInteger ("ffffffff00000001000000000000000000000000fffffffffffffffffffffffc",16);
		BigInteger _b = new BigInteger ("5ac635d8aa3a93e7b3ebbd55769886bc651d06b0cc53b0f63bce3c3e27d2604b",16);
		BigInteger _xG = new BigInteger ("6b17d1f2e12c4247f8bce6e563a440f277037d812deb33a0f4a13945d898c296",16);
		BigInteger _yG = new BigInteger ("4fe342e2fe1a7f9b8ee7eb4a7c0f9e162bce33576b315ececbb6406837bf51f5",16);
		BigInteger _n = new BigInteger ("ffffffff00000000ffffffffffffffffbce6faada7179e84f3b9cac2fc632551",16);
		
		BigInteger pri = new BigInteger("100600789377147023461072082702934359437205647979148520028102921837690663199091");
		Point pub = new Point(new BigInteger("19212080424464390540063596887575911368054913997721170672481358068768598080413"), new BigInteger("12323678239184652402150218964501452920351633338132841133745710599253251301001"));
		
		// System.out.println("pri: " + pri);
		// System.out.println("pub: " + pub);
		
		ECDSA ecdsa = new ECDSA();
		
		//ecdsa.generatePublicKeyECDSA(dA, _a, _b, _p, _G);
		//ecdsa.generatePrivateKeyECDSA(n);
		
		ecdsa.signatureGeneration(pri, "Alifa Nurani Putri syulululululu", _a, _b, _p, new Point(_xG,_yG), _n);
		
		System.out.println("r: " + ecdsa.getR().toString());
		System.out.println("s: " +ecdsa.getS().toString());
		
		boolean valid = ecdsa.verifySignature(pub, "Alifa Nurani Putri syulululululu",  _a, _b, _p, new Point(_xG,_yG), _n , ecdsa.getR(), ecdsa.getS());
		
		if (valid)
			System.out.println("benar");
		else 
			System.out.println("salah");

	}

}
