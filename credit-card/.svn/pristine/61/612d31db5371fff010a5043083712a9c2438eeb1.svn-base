package com.pay.card.utils.crypt;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;

/**
 * 基于base64的des加密解密
 *
 */
public final class Skip32 {

    public static final int KEY_LENGTH = 10;

    private final static long INT_BYTES = 4;

    /**
     * Decrypts the provided value using the specified key
     *
     * The key should be a byte array of 10 elements.
     *
     * @param value
     * @param key
     * @return The decrypted value
     */
    public static int decrypt(int value, byte[] key) {
        int[] buf = new int[4];

        buf[0] = ((value >> 24) & 0xff);
        buf[1] = ((value >> 16) & 0xff);
        buf[2] = ((value >> 8) & 0xff);
        buf[3] = ((value >> 0) & 0xff);

        skip32(key, buf, false);

        int out = ((buf[0]) << 24) | ((buf[1]) << 16) | ((buf[2]) << 8) | (buf[3]);

        return out;
    }

    /**
     * Encrypts the provided value using the specified key
     *
     * The key should be a byte array of 10 elements.
     *
     * @param value
     * @param key
     * @return The encrypted value
     */
    public static int encrypt(int value, byte[] key) {
        int[] buf = new int[4];
        buf[0] = ((value >> 24) & 0xff);
        buf[1] = ((value >> 16) & 0xff);
        buf[2] = ((value >> 8) & 0xff);
        buf[3] = ((value >> 0) & 0xff);

        skip32(key, buf, true);

        int out = ((buf[0]) << 24) | ((buf[1]) << 16) | ((buf[2]) << 8) | (buf[3]);

        return out;
    }

    public static void main(String[] args) {
        byte[] key = { 0x11, 0x22, 0x33, 0x44, 0x55, 0x66, 0x77, (byte) 0x88, (byte) 0x99, (byte) 0xAA };
        Skip32 helper = Skip32.of(key);
        for (int i = 1; i <= 100; i++) {
            long t1 = System.nanoTime();
            String codec = helper.encodeBase64URLSafeStringInt(i);
            long t2 = System.nanoTime();
            int decoded = helper.decodeBase64Int(codec);
            long t3 = System.nanoTime();
            System.out.println(decoded + "->[" + codec + "], encode cost:" + (t2 - t1) + ", decode cost:" + (t3 - t2));
        }
    }

    public static final Skip32 of(byte[] secretStr) {
        return new Skip32(secretStr);
    }

    /**
     * Applies the SKIP32 function on the provided value stored in buf and
     * modifies it inplace. This is a low-level function used by the encrypt and
     * decrypt functions.
     *
     * @param key
     * @param buf
     * @param encrypt
     */
    public static void skip32(byte[] key, int[] buf, boolean encrypt) {
        int k; /* round number */
        int i; /* round counter */
        int kstep;
        int wl, wr;

        /* sort out direction */
        if (encrypt) {
            kstep = 1;
            k = 0;
        } else {
            kstep = -1;
            k = 23;
        }

        /* pack into words */
        wl = (buf[0] << 8) + buf[1];
        wr = (buf[2] << 8) + buf[3];

        /* 24 feistel rounds, doubled up */
        for (i = 0; i < 24 / 2; ++i) {
            wr ^= g(key, k, wl) ^ k;
            k += kstep;
            wl ^= g(key, k, wr) ^ k;
            k += kstep;
        }

        /* implicitly swap halves while unpacking */
        buf[0] = (wr >> 8);
        buf[1] = (wr & 0xFF);
        buf[2] = (wl >> 8);
        buf[3] = (wl & 0xFF);
    }

    private static int g(byte[] key, int k, int w) {
        int g1, g2, g3, g4, g5, g6;

        g1 = w >> 8;
        g2 = w & 0xff;

        g3 = SkipConstants.F[g2 ^ (key[(4 * k) % 10] & 0xFF)] ^ g1;
        g4 = SkipConstants.F[g3 ^ (key[(4 * k + 1) % 10] & 0xFF)] ^ g2;
        g5 = SkipConstants.F[g4 ^ (key[(4 * k + 2) % 10] & 0xFF)] ^ g3;
        g6 = SkipConstants.F[g5 ^ (key[(4 * k + 3) % 10] & 0xFF)] ^ g4;

        return (g5 << 8) + g6;
    }

    private final byte[] secretKey;

    private Skip32(byte[] secretStr) {
        secretKey = secretStr;
    }

    public int decodeBase64Int(String value) {
        byte[] encrypted = Base64.decodeBase64(value);
        if ((encrypted == null) || (encrypted.length != INT_BYTES)) {
            throw new RuntimeException("fail to decode: " + value);
        }
        int encoded = ByteBuffer.wrap(encrypted).getInt();
        return decrypt(encoded, secretKey);
    }

    public String encodeBase64URLSafeStringInt(int value) {
        int encoded = encrypt(value, secretKey);
        byte[] rawData = ByteBuffer.allocate(4).putInt(encoded).array();
        return Base64.encodeBase64URLSafeString(rawData);
    }
}
