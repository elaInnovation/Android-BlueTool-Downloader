package com.kaplacat.bluetooldownloader.Models;

import android.bluetooth.le.ScanResult;

import com.kaplacat.bluetooldownloader.ElaUtils;

import java.util.Objects;

public class TagTemperature extends TagBase{
    private String Temp;
    private String[] RawData;
    private int MSB_TEMP = 24;
    private int LSB_TEMP = 21;

    /**
     * Constructor for Temperature Tags
     * @param scanResult Scan result given by the Android library
     */
    public TagTemperature(ScanResult scanResult)
    {
        this.Name = scanResult.getDevice().getName();
        this.Rssi = scanResult.getRssi();
        this.RawData = ElaUtils.bytesToHex(Objects.requireNonNull(scanResult.getScanRecord()).getBytes());
        this.Temp = computeTemp(scanResult);
        this.Device = scanResult.getDevice();
    }

    /**
     * Update all value of the tags (name, rssi, rawData, Temperature)
     * @param scanResult Scan result given by the Android library
     */
    @Override
    public void updateTag(ScanResult scanResult)
    {
        this.Name = scanResult.getDevice().getName();
        this.Rssi = scanResult.getRssi();
        this.RawData = ElaUtils.bytesToHex(Objects.requireNonNull(scanResult.getScanRecord()).getBytes());
        this.Temp = computeTemp(scanResult);
        this.Device = scanResult.getDevice();
    }

    /**
     * Update only the Rssi of the tag
     * @param scanResult Scan result given by the Android library
     */
    @Override
    public void updateRssi(ScanResult scanResult)
    {
        if(scanResult == null) {return;}
        this.Rssi = scanResult.getRssi();
    }

    /**
     * Update only raw data and advertising data of the tag
     * @param scanResult Scan result given by the Android library
     */
    @Override
    public void updateAdvData(ScanResult scanResult)
    {
        if(scanResult == null) {return;}
        this.RawData =  ElaUtils.bytesToHex(Objects.requireNonNull(scanResult.getScanRecord()).getBytes());
    }

    /**
     * Compute and extract data from the Scan Result ot get temperature
     * @param scanResult Scan result given by the Android library
     * @return Temperature as string (float format)
     */
    private String computeTemp(ScanResult scanResult)
    {
        String[] advData = ElaUtils.bytesToHex(scanResult.getScanRecord().getBytes());
        String temp = advData[8].concat(advData[7]);
        if(advData[8].substring(0,1).equals("F"))
        {
            short decimal = Integer.valueOf(temp,16).shortValue();
            return String.valueOf((double)decimal / 100);
        }
        else
        {
            int decimal=Integer.parseInt(temp,16);
            return String.valueOf((double)decimal / 100);
        }
    }

    /**
     * Getter on Temp value
     * @return String
     */
    public String getTemp()
    {
        return this.Temp;
    }
}
