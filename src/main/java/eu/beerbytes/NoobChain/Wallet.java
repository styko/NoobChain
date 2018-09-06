package eu.beerbytes.NoobChain;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Wallet {
	private PublicKey publicKey;
	private PrivateKey privateKey;
	private BlockChain blockChain;
	
	public Map<String,TransactionOutput> unusedTxOutputs = new HashMap<String,TransactionOutput>(); 
	
	public Wallet(BlockChain blockChain) {
		generateKeyPair();
		unusedTxOutputs = new HashMap<String,TransactionOutput>();
		this.blockChain = blockChain;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public void generateKeyPair() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");
			
			keyPairGenerator.initialize(ecSpec, secureRandom);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			
			publicKey = keyPair.getPublic();
			privateKey = keyPair.getPrivate();
		} catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
			throw new RuntimeException(e);
		}
	}
	
	public float getBalance() { 
		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : blockChain.getUnusedTxOutputs().entrySet()) {
			TransactionOutput unusedTxOutput = item.getValue();

			if (unusedTxOutput.checkOwnership(publicKey)) {
				unusedTxOutputs.put(unusedTxOutput.id, unusedTxOutput);
				total += unusedTxOutput.value;
			}
		}
		return total;
	}
	
	public Transaction sendMoney(PublicKey recipient, float value) {
		if (getBalance() < value) { 
			System.out.println("#Not Enough funds to send transaction. Transaction Discarded.");
			return null;
		}
		
		// create array list of inputs
		List<TransactionInput> inputs = new ArrayList<TransactionInput>();

		float total = 0;
		for (Map.Entry<String, TransactionOutput> item : unusedTxOutputs.entrySet()) {
			TransactionOutput unusedTxOutput = item.getValue();
			total += unusedTxOutput.value;
			inputs.add(new TransactionInput(unusedTxOutput.id));
			if (total > value){
				break;
			}
		}

		Transaction newTransaction = new Transaction(publicKey, recipient, value, inputs);
		newTransaction.sign(privateKey);

		for (TransactionInput input : inputs) {
			unusedTxOutputs.remove(input.getTransactionOutputId());
		}
		
		return newTransaction;
	}

}
