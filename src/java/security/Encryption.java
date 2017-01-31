package security;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import utilities.Debug;
import utilities.WebErrorLogger;

/**
 * This class contains a variety of methods used for encrypting, decrypting, and hashing
 * strings. 
 *
 * @author cjones
 */
public class Encryption {

    private static SecretKey key = null;

    /**
     * Helper function to initialize the secret key when we do not already have
     * one stored in a file.
     */
    public static void initialize() {
        initialize(null);
    }

    /**
     * Obtains the secret AES key either by reading it from a file or generating a
     * new key.
     *
     * @param filename The name of the file containing our secret key. If <code> filename
     * </code> is null, then a new key is generated.
     */
    public static void initialize(String filename) {
        if (filename == null) {
            key = generateAESKey();
            return;
        }
        key = readKey(new File(filename));
    }

    /**
     * Encrypts the given string using the stored secret key. If no key is
     * stored, a key is generated.
     *
     * @param str The string to encrypt using the AES algorithm.
     * @return The encrypted string or the original
     * string if no encryption key is available.
     */
    public static String keyEncryptAES(String str) {
        if (key == null) {
            key = generateAESKey();
        }
        return Encryption.keyEncryptAES(key, str);

    }

    /**
     * Decrypts the given string with the stored secret key using the AES algorithm. 
     * If no key is stored, then the original string is returned.
     *
     * @param str The string to decrypt using AES.
     * @return The decrypted string.
     */
    public static String keyDecryptAES(String str) {
        if (key == null) {
            return str;
        }
        return keyDecryptAES(key, str);
    }

    /**
     * Encrypts the given string using the secret key and the provided algorithm. 
     * If an error occurs, then null is returned. 
     *
     * @param key The key.
     * @param str The string to encrypt using the AES algorithm.
     * @return The encrypted string or <code> null</code>.
     */
    public static String keyEncryptAES(SecretKey key, String str) {
        try {
            Cipher ecipher = Cipher.getInstance("AES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");
            /*
             We could also use the default character set
             byte[] stringBytes = str.getBytes(); // uses default character set
            */
            
            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (IllegalBlockSizeException ex) {
            WebErrorLogger.log(Level.SEVERE, "IllegalBlockSizeException is thrown while "
                    + "trying to use AES to encrypt the string. " + str, ex);
        } catch (InvalidKeyException ex) {
            WebErrorLogger.log(Level.SEVERE, "InvalidKeyException is thrown while "
                    + "trying to use AES to encrypt the string. " + str, ex);
        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    + "trying to use AES to encrypt the string. " + str, ex);
        } catch (NoSuchPaddingException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchPaddingException is thrown while "
                    + "trying to use AES to encrypt the string. " + str, ex);
        } catch (javax.crypto.BadPaddingException ex) {
            WebErrorLogger.log(Level.SEVERE, "javax.crypto.BadPaddingException is thrown while "
                    + "trying to use AES to encrypt the string. " + str, ex);
        } catch (java.io.IOException ex) {
            WebErrorLogger.log(Level.SEVERE, "java.io.IOException is thrown while "
                    + "trying to use AES to encrypt the string. " + str, ex);
        }
        return null;
    }

    /**
     * Encrypts the given string using the secret key and the Triple DES
     * algorithm. 
     * If an error occurs, then null is returned. 
     *
     * @param key The key.
     * @param str The string to encrypt using the triple DES algorithm.
     * @return A string that represents the encrypted string or <code> null</code>.
     */
    public static String keyEncryptTripleDES(SecretKey key, String str) {
        try {
            Cipher ecipher = Cipher.getInstance("DESede");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);
            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (IllegalBlockSizeException ex) {
            WebErrorLogger.log(Level.SEVERE, "IllegalBlockSizeException is thrown while "
                    + "trying to use the triple DES algorithm to encrypt the string. " + str, ex);
        } catch (InvalidKeyException ex) {
            WebErrorLogger.log(Level.SEVERE, "InvalidKeyException is thrown while "
                    + "trying to use the triple DES algorithm to encrypt the string. " + str, ex);
        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    + "trying to use the triple DES algorithm to encrypt the string. " + str, ex);
        } catch (NoSuchPaddingException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchPaddingException is thrown while "
                    + "trying to use the triple DES algorithm to encrypt the string. " + str, ex);
        } catch (javax.crypto.BadPaddingException ex) {
            WebErrorLogger.log(Level.SEVERE, "javax.crypto.BadPaddingException is thrown while "
                    + "trying to use the triple DES algorithm to encrypt the string. " + str, ex);
        } catch (java.io.IOException ex) {
            WebErrorLogger.log(Level.SEVERE, "java.io.IOException is thrown while "
                    + "trying to use the triple DES algorithm to encrypt the string. " + str, ex);
        }
        return null;
    }

    /**
     * Decrypts the given string with the secret key and the AES algorithm.
     * If an error occurs, then null is returned. 
     * @param key The key.
     * @param str The string to keyDecryptDES.
     * @return A string that represents the decrypted string or <code> null</code>.
     */
    public static String keyDecryptAES(SecretKey key, String str) {
        try {
            Cipher dcipher = Cipher.getInstance("AES"); // Implementation determines the default mode and padding scheme.
         // Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding"); // 128 bits and specifies the mode and padding scheme
            dcipher.init(Cipher.DECRYPT_MODE, key);
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (InvalidKeyException ex) {
            WebErrorLogger.log(Level.SEVERE, "InvalidKeyException is thrown while "
                    + "trying to use AES to decrypt the string. " + str, ex);
        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    + "trying to use AES to decrypt the string. " + str, ex);
        } catch (NoSuchPaddingException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchPaddingException is thrown while "
                    + "trying to use AES to decrypt the string. " + str, ex);
        } catch (javax.crypto.BadPaddingException ex) {
            WebErrorLogger.log(Level.SEVERE, "javax.crypto.BadPaddingException is thrown while "
                    + "trying to use AES to decrypt the string. " + str, ex);
        } catch (IllegalBlockSizeException ex) {
            WebErrorLogger.log(Level.SEVERE, "IllegalBlockSizeException is thrown while "
                    + "trying to use AES to decrypt the string. " + str, ex);
        } catch (java.io.IOException ex) {
            WebErrorLogger.log(Level.SEVERE, "java.io.IOException is thrown while "
                    + "trying to use AES to decrypt the string. " + str, ex);
        }
        return null;
    }
