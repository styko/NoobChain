package eu.beerbytes.NoobChain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BlockChain {
	private Map<String, TransactionOutput> unusedTxOutputs = new HashMap<String, TransactionOutput>();
	private ArrayList<Block> blockchain = new ArrayList<Block>();
	private CryptoUtil cryptoUtil;
	private int difficulty;
	private MerkleRootCalculator merkleRootCalculator;

	public BlockChain(CryptoUtil cryptoUtil, MerkleRootCalculator merkleRootCalculator, int difficulty) {
		super();
		this.cryptoUtil = cryptoUtil;
		this.difficulty = difficulty;
		this.merkleRootCalculator = merkleRootCalculator;
	}

	public ArrayList<Block> getBlockchain() {
		return blockchain;
	}

	public void addBlock(Block newBlock) {
		newBlock.mine(difficulty, cryptoUtil, merkleRootCalculator);
		blockchain.add(newBlock);
	}

	public Map<String, TransactionOutput> getUnusedTxOutputs() {
		return unusedTxOutputs;
	}

	public void addNewUnusedTxOutput(String transactionOutputId, TransactionOutput transactionOutput) {
		unusedTxOutputs.put(transactionOutputId, transactionOutput);
	}
}
