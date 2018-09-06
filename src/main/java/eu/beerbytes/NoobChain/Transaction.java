package eu.beerbytes.NoobChain;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Transaction {
	private static float MINIMUM_TRANSACTION = 0.1f;
	private static int sequence = 0; // refactor

	private String transactionId;
	private PublicKey sender;
	private PublicKey reciepient;
	private float value;
	private byte[] signature;

	private List<TransactionInput> inputs;
	private List<TransactionOutput> outputs;

	public Transaction(PublicKey sender, PublicKey recipient, float value, List<TransactionInput> inputs) {
		super();
		this.sender = sender;
		this.reciepient = recipient;
		this.value = value;
		this.inputs = inputs;
		this.outputs = new ArrayList<>();
	}
	
	public PublicKey getSender() {
		return sender;
	}

	public PublicKey getRecipient() {
		return reciepient;
	}

	public float getValue() {
		return value;
	}
	
	public List<TransactionInput> getInputs() {
		return inputs;
	}

	public List<TransactionOutput> getOutputs() {
		return outputs;
	}

	public boolean processTransaction(Map<String, TransactionOutput> unusedTxOutputs) {
		if(!verifySignature()) {
			System.out.println("#Transaction Signature failed to verify");
			return false;
		}
				
		//gather transaction inputs (Make sure they are unspent):
		for(TransactionInput i : inputs) {
			i.setUnspentTransactionOutput(unusedTxOutputs.get(i.getTransactionOutputId()));
		}

		//check if transaction is valid:
		if(getInputsValue() < MINIMUM_TRANSACTION) {
			System.out.println("#Transaction Inputs to small: " + getInputsValue());
			return false;
		}
		
		//generate transaction outputs:
		float leftOver = getInputsValue() - value; //get value of inputs then the left over change:
		transactionId = calculateHash();
		sendValueToRecipient();
		sendLeftOverToSender(leftOver); 		
				
		//add outputs to Unspent list
		for(TransactionOutput o : outputs) {
			unusedTxOutputs.put(o.id , o);
		}
		
		//remove transaction inputs from UTXO lists as spent:
		for(TransactionInput i : inputs) {
			if(i.getUnspentTransactionOutput() == null) continue; //if Transaction can't be found skip it 
			unusedTxOutputs.remove(i.getUnspentTransactionOutput().id);
		}
		
		System.out.println("Debug transaction" + this);
		return true;
	}

	private void sendLeftOverToSender(float leftOver) {
		outputs.add(new TransactionOutput( this.sender, leftOver,transactionId));
	}

	private void sendValueToRecipient() {
		outputs.add(new TransactionOutput( this.reciepient, value,transactionId));
	}
	
	public String getTransactionId() {
		return transactionId;
	}
	
	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	// returns sum of inputs(UTXOs) values
	public float getInputsValue() {
		float total = 0;
		for (TransactionInput input : inputs) {
			if (input.getUnspentTransactionOutput() == null)
				continue; // if Transaction can't be found skip it
			total += input.getUnspentTransactionOutput().value;
		}
		return total;
	}

	// returns sum of outputs:
	public float getOutputsValue() {
		float total = 0;
		for (TransactionOutput output : outputs) {
			total += output.value;
		}
		return total;
	}

	public void sign(PrivateKey privateKey) {
		CryptoUtil cryptoUtil = new CryptoUtil();
		String data = cryptoUtil.getStringFromKey(sender) + cryptoUtil.getStringFromKey(reciepient) + Float.toString(value);
		signature = cryptoUtil.sign(privateKey, data);
	}
	
	public boolean verifySignature() {
		CryptoUtil cryptoUtil = new CryptoUtil();
		String data = cryptoUtil.getStringFromKey(sender) + cryptoUtil.getStringFromKey(reciepient) + Float.toString(value);
		return cryptoUtil.verifySignature(sender, data, signature);
	}
	
	private String calculateHash() {
		sequence++;

		CryptoUtil cryptoUtil = new CryptoUtil();
		return cryptoUtil.applySha256(
				cryptoUtil.getStringFromKey(sender)
				+ cryptoUtil.getStringFromKey(reciepient)
				+ Float.toString(value) 
				+ sequence);
	}

	@Override
	public String toString() {
		return "Transaction [sender=" + Key2WalletTranslator.getValletName(sender)
				+ ", reciepient=" + Key2WalletTranslator.getValletName(reciepient) + ", value=" + value + ", inputs="
				+ inputs + ", outputs=" + outputs + "]";
	}
}