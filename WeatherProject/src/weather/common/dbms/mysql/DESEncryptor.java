
package weather.common.dbms.mysql;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.logging.Level;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import weather.common.utilities.WeatherException;
import weather.common.utilities.WeatherLogger;

/**
 * Encrypts and decrypts a string into a string. Generates a SecretKey, writes
 * it to a file and reads it from that file.
 *
 * @author Ioulia Lee (2010)
 * @version Spring 2010
 */
public class DESEncryptor {

   /**
    * Encrypts the given string using the secret key.
    *
    * @param key The key.
    * @param str The string to encrypt.
    * @return A string that represents the encrypted string.
    */
   public static String encrypt(SecretKey key, String str) {
        try {
             Cipher ecipher = Cipher.getInstance("DES");
            ecipher.init(Cipher.ENCRYPT_MODE, key);
            // Encode the string into bytes using utf-8
            byte[] utf8 = str.getBytes("UTF8");

            // Encrypt
            byte[] enc = ecipher.doFinal(utf8);

            // Encode bytes to base64 to get a string
            return new sun.misc.BASE64Encoder().encode(enc);
        } catch (IllegalBlockSizeException ex) {
            WeatherLogger.log(Level.SEVERE, "IllegalBlockSizeException is thrown while "
                      +"trying to encrypt the database access password.", ex);
            new WeatherException(0100, ex, "Unable to encrypt the database "+
                    "access password.").show();
        } catch (InvalidKeyException ex) {
            WeatherLogger.log(Level.SEVERE, "InvalidKeyException is thrown while "
                      +"trying to encrypt the database access password.", ex);
            new WeatherException(0101, ex, "Unable to encrypt the database "+
                    "access password.").show();
        } catch (NoSuchAlgorithmException ex) {
            WeatherLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                      +"trying to encrypt the database access password.", ex);
            new WeatherException(0102, ex, "Unable to encrypt the database "+
                    "access password.").show();
        } catch (NoSuchPaddingException ex) {
            WeatherLogger.log(Level.SEVERE, "NoSuchPaddingException is thrown while "
                      +"trying to encrypt the database access password.", ex);
            new WeatherException(0103, ex, "Unable to encrypt the database "+
                    "access password.").show();
        } catch (javax.crypto.BadPaddingException ex) {
            WeatherLogger.log(Level.SEVERE, "javax.crypto.BadPaddingException is thrown while "
                      +"trying to encrypt the database access password.", ex);
            new WeatherException(0104, ex, "Unable to encrypt the database "+
                    "access password.").show();
        } catch (java.io.IOException ex) {
            WeatherLogger.log(Level.SEVERE, "java.io.IOException is thrown while "
                      +"trying to encrypt the database access password.", ex);
            new WeatherException(0105, ex, "Unable to encrypt the database "+
                    "access password.").show();
        }
        return null;
    }

    /**
     * Decrypts the given string with the secret key.
     *
     * @param key The key.
     * @param str The string to decrypt.
     * @return A string that represents the decrypted string.
     */
    public static String decrypt(SecretKey key, String str) {
        try {
             Cipher dcipher = Cipher.getInstance("DES");
            dcipher.init(Cipher.DECRYPT_MODE, key);
            // Decode base64 to get bytes
            byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

            // Decrypt
            byte[] utf8 = dcipher.doFinal(dec);

            // Decode using utf-8
            return new String(utf8, "UTF8");
        } catch (InvalidKeyException ex) {
            WeatherLogger.log(Level.SEVERE, "InvalidKeyException is thrown while "
                      +"trying to decrypt the database access password.", ex);
            new WeatherException(0101,true,ex, "Unable to connect to the database "+
                    "due to an internal error. The program will terminate.").show();
        } catch (NoSuchAlgorithmException ex) {
            WeatherLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                      +"trying to decrypt the database access password.", ex);
            new WeatherException(0102,true,ex, "Unable to connect to the database "+
                    "due to an internal error. The program will terminate.").show();
        } catch (NoSuchPaddingException ex) {
            WeatherLogger.log(Level.SEVERE, "NoSuchPaddingException is thrown while "
                      +"trying to decrypt the database access password.", ex);
            new WeatherException(0103,true,ex, "Unable to connect to the database "+
                    "due to an internal error. The program will terminate.").show();
        } catch (javax.crypto.BadPaddingException ex) {
            WeatherLogger.log(Level.SEVERE, "javax.crypto.BadPaddingException is thrown while "
                      +"trying to decrypt the database access password.", ex);
            new WeatherException(0104,true,ex, "Unable to connect to the database "+
                    "due to an internal error. The program will terminate.").show();
        } catch (IllegalBlockSizeException ex) {
            WeatherLogger.log(Level.SEVERE, "IllegalBlockSizeException is thrown while "
                      +"trying to decrypt the database access password.", ex);
            new WeatherException(0100,true,ex, "Unable to connect to the database "+
                    "due to an internal error. The program will terminate.").show();
        } catch (java.io.IOException ex) {
            WeatherLogger.log(Level.SEVERE, "java.io.IOException is thrown while "
                      +"trying to decrypt the database access password.", ex);
            new WeatherException(0105,true,ex, "Unable to connect to the database "+
                    "due to an internal error. The program will terminate.").show();
        }
        return null;
    }

  /**
   * Generates a secret DES encryption/decryption key.
   *
   * @return The secret key.
   */
  public static SecretKey generateKey(){
      KeyGenerator keygenerator = null;
      try {
          // Get a key generator for Triple DES
          keygenerator = KeyGenerator.getInstance("DES");

      } catch (NoSuchAlgorithmException ex) {
          WeatherLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown while "
                    +"trying to generate a secret key.", ex);
          new WeatherException(0102, ex, "Unable to generate a secret key.").show();
      }

     return keygenerator.generateKey();
  }

  /**
   * Saves the given SecretKey to the given file.
   *
   * @param key The secret key.
   * @param f The file.
   */
  public static void writeKey(SecretKey key, File f){
      FileOutputStream out = null;
      try {
           SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES");
           DESKeySpec keyspec = (DESKeySpec) keyfactory.getKeySpec(key,
           DESKeySpec.class);
           byte[] bytes = keyspec.getKey();
           out = new FileOutputStream(f);
           out.write(bytes);
           out.close();
      } catch (IOException ex) {
           WeatherLogger.log(Level.SEVERE, "IOException is thrown while "+
                "trying to write the secret key to the file: "+f+".", ex);
           new WeatherException(0105, ex, "Cannot write the secret key "+
                 "to the file: "+f+".").show();
      } catch (NoSuchAlgorithmException ex) {
           WeatherLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown "+
                 "while trying to write the secret key to the file: "+f+".", ex);
           new WeatherException(0102, ex, "Cannot write the secret key "+
                 "to the file: "+f+".").show();
      } catch (InvalidKeySpecException ex) {
           WeatherLogger.log(Level.SEVERE, "InvalidKeySpecException is thrown "+
                 "while trying to write the secret key to the file: "+f+".", ex);
           new WeatherException(0106, ex, "Cannot write the secret key "+
                  "to the file: "+f+".").show();
      } finally {
           try {
                out.close();
           } catch (IOException ex) {
                WeatherLogger.log(Level.SEVERE, "IOException is thrown while "+
                     "trying to close the file output stream.", ex);
                new WeatherException(0107, ex, "Unable to close the file "+
                     "output stream.").show();
           }
      }
  }

  /**
   * Reads a DES secret key from the file.
   *
   * @param f The file.
   * @return A SecretKey that was read from the file.
   */
  public static SecretKey readKey(File f) {
      SecretKey key = null;
        try {
            // Read the bytes from the keyfile
            DataInputStream in = new DataInputStream(new FileInputStream(f));
            byte[] bytes = new byte[(int) f.length()];
            in.readFully(bytes);
            in.close();
            // Convert the bytes to a secret key
            DESKeySpec keyspec = new DESKeySpec(bytes);
            SecretKeyFactory keyfactory = SecretKeyFactory.getInstance("DES");
            key = keyfactory.generateSecret(keyspec);

        } catch (InvalidKeySpecException ex) {
            WeatherLogger.log(Level.SEVERE, "InvalidKeySpecException is thrown "+
                        "while trying to read the file: "+f+".", ex);
            new WeatherException(0106, true, ex, "Cannot connect to the database "+
                    "due to an internal problem.").show();
        } catch (NoSuchAlgorithmException ex) {
            WeatherLogger.log(Level.SEVERE, "NoSuchAlgorithmException is thrown "+
                        "while trying to read the file: "+f+".", ex);
            new WeatherException(0102, true, ex, "Cannot connect to the database "+
                    "due to an internal problem.").show();
        } catch (InvalidKeyException ex) {
            WeatherLogger.log(Level.SEVERE, "InvalidKeyException is thrown "+
                        "while trying to read the file: "+f+".", ex);
            new WeatherException(0101, true, ex, "Cannot connect to the database "+
                    "due to an internal problem.").show();
        } catch (IOException ex) {
            WeatherLogger.log(Level.SEVERE, "IOException is thrown "+
                        "while trying to read the file: "+f+".", ex);
            new WeatherException(0105, true, ex, "Cannot connect to the database "+
                    "due to an internal problem.").show();
        }
      return key;
  }

  
  // * Please keep the main method.  Do not delete.
  
    public static void main(String[] args) {
        
        //SecretKey key = generateKey();
        File file = new File("DoNotDeleteFile.txt");
        //writeKey(key, file);
        SecretKey key = readKey(file);
        // Encrypt
        String encrypted = encrypt(key, "project");
        System.out.println("Encrypted string: " + encrypted);
        String decrypted = decrypt(key, encrypted);
        System.out.println("Decrypted string: " + decrypted);
        
        
    }
     
}
