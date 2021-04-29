package com.kaplacat.bluetooldownloader;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.os.Handler;

import java.util.List;

public class ScannerEla
{
    public static final int REQUEST_ENABLE_BT = 456;
    private static ScannerEla instance = null;
    private boolean isScanning = false;

    /**
     * Scan period, can be changed (in ms)
     */
    public long SCAN_PERIOD = 300000;

    /**
     * Instance of the Buetooth scanner
     * @return instance
     */
    public static ScannerEla getInstance()
    {
        if(null == instance)
            instance = new ScannerEla();
        return instance;
    }

    /**
     * Define BLE scan callbacks
     */
    private ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            if(result.getDevice() == null) {return;}
            if (result.getDevice().getName() == null) { return; }
            if(result.getDevice().getName().isEmpty()) {return;}

            ScanFactory.getInstance().buildScan(result);

            super.onScanResult(callbackType, result);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };

    private BluetoothAdapter BlueAdapter = null;
    private BluetoothLeScanner BlueLeScanner = null;
    private final Handler handler = new Handler();

    /**
     * Init the scanner of the phone
     * @return true if sucess, false if failed
     */
    public boolean initScanner()
    {
        this.BlueAdapter = BluetoothAdapter.getDefaultAdapter();
        this.BlueLeScanner = this.BlueAdapter.getBluetoothLeScanner();

        if(this.BlueLeScanner == null)
        {
            return false;
        }
        return true;
    }

    /**
     * Enable Bluetooth, if his disabled
     * @return true if sucess, false if failed
     */
    public boolean enableBluetooth()
    {
        if(this.BlueAdapter == null) {return false;}

        if(this.BlueAdapter.enable()) {
            return true;
        }
        return false;
    }

    /**
     * Disable bluetooth
     * @return true if sucess, false if failed
     */
    public boolean disableBluetooth()
    {
        if(this.BlueAdapter == null) {return false;}

        if(this.BlueAdapter.disable())
            return true;
        return false;
    }

    /**
     * Start scanLe function in the main thread
     * @param enable : Enable or disable scan
     */
    private void scanLeDevice(final boolean enable) {

        if (enable)
        {
            handler.postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    isScanning = false;
                    BlueLeScanner.stopScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

            isScanning = true;
            BlueLeScanner.startScan(mLeScanCallback);
        }
        else
            {
            isScanning = false;
            BlueLeScanner.stopScan(mLeScanCallback);
        }
    }

    /**
     * Check if Scanner Le is scanning
     * @return isScanning variable
     */
    public boolean isScanning()
    {
        return this.isScanning;
    }


    /**
     * Start the scan with Bluetooth Le scanner
     * @return true if sucess, false if failed
     */
    public boolean startScanner()
    {
        if(this.BlueAdapter == null) {return false;}
        if(isScanning())
        {
            return false;
        }
        scanLeDevice(true);
        return true;
    }

    /**
     * Stop the scanner
     * @return true if sucess, false if failed
     */
    public boolean stopScanner()
    {
        if(this.BlueAdapter == null) {return false;}

        if(!isScanning())
        {
            return false;
        }
        scanLeDevice(false);
        return true;
    }
}
