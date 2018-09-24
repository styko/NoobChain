package eu.beerbytes.NoobChain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

	public String getPreviousHash() {
		return previousHash;
	}

	public String calculateHash(CryptoUtil cryptoUtil) {
		return cryptoUtil.applySha256(
				previousHash 
				+ Long.toString(timeStamp) 
				+ Integer.toString(numberUsedOnce) 
				+ merkleRoot);
	}
	
	public void mine(int difficulty, CryptoUtil cryptoUtil, MerkleRootCalculator merkleRootCalculator) {
		merkleRoot = merkleRootCalculator.getMerkleRoot(transactions);
		String target = cryptoUtil.createTarget(difficulty);
		while (hash.isEmpty() || !hash.substring(0, difficulty).equals(target)) {
			numberUsedOnce++;
			hash = calculateHash(cryptoUtil);
		}
		
		System.out.println("Block Mined!!! : " + hash + "   nonce=" + numberUsedOnce);
	}
	
	public boolean addTransaction(Transaction transaction, Map<String, TransactionOutput> unusedTxOutputs) {
		if(transaction == null){
			return false;		
		}
		
		if(!isGenesisBlock()) {
			boolean isTransactionProcessed = transaction.processTransaction(unusedTxOutputs);
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
}
