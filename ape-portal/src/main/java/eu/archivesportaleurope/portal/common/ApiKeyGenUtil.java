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

/**
 *
 * @author kaisar
 */
public class ApiKeyGenUtil {

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
        try {
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            value = random.nextLong();
        } catch (NoSuchAlgorithmException ex) {
            sun.security.provider.SecureRandom r = new sun.security.provider.SecureRandom();
            byte[] b = new byte[4];
            r.engineNextBytes(b);
            value = ((0xFF & b[0]) << 24) | ((0xFF & b[1]) << 16)
                    | ((0xFF & b[2]) << 8) | (0xFF & b[3]);
        }
        value = Math.abs(value);
        String token = Long.toString(value);
        return token;
    }
}
