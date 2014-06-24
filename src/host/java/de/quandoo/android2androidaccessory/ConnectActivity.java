package de.quandoo.android2androidaccessory;

import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import java.util.HashMap;

import butterknife.ButterKnife;

public class ConnectActivity extends ActionBarActivity {

    public static final String DEVICE_EXTRA_KEY = "device";
    private UsbManager mUsbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.inject(this);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onPostResume();

        final HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        if (deviceList == null || deviceList.size() == 0) {
            final Intent intent=new Intent(this,InfoActivity.class);
            startActivity(intent);

            finish();
            return;
        }

        if (searchForUsbAccessory(deviceList)) {
            return;
        }

        for (UsbDevice device:deviceList.values()) {
            initAccessory(device);
        }

        finish();
    }

    private boolean searchForUsbAccessory(final HashMap<String, UsbDevice> deviceList) {
        for (UsbDevice device:deviceList.values()) {
            if (isUsbAccessory(device)) {

                final Intent intent=new Intent(this,ChatActivity.class);
                intent.putExtra(DEVICE_EXTRA_KEY, device);
                startActivity(intent);

                finish();
                return true;
            }
        }

        return false;
    }

    private boolean isUsbAccessory(final UsbDevice device) {
        return (device.getProductId() == 0x2d00) || (device.getProductId() == 0x2d01);
    }

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

        connection.cont .controlTransfer(0x40, 53, 0, 0, new byte[]{}, 0, 100);

        connection.close();

        return true;
    }

    private void initStringControlTransfer(final UsbDeviceConnection deviceConnection,
                                           final int index,
                                           final String string) {
        deviceConnection.controlTransfer(0x40, 52, 0, index, string.getBytes(), string.length(), 100);
    }
}
