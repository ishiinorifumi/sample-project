package jp.co.disney.spplogin.helper;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.Base64;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

public class URLDecodeHelper {

	private URI url;
	private List<NameValuePair> queryStrings;
	
	public URLDecodeHelper(URI url) {
		this.url = url;
		this.queryStrings = URLEncodedUtils.parse(url, "UTF-8");
	}
	
	/**
	 * クエリ文字列のキーに対応する値を返す。
	 * @param queryKey キー
	 * @return 値
	 */
	public String getQueryValue(String queryKey) {
		String val = null;
		for(NameValuePair p : queryStrings){
			if(p.getName().equals(queryKey)) {
				//return new String(Base64.getUrlDecoder().decode(p.getValue()), "UTF-8");
				return p.getValue();
			}
		}
		return val;
	}
	
	/**
	 * クエリ文字列のキーに対応する値をURLデコードして返す。
	 * @param queryKey キー
	 * @return 値
	 */
	public String getQueryValueWithUrlDecode(String queryKey) {
		String val = null;
		for(NameValuePair p : queryStrings){
			if(p.getName().equals(queryKey)) {
				try {
					return new String(Base64.getUrlDecoder().decode(p.getValue()), "UTF-8");
				} catch (UnsupportedEncodingException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return val;
	}
}
