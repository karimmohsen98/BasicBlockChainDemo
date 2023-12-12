package org.blockChaimEx.Entities;

import org.blockChaimEx.Main;
import org.blockChaimEx.Util.StringUtil;

import java.lang.management.ManagementFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;

public class Transaction {
    public String transactionId;
    public PublicKey sender;
    public PublicKey recipient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0;

    public Transaction(PublicKey from,PublicKey to,float value,ArrayList<TransactionInput> inputs){
        this.sender = from;
        this.recipient = to;
        this.value=value;
        this.inputs = inputs;

    }
    private String calculateHash() {
        sequence++; //increase the sequence to avoid 2 identical transactions having the same hash
        return StringUtil.applySha256(
                StringUtil.getStringFromKey(sender) +
                        StringUtil.getStringFromKey(recipient) +
                        Float.toString(value) + sequence
        );
    }

    //This function signs all the data inorder to avoid tempering with it
    public void generateSignature(PrivateKey privateKey){
        String data = StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(recipient)+Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey,data);
    }

    public boolean verifySignature(){
        String data = StringUtil.getStringFromKey(sender)+StringUtil.getStringFromKey(recipient) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public Boolean processTransaction(){
        if (verifySignature()==false){
            System.out.println("Transaction Signature failed to verify");
            return false;
        }
        for (TransactionInput i :inputs){
            i.UTXO = Main.UTXOs.get(i.transactionOutputId);
        }
        if (getInputsValue()<Main.minimumTransaction){
            System.out.println("Transaction Amount is too small: " + getInputsValue());
            return false;

        }
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add(new TransactionOutput(this.recipient,value,transactionId));
        outputs.add(new TransactionOutput(this.sender,leftOver,transactionId));

        for (TransactionOutput o :outputs){
            Main.UTXOs.put(o.id,o);
        }

        for (TransactionInput i : inputs){
            if (i.UTXO == null) continue;
            Main.UTXOs.remove(i.UTXO.id);
        }
        return true;
    }


    private float getInputsValue() {
        float total = 0;
        for (TransactionInput i : inputs){
            if (i.UTXO == null) continue;
            total += i.UTXO.value;
        }
        return total;
    }
    public float getOutputValue(){
        float total = 0;
        for (TransactionOutput o : outputs){
            total += o.value;
        }
        return total;
    }


}
