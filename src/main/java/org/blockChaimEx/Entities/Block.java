package org.blockChaimEx.Entities;

import org.blockChaimEx.Util.StringUtil;

import java.util.ArrayList;
import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
    private String data;
    private long timeStamp;
    private int nonce;

    public Block(String data,String previousHash ) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();

        this.hash = calculateHash();
    }

    public String calculateHash() {
        String calculatedhash = StringUtil.applySha256(
                previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
        return calculatedhash;
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
            if (transaction.processTransaction()!=true){
                System.out.println("Transaction process failed , transaction is to be discarded");
                return false;
            }
        }
        transactions.add(transaction);
        System.out.println("Transaction successfully added to block");
        return true;

    }
}