/**
     * Decrypts the given string with the secret key and the AES algorithm.
     * If an error occurs, then null is returned. 
     *
     * @param key The key.
     * @param str The string to keyDecryptDES.
     * @return A string that represents the decrypted string or <code> null</code>.
     */
    public static String keyDecryptTripleDES(SecretKey key, String str) {
        try {
            Cipher dcipher = Cipher.getInstance("DESede"); // 168 bits - The vendor chooses the mode and padding scheme.
     //     Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding"); // Specifies the mode and padding scheme.
            dcipher.init(Cipher.DECRYPT_MODE, key);
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (InvalidKeyException ex) {
            WebErrorLogger.log(Level.SEVERE, "InvalidKeyException is thrown while "
                    + "trying to use DESede to decrypt the string. " + str, ex);
        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    + "trying to use DESede to decrypt the string. " + str, ex);
        } catch (NoSuchPaddingException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchPaddingException is thrown while "
                    + "trying to use DESede to decrypt the string. " + str, ex);
        } catch (javax.crypto.BadPaddingException ex) {
            WebErrorLogger.log(Level.SEVERE, "javax.crypto.BadPaddingException is thrown while "
                    + "trying to use DESede to decrypt the string. " + str, ex);
        } catch (IllegalBlockSizeException ex) {
            WebErrorLogger.log(Level.SEVERE, "IllegalBlockSizeException is thrown while "
                    + "trying to use DESede to decrypt the string. " + str, ex);
        } catch (java.io.IOException ex) {
            WebErrorLogger.log(Level.SEVERE, "java.io.IOException is thrown while "
                    + "trying to use DESede to decrypt the string. " + str, ex);
        }
        return null;
    }
    
    public static SecretKey generateSecretKey(String algorithm){
        KeyGenerator keygenerator = null;
        try {
            // Get a key generator for the algorithm requested
            keygenerator = KeyGenerator.getInstance(algorithm);  
        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    + "trying to generate a secret key for " + algorithm+".", ex);
            return null;
        }
        if (keygenerator == null) {
            return null;
        } else {
            return keygenerator.generateKey();
        }
    }

    /**
     * Generates a secret AES encryption/decryption key.
     *
     * @return The secret key.
     */
    public static SecretKey generateAESKey() {
        KeyGenerator keygenerator = null;
        try {
            // Get a key generator for AES
            keygenerator = KeyGenerator.getInstance("AES"); //128 bit 
//          keygenerator = KeyGenerator.getInstance("DESede"); //Triple DES -- 168 bit

        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    + "trying to generate a secret key for AES.", ex);
            return null;
        }
        if (keygenerator == null) {
            return null;
        } else {
            return keygenerator.generateKey();
        }
    }
