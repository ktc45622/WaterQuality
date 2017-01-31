
package security;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.*;

/**
 * This class uses AES encryption with a secret key built into the file. 
 * The purpose of the class is to show students some ideas in two-way encryption. 
 * 
 * @author cjones
 */
public class AESEncrypt {
    
    private static final String ALGORITHM = "AES";
    private static final byte[] keyValue = 
        new byte[] { 'T', 'h', 'e', 'B', 'e', 's', 't', 'S', 'e', 'c', 'r','e', 't', 'K', 'e', 'y' };
    /* The secret key is provided above instead of asking Java to generate it.
       We are using "TheBestSecretKey" as the secret key
       The AES algorithm can use a key of 128 bits.
       Each character in UTF8 (default encodeing of characters in US) is 8 bits or 1 byte.
       Our string is 16 characters long and 16 characters * 8 bits is 128 bits.
       If we used DES then we would need 56 bits and if we used Triple DES,
       then would need 3 times 56 or 168 bits
    */


public static String encrypt(String inputString) throws Exception {
        SecretKeySpec key = getKey();
 //     Cipher cipher = Cipher.getInstance("AES"); // 128 bit -  provider-specific default values for the mode and padding scheme are used
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding"); // 128 bits and specifies the mode and padding scheme
 //     Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding"); // 168 bits -- Triple DES algorithm
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = cipher.doFinal(inputString.getBytes());
        String encryptedValue = new BASE64Encoder().encode(encVal);
        return encryptedValue;
    }

    public static String decrypt(String encryptedData) throws Exception {
        Key key = getKey();
        Cipher c = Cipher.getInstance(ALGORITHM);
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decordedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decordedValue);
        String decryptedValue = new String(decValue);
        return decryptedValue;
    }
    private static SecretKeySpec getKey() throws Exception {
        SecretKeySpec key = new SecretKeySpec(keyValue, ALGORITHM);
        return key;
    }

}

