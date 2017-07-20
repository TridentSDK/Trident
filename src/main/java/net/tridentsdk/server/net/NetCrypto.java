/*
 * Trident - A Multithreaded Server Alternative
 * Copyright 2017 The TridentSDK Team
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
package net.tridentsdk.server.net;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import net.tridentsdk.server.packet.login.LoginOutEncryptionRequest;

import javax.annotation.concurrent.ThreadSafe;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.util.Arrays;
import java.util.function.Function;

import static net.tridentsdk.server.net.NetData.arr;

/**
 * This class is a handler for packet encryption and
 * decryption, and holds the keys and security accessors to
 * the client crypt.
 */
@ThreadSafe
public class NetCrypto {
    /**
     * Length of the shared token used for verification
     */
    private static final int TOKEN_LEN = 4;
    /**
     * The bits used in generation of a key pair
     */
    private static final int KEY_PAIR_BITS = 1024;
    /**
     * The algorithm used to generate the key pair
     */
    private static final String CIPHER_NAME = "AES/CFB8/NoPadding";
    /**
     * The secret key spec algorithm
     */
    private static final String SECRET_ALGO = "AES";
    /**
     * The keypair algorithm
     */
    private static final String KEY_PAIR_ALGO = "RSA";
    /**
     * Secure random for generating the tokens
     *
     * <p>Static initializer seeds the random before it is
     * used for any clients.</p>
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    static {
        RANDOM.nextBytes(new byte[TOKEN_LEN]);
    }

    /**
     * The keypair used to share encryption details
     */
    private final KeyPair kp;
    /**
     * The verification token
     */
    private final byte[] token;
    /**
     * The encryption cipher
     */
    private final ThreadLocal<Cipher> encrypt = new ThreadLocal<>();
    /**
     * The decryption cipher
     */
    private final ThreadLocal<Cipher> decrypt = new ThreadLocal<>();
    /**
     * Whether or not the crypto is ready
     */
    private volatile Function<Integer, Cipher> cipherInit;
    /**
     * Whether crypto is enabled
     */
    @Getter
    private volatile boolean cryptoEnabled;

    /**
     * Constructs a new crypto module.
     */
    public NetCrypto() {
        KeyPair localPair;
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance(KEY_PAIR_ALGO);
            generator.initialize(KEY_PAIR_BITS);
            localPair = generator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        this.kp = localPair;

        this.token = new byte[TOKEN_LEN];
        RANDOM.nextBytes(this.token);
    }

    /**
     * Obtains a new instance of an encryption request
     * packet with its data filled in by this crypto
     * module.
     *
     * @return the new packet
     */
    public LoginOutEncryptionRequest reqCrypto() {
        return new LoginOutEncryptionRequest(this.kp.getPublic().getEncoded(), this.token);
    }

    /**
     * Obtains the keypair associated with this instance of
     * the crypto module.
     *
     * @return the keypair
     */
    public KeyPair kp() {
        return this.kp;
    }

    /**
     * Begins encryption checking.
     *
     * @param encryptedSecret the encrypted shared secret
     * @param encryptedToken the encrypted token
     * @return the decrypted secret, or {@code null} if this
     * operation did not complete successfully
     */
    public byte[] begin(byte[] encryptedSecret, byte[] encryptedToken) {
        try {
            Cipher keyPairCipher = Cipher.getInstance(KEY_PAIR_ALGO);
            keyPairCipher.init(Cipher.DECRYPT_MODE, this.kp.getPrivate());

            byte[] decryptedSecret = keyPairCipher.doFinal(encryptedSecret);
            byte[] decryptedToken = keyPairCipher.doFinal(encryptedToken);

            if (Arrays.equals(decryptedToken, this.token)) {
                SecretKey sharedSecret = new SecretKeySpec(decryptedSecret, SECRET_ALGO);
                IvParameterSpec iv = new IvParameterSpec(sharedSecret.getEncoded());

                this.cipherInit = mode -> {
                    try {
                        if (mode == Cipher.DECRYPT_MODE) {
                            Cipher instance = this.decrypt.get();
                            if (instance == null) {
                                instance = Cipher.getInstance(CIPHER_NAME);
                                instance.init(mode, sharedSecret, iv);
                                this.decrypt.set(instance);
                            }

                            return instance;
                        } else {
                            Cipher instance = this.encrypt.get();
                            if (instance == null) {
                                instance = Cipher.getInstance(CIPHER_NAME);
                                instance.init(mode, sharedSecret, iv);
                                this.encrypt.set(instance);
                            }

                            return instance;
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                };
                cryptoEnabled = true;
                return decryptedSecret;
            }
            // rofl @ 6 exceptions
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | BadPaddingException | IllegalBlockSizeException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    /**
     * Encrypts the given byte buffer with the information
     * provided by this crypto module.
     *
     * @param buf the buffer
     * @param dest the destination
     */
    public void encrypt(ByteBuf buf, ByteBuf dest) {
        Function<Integer, Cipher> init = this.cipherInit;
        if (init == null) {
            dest.writeBytes(buf);
            return;
        }

        byte[] bytes = arr(buf);
        Cipher cipher = init.apply(Cipher.ENCRYPT_MODE);
        dest.writeBytes(cipher.update(bytes));
    }

    /**
     * Decrypts the given byte buffer with the information
     * provided by this crypto module.
     *
     * @param buf the buffer
     * @param dest the destination
     */
    public void decrypt(ByteBuf buf, ByteBuf dest) {
        Function<Integer, Cipher> init = this.cipherInit;
        if (init == null) {
            dest.writeBytes(buf);
            return;
        }

        byte[] bytes = arr(buf);
        Cipher cipher = init.apply(Cipher.DECRYPT_MODE);
        dest.writeBytes(cipher.update(bytes));
    }
}