/**
 *
 */
package com.pay.card.utils.crypt;

import java.nio.ByteBuffer;

import org.apache.commons.codec.binary.Base64;

/**
 *
 * SKIPJACK - 64bit Cookbook version, 80bit key.
 *
 * @author qiaohui
 *
 */
public class SkipJack {

    private final static long LONG_BYTES = 8;

    public static long decrypt(long message, int[] key) {
        long state = message;

        int stepcounter = 31;// k starts at 31
        while (stepcounter > 23) { // 8 rounds of rule B.
            state = roundBprime(stepcounter, key, state);
            stepcounter--;
        }
        while (stepcounter > 15) { // 8 rounds of rule A.
            state = roundAprime(stepcounter, key, state);
            stepcounter--;
        }

        // do that same thing again. Thus 32 rounds.
        while (stepcounter > 7) {
            state = roundBprime(stepcounter, key, state);
            stepcounter--;
        }
        while (stepcounter > -1) {
            state = roundAprime(stepcounter, key, state);
            stepcounter--;
        }

        return state;
    }

    public static long encrypt(long message, int[] key) {
        long state = message;

        int stepcounter = 0;// k starts at 0
        while (stepcounter < 8) { // 8 rounds of rule A.
            state = roundA(stepcounter, key, state);
            stepcounter++;
        }
        while (stepcounter < 16) { // 8 rounds of rule B.
            state = roundB(stepcounter, key, state);
            stepcounter++;
        }

        // do that same thing again. Thus 32 rounds.
        while (stepcounter < 24) {
            state = roundA(stepcounter, key, state);
            stepcounter++;
        }
        while (stepcounter < 32) {
            state = roundB(stepcounter, key, state);
            stepcounter++;
        }

        return state;
    }

    public static void main(String[] args) {
        int[] key = { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 };
        SkipJack helper = SkipJack.of(key);
        for (long i = 1; i <= 100; i++) {
            long t1 = System.nanoTime();
            String codec = helper.encodeBase64URLSafeStringLong(i);
            long t2 = System.nanoTime();
            long decoded = helper.decodeBase64Long(codec);
            long t3 = System.nanoTime();
            System.out.println(decoded + "->[" + codec + "], encode cost:" + (t2 - t1) + ", decode cost:" + (t3 - t2));
        }
    }

    public static final SkipJack of(int[] secretStr) {
        return new SkipJack(secretStr);
    }

    private static long G(int step, int[] key, long w) {
        int g_1, g_2, g_3, g_4, g_5, g_6, cv0, cv1, cv2, cv3;
        g_1 = g_2 = g_3 = g_4 = g_5 = g_6 = cv0 = cv1 = cv2 = cv3 = 0;

        g_1 = (int) (w >>> 8);
        g_2 = (int) (w & 0xFF);

        cv0 = key[(step * 4) % 10];
        cv1 = key[(step * 4 + 1) % 10];
        cv2 = key[(step * 4 + 2) % 10];
        cv3 = key[(step * 4 + 3) % 10];

        g_3 = (SkipConstants.F[g_2 ^ cv0] ^ g_1);
        g_4 = (SkipConstants.F[g_3 ^ cv1] ^ g_2);
        g_5 = (SkipConstants.F[g_4 ^ cv2] ^ g_3);
        g_6 = (SkipConstants.F[g_5 ^ cv3] ^ g_4);

        return ((long) g_5 << 8) | g_6;
    }

    private static long Gprime(int step, int[] key, long w) {
        int g_1, g_2, g_3, g_4, g_5, g_6, cv0, cv1, cv2, cv3;
        g_1 = g_2 = g_3 = g_4 = g_5 = g_6 = cv0 = cv1 = cv2 = cv3 = 0;

        g_1 = (int) (w & 0xFF);
        g_2 = (int) (w >>> 8);

        cv0 = key[(step * 4 + 3) % 10];
        cv1 = key[(step * 4 + 2) % 10];
        cv2 = key[(step * 4 + 1) % 10];
        cv3 = key[(step * 4) % 10];

        g_3 = (SkipConstants.F[g_2 ^ cv0] ^ g_1);
        g_4 = (SkipConstants.F[g_3 ^ cv1] ^ g_2);
        g_5 = (SkipConstants.F[g_4 ^ cv2] ^ g_3);
        g_6 = (SkipConstants.F[g_5 ^ cv3] ^ g_4);

        return ((long) g_6 << 8) | g_5;
    }

