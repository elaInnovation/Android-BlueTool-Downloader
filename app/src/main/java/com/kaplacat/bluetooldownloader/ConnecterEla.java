package com.kaplacat.bluetooldownloader;


import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.widget.TextView;

import com.kaplacat.bluetooldownloader.Models.TagBase;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class ConnecterEla
{
    private String COMMAND = "LOG_DL";

    TextView txtLog;
    private static ConnecterEla instance = null;
    private TagBase TagToConnect = null;
    private BluetoothGatt bluetoothGatt;
    private BluetoothGattService bluetoothGattService;
    private BluetoothGattService UartService = null;
    private BluetoothGattCharacteristic RxService = null;
    private BluetoothGattCharacteristic TxService = null;
    public static final String UUID_SERVICE = "6e400001-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_RX = "6e400003-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_TX = "6e400002-b5a3-f393-e0a9-e50e24dcca9e";
    public static final String UUID_NORDIC = "00002902-0000-1000-8000-00805f9b34fb";

    /**
     * Instance of the Ela Connecter
     * @return instance
     */
    public static ConnecterEla getInstance()
    {
        if(null == instance)
            instance = new ConnecterEla();
        return instance;
    }

    /**
     * Select tag to perform commands on it
     * @param tag : Tag selected
     * @param txtLog : Textview object to write logs
     */
    public void setTagToConnect(TagBase tag, TextView txtLog)
    {
        if(null == tag) {return;}
        this.TagToConnect = tag;
        txtLog.setText("");
        txtLog.setText("Connect to :" + tag.getName() + "\n");
        this.txtLog = txtLog;
    }

    /**
     * Connect to the selected tag of the instance
     * @param context : Context of the application
     */
    public void connectToTag(Context context)
    {
        this.bluetoothGatt = this.TagToConnect.getDevice().connectGatt(context,false,gattCallback);
        this.bluetoothGatt.connect();
    }

    /**
     * Disconnect the selected tag of the instance
     */
    public void disconnectTag()
    {
        if(this.bluetoothGatt != null)
            this.bluetoothGatt.disconnect();
    }

    /**
     * Define the Bluetooth Gatt Callbacks
     * To get answer from ELA tags, you have to follow these steps :
     * 1 : Enable notifications on Rx characteristic with the gatt
     * >>> gatt.setCharacteristicNotification(characteristic, true);
     * 2 : Get the specific descriptor and set value to ENABLE_NOTIFICATION_VALUE and write it
     * >>> descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
     *
     * To send a command to the tag you have to configure the write type as WRITE_TYPE_NO_RESPONSE
     * >>> characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
     *
     * Answer will fire in the onCharacteristicChanged callback
     */
   private BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            txtLog.setText(txtLog.getText() + "Connection state change to : " + newState + "\n");
            if(newState == 30)
                txtLog.setText(txtLog.getText() + "Tag failed to connect\n");
            else if(newState == 2) {
                txtLog.setText(txtLog.getText() + "Tag is connected\n");
                gatt.discoverServices();
            }
            else if(newState == 0)
                txtLog.setText(txtLog.getText() + "Tag is disconnected\n");

            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            txtLog.setText(txtLog.getText() + "Service discovered\n");
            List<BluetoothGattService> serviceList = gatt.getServices();
            for(BluetoothGattService service : serviceList)
            {
                if(service.getUuid().toString().equals(UUID_SERVICE)) {
                    List<BluetoothGattCharacteristic> characteristicsList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristicsList) {
                        if (characteristic.getUuid().toString().equals(UUID_RX)) {
                            gatt.setCharacteristicNotification(characteristic, true);
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptors().get(0);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                            break;
                        }
                    }
                }
            }
            super.onServicesDiscovered(gatt, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            txtLog.setText(txtLog.getText() + new String(characteristic.getValue(), StandardCharsets.UTF_8) + "\n");
            super.onCharacteristicChanged(gatt, characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            List<BluetoothGattService> serviceList = gatt.getServices();
            for (BluetoothGattService service : serviceList) {
                if (service.getUuid().toString().equals(UUID_SERVICE)) {
                    List<BluetoothGattCharacteristic> characteristicsList = service.getCharacteristics();
                    for (BluetoothGattCharacteristic characteristic : characteristicsList) {
                        if (characteristic.getUuid().toString().equals(UUID_TX)) {
                            characteristic.setValue(COMMAND.getBytes(Charset.forName("UTF-8")));
                            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
                            gatt.writeCharacteristic(characteristic);
                            break;
                        }
                    }
                }
            }
            super.onDescriptorWrite(gatt, descriptor, status);
        }
    };
}
