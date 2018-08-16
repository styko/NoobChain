package eu.beerbytes.NoobChain;

import java.security.PublicKey;

public class TransactionOutput {
	public String id;
	public PublicKey reciepient; 
	public float value; 
	public String parentTransactionId; //the id of the transaction this output was created in
	
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		CryptoUtil cryptoUtil = new CryptoUtil();
		this.id = cryptoUtil.applySha256(cryptoUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
	}
	
	public boolean checkOwnership(PublicKey publicKey) {
		return publicKey.equals(reciepient);
	}

	@Override
	public String toString() {
		return "TransactionOutput [reciepient=" + Key2WalletTranslator.getValletName(reciepient) + ", value=" + value+ "]";
	}
}