/**
     * Generates a secret AES encryption/decryption key.
     *
     * @return The secret key.
     */
    public static SecretKey generateTripleDESKey() {
        KeyGenerator keygenerator = null;
        try {
            // Get a key generator for Triple DES
            keygenerator = KeyGenerator.getInstance("DESede"); //Triple DES -- 168 bit

        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    + "trying to generate a secret key.", ex);
        }
        if (keygenerator == null) {
            return null;
        } else {
            return keygenerator.generateKey();
        }
    }
    
    /**
     * Saves the DES SecretKey to the given file.
     *
     * @param key The secret key.
     * @param f The file.
     */
    public static void writeTripleDESKey(SecretKey key, File f) {
        FileOutputStream out = null;
        try {
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede"); // could be DESede for triple DES
            DESKeySpec keyspec = (DESKeySpec) keyfactory.getKeySpec(key,
                    DESKeySpec.class);
            byte[] bytes = keyspec.getKey();
            out = new FileOutputStream(f);
            out.write(bytes);
            out.close();
        } catch (IOException ex) {
            WebErrorLogger.log(Level.SEVERE, "IOException is thrown while "
                    + "trying to write the secret key to the file: " + f + ".", ex);
        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown "
                    + "while trying to write the secret key to the file: " + f + ".", ex);
        } catch (InvalidKeySpecException ex) {
            WebErrorLogger.log(Level.SEVERE, "InvalidKeySpecException is thrown "
                    + "while trying to write the secret key to the file: " + f + ".", ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                WebErrorLogger.log(Level.SEVERE, "IOException is thrown while "
                        + "trying to close the file output stream.", ex);
            }
        }
    }

    /**
     * Reads a DES secret key from the file.
     *
     * @param f The file.
     * @return A SecretKey that was read from the file.
     */
    public static SecretKey readTripleDESKey(File f) {
        SecretKey key = null;
        try {
            // Read the bytes from the keyfile
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            byte[] bytes = new byte[(int) f.length()];
            in.readFully(bytes);
            in.close();
            // Convert the bytes to a secret key
            DESKeySpec keyspec = new DESKeySpec(bytes);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DESede"); // could be DES 
            key = keyfactory.generateSecret(keyspec);

        } catch (InvalidKeySpecException ex) {
            WebErrorLogger.log(Level.SEVERE, "InvalidKeySpecException is thrown "
                    + "while trying to read the file: " + f + ".", ex);

        } catch (NoSuchAlgorithmException ex) {
            WebErrorLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown "
                    + "while trying to read the file: " + f + ".", ex);

        } catch (InvalidKeyException ex) {
            WebErrorLogger.log(Level.SEVERE, "InvalidKeyException is thrown "
                    + "while trying to read the file: " + f + ".", ex);

        } catch (IOException ex) {
            WebErrorLogger.log(Level.SEVERE, "IOException is thrown "
                    + "while trying to read the file: " + f + ".", ex);
        }
        return key;
    }
    
       /**
     * Reads a  secret key from the file.
     *
     * @param f The file.
     * @return A SecretKey that was read from the file.
     */
    public static SecretKey readKey(File f) {
        SecretKey key = null;
        try {
            // Read the bytes from the keyfile
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
            try {
                key = (SecretKey)in.readObject();
            } catch (ClassNotFoundException ex) {
                WebErrorLogger.log(Level.SEVERE, "Class import javax.crypto.SecretKey was not located", ex);
            }
            in.close();
        } catch (IOException ex) {
            WebErrorLogger.log(Level.SEVERE, "IOException is thrown "
                    + "while trying to read the file: " + f + ".", ex);
        }
        return key;
    }
    
     /**
     * Saves the SecretKey to the given file.
     *
     * @param key The secret key.
     * @param f The file.
     */
    public static void writeKey(SecretKey key, File f) {
        ObjectOutputStream out = null;
        try {
             out = new ObjectOutputStream(new FileOutputStream(f));
             out.writeObject(key);
             out.close();
        } catch (IOException ex) {
            WebErrorLogger.log(Level.SEVERE, "IOException is thrown while "
                    + "trying to write the secret key to the file: " + f + ".", ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                WebErrorLogger.log(Level.SEVERE, "IOException is thrown while "
                        + "trying to close the file output stream.", ex);
            }
        }
    }


    /**
     * Encrypts a string in hexadecimal format using the SHA-1 hash algorithm.
     * If SHA-1 does not exist, then the original string is returned. This is a
     * cryptographic hash function, designed not to be decrypted. Use this
     * concept to store non-recoverable passwords in a database
     *
     * @param orig The original string.
     * @return The encrypted string.
     */
    public static String encryptSHA1(String orig) {
        String alg = "SHA-1";
        byte[] bytes = null;
        try {
            // gets bytes from encryption algorithm
            bytes = MessageDigest.getInstance(alg).digest(orig.getBytes());
        } catch (NoSuchAlgorithmException e) {
            String msg = "The encryption algorithm " + alg
                    + " is not available or does not exist.";
            WebErrorLogger.log(Level.SEVERE, msg, e);
            return orig;
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
     * Encrypts a string in hexadecimal format using the SHA-256 hash algorithm.
     * If SHA-256 does not exist, then the original string is returned. This is
     * a cryptographic hash function, designed not to be decrypted. Use this
     * concept to store passwords in a database
     *
     * @param s The original string.
     * @return The encrypted string.
     */
    public static String hashString(String s) {
        byte[] digest = null;
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(s.getBytes("UTF-8"));
            digest = md.digest();
            for (int i = 0; i < digest.length; i++) {
                if ((0xff & digest[i]) < 0x10) {
                    hexString.append("0");
                }
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            WebErrorLogger.log(Level.SEVERE, "Error trying to use SHA-256", ex);
            Debug.println("Error trying to use SHA-256");
            return s;
        }
        return hexString.toString();
    }

    /**
     * Encrypts a string in hexadecimal format using a one-way hash algorithm.
     * If the algorithm passed to this method does not exist, then the original
     * string is returned. This is a cryptographic hash function, designed not
     * to be decrypted. Use this concept to store passwords in a database
     *
     * @param algorithm The one way hash algorithm to use to encrypt this
     * string.
     * @param s The original string.
     * @return The encrypted string.
     */
    public static String hashString(String algorithm, String s) {
        byte[] digest = null;
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            md.update(s.getBytes("UTF-8"));
            digest = md.digest();
            for (int i = 0; i < digest.length; i++) {
                if ((0xff & digest[i]) < 0x10) {
                    hexString.append("0");
                }
                hexString.append(Integer.toHexString(0xFF & digest[i]));
            }
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException ex) {
            WebErrorLogger.log(Level.SEVERE, algorithm + " does not exists. ", ex);
            Debug.println("Error trying to use  " + algorithm);
            return s;
        }
        return hexString.toString();
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        return new BigInteger(130, random).toString(32);
    }

    private static final String DEFAULT_ALGORITHM = "AES";

    /*
     Could write code to use AES algorithm here or write generic code 
      and pass in this algorithm name.
     */

    // * Please keep the main method.  Do not delete.
    public static void main(String[] args) {
        //The file that will  hold our key value
        File file = new File("C:\\Projects\\DoNotDeleteFile");
        SecretKey key;
        boolean generateNewKey = false;
        if (generateNewKey) {
            System.out.println("Generating a new key file");
            key = generateAESKey();
            writeKey(key, file);
        } else {
            System.out.println("Using the current key file");
            key = readKey(file);
        }

        // Now Encrypt and decrypt some strings that we use. 
        System.out.println("The following strings will be encrypted and decrypted using the ");
        System.out.println("AES encryption algorithm ");
        String encrypted = Encryption.keyEncryptAES(key, "SE2015");
        System.out.println("Encrypted MySQLUserName: " + encrypted);
        String decrypted = keyDecryptAES(key, encrypted);
        System.out.println("Decrypted MySQLUserName: " + decrypted);
        String encrypted2 = Encryption.keyEncryptAES(key, "Soft3ng2015");
        System.out.println("Encrypted password: " + encrypted2);
        String decrypted2 = keyDecryptAES(key, encrypted2);
        System.out.println("Decrypted password: " + decrypted2);
        String databaseURL = "hermes.bloomu.edu";
        String encrypted3 = Encryption.keyEncryptAES(key, databaseURL);
        System.out.println("Encrypted host: " + encrypted3);
        String decrypted3 = keyDecryptAES(key, encrypted3);
        System.out.println("Decrypted host: " + decrypted3);

        String encrypted4 = Encryption.keyEncryptAES(key, "ISISTools");
        System.out.println("Encrypted db name: " + encrypted4);
        String decrypted4 = keyDecryptAES(key, encrypted4);
        System.out.println("Decrypted db name: " + decrypted4);

        String encrypted5 = Encryption.keyEncryptAES(key, "buweatherproject@gmail.com");
        System.out.println("Encrypted email: " + encrypted5);
        String decrypted5 = keyDecryptAES(key, encrypted5);
        System.out.println("Decrypted email: " + decrypted5);

        String encrypted6 = Encryption.keyEncryptAES(key, "SoftwareEngineering2014");
        System.out.println("Encrypted email password: " + encrypted6);
        String decrypted6 = keyDecryptAES(key, encrypted6);
        System.out.println("Decrypted email password: " + decrypted6);

        System.out.println("\n\nOne way encryption algorithms");
        System.out.println("encryptSHA1(YourPassword)    is " + encryptSHA1("YourPassword"));
        System.out.println("encryptSHA-256(YourPassword)  is " + hashString("YourPassword"));
        System.out.println("encrypt(SHA-256,YourPassword)is " + hashString("SHA-256", "YourPassword"));

    }

}
