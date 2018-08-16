package eu.beerbytes.NoobChain;

public class TransactionInput {
	public String transactionOutputId; //Reference to TransactionOutputs -> transactionId
	public TransactionOutput unspentTransactionOutput; //Contains the Unspent transaction output /UTXO
	
	public TransactionInput(String transactionOutputId) {
		this.transactionOutputId = transactionOutputId;
	}

	@Override
	public String toString() {
		return "TransactionInput [unspentTransactionOutput=" + unspentTransactionOutput + "]";
	}
}
