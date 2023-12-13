package org.blockChaimEx.Entities;

import org.blockChaimEx.Util.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class Block {

    public String hash;
    public String previousHash;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private String merkleRoot;
    private long timeStamp;
    private int nonce;

    private static final Logger logger = Logger.getLogger(Block.class.getName());
    public Block(String previousHash ) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {
        return StringUtil.applySha256(
                previousHash +
                        timeStamp +
                        nonce +
                        merkleRoot

        );
    }

    public void mineBlock(int difficulty) {
        String target = new String(new char[difficulty]).replace('\0', '0'); //Create a string with difficulty * "0"
        while(!hash.substring( 0, difficulty).equals(target)) {
            nonce ++;
            hash = calculateHash();
        }
        System.out.println("Block Mined!!! : " + hash);
    }
    public Boolean addTransaction(Transaction transaction){
        if (transaction==null) return false;
        if ((previousHash != "0")){
            if (Boolean.FALSE.equals(transaction.processTransaction())){
                logger.info("Transaction process failed , transaction is to be discarded");
                return false;
            }
        }
        transactions.add(transaction);
        logger.info("Transaction successfully added to block");
        return true;

    }
}
