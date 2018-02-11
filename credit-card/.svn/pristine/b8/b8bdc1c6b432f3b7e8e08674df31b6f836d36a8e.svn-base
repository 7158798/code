/**
 *
 */
package com.pay.card.utils;

import java.util.Random;

import com.pay.card.utils.crypt.Skip32;
import com.pay.card.utils.crypt.SkipJack;

/**
 * @author qiaohui
 *
 */
public final class UuidUtils {

    private static volatile SkipJack skipJack;

    private static volatile Skip32 skip32;

    public static int getIntId(String uuid) {
        return skip32().decodeBase64Int(uuid);
    }

    /*
     * 1679615=ZZZZ
     */
    public static String GetInvitationCode(int id) {
        String[] baseString = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G",
                "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
        StringBuilder resultStr = new StringBuilder();
        // 1610/16=100……10(A)；
        // 100 /16= 6……4；
        // 6 /16= 0……6；
        int r = id;
        int tmp = r % (baseString.length);
        while ((r / (baseString.length)) > 0) {
            resultStr.insert(0, baseString[tmp]);
            r = r / (baseString.length);
            tmp = r % (baseString.length);
        }
        if (r > 0) {
            resultStr.insert(0, baseString[tmp]);
        }
        while (resultStr.length() < 4) {
            resultStr.insert(0, "0");
        }
        return resultStr.toString();
    }

    public static int GetInvitationId(String code) {
        String baseString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        // 用竖式计算：2AF5换算成10进制:
        // 第0位：5 * 16^0 = 5
        // 第1位：F * 16^1 = 240
        // 第2位：A * 16^2 = 2560
        // 第3位：2 * 16^3 = 8192 ＋
        int result = 0;
        char[] c = code.toCharArray();
        for (int i = 0; i < c.length; i++) {
            result += baseString.indexOf(String.valueOf(c[i]).toUpperCase()) * Math.pow(36, c.length - i - 1);
        }
        return result;
    }

    public static long getLongId(String uuid) {
        return aes().decodeBase64Long(uuid);
    }

    public static String GetRandomString(int Len) {

        String[] baseString = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G",
                "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z" };
        Random random = new Random();
        int length = baseString.length;
        String randomString = "";
        for (int i = 0; i < length; i++) {
            randomString += baseString[random.nextInt(length)];
        }
        random = new Random(System.currentTimeMillis());
        String resultStr = "";
        for (int i = 0; i < Len; i++) {
            resultStr += randomString.charAt(random.nextInt(randomString.length() - 1));
        }
        return resultStr;
    }

    public static final String getUuid(int id) {
        return skip32().encodeBase64URLSafeStringInt(id);
    }

    public static final String getUuid(long id) {
        return aes().encodeBase64URLSafeStringLong(id);
    }

    public static void main(String args[]) {
        for (int i = 0; i < 300; i++) {
            System.out.println(GetRandomString(10));
        }
    }

    private static SkipJack aes() {
        if (skipJack == null) {
            synchronized (UuidUtils.class) {
                if (skipJack == null) {
                    skipJack = SkipJack.of(new int[] { 'a', '1', 'b', '2', 'c', '3', 'd', '4', 'e', '#' }); // TODO
                }
            }
        }
        return skipJack;
    }

    private static Skip32 skip32() {
        if (skip32 == null) {
            synchronized (UuidUtils.class) {
                if (skip32 == null) {
                    skip32 = Skip32.of(new byte[] { 'a', '1', 'b', '2', 'c', '3', 'd', '4', 'e', '#' }); // TODO
                }
            }
        }
        return skip32;
    }
}
