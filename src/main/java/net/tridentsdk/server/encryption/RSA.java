/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2014 The TridentSDK Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.tridentsdk.server.encryption;

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
     * @throws NoSuchAlgorithmException if RSA cipher is removed in the future
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

        return cipher.doFinal(new byte[] { data });
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

        return cipher.doFinal(new byte[] { data });
    }
}
