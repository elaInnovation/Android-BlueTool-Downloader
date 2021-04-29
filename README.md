# Android-BlueTool-Downloader
ELA Innovation provide this project for Android Studio to help users to intergrate easily the Tags provide by ELA Innovation Company. You can directly clone this project from Github. This project contains the code of a simple app to manage Bluetooth scanner for your mobile project and use Gatt and Services to use the connected mode from our Bluetooth tag.

## Build
Before starting, please download Android Studio and install it. Then, to build the application, open the solution file using Android Studio and generate the solution. You can use your own Android phone.

## Requirements
This app is build for Android 5 and above. Note that this app could not work on old phones. The phone must support BLE. Only ELA BLE tags form ELA Innovation can work, other BLE devices will be not recognized by the app. For now, only Temperature or RHT tag can be detected by the app.
Once you installed the app, you have to allow app to access to your localisation. This parameter is on your phone. Parameters -> Applications -> Authorization

## Code
This code use a better implementation of BLE features than the Android-BlueTool-Box app (https://github.com/elaInnovation/Android-Blue-Tool-Box).

### Scanner
The ScannerEla class allow you to start a LE scanner for 100 seconds. You can also stop it whenever you want. LE scanner is launch on the main thread to be more performant, this is the main function of this scanner: 
```java
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
```
The Handler wil fire when a tag is detected by the scanner. You can handle events with a ScanCallback :
```java
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
```

### Connecter
The ConnecterEla class allow you to connect, send a command and disconnect your tag from the app. These functions can be used if you give a tag to the class.
When it's done, you can define you own BluetoothGattCallback and overrid some functions.
#### onConnectionStateChange
You can use it to check the connection status of your tag. Each state is defined on the Android developer wiki.
```java
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
```
#### onServicesDiscovered
You can use it to get characteristics of the tag. Here we need the Rx and Tx characteristics. Once each characteristic is identify, just enable notification on the characteristic Rx with the gatt object. Then next, get the descriptor and write some bytes to enable notifications. Finally, you can write the descriptor to the Rx characteristic.
```java
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
```
#### onDescriptorWrite
Once the descriptor is write all characteristics are ready to be used. You just have to send the COMMAND as an array of bytes. Important : Add a WRITE_TYPE_NO_RESPONSE to the write type. Else the tag will reboot and denied your command.
```java
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
```
#### onCharacteristicChanged
Once the command is send, you can get your data on this events.
```java
 @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            txtLog.setText(txtLog.getText() + new String(characteristic.getValue(), StandardCharsets.UTF_8) + "\n");
            super.onCharacteristicChanged(gatt, characteristic);
        }
```
