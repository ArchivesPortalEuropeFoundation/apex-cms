/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.archivesportaleurope.portal.common;

import com.liferay.portal.model.User;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import org.apache.log4j.Logger;

/**
 *
 * @author kaisar
 */
public class ApiKeyGenUtil {

    private final static Logger LOGGER = Logger.getLogger(ApiKeyGenUtil.class);

    public static void main(String args[]) {
        System.out.println(getSecureRandomToken());
    }

    public static String generateApiKey(User user) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        StringBuilder stringToBeHashed = new StringBuilder();
        stringToBeHashed.append(getSecureRandomToken());
        stringToBeHashed.append(user.getPassword());
        stringToBeHashed.append(user.getEmailAddress());
        messageDigest.update(stringToBeHashed.toString().getBytes());

        byte[] digest = messageDigest.digest();
        BigInteger bigInt = new BigInteger(1, digest);
        String keyText = bigInt.toString(16);
        return keyText;
    }

    private static String getSecureRandomToken() {
        long value;
        SecureRandom rand = new SecureRandom();
//            rand.nextLong()
//            SecureRandom random = SecureRandom.getInstanceStrong();
        value = rand.nextLong();

        value = Math.abs(value);
        String token = Long.toString(value);
        return token;
    }
}
