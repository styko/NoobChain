package eu.beerbytes.NoobChain;

public class NoobCoin {
	
	public static void main(String[] args) {
		CryptoUtil cryptoUtil = new CryptoUtil();
		MerkleRootCalculator merkleRootCalculator = new MerkleRootCalculator(cryptoUtil);
		int difficulty = 3;
		BlockChain blockChain = new BlockChain(cryptoUtil, merkleRootCalculator, difficulty);
		
		Wallet walletA = new Wallet(blockChain);
		Wallet walletB = new Wallet(blockChain);
		Wallet coinBase = new Wallet(blockChain);
		Key2WalletTranslator.register(walletA.getPublicKey(), "walletA");
		Key2WalletTranslator.register(walletB.getPublicKey(), "walletB");
		Key2WalletTranslator.register(coinBase.getPublicKey(), "coinBase");
		
		Block genesis = createGenesisBlock(blockChain, walletA, coinBase);
		
		Block block1 = new Block(genesis.getHash());
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("\nWalletA is Attempting to send funds (40) to WalletB...");
		block1.addTransaction(walletA.sendMoney(walletB.getPublicKey(), 40f), blockChain.getUnusedTxOutputs());
		blockChain.addBlock(block1);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block2 = new Block(block1.getHash());
		System.out.println("\nWalletA Attempting to send more funds (1000) than it has...");
		block2.addTransaction(walletA.sendMoney(walletB.getPublicKey(), 1000f), blockChain.getUnusedTxOutputs());
		blockChain.addBlock(block2);
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		Block block3 = new Block(block2.getHash());
		System.out.println("\nWalletB is Attempting to send funds (20) to WalletA...");
		block3.addTransaction(walletB.sendMoney( walletA.getPublicKey(), 20), blockChain.getUnusedTxOutputs());
		System.out.println("\nWalletA's balance is: " + walletA.getBalance());
		System.out.println("WalletB's balance is: " + walletB.getBalance());
		
		BlockChainValidator blockChainValidator = new BlockChainValidator(cryptoUtil);
		blockChainValidator.isChainValid(blockChain.getBlockchain(), difficulty, createGenesisTransaction(blockChain, walletA, coinBase)); //refactor
	}

	private static Block createGenesisBlock(BlockChain blockChain, Wallet walletA, Wallet coinBase) {
		System.out.println("Creating and Mining Genesis block... ");
		Block genesis = new Block("0");
		genesis.addTransaction(createGenesisTransaction(blockChain, walletA, coinBase), blockChain.getUnusedTxOutputs());
		blockChain.addBlock(genesis);
		return genesis;
	}

	private static Transaction createGenesisTransaction(BlockChain blockChain, Wallet walletA, Wallet coinBase) {
		Transaction genesisTransaction = new Transaction(coinBase.getPublicKey(), walletA.getPublicKey(), 100f, null);
		
		genesisTransaction.sign(coinBase.getPrivateKey());	 //manually sign the genesis transaction	
		genesisTransaction.setTransactionId("0"); //manually set the transaction id
		genesisTransaction.getOutputs().add(new TransactionOutput(genesisTransaction.getRecipient(), genesisTransaction.getValue(), genesisTransaction.getTransactionId())); //manually add the Transactions Output
		blockChain.addNewUnusedTxOutput( //its important to store our first transaction in the UTXOs list.
			genesisTransaction.getOutputs().get(0).id,
			genesisTransaction.getOutputs().get(0)
		);
		return genesisTransaction;
	}
}
