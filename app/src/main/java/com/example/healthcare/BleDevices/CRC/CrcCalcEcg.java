package com.example.healthcare.BleDevices.CRC;

public class CrcCalcEcg {

    public static byte crc8_compute(byte[] pdata, int data_size, byte crc_in) {
        byte crc_poly = 0x07;
        byte data_tmp = 0;
        for (int i = 0; i < data_size; i++) {
            data_tmp = pdata[i];
            crc_in ^= (data_tmp << 0);
            for (int cnt = 0; cnt < 8; cnt++) {
                if ((crc_in & 0x80) != 0) {
                    crc_in = (byte) ((crc_in << 1) ^ crc_poly);
                } else {
                    crc_in = (byte) (crc_in << 1);
                }
            }
        }
        return crc_in;
    }
}
