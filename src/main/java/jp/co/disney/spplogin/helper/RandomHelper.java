package jp.co.disney.spplogin.helper;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class RandomHelper {
	/**
	 * ランダムなIDを返す。
	 * @return
	 */
	public String randomID() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}
