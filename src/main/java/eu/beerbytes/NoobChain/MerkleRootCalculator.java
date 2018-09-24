package eu.beerbytes.NoobChain;

import java.util.ArrayList;
import java.util.List;

public class MerkleRootCalculator {
	
	private CryptoUtil cryptoUtil;
	
	public MerkleRootCalculator(CryptoUtil cryptoUtil) {
		super();
		this.cryptoUtil = cryptoUtil;
	}

	
	public String getMerkleRoot(List<Transaction> transactions) { 
		int countOfTransactions = transactions.size();
		
		List<String> previousTreeLayer = getTransactionIDs(transactions);
		List<String> treeLayer = previousTreeLayer;
		while(countOfTransactions > 1) {
			treeLayer = new ArrayList<String>();
			for(int index=0; index < previousTreeLayer.size();) {
				String first = previousTreeLayer.get(index);
				index++;
				
				if(index != previousTreeLayer.size()){
					String second = previousTreeLayer.get(index);
					index++;
					treeLayer.add(cryptoUtil.applySha256(first + second));
				} else {
					treeLayer.add(first);
				}
				
			}
			countOfTransactions = treeLayer.size();
			previousTreeLayer = treeLayer;
		}
		
		return (treeLayer.size() == 1) ? treeLayer.get(0) : "";
	}
	
	private List<String> getTransactionIDs(List<Transaction> transactions) {
		List<String> previousTreeLayer = new ArrayList<String>();
		for(Transaction transaction : transactions) {
			previousTreeLayer.add(transaction.getTransactionId());
		}
		return previousTreeLayer;
	}
	
}
