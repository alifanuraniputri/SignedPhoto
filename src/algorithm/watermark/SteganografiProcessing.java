package algorithm.watermark;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.Arrays;

import javax.imageio.ImageIO;

import algorithm.dsa.ECDSA;
import algorithm.dsa.Point;


public class SteganografiProcessing {

	private BufferedImage citra;
	private BufferedImage steganoCitra;
	private String digitalSignature;
	private String namaFile;
	BigInteger _p;
	BigInteger _a;
	BigInteger _b;
	BigInteger _xG;
	BigInteger _yG;
	BigInteger _n;
	BigInteger privateKey;
	Point publicKey;
	
	int start;
	String content;

	
	public SteganografiProcessing(BufferedImage chosen, String namaFile, BigInteger privateK, 
			BigInteger a, BigInteger b, BigInteger p, Point _G, BigInteger n) {  
		super();
		this.citra = chosen;
		_p = p;
		_a = a;
		_b = b;
		_xG = _G.x;
		_yG = _G.y;
		_n = n;
		privateKey = privateK;
		this.namaFile = namaFile;
	}
	
	public SteganografiProcessing(BufferedImage chosen, Point pubKey, 
			BigInteger a, BigInteger b, BigInteger p, Point _G, BigInteger n) {  
		super();
		this.steganoCitra = chosen;
		_p = p;
		_a = a;
		_b = b;
		_xG = _G.x;
		_yG = _G.y;
		_n = n;
		publicKey = pubKey;
	}

	public BufferedImage sisipkanLSBstandard() {
		String PesanMax = namaFile + '@' + _n.toString(16) + '@' + _n.toString(16) + '@';
		byte[] bytePesanMax=null;
		try{
			bytePesanMax = PesanMax.getBytes("UTF-8");
		} catch (Exception e) {
			
		}
		int nBit = bytePesanMax.length * 8;
		int space = (int) Math.ceil((double) nBit/3);
		
		int max = citra.getHeight() * citra.getWidth();
		
		System.out.println("debug1 max: "+max);
		BufferedImage contentImage = new BufferedImage(citra.getWidth(), citra.getHeight(), citra.getType());
		
		start = 11+space;
		for (int i=start; i<max; i++) {
				int xi = i % citra.getWidth();
				int yi = i / citra.getWidth();
				int pixel = citra.getRGB(xi, yi);
				contentImage.setRGB(xi, yi, pixel);
			//System.out.println(i);
		}
		File outputfile = new File("image.png");
		try {
			ImageIO.write(contentImage, "png", outputfile);
			
			BufferedReader br = new BufferedReader(new FileReader(outputfile));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
	
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			content = sb.toString();
			br.close();
		} catch (Exception e) {
			
		}
		//System.out.println(content);
		System.out.println("debug content "+content.length());
		
		ECDSA ecdsa = new ECDSA();
		ecdsa.signatureGeneration(privateKey, content, _a, _b, _p, new Point(_xG,_yG), _n);
				
		String r =ecdsa.getR().toString(16);
		String s = ecdsa.getS().toString(16);
		digitalSignature = r + '@' +s;
		try {
			if (citra.getType()!=BufferedImage.TYPE_BYTE_GRAY)
				steganoCitra = new BufferedImage(citra.getWidth(), citra.getHeight(),
					citra.getType());
			else 
				steganoCitra = new BufferedImage(citra.getWidth(), citra.getHeight(),
						BufferedImage.TYPE_INT_RGB);
			digitalSignature = namaFile+'@'+digitalSignature+'@';
			System.out.println("DS " + digitalSignature);
			byte[] bytePesan = digitalSignature.getBytes("UTF-8");
			sisipkanPanjangPesan(bytePesan.length);
			sisipkanPesanLSBStandard(bytePesan);
		} catch (Exception e) {}
		return steganoCitra;
	}
	
