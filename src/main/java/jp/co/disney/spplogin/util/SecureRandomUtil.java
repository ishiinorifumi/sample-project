package jp.co.disney.spplogin.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class SecureRandomUtil {
	private static int TOKEN_LENGTH = 16;//
	
	private SecureRandomUtil(){}
	
	public static String genToken() {
		byte token[] = new byte[TOKEN_LENGTH];
		StringBuffer buf = new StringBuffer();
		SecureRandom random = null;
		
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(token);
			for (int i = 0; i < token.length; i++) {
				buf.append(String.format("%02x", token[i]));
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	 
	    return buf.toString();

	}
}
