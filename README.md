What is it?
===========

This is as an example on how to leverage the accessory mode to communicate between 2 Android devices. You connect 2 devices via one USB-cable + one USB-OTG adapter and chat between devices ( or exchange any other data in a real-world application ).

The App comes in 2 flavors - one host flavor and one device flavor. You need to install one of these flavors on each device.

Install
=======
for the host:

``` bash
$> gradle installHostDebug
```

for the device:

``` bash
$> gradle installDeviceDebug
```

Notes
=====

Be aware that the direction of the cable matters even though there is micro-USB on both ends. The OTG adapter has to be on the device with the host flavor installed. 

A core problem was to figure out how to get a device to switch to accessory mode - the Idea was taken from some [C code inside the Android Compatibility Test Suite](https://code.google.com/p/android-source-browsing/source/browse/apps/cts-usb-accessory/cts-usb-accessory.c?repo=platform--cts&r=62cd9f5c10470150d5b96f4f555c539a2a670713) - this is how it looks in java here:




```java

    private boolean initAccessory(final UsbDevice device) {

        final UsbDeviceConnection connection = mUsbManager.openDevice(device);

        if (connection == null) {
            return false;
        }
        
        initStringControlTransfer(connection, 0, "quandoo"); // MANUFACTURER
        initStringControlTransfer(connection, 1, "Android2AndroidAccessory"); // MODEL
        initStringControlTransfer(connection, 2, "showcasing android2android USB communication"); // DESCRIPTION
        initStringControlTransfer(connection, 3, "0.1"); // VERSION
        initStringControlTransfer(connection, 4, "http://quandoo.de"); // URI
        initStringControlTransfer(connection, 5, "42"); // SERIAL

        connection.controlTransfer(0x40, 53, 0, 0, new byte[]{}, 0, Constants.USB_TIMEOUT_IN_MS);

        connection.close();

        return true;
    }

    private void initStringControlTransfer(final UsbDeviceConnection deviceConnection,
                                           final int index,
                                           final String string) {
        deviceConnection.controlTransfer(0x40, 52, 0, index, string.getBytes(), string.length(), Constants.USB_TIMEOUT_IN_MS);
    }
```

License
=======

Released under the terms of GPLv3.


Links
=====
http://developer.android.com/guide/topics/connectivity/usb/accessory.html

http://stackoverflow.com/questions/22710198/how-can-i-handle-communication-between-two-android-devices-via-otg-usb-cable
