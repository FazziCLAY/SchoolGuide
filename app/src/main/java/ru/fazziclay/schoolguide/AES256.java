package ru.fazziclay.schoolguide;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AES256 {
    private static final String AES = "AES";

    public static byte[] encrypt(byte[] secretKey, byte[] toEncrypt) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");

        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        return cipher.doFinal(toEncrypt);
    }

    public static byte[] decrypt(byte[] secretKey, byte[] toDecrypt) throws Exception {
        SecretKeySpec key = new SecretKeySpec(secretKey, "AES");

        Cipher cipher = Cipher.getInstance(AES);
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(toDecrypt);
    }
}
