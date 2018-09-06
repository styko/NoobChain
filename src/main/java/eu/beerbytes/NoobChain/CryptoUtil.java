package eu.beerbytes.NoobChain;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class CryptoUtil {
	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}
	
	public String getMerkleRoot(List<Transaction> transactions) { //TODO rework
		int countOfTransactions = transactions.size();
		
		List<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.getTransactionId());
		}
		
		List<String> treeLayer = previousTreeLayer;
		while(countOfTransactions > 1) {
			treeLayer = new ArrayList<String>();
			for(int i=1; i < previousTreeLayer.size(); i++) {
				treeLayer.add(applySha256(previousTreeLayer.get(i-1) + previousTreeLayer.get(i)));
			}
			countOfTransactions = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		
		return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
	}
	
	public byte[] sign(PrivateKey privateKey, String input){
		try {
			Signature dsa = Signature.getInstance("ECDSA", "BC");
			dsa.initSign(privateKey);
			dsa.update(input.getBytes());
			return dsa.sign();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean verifySignature(PublicKey publicKey, String data, byte[] signature) {
		try {
			Signature ecdsaVerify = Signature.getInstance("ECDSA", "BC");
			ecdsaVerify.initVerify(publicKey);
			ecdsaVerify.update(data.getBytes());
			return ecdsaVerify.verify(signature);
		}catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	public String getStringFromKey(Key key) {
		return Base64.getEncoder().encodeToString(key.getEncoded());
	}
	
	
	public String applySha256(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(input.getBytes("UTF-8"));
			return convertToHex(hash);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private String convertToHex(byte[] hash) {
		StringBuffer hexString = new StringBuffer(); 
		for (int i = 0; i < hash.length; i++) {
			int convertToIntegerAndKeepOnlyLastByte = convertToIntegerAndKeepOnlyLastByte(hash, i);
			String hex = Integer.toHexString(convertToIntegerAndKeepOnlyLastByte);
			preserveLeadingZero(hexString, hex);
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private void preserveLeadingZero(StringBuffer hexString, String hex) {
		if (hex.length() == 1){
			hexString.append('0');
		}
	}

	private int convertToIntegerAndKeepOnlyLastByte(byte[] hash, int i) {
		return 0xff & hash[i];
	}
	

}