    private static long roundA(int step, int[] key, long block) {
        long w_1i, w_2i, w_3i, w_4i;
        long w_1o, w_2o, w_3o, w_4o;
        w_1i = w_2i = w_3i = w_4i = w_1o = w_2o = w_3o = w_4o = 0L;

        w_1i = (block >>> 48);
        w_2i = (block >> 32) & 0xFFFFL;
        w_3i = (block >> 16) & 0xFFFFL;
        w_4i = (block & 0xFFFFL);

        w_1o = G(step, key, w_1i) ^ w_4i ^ (step + 1);
        w_2o = G(step, key, w_1i);
        w_3o = w_2i;
        w_4o = w_3i;

        long ret = w_1o << 48 | w_2o << 32 | w_3o << 16 | w_4o;
        return ret;
    }

    private static long roundAprime(int step, int[] key, long block) {
        long w_1i, w_2i, w_3i, w_4i;
        long w_1o, w_2o, w_3o, w_4o;
        w_1i = w_2i = w_3i = w_4i = w_1o = w_2o = w_3o = w_4o = 0L;

        w_1i = (block >>> 48);
        w_2i = (block >> 32) & 0xFFFFL;
        w_3i = (block >> 16) & 0xFFFFL;
        w_4i = (block & 0xFFFFL);

        w_1o = Gprime(step, key, w_2i);
        w_2o = w_3i;
        w_3o = w_4i;
        w_4o = w_1i ^ w_2i ^ (step + 1);

        long ret = w_1o << 48 | w_2o << 32 | w_3o << 16 | w_4o;
        return ret;
    }

    private static long roundB(int step, int[] key, long block) {
        long w_1i, w_2i, w_3i, w_4i;
        long w_1o, w_2o, w_3o, w_4o;
        w_1i = w_2i = w_3i = w_4i = w_1o = w_2o = w_3o = w_4o = 0;

        w_1i = (block >>> 48);
        w_2i = ((block >> 32) & 0xFFFFL);
        w_3i = ((block >> 16) & 0xFFFFL);
        w_4i = (block & 0xFFFFL);

        w_1o = w_4i;
        w_2o = G(step, key, w_1i);
        w_3o = w_1i ^ w_2i ^ (step + 1);
        w_4o = w_3i;

        long ret = w_1o << 48 | w_2o << 32 | w_3o << 16 | w_4o;
        return ret;
    }

    private static long roundBprime(int step, int[] key, long block) {
        long w_1i, w_2i, w_3i, w_4i;
        long w_1o, w_2o, w_3o, w_4o;
        w_1i = w_2i = w_3i = w_4i = w_1o = w_2o = w_3o = w_4o = 0;

        w_1i = (block >>> 48);
        w_2i = ((block >> 32) & 0xFFFFL);
        w_3i = ((block >> 16) & 0xFFFFL);
        w_4i = (block & 0xFFFFL);

        w_1o = Gprime(step, key, w_2i);
        w_2o = Gprime(step, key, w_2i) ^ w_3i ^ (step + 1);
        w_3o = w_4i;
        w_4o = w_1i;

        long ret = w_1o << 48 | w_2o << 32 | w_3o << 16 | w_4o;
        return ret;
    }

    private final int[] secretKey;

    private SkipJack(int[] secretStr) {
        secretKey = secretStr;
    }

    public long decodeBase64Long(String value) {
        byte[] encrypted = Base64.decodeBase64(value);
        if ((encrypted == null) || (encrypted.length != LONG_BYTES)) {
            throw new RuntimeException("fail to decode: " + value);
        }
        long encoded = ByteBuffer.wrap(encrypted).getLong();
        return decrypt(encoded, secretKey);
    }

    public String encodeBase64URLSafeStringLong(long value) {
        long encoded = encrypt(value, secretKey);
        byte[] rawData = ByteBuffer.allocate(8).putLong(encoded).array();
        return Base64.encodeBase64URLSafeString(rawData);
    }

}
