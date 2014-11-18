/*
 *     Trident - A Multithreaded Server Alternative
 *     Copyright (C) 2014, The TridentSDK Team
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.tridentsdk.impl.encryption;

import javax.crypto.Cipher;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;

/**
 * Produces RSA encryption digests
 *
 * @author The TridentSDK Team
 */
public final class RSA {
    private RSA() {
    }

    /**
     * Generates a KeyPair with the specified amount of bits using an RSA cipher
     *
     * @param bits the bits in the final digest for the KeyPair
     * @return the KeyPair that has the specified bits and RSA cipher
     * @throws java.security.NoSuchAlgorithmException if RSA cipher is removed in the future
     */
    public static KeyPair generate(int bits) throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

        keyGen.initialize(bits);
        return keyGen.generateKeyPair();
    }

    /**
     * Encrypts the data into the cipher with the given key
     *
     * @param data the data to be ciphered
     * @param key  the key to use for initialization
     * @return the encrypted bytes
     * @throws Exception if something happens to occur
     */
    public static byte[] encrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    /**
     * Encrypts the data into the cipher with the given key, which is copied to a single element byte array
     *
     * @param data the data to be ciphered
     * @param key  the key to use for initialization
     * @return the encrypted bytes
     * @throws Exception if something happens to occur
     */
    public static byte[] encrypt(byte data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        return cipher.doFinal(new byte[]{data});
    }

    /**
     * Decrypts the data into the cipher with the given key
     *
     * @param data the data to be ciphered
     * @param key  the key to use for initialization
     * @return the decrypted bytes
     * @throws Exception if something happens to occur
     */
    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(data);
    }

    /**
     * Decrypts the data into the cipher with the given key, copied into a single element byte array
     *
     * @param data the data to be ciphered
     * @param key  the key to use for initialization
     * @return the decrypted bytes
     * @throws Exception if something happens to occur
     */
    public static byte[] decrypt(byte data, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, key);

        return cipher.doFinal(new byte[]{data});
    }
}
