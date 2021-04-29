package com.kaplacat.bluetooldownloader;

public class ElaUtils
{
    /**
     * Convert bytes to hex string
     * @param hashInBytes
     * @return [String] : data in a string list
     */
    public static String[] bytesToHex(byte[] hashInBytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : hashInBytes) {
            sb.append(String.format("%02x ", b));
        }
        return  sb.toString().toUpperCase().split(" ");
    }
}
