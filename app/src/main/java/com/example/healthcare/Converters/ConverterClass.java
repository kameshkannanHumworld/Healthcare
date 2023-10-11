package com.example.healthcare.Converters;

import java.util.ArrayList;
import java.util.List;

public class ConverterClass {
    public static String byteToHexadecimal(byte[] data, boolean addSpace) {
        if (data == null || data.length < 1)
            return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
            if (addSpace)
                sb.append(" ");
        }
        return sb.toString().trim();
    }


    public static int hexadecimalToDecimal(String hexString) {
        int decimalValue = 0;

        for (int i = 0; i < hexString.length(); i++) {
            char hexChar = hexString.charAt(i);
            int hexDigit = Character.digit(hexChar, 16);
            decimalValue = decimalValue * 16 + hexDigit;
        }

        return decimalValue;
    }

    public static int byteArrayToDecimal(byte[] byteArray) {
        int decimalValue = 0;

        for (int i = 0; i < byteArray.length; i++) {
            int byteValue = byteArray[i] & 0xFF; // Convert byte to unsigned int
            decimalValue = decimalValue * 256 + byteValue; // Each byte contributes to a higher power of 256
        }

        return decimalValue;
    }


    public static List<String> getPairsFromHexString(byte[] data) {
        if (data == null || data.length < 1) {
            return null;
        }

        List<String> pairs = new ArrayList<>();

        for (int i = 0; i < data.length; i++) {
            String hex = Integer.toHexString(data[i] & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            pairs.add(hex);
        }

        return pairs;
    }


    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i+1), 16));
        }
        return byteArray;
    }

    public static String formatHexString(String hexString) {
        StringBuilder formattedString = new StringBuilder("(0x) ");

        for (int i = 0; i < hexString.length(); i += 2) {
            formattedString.append(hexString.substring(i, i + 2));
            if (i < hexString.length() - 2) {
                formattedString.append("-");
            }
        }

        return formattedString.toString();
    }


}
