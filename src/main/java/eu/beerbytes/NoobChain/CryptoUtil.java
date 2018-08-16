package eu.beerbytes.NoobChain;

import java.security.Key;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CryptoUtil {
	
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
	
	public Boolean isChainValid(ArrayList<Block> blockchain,int difficulty, Transaction genesisTransaction ) {
		String hashTarget = new String(new char[difficulty]).replace('\0', '0');
		
		Map<String,TransactionOutput> tempUnusedTxOutputs = new HashMap<String,TransactionOutput>(); //a temporary working list of unspent transactions at a given block state.
		tempUnusedTxOutputs.put(genesisTransaction.getOutputs().get(0).id, genesisTransaction.getOutputs().get(0));
	
		
		for(int i=1; i < blockchain.size(); i++) {
			Block currentBlock = blockchain.get(i);
			Block previousBlock = blockchain.get(i-1);
			
			if (!isCurrentHasEqual(currentBlock)
					&& isPreviousHashEqual(currentBlock, previousBlock)
					&& isHashSolved(difficulty, currentBlock, hashTarget)) {
				return false;
			}
			
			//loop thru blockchains transactions:
			TransactionOutput tempOutput;
			for(int t=0; t <currentBlock.getTransactions().size(); t++) {
				Transaction currentTransaction = currentBlock.getTransactions().get(t);
				
				if(!currentTransaction.verifySignature()) {
					System.out.println("#Signature on Transaction(" + t + ") is Invalid");
					return false; 
				}
				if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
					System.out.println("#Inputs are note equal to outputs on Transaction(" + t + ")");
					return false; 
				}
				
				for(TransactionInput input: currentTransaction.getInputs()) {	
					tempOutput = tempUnusedTxOutputs.get(input.transactionOutputId);
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.unspentTransactionOutput.value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUnusedTxOutputs.remove(input.transactionOutputId);
				}
				
				for(TransactionOutput output: currentTransaction.getOutputs()) {
					tempUnusedTxOutputs.put(output.id, output);
				}
				
				TransactionOutput firstTransactionOutput = currentTransaction.getOutputs().get(0);
				PublicKey reciepientFromFirstOutput = firstTransactionOutput.reciepient;
				if( reciepientFromFirstOutput != currentTransaction.getRecipient()) {
					System.out.println("#Transaction(" + t + ") output reciepient is not who it should be");
					
					return false;
				}
				
				
				PublicKey reciepientSecondOutput = currentTransaction.getOutputs().get(1).reciepient;
				if( reciepientSecondOutput != currentTransaction.getSender()) {
					System.out.println("#Transaction(" + t + ") output 'change' is not sender.");
					
					return false;
				}
				
			}
			
			
		}
		
		System.out.println("Blockchain is valid");
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
