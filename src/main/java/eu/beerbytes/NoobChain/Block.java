package eu.beerbytes.NoobChain;

import java.util.Date;

public class Block {

	private String hash;
	private String previousHash;
	private String data; 
	private long timeStamp; 
	private int numberUsedOnce;
	
	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public Block(String data, String previousHash) {
		super();
		this.data = data;
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = "";
	}

	public String calculateHash() {
		return new CryptoUtil().applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(numberUsedOnce) + data);
	}
	
	public void mine(int difficulty) {
		String target = createTarget(difficulty);
		while (hash.isEmpty() || !hash.substring(0, difficulty).equals(target)) {
			numberUsedOnce++;
			hash = calculateHash();
		}
		
		System.out.println("Block Mined!!! : " + hash + "   nonce=" + numberUsedOnce);
	}
	
	private String createTarget(int difficulty) {
		return String.format("%0"+difficulty+"d", 0);
	}
}
