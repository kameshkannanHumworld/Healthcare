package com.example.healthcare.Converters;

import java.util.ArrayList;
import java.util.List;

public class ConverterClass {

    /*
        Convert ByteArray to Hexadecimal
            params1 - byte array
            params2 - boolean (need space or not)
            return string
    */
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


    /*
       Convert hexadecimal to decimal
           params1 - String Hexdecimal value
           return int
   */
    public static int hexadecimalToDecimal(String hexString) {
        int decimalValue = 0;

        for (int i = 0; i < hexString.length(); i++) {
            char hexChar = hexString.charAt(i);
            int hexDigit = Character.digit(hexChar, 16);
            decimalValue = decimalValue * 16 + hexDigit;
        }

        return decimalValue;
    }


    /*
       Convert ByteArray to decimal
           params1 - byte array
           return int
   */
    public static int byteArrayToDecimal(byte[] byteArray) {
        int decimalValue = 0;

        for (int i = 0; i < byteArray.length; i++) {
            int byteValue = byteArray[i] & 0xFF; // Convert byte to unsigned int
            decimalValue = decimalValue * 256 + byteValue; // Each byte contributes to a higher power of 256
        }

        return decimalValue;
    }


    /*
       get pairs from a Hexadecimal value(String)
           params1 - byte array
           return list
   */
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


    /*
       Convert hexadecimal(String) to ByteArray
           params1 - hexadecimal(String)
          return byteArray
   */
    public static byte[] hexStringToByteArray(String hexString) {
        int len = hexString.length();
        byte[] byteArray = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            byteArray[i / 2] = (byte) ((Character.digit(hexString.charAt(i), 16) << 4)
                    + Character.digit(hexString.charAt(i + 1), 16));
        }
        return byteArray;
    }


    /*
       Convert DateTime to hexadecimal(String)
           params1 - Date(String)
           params2 - Time(String)
          return String
   */
    public static String convertDateToHex(String currentDate, String currentTime) {
        // Split the input date into components
        String[] dateComponents = currentDate.split("-");
        String[] timeComponents = currentTime.split(":");

        // Convert each component to hexadecimal and concatenate
        StringBuilder hexDate = new StringBuilder();

        for (String component : dateComponents) {
            int intValue = Integer.parseInt(component);
            String hexValue = Integer.toHexString(intValue);
            if (hexValue.length() == 1) {
                hexValue = "0" + hexValue; // Add leading zero if needed
            }
            hexDate.append(hexValue).append(" ");
        }

        for (String component : timeComponents) {
            int intValue = Integer.parseInt(component);
            String hexValue = Integer.toHexString(intValue);
            if (hexValue.length() == 1) {
                hexValue = "0" + hexValue; // Add leading zero if needed
            }
            hexDate.append(hexValue).append(" ");
        }

        return hexDate.toString().trim();
    }

    /*
      get values from the  Hexadecimal pairs(String)
           params1 - hexadecimal(String)
          return list
   */
    public static List<String> getValuesFromPairs(String hexString) {
        List<String> values = new ArrayList<>();
        String[] hexValues = hexString.split(" ");

        for (int i = 0; i < hexValues.length; i += 2) {
            String value1 = hexValues[i];
            String value2 = hexValues[i + 1];

            values.add(value1);
            values.add(value2);
        }

        return values;
    }


    /*
      decode the Hexadecimal DateTime
           params1 - hexadecimal(String)
          return String
   */
    public static String decodeHexDateTime(String hexDateTime) {
        List<Integer> decimalValues = new ArrayList<>();
        String[] hexValues = hexDateTime.split(" ");

        for (String hexValue : hexValues) {
            int decimalValue = Integer.parseInt(hexValue, 16);
            decimalValues.add(decimalValue);
        }

        int day = decimalValues.get(0);
        int month = decimalValues.get(1);
        int year = decimalValues.get(2);
        int hour = decimalValues.get(3);
        int minute = decimalValues.get(4);
        int second = decimalValues.get(5);

        String dateAndTime = day + "/" + month + "/" + year + "-" + hour + ":" + minute + ":" + second;

        return dateAndTime;
    }



    /*
        reverse the  Hexadecimal(String)
           params1 - hexadecimal(String)
           return list
   */
    public static List<String> reverseHexaDecimal(String hexString) {
        if (hexString.length() % 2 != 0) {
            hexString = "0" + hexString; // Add leading zero if length is odd
        }

        List<String> parts = new ArrayList<>();
        parts.add(hexString.substring(0, hexString.length() / 2));
        parts.add(hexString.substring(hexString.length() / 2));

        return parts;
    }

}
