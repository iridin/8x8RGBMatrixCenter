package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.BT_DATA_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixActivity.BT_DEVICE_MAC_KEY;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_HEIGHT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_WIDTH;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class BluetoothService extends IntentService {

    public BluetoothService() {
        super("BluetoothService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String connectedDeviceMac = intent.getStringExtra(BT_DEVICE_MAC_KEY);
        Log.i("MAC", connectedDeviceMac);
        int[] colors = intent.getIntArrayExtra(BT_DATA_KEY);
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        BluetoothDevice connectedDevice = null;

        if (connectedDeviceMac != null
                && btAdapter != null
                && btAdapter.isEnabled()) {
            Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
            for (BluetoothDevice device : pairedDevices) {
                if (device.getAddress().equals(connectedDeviceMac)) {
                    connectedDevice = device;
                }
            }
        }

        if (connectedDevice != null) {
            try {
                // UUID: 00001101-0000-1000-8000-00805f9b34fb
                // UUID: 00000000-0000-1000-8000-00805f9b34fb
                UUID uuid = connectedDevice.getUuids()[0].getUuid();
                Log.i("UUID", uuid.toString());
                BluetoothSocket socket = connectedDevice.createInsecureRfcommSocketToServiceRecord(uuid);

                try {
                    socket.connect();
                } catch (IOException e) { // TODO: FIXME: nasty fallback  :-(
                    Log.e("BluetoothActivity", e.getMessage());
                    try {
                        Log.e("BluetoothActivity", "trying fallback...");

                        socket = (BluetoothSocket) connectedDevice.getClass().getMethod("createInsecureRfcommSocket", new Class[]{int.class}).invoke(connectedDevice, 1);
                        socket.connect();

                        Log.e("BluetoothActivity", "Connected");
                    } catch (Exception e2) {
                        Log.e("BluetoothActivity", "Couldn't establish Bluetooth connection!");
                    }
                }
                OutputStream out = socket.getOutputStream();
                out.write(colors2message(colors));

                out.close();
                socket.close();
            } catch (IOException e) {
                Log.e("BluetoothActivity", "Can't communicate to device " + connectedDeviceMac);
                Log.e("BluetoothActivity", e.getMessage());
            }
        }
    }

    private byte[] colors2message(int[] colors) {
        byte[] msg = new byte[LED_ARRAY_HEIGHT * LED_ARRAY_WIDTH + 2];
        msg[0] = (byte) 0b10000000;

        int i = 1;
        for (int color : colors) {
            msg[i] = color2byte(color);
            i++;
        }

        msg[msg.length - 1] = (byte) 0b01000000;
        return msg;
    }

    private byte color2byte(int color) {
        final int red_shift = 4;
        final int green_shift = 2;
        final int blue_shift = 0;

        byte res = 0x00;
        res |= (ColorHelper.color2LedRed(color) << red_shift);
        res |= (ColorHelper.color2LedGreen(color) << green_shift);
        res |= (ColorHelper.color2LedBlue(color) << blue_shift);

        return res;
    }

}
