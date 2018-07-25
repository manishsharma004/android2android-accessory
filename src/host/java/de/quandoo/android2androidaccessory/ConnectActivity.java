package de.quandoo.android2androidaccessory;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;

import java.util.HashMap;

import butterknife.ButterKnife;

public class ConnectActivity extends ActionBarActivity {

    private static String TAG = "ConnectActivity";

    public static final String DEVICE_EXTRA_KEY = "device";
    private UsbManager mUsbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ButterKnife.bind(this);

        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        Log.i(TAG, "onCreate: mUsbManager=" + mUsbManager);
    }

    @Override
    protected void onResume() {
        super.onResume();

        final HashMap<String, UsbDevice> deviceList = mUsbManager.getDeviceList();

        Log.i(TAG, "onResume: deviceList=" + deviceList);

        if (deviceList == null || deviceList.size() == 0) {
            final Intent intent=new Intent(this, InfoActivity.class);
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
        Log.i(TAG, "searchForUsbAccessory: deviceList=" + deviceList);
        for (UsbDevice device:deviceList.values()) {
            if (isUsbAccessory(device)) {
                Log.i(TAG, "searchForUsbAccessory(if (isUsbAccessory(device))): " + device);
                final Intent intent=new Intent(this,ChatActivity.class);
                intent.putExtra(DEVICE_EXTRA_KEY, device);
                startActivity(intent);

                finish();
                return true;
            }
        }

        return false;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean isUsbAccessory(final UsbDevice device) {
        Log.i(TAG, "isUsbAccessory: device=[name=" + device.getDeviceName() +
                ", manufacturerName=" + device.getManufacturerName() +
                ", productName=" + device.getProductName() +
                ", deviceId=" + device.getDeviceId() +
                ", productId=" + device.getProductId() +
                ", deviceProtocol=" + device.getDeviceProtocol() + "]");
        return (device.getProductId() == 0x2d00) || (device.getProductId() == 0x2d01);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private boolean initAccessory(final UsbDevice device) {
        Log.i(TAG, "initAccessory: device=[name=" + device.getDeviceName() +
                ", manufacturerName=" + device.getManufacturerName() +
                ", productName=" + device.getProductName() +
                ", deviceId=" + device.getDeviceId() +
                ", productId=" + device.getProductId() +
                ", deviceProtocol=" + device.getDeviceProtocol() + "]");

        if (!mUsbManager.hasPermission(device)) {
            Log.i(TAG, "initAccessory: Do not have permission on device=" + device.getProductName());
            Intent intent = new Intent(this, this.getClass());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Log.i(TAG, "initAccessory: Trying to get permissions with pendingIntent=" + pendingIntent);
            mUsbManager.requestPermission(device, pendingIntent);
        }
        final UsbDeviceConnection connection = mUsbManager.openDevice(device);
        Log.i(TAG, "initAccessory: conneciton=" + connection);
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
        Log.i(TAG, "initStringControlTransfer: deviceConnection=" + deviceConnection +
                ", index=" + index + ", string=" + string);
        deviceConnection.controlTransfer(0x40, 52, 0, index,
                string.getBytes(), string.length(), Constants.USB_TIMEOUT_IN_MS);
    }
}
