package algorithm.watermark;
public class VigenereExtended {

	public static void main(String args[]){
		//System.out.println(CharToASCII('c'));
		//System.out.println(ASCIIToChar(91));
		String enc = Enkrip("abc","Ini hanyalah sebuah percobaan.");
		System.out.println(enc);
		String dec = Dekrip("abc",enc);
		System.out.println(dec);	
		System.out.println("===================================");
		String key = genAutoKey("abc","Ini hanyalah sebuah percobaan.");
		
		enc = Enkrip(key,"Ini hanyalah sebuah percobaan.");
		System.out.println(enc);
		dec = Dekrip(key,enc);
		System.out.println(dec);
	}

	/*
	 * Convert the characters to ASCII value
	 * @param character character
	 * @return ASCII value
	 * char c = yourString.charAt(0);
	 */

	private static int CharToASCII(final char character){
		return (int)character;
	}
 
	/**
	 * Convert the ASCII value to character
	 * @param ascii ascii value
	 * @return character value
	 */

	private static char ASCIIToChar(final int ascii){
		return (char)ascii;		
	}

	public static String genAutoKey(String kunci, String plaintext){
		int panjangPlain = plaintext.length();
		int panjangKunci = kunci.length();

		if (panjangKunci >= panjangPlain) return kunci;
		else {
			int cpy = panjangPlain - panjangKunci;
			String subplain = plaintext.substring(0,cpy);
			kunci = kunci + subplain;
		}
		return kunci;
	}

	public static String Enkrip(String kunci, String plaintext){
		String cipher="";
		int panjangPlain = plaintext.length();
		int panjangKunci = kunci.length();
		for (int i=0; i<panjangPlain; i++){
			int idxKunci = i % panjangKunci;
			
			int ASCIIEnkrip = ( CharToASCII(plaintext.charAt(i)) + CharToASCII(kunci.charAt(idxKunci)) ) % 256;
			//System.out.println(ASCIIToChar(ASCIIEnkrip));
			cipher = cipher+ASCIIToChar(ASCIIEnkrip);
		}
		return cipher;
	}

	public static String Dekrip(String kunci, String ciphertext){
		String plain = "";

		int panjangKunci = kunci.length();
		int panjangCipher = ciphertext.length();

		for (int i=0; i<panjangCipher; i++) {
			int idxKunci = i % panjangKunci;

			int ASCIIDekrip = CharToASCII(ciphertext.charAt(i)) - CharToASCII(kunci.charAt(idxKunci));
			if (ASCIIDekrip < 0) ASCIIDekrip = ASCIIDekrip + 256;

			plain = plain + ASCIIToChar(ASCIIDekrip);
		}
		return plain;
	}

}