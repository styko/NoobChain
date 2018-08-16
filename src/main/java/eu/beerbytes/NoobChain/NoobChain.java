package eu.beerbytes.NoobChain;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoobChain {
	public static Map<String,TransactionOutput> unusedTxOutputs = new HashMap<String,TransactionOutput>(); // refactor
	public static float minimumTransaction = 0.1f;
	public static ArrayList<Block> blockchain = new ArrayList<Block>(); 
	public static int difficulty = 3;
	public static void main(String[] args) {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider()); 
		
		Wallet walletA = new Wallet();
		Wallet walletB = new Wallet();
		Wallet coinBase = new Wallet();
		Key2WalletTranslator.register(walletA.getPublicKey(), "walletA");
		Key2WalletTranslator.register(walletB.getPublicKey(), "walletB");
		Key2WalletTranslator.register(coinBase.getPublicKey(), "coinBase");
		
		//create genesis transaction, which sends 100 NoobCoin to walletA: 
		Transaction genesisTransaction = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		genesisTransaction.sign(coinBase.getPrivateKey());	 //manually sign the genesis transaction	
		genesisTransaction.setTransactionId("0"); //manually set the transaction id
		genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId())); //manually add the Transactions Output
		unusedTxOutputs.put(genesisTransaction.getOutputs().get(0).id, genesisTransaction.getOutputs().get(0)); //its important to store our first transaction in the UTXOs list.
	
		
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(genesisTransaction);
		addBlock(genesis);
		
		
		Block block1 = new Block(genesis.getHash());
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 40f));
		addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendFunds(walletB.getPublicKey(), 1000f));
		addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendFunds( walletA.getPublicKey(), 20));
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		new CryptoUtil().isChainValid(blockchain, difficulty, genesisTransaction);
	}
	
	public static void addBlock(Block newBlock) {
		newBlock.mine(difficulty);
		blockchain.add(newBlock);
	}
}
