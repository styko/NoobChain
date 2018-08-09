package eu.beerbytes.NoobChain;

import java.security.MessageDigest;
import java.util.ArrayList;

public class CryptoUtil {
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
	
	public Boolean isChainValid(ArrayList<Block> blockchain,int difficulty ) {
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		for(int i=1; i < blockchain.size(); i++) {
			Block currentBlock = blockchain.get(i);
			Block previousBlock = blockchain.get(i-1);
			
			if (!isCurrentHasEqual(currentBlock)
					&& isPreviousHashEqual(currentBlock, previousBlock)
					&& isHashSolved(difficulty, currentBlock, hashTarget)) {
				return false;
			}
		}
		return true;
	}

	private boolean isHashSolved(int difficulty, Block currentBlock, String hashTarget) {
		boolean isHashSolved = currentBlock.getHash().substring(0, difficulty).equals(hashTarget);
		
		if(!isHashSolved) {
			System.out.println("This block hasn't been mined");
		}
		
		return isHashSolved;
	}

	private boolean isPreviousHashEqual(Block currentBlock, Block previousBlock) {
		boolean isPreviusHashEqual = previousBlock.getHash().equals(currentBlock.getPreviousHash());
		
		if(!isPreviusHashEqual) {
			System.out.println("Previous Hashes not equal");
		}
		
		return isPreviusHashEqual;
	}

	private boolean isCurrentHasEqual(Block currentBlock) {
		boolean isCurrentHashEqual = currentBlock.getHash().equals(currentBlock.calculateHash());
		
		if(!isCurrentHashEqual ){
			System.out.println("Current Hashes not equal");			
		}
		
		return isCurrentHashEqual;
	}
}
