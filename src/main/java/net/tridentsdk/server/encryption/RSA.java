/*
 * Copyright (c) 2014, TridentSDK Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of TridentSDK nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
