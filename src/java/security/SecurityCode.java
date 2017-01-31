package security;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 *
 * @author cjones
 */
public class SecurityCode {
    
    /**
     * Encrypts a string in hexadecimal format using the SHA-256 hash algorithm.
     * If SHA-256 does not exist, then the original string is returned. This is
     * a cryptographic hash function, designed not to be decrypted. Use this
     * concept to store non-recoverable passwords in a database
     *
     * @param originalString The original string.
     * @return The encrypted string.
     */
    public static String encryptSHA256(String originalString) {
        byte[] digest = null;
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(originalString.getBytes("UTF-8"));
            digest = md.digest();
            for (int i = 0; i < digest.length; i++) {
                if ((0xff & digest[i]) < 0x10) {
                    hexString.append("0");
                }
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            System.out.println("Error trying to use SHA-256");
            return originalString;
        }
        return hexString.toString();
    }

    /**
     * Returns a 32 character long randomly created string. This string is 
     * intended to be used as a salt for passwords. The number of possible integer values 
     * used to generate the string is 2 raised to the 130th power (2^130).
     * 
     * @return A randomly generated 32 character long string. 
     */
    public static String generateSalt() {
         SecureRandom random = new SecureRandom();
         return new BigInteger(130, random).toString(32); //32 characters  (or 256 bits are returned)
    }
    
    /**
     * Encrypts a string in hexadecimal format using the SHA-1 hash algorithm.
     * If SHA-1 does not exist, then the original string is returned. This is a
     * cryptographic hash function, designed not to be decrypted. Use this
     * concept to store non-recoverable passwords in a database
     *
     * @param originalString The original string.
     * @return The encrypted string.
     */
    public static String encryptSHA1(String originalString) {
        String alg = "SHA-1";
        byte[] bytes = null;
        try {
            // gets bytes from encryption algorithm
            bytes = MessageDigest.getInstance(alg).digest(originalString.getBytes());
        } catch (NoSuchAlgorithmException e) {
            String msg = "The encryption algorithm " + alg
                    + " is not available or does not exist.";
           System.out.println(msg);
            return originalString;
        }

        // translates bytes to hex string
        StringBuilder hexStrBuf = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(b & 0xff);
            hexStrBuf.append(str.length() == 1 ? "0" : "").append(str);
        }

        return hexStrBuf.toString();
    }
    
    /**
     * Encrypts a string in hexadecimal format using the SHA-1 hash algorithm.
     * If SHA-1 does not exist, then the original string is returned. This is a
     * cryptographic hash function, designed not to be decrypted. Use this
     * concept to store non-recoverable passwords in a database
     *
     * @param originalString The original string.
     * @return The encrypted string.
     */
    public static String encryptMD5(String originalString) {
        String alg = "MD5";
        byte[] bytes = null;
        try {
            // gets bytes from encryption algorithm
            bytes = MessageDigest.getInstance(alg).digest(originalString.getBytes());
        } catch (NoSuchAlgorithmException e) {
            String msg = "The encryption algorithm " + alg
                    + " is not available or does not exist.";
            System.out.println(msg);
            return originalString;
        }

        // translates bytes to hex string
        StringBuilder hexStrBuf = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(b & 0xff);
            hexStrBuf.append(str.length() == 1 ? "0" : "").append(str);
        }

        return hexStrBuf.toString();
    }
    
    /**
     * Returns the name of the algorithm used to encrypt this string or null
     * if the string is either null or of an encoding type we don't recognize.
     * The length of the string is used to determine which algorithm created the 
     * encoding. 
     * 
     * @param hashedValue The encoded string.
     * @return The algorithm used to encode this string or null. 
    */
    public static String determineHashAlgorithm(String hashedValue){  
       String algorithm = null;
       if(hashedValue == null) return algorithm;
       if(hashedValue.length() == 32)return "MD5";
       if(hashedValue.length() == 40)return "SHA-1";
       if(hashedValue.length() == 64)return "SHA-256";
       return algorithm;
    }
    
    /**
     * Encrypt the given string based on the name of the one way hashing algorithm 
     * specified. Legal algorithms are MD5, SHA-1, SHA-256. 
     * 
     * @param originalString The string to hash.
     * @param algorithm The algorithm to sue to perform the hashing.
     * @return The hashed string if a legal algorithm is specified, otherwise 
     * the original string.  
     */
    
    public static String encrypt(String originalString, String algorithm) {
        if(originalString == null || algorithm == null ) return originalString;
     
        byte[] bytes = null;
        try {
            // gets bytes from encryption algorithm
            bytes = MessageDigest.getInstance(algorithm).digest(originalString.getBytes());
        } catch (NoSuchAlgorithmException e) {
            String msg = "The encryption algorithm " + algorithm
                    + " is not available or does not exist.";
            System.out.println(msg);
            return originalString;
        }

        // translates bytes to hex string
        StringBuilder hexStrBuf = new StringBuilder();
        for (byte b : bytes) {
            String str = Integer.toHexString(b & 0xff);
            hexStrBuf.append(str.length() == 1 ? "0" : "").append(str);
        }

        return hexStrBuf.toString(); 
    }
    
    
}
