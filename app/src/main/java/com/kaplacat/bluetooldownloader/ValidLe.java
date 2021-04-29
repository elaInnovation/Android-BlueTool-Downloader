package com.kaplacat.bluetooldownloader;

import android.bluetooth.le.ScanRecord;

class ValidLe
{
    private static String CARAC_T_1 = "6e";
    private static String CARAC_T_2 = "2a";
    private static int FIRST_CARAC = 15;
    private static int SECOND_CARAC = 18;

    /**
     * Check wich ELA tags format is
     * @param scanLe : ScanRecord return by the BLE callback
     * @return Type of the tag
     */
    static eTagType isElaLe(ScanRecord scanLe)
    {
        if(scanLe == null) {return eTagType.NULL;}
        if(scanLe.getDeviceName() == null) {return eTagType.NULL;}
        if(scanLe.getDeviceName().isEmpty()) {return eTagType.NULL;}

        if(isElaTemp(ElaUtils.bytesToHex(scanLe.getBytes())))
            return eTagType.TEMPERATURE;
        else
            return eTagType.NULL;
    }

    /**
     * Check if tag is Temperature format
     * @param data : advertising raw data
     * @return true or false
     */
    private static boolean isElaTemp(String[] data)
    {
        return data[5].equals("6E") &&
                data[6].equals("2A");
    }
}
