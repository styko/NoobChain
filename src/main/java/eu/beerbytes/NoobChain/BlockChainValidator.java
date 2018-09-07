package eu.beerbytes.NoobChain;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockChainValidator {
	
	private CryptoUtil cryptoUtil;
	
	public BlockChainValidator(CryptoUtil cryptoUtil) {
		this.cryptoUtil = cryptoUtil;
	}

	public Boolean isChainValid(ArrayList<Block> blockchain,int difficulty, Transaction genesisTransaction ) {
		String hashTarget = cryptoUtil.createTarget(difficulty);
		
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
					tempOutput = tempUnusedTxOutputs.get(input.getTransactionOutputId());
					
					if(tempOutput == null) {
						System.out.println("#Referenced input on Transaction(" + t + ") is Missing");
						return false;
					}
					
					if(input.getUnspentTransactionOutput().value != tempOutput.value) {
						System.out.println("#Referenced input Transaction(" + t + ") value is Invalid");
						return false;
					}
					
					tempUnusedTxOutputs.remove(input.getTransactionOutputId());
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
		boolean isCurrentHashEqual = currentBlock.getHash().equals(currentBlock.calculateHash(cryptoUtil));
		
		if(!isCurrentHashEqual ){
			System.out.println("Current Hashes not equal");			
		}
		
		return isCurrentHashEqual;
	}
}
