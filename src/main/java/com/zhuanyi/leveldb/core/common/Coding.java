package com.zhuanyi.leveldb.core.common;

import javafx.util.Pair;


public class Coding {

    private static final int B = 128;

    public static int encodeVarInt32(byte[] dst, int begin, int value) {
        // 这个编码方案是没法对负数编码的，确保value值是正数
        assert (value >= 0);

        if (value < (1 << 7)) {
            dst[begin++] = (byte) value;
        } else if (value < (1 << 14)) {
            dst[begin++] = (byte) (value | B);
            dst[begin++] = (byte) (value >> 7);
        } else if (value < (1 << 21)) {
            dst[begin++] = (byte) (value | B);
            dst[begin++] = (byte) ((value >> 7) | B);
            dst[begin++] = (byte) (value >> 14);
        } else if (value < (1 << 28)) {
            dst[begin++] = (byte) (value | B);
            dst[begin++] = (byte) ((value >> 7) | B);
            dst[begin++] = (byte) ((value >> 14) | B);
            dst[begin++] = (byte) (value >> 21);
        } else {
            dst[begin++] = (byte) (value | B);
            dst[begin++] = (byte) ((value >> 7) | B);
            dst[begin++] = (byte) ((value >> 14) | B);
            dst[begin++] = (byte) ((value >> 21) | B);
            dst[begin++] = 0;
        }
        return begin;
    }

    public static void encodeFixed64(byte[] dst, int begin, long value) {
        dst[begin++] = (byte) value;
        dst[begin++] = (byte) (value >> 8);
        dst[begin++] = (byte) (value >> 16);
        dst[begin++] = (byte) (value >> 24);
        dst[begin++] = (byte) (value >> 32);
        dst[begin++] = (byte) (value >> 40);
        dst[begin++] = (byte) (value >> 48);
        dst[begin] = (byte) (value >> 56);
    }

    public static void putFixed64(byte[] dst, int begin, long value) {
        byte[] buffer = new byte[8];
        encodeFixed64(buffer, 0, value);
        System.arraycopy(dst, begin, buffer, 0, 8);
    }

    public static Pair<Integer, Integer> getVarInt32Ptr(byte[] dst, int begin, int end) {
        if(begin < end) {
            if((dst[begin] & 128) == 0) {
                return new Pair<>(begin + 1, (int) dst[begin]);
            }
        }
        return getVarInt32PtrFallback(dst, begin, end);
    }

    public static Pair<Integer, Integer> getVarInt32PtrFallback(byte[] dst, int begin, int end) {
        int result = 0;
        for (int shift = 0; shift <= 28 && begin < end; shift += 7) {
            byte b = dst[begin++];
            if ((b & B) > 0) {
                result |= ((b & 127) << shift);
            } else {
                result |= (b << shift);
                return new Pair<>(begin, result);
            }
        }
        return null;
    }

    public static void main(String[] args) {
        byte[] dst = new byte[10];
        int end = encodeVarInt32(dst, 0, 1);
        System.out.println(getVarInt32PtrFallback(dst, 0, end));
        //System.out.println(Arrays.toString(dst));
    }
}
