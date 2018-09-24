package eu.beerbytes.NoobChain;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class MerkleRootCalculatorTest {
	
	@Test
	public void testMerkleRoot(){
		Transaction t1 = Mockito.mock(Transaction.class);
		Mockito.when(t1.getTransactionId()).thenReturn("HASH_T1");
		Transaction t2 = Mockito.mock(Transaction.class);
		Mockito.when(t2.getTransactionId()).thenReturn("HASH_T2");
		Transaction t3 = Mockito.mock(Transaction.class);
		Mockito.when(t3.getTransactionId()).thenReturn("HASH_T3");
		Transaction t4 = Mockito.mock(Transaction.class);
		Mockito.when(t4.getTransactionId()).thenReturn("HASH_T4");
		Transaction t5 = Mockito.mock(Transaction.class);
		Mockito.when(t5.getTransactionId()).thenReturn("HASH_T5");
		List<Transaction> transactions = Arrays.asList(t1,t2,t3,t4,t5);
		
		CryptoUtil cryptoUtil = Mockito.mock(CryptoUtil.class);
		Mockito.when(cryptoUtil.applySha256("HASH_T1HASH_T2")).thenReturn("HASH_T1T2");
		Mockito.when(cryptoUtil.applySha256("HASH_T3HASH_T4")).thenReturn("HASH_T3T4");
		Mockito.when(cryptoUtil.applySha256("HASH_T1T2HASH_T3T4")).thenReturn("HASH_T1T2T3T4");
		Mockito.when(cryptoUtil.applySha256("HASH_T1T2T3T4HASH_T5")).thenReturn("HASH_T1T2T3T4T5");
		
		MerkleRootCalculator merkleRootCalculator = new MerkleRootCalculator(cryptoUtil);
		String merkleRoot = merkleRootCalculator.getMerkleRoot(transactions);
		Assert.assertEquals("HASH_T1T2T3T4T5", merkleRoot);
	}
}
