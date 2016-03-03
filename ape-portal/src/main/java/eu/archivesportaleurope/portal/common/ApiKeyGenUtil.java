/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.archivesportaleurope.portal.common;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author kaisar
 */
public class ApiKeyGenUtil {

    public static String generateApiKey(String msg) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        String textToEncrypt = new StringBuilder().append(String.valueOf(System.currentTimeMillis())).append(msg).toString();
        messageDigest.update(textToEncrypt.getBytes());

        byte[] digest = messageDigest.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String keyText = bigInt.toString(16);
        return keyText;
    }

}
