package eu.beerbytes.NoobChain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {

	private String hash;
	private String previousHash;
	private String merkleRoot;
	private List<Transaction> transactions;
	private long timeStamp; 
	private int numberUsedOnce;
	
	public Block(String previousHash) {
		super();
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.transactions = new ArrayList<>();
		this.hash = "";
		this.merkleRoot = "";
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

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

	public String calculateHash() {
		return new CryptoUtil().applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(numberUsedOnce) + merkleRoot);
	}
	
	public void mine(int difficulty) {
		merkleRoot = new CryptoUtil().getMerkleRoot(transactions);
		String target = createTarget(difficulty);
		while (hash.isEmpty() || !hash.substring(0, difficulty).equals(target)) {
			numberUsedOnce++;
			hash = calculateHash();
		}
		
		System.out.println("Block Mined!!! : " + hash + "   nonce=" + numberUsedOnce);
	}
	
	public boolean addTransaction(Transaction transaction) {
		if(transaction == null){
			return false;		
		}
		
		if(!isGenesisBlock()) {
			boolean isTransactionProcessed = transaction.processTransaction();
			if((!isTransactionProcessed)) {
				System.out.println("Transaction failed to process. Discarded.");
				return false;
			}
		} 
		transactions.add(transaction);
		System.out.println("Transaction Successfully added to Block");
		return true;
	}

	private boolean isGenesisBlock() {
		return "0".equals(previousHash);
	}
	
	private String createTarget(int difficulty) {
		return String.format("%0"+difficulty+"d", 0);
	}
}
