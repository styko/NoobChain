package eu.beerbytes.NoobChain;

import java.util.ArrayList;

import com.google.gson.GsonBuilder;

public class NoobChain {
	public static void main(String[] args) {
		ArrayList<Block> blockchain = new ArrayList<Block>(); 
		int difficulty = 5;
		
		blockchain.add(new Block("Hi im the first block", "0"));
		System.out.println("Trying to Mine block 1... ");
		blockchain.get(0).mine(difficulty);
		
		blockchain.add(new Block("Yo im the second block", blockchain.get(blockchain.size()-1).getHash()));
		System.out.println("Trying to Mine block 2... ");
		blockchain.get(1).mine(difficulty);
		
		blockchain.add(new Block("Hey im the third block", blockchain.get(blockchain.size()-1).getHash()));
		System.out.println("Trying to Mine block 3... ");
		blockchain.get(2).mine(difficulty);	
		
		System.out.println("\nBlockchain is Valid: " + new CryptoUtil().isChainValid(blockchain,difficulty));
		
		String blockchainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockchain);
		System.out.println("\nThe block chain: ");
		System.out.println(blockchainJson); 
	}
}
