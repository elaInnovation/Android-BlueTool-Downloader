package com.kaplacat.bluetooldownloader.Models;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;

/**
 * Base of tags
 */
public abstract class TagBase
{
    String Name = "";
    int Rssi = 0;
    BluetoothDevice Device = null;

    /**
     * Getter on Name value of the tag
     * @return String name
     */
    public String getName()
    {
        return this.Name;
    }

    /**
     * Getter on Rssi value of the tag
     * @return int Rssi
     */
    public int getRssi()
    {
        return this.Rssi;
    }

    /**
     * Getter on Device of the tag
     * @return BluetoothDevice
     */
    public BluetoothDevice getDevice()
    {
        return this.Device;
    }

    public abstract void updateTag(ScanResult scanResult);
    public abstract void updateRssi(ScanResult scanResult);
    public abstract void updateAdvData(ScanResult scanResult);
}
