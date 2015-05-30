package algorithm.dsa;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class SHA1 {

	private String message;
	private byte[] byteMsg;
	private byte[] bytePadded;
	private String hexString;
	private int[] hblocks;
	private int[] word;
	private final int[] H = { 0x67452301, 0xEFCDAB89, 0x98BADCFE, 0x10325476,
			0xC3D2E1F0 };
	private final int[] K = { 0x5A827999, 0x6ED9EBA1, 0x8F1BBCDC, 0xCA62C1D6 };

	public SHA1() {

	}

	public SHA1(String message) {
		super();
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public byte[] getByteMsg() {
		return byteMsg;
	}

	public void setByteMsg(byte[] byteMsg) {
		this.byteMsg = byteMsg;
	}

	public String getHexString() {
		return hexString;
	}

	public void setHexString(String hexString) {
		this.hexString = hexString;
	}

	public int[] getHblocks() {
		return hblocks;
	}

	public void setHblocks(int[] hblocks) {
		this.hblocks = hblocks;
	}

	/**
	 * Padding : add single 1 followed by 0s until 448 bit messages, then add 64
	 * bit of messages size
	 */
	public void padding() {

		byteMsg = new byte[message.length()];
		for (int i = 0; i < message.length(); i++) {
			byteMsg[i] = (byte) ((int) message.charAt(i));
		}
		int sizeMsg = message.length() * 8;
		if (sizeMsg % 512 != 448) {
			byteMsg = Arrays.copyOf(byteMsg,
					byteMsg.length + (56 - (message.length() % 56)));
			byteMsg[message.length()] = (byte) 128;
		}

		long size = Long.valueOf(sizeMsg);
		byte[] sizeb = ByteBuffer.allocate(8).order(ByteOrder.BIG_ENDIAN)
				.putLong(size).array();
		bytePadded = new byte[byteMsg.length + sizeb.length];
		System.arraycopy(byteMsg, 0, bytePadded, 0, byteMsg.length);
		System.arraycopy(sizeb, 0, bytePadded, byteMsg.length, sizeb.length);

	}

	public void initializeHashBlocks() {

		hblocks = new int[5];
		System.arraycopy(H, 0, hblocks, 0, H.length);
	}

	public static final byte[] intToByteArray(int value) {
		return new byte[] { (byte) (value >>> 24), (byte) (value >>> 16),
				(byte) (value >>> 8), (byte) value };
	}

	public int SHAFunction(int t, int a, int b, int c, int d, int e) {
		int result = 0;
		if (t < 20) {
			// f1 = f(t, B, C, D): (B AND C) OR (B` AND D),
			result = (b & c) | ((~b) & d);
		} else if (t >= 20 && t < 40) {
			// f2 = f(t, B, C, D): B + C + D
			result = b ^ c ^ d;
		} else if (t >= 40 && t < 60) {
			// f3 = f(t, B, C, D): (B AND C) OR (B AND D) OR (C AND D)
			result = (b & c) | (b & d) | (c & d);
		} else if (t >= 60) {
			// f4 = f(t, B, C, D): B + C + D
			result = b ^ c ^ d;
		}
		return result;
	}

	public int KFunction(int t) {
		int result = 0;
		if (t < 20) {
			result = K[0];
		} else if (t >= 20 && t < 40) {
			result = K[1];
		} else if (t >= 40 && t < 60) {
			result = K[2];
		} else if (t >= 60) {
			result = K[3];
		}
		return result;
	}

	/**
	 * Prepare words : 80 element array. element 1-16 : 512 bit msg split into
	 * 16 element 17-80 : word[i] = word[i - 16] ^ word[i - 14] ^ word[i - 8] ^
	 * word[i - 3];
	 * */
	public void prepareW() {
		word = new int[80];
		for (int i = 0; i < 16; i++) {
			word[i] = bytePadded[4 * i] << 24
					| (bytePadded[4 * i + 1] & 0xFF) << 16
					| (bytePadded[4 * i + 2] & 0xFF) << 8
					| (bytePadded[4 * i + 3] & 0xFF);
		}
		for (int i = 16; i < 80; i++) {
			word[i] = word[i - 16] ^ word[i - 14] ^ word[i - 8] ^ word[i - 3];
			word[i] = Integer.rotateLeft(word[i], 1);
		}

	}

	/**
	 * 80 round hashing
	 * */
	public String hash() {
		padding();
		prepareW();
		initializeHashBlocks();

		for (int t = 0; t < 80; t++) {

			int f = SHAFunction(t, hblocks[0], hblocks[1], hblocks[2],
					hblocks[3], hblocks[4]);
			int k = KFunction(t);

			int tmp = Integer.rotateLeft(hblocks[0], 5) + f + hblocks[4] + k
					+ word[t];

			hblocks[4] = hblocks[3];
			hblocks[3] = hblocks[2];
			hblocks[2] = Integer.rotateLeft(hblocks[1], 30);
			hblocks[1] = hblocks[0];
			hblocks[0] = tmp;

		}

		hblocks[0] = hblocks[0] + H[0];
		hblocks[1] = hblocks[1] + H[1];
		hblocks[2] = hblocks[2] + H[2];
		hblocks[3] = hblocks[3] + H[3];
		hblocks[4] = hblocks[4] + H[4];

		hexString = "";
		for (int i = 0; i < hblocks.length; i++) {
			hexString = hexString + Integer.toHexString(hblocks[i]);
		}

		System.out.println("hash : "+ hexString);
		return hexString;

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SHA1 sha1 = new SHA1("alifa nurani putri");
		sha1.hash();
	}

}
