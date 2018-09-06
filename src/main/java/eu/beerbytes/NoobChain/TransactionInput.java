package eu.beerbytes.NoobChain;

public class TransactionInput {
	private String transactionOutputId; //Reference to TransactionOutputs -> transactionId
	private TransactionOutput unspentTransactionOutput; //Contains the Unspent transaction output /UTXO
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	@Override
	public String toString() {
		return "TransactionInput [unspentTransactionOutput=" + unspentTransactionOutput + "]";
	}

	public String getTransactionOutputId() {
		return transactionOutputId;
	}

	public TransactionOutput getUnspentTransactionOutput() {
		return unspentTransactionOutput;
	}

	public void setUnspentTransactionOutput(TransactionOutput unspentTransactionOutput) {
		this.unspentTransactionOutput = unspentTransactionOutput;
	}
}
