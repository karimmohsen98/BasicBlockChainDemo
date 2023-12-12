package org.blockChaimEx.Entities;

import org.blockChaimEx.Main;

import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {
    public PrivateKey privateKey;
    public PublicKey publicKey;
    public HashMap<String,TransactionOutput> UTXOs = new HashMap<String,TransactionOutput>(); //only UTXOs owned by this wallet.

    public Wallet() {
        generateKeyPairs();
    }

    private void generateKeyPairs() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecGenParameterSpec = new ECGenParameterSpec("prime192v1");

            keyPairGenerator.initialize(ecGenParameterSpec, secureRandom);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public float getBalance(){
        float total = 0;
        for (Map.Entry<String,TransactionOutput> item: Main.UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();

            if(UTXO.coinIsMine(publicKey)){
                UTXOs.put(UTXO.id,UTXO);
                total += UTXO.value;
            }

        }
        return total;
    }
    public Transaction sendFunds(PublicKey _recipient , float value){
        if (getBalance()<value){
            System.out.println("Not Enough Funds , Transaction Cancelled");
            return null;
        }
        ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
        float total = 0;
        for (Map.Entry<String,TransactionOutput> item : UTXOs.entrySet()){
            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add(new TransactionInput(UTXO.id));
            if (total>value) break;

        }
        Transaction newTransaction = new Transaction(publicKey ,_recipient ,value,inputs);
        newTransaction.generateSignature(privateKey);

        for (TransactionInput input:inputs){
            UTXOs.remove(input.transactionOutputId);
        }
        return  newTransaction;

    }
}