	public String getPlainTextLSBstandard() {
		int panjang = getPanjangPesan();
		getPlainLSB(panjang);
		System.out.println(digitalSignature);
		
		 String[] s2 = digitalSignature.split("@");
	      for(String results : s2) {
	          System.out.println(results);
	      }
	      
	      
	      ECDSA ecdsa = new ECDSA();
	     
		boolean valid = ecdsa.verifySignature(publicKey, content, _a, _b, _p, new Point(_xG,_yG), _n, new BigInteger(s2[1],16), new BigInteger(s2[2],16));
	      if (valid) {
	    	  System.out.println("valid");
	      } else {
	    	  System.out.println("invalid");
	      }
	      
		
		return digitalSignature;
	}

	public void getPlainLSB(int panjang) {
		String bit = "";
		int nBit = panjang * 8;
		//Random rand = new Random(this.seed);
		int max = steganoCitra.getHeight() * steganoCitra.getWidth();
		int min = 11;
		
		
		// ekstrak LSB
		System.out.println("panjang: "+nBit);
		int numBit = 0;
		for (int i = 11; i < max && numBit < nBit; i++) {
			int x = i % steganoCitra.getWidth();
			int y = i / steganoCitra.getWidth();
			int pixel = steganoCitra.getRGB(x, y);
			int red = (pixel >> 16) & 0x000000FF, green = (pixel >> 8) & 0x000000FF, blue = (pixel) & 0x000000FF;
			int bitPesan;
			// red
			if (numBit < nBit) {
				bitPesan = getBitValue(red, 0);
				bit = bit + bitPesan;
				numBit++;
			}
			// green
			if (numBit < nBit) {
				bitPesan = getBitValue(green, 0);
				bit = bit + bitPesan;
				numBit++;
			}
			// blue
			if (numBit < nBit) {
				bitPesan = getBitValue(blue, 0);
				bit = bit + bitPesan;
				numBit++;
			}
		}
		System.out.println(bit);
		byte[] bytes = new BigInteger(bit, 2).toByteArray();
		try {
			digitalSignature = new String(bytes,  "UTF-8");
			System.out.println( "debug1" + Arrays.toString(bytes));
			System.out.println( "debug2" +digitalSignature);
		} catch (Exception e) {

		}
		
		int space = (int) Math.ceil((double) nBit/3);
		BufferedImage contentImage = new BufferedImage(steganoCitra.getWidth(), steganoCitra.getHeight(), steganoCitra.getType());
		
		start = 11+space;
		for (int i=start; i<max; i++) {
				int xi = i % steganoCitra.getWidth();
				int yi = i / steganoCitra.getWidth();
				int pixel = steganoCitra.getRGB(xi, yi);
				contentImage.setRGB(xi, yi, pixel);
			//System.out.println(i);
		}
		
		File outputfile = new File("image.png");
		try {
			ImageIO.write(contentImage, "png", outputfile);
			
			BufferedReader br = new BufferedReader(new FileReader(outputfile));
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
	
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}
			content = sb.toString();
			br.close();
		} catch (Exception e) {
		}
		
	}
	
	public String getNamaFile() {
		return namaFile;
	}

	public int getPanjangPesan() {
		int panjang = 0;
		int count = 0;
		for (int i = 0; i < 11 && count < 32; i++) {
			int x = i % steganoCitra.getWidth();
			int y = i / steganoCitra.getWidth();
			int pixel = steganoCitra.getRGB(x, y);
			int red = (pixel >> 16) & 0x000000FF, green = (pixel >> 8) & 0x000000FF, blue = (pixel) & 0x000000FF;

			// red
			int bitPanjangPesan = getBitValue(red, 0);
			panjang = setBitValue(panjang, count, bitPanjangPesan);
			count++;

			// green
			if (count < 32) {
				bitPanjangPesan = getBitValue(green, 0);
				panjang = setBitValue(panjang, count, bitPanjangPesan);
				count++;
			}
			// blue
			if (count < 32) {
				bitPanjangPesan = getBitValue(blue, 0);
				panjang = setBitValue(panjang, count, bitPanjangPesan);
				count++;
			}

		}
		return panjang;
	}

	public void sisipkanPesanLSBStandard(byte[] bytePesan) {
		int nBit = bytePesan.length * 8;
		String bit = toBinary(bytePesan);

		// edit LSB
		int numBit = 0;
		int max = citra.getHeight() * citra.getWidth();
		for (int i=11; i<max; i++) {
			int x = i % citra.getWidth();
			int y = i / citra.getWidth();
			int pixel = citra.getRGB(x, y);
			int bitPanjangPesan;
			int alpha = (pixel >> 24) & 0x000000FF;
			int red = (pixel >> 16) & 0x000000FF, green = (pixel >> 8) & 0x000000FF, blue = (pixel) & 0x000000FF;
			if (numBit < nBit) {
				// red
				bitPanjangPesan = Character.getNumericValue(bit.charAt(numBit));
				red = setBitValue(red, 0, bitPanjangPesan);
				numBit++;
			}
			// green
			if (numBit < nBit) {
				bitPanjangPesan = Character.getNumericValue(bit.charAt(numBit));
				green = setBitValue(green, 0, bitPanjangPesan);
				numBit++;
			}
			// blue
			if (numBit < nBit) {
				bitPanjangPesan = Character.getNumericValue(bit.charAt(numBit));
				blue = setBitValue(blue, 0, bitPanjangPesan);
				numBit++;
			}
			// new pixel
			int newPixel = (((int) alpha & 0xFF) << 24) | // alpha
					(((int) red & 0xFF) << 16) | // red
					(((int) green & 0xFF) << 8) | // green
					(((int) blue & 0xFF) << 0); // blue

			steganoCitra.setRGB(x, y, newPixel);
		}

		
	}
	
	public void sisipkanPanjangPesan(int panjang) {
		// panjang |= (1 << 31);
		int widthX = citra.getWidth(), heightY = citra.getHeight();
		int startX = 0, startY = 0;
		int count = 0;
		for (int i = startY; i < heightY && count < 32; i++) {
			for (int j = startX; j < widthX && count < 32; j++) {
				int pixel = citra.getRGB(j, i);
				int bitPanjangPesan = getBitValue(panjang, count);
				int alpha = (pixel >> 24) & 0x000000FF;
				int red = (pixel >> 16) & 0x000000FF, green = (pixel >> 8) & 0x000000FF, blue = (pixel) & 0x000000FF;
				// red
				red = setBitValue(red, 0, bitPanjangPesan);
				count++;

				// green
				if (count < 32) {
					bitPanjangPesan = getBitValue(panjang, count);
					green = setBitValue(green, 0, bitPanjangPesan);
					count++;
				}
				// blue
				if (count < 32) {
					bitPanjangPesan = getBitValue(panjang, count);
					blue = setBitValue(blue, 0, bitPanjangPesan);
					count++;
				}
				// new pixel
				int newPixel = (((int) alpha & 0xFF) << 24) | // alpha
						(((int) red & 0xFF) << 16) | // red
						(((int) green & 0xFF) << 8) | // green
						(((int) blue & 0xFF) << 0); // blue

				steganoCitra.setRGB(j, i, newPixel);
			}
		}

	}
	

	private int getBitValue(int n, int location) {
		int v = n & (int) Math.round(Math.pow(2, location));
		return v == 0 ? 0 : 1;
	}

	private int setBitValue(int n, int location, int bit) {
		int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
		if (bv == bit)
			return n;
		if (bv == 0 && bit == 1)
			n |= toggle;
		else if (bv == 1 && bit == 0)
			n ^= toggle;
		return n;
	}

	String toBinary(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);
		for (int i = 0; i < Byte.SIZE * bytes.length; i++)
			sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0'
					: '1');
		return sb.toString();
	}
}