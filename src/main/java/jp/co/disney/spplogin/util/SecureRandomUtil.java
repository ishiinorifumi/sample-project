package jp.co.disney.spplogin.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * java.security.SecureRandomを使用するユーティリティクラス
 *
 */
public class SecureRandomUtil {
	private static int TOKEN_LENGTH = 16;
	
	private SecureRandomUtil(){}
	
	/**
	 * セキュアランダムな32文字の16進文字列を生成する。
	 * @return ランダムな16進文字列
	 */
	public static String genToken() {
		byte token[] = new byte[TOKEN_LENGTH];
		StringBuffer buf = new StringBuffer();
		SecureRandom random = null;
		
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(token);
			for(int b : token) {
				buf.append(Character.forDigit(b >> 4 & 0xF, 16));
				buf.append(Character.forDigit(b & 0xF, 16));
			}
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	 
	    return buf.toString();

	}
}
