package eu.beerbytes.NoobChain;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class Key2WalletTranslator {
	
	private static Map<PublicKey, String> publicKeyToValletMap = new HashMap<>();
	
	public static String getValletName(PublicKey key){
		return publicKeyToValletMap.get(key);
	}
	
	public static void register(PublicKey key, String value){
		publicKeyToValletMap.put(key, value);
	}
}
