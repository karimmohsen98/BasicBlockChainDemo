package org.blockChaimEx.Util;

import org.blockChaimEx.Entities.Transaction;

import java.security.*;
import java.util.ArrayList;
import java.util.Base64;

public class StringUtil {
    public static String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(input.getBytes("UTF-8"));

            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
    public static byte[] applyECDSASig(PrivateKey privateKey,String input){
        byte[] bytes = new byte[0];
        try {
            Signature signature = Signature.getInstance("ECDSA","BC");
            signature.initSign(privateKey);
            byte[] strByte = input.getBytes();
            signature.update(strByte);
            byte[] realSig = signature.sign();
            bytes = realSig;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return bytes;
    }
    public static boolean verifyECDSASig(PublicKey publicKey,String data , byte[] signature){
        try {
        Signature ecdsaVerif = Signature.getInstance("ECDSA","BC");
        ecdsaVerif.initVerify(publicKey);
        ecdsaVerif.update(data.getBytes());
        return ecdsaVerif.verify(signature);
    }catch (Exception e){
        throw new RuntimeException(e);
        }
    }

    public static String getStringFromKey(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    public static String getMerkelRoot(ArrayList<Transaction> transactions){
        int count = transactions.size();
        ArrayList<String> previousTreeLayer = new ArrayList<String>();
        for (Transaction T:transactions){
            previousTreeLayer.add(T.transactionId);
        }
        ArrayList<String> treeLayer = previousTreeLayer;
        while (count > 1){
            treeLayer = new ArrayList<String>();
            for (int i=1;i<previousTreeLayer.size();i++){
                treeLayer.add(applySha256(previousTreeLayer.get(i-1)+previousTreeLayer.get(i)));
            }
            count = treeLayer.size();
            previousTreeLayer = treeLayer;

        }
        String merkelRoot = (treeLayer.size() == 1) ? treeLayer.get(0) : "";
        return merkelRoot;
    }
}
