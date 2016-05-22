package cz.cuni.mff.a8x8rgbmatrixcenter;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_HEIGHT;
import static cz.cuni.mff.a8x8rgbmatrixcenter.MatrixView.LED_ARRAY_WIDTH;

/**
 * Created by Dominik Skoda on 19.04.2016.
 */
public class BluetoothService extends Service {

    public static final String BT_COMMAND_KEY = "command";
    public static final String BT_DEVICE_MAC_KEY = "BT_DEVICE_MAC";
    public static final String BT_DATA_KEY = "BT_DATA";

    public static final String REQUEST_CONNECT = "connect";
    public static final String REQUEST_DISCONNECT = "disconnect";
    public static final String REQUEST_SEND = "send";

    private static final long SEND_FAILED_MSG_DELAY = 5000;


    public class LocalBinder extends Binder {
        BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    BluetoothDevice connectedDevice;
    BluetoothSocket connectedSocket;
    OutputStream connectedOut;
    private long lastToast;


    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    public void onCreate(){
        connectedDevice = null;
        connectedSocket = null;
        connectedOut = null;
        lastToast = 0L;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String command = intent.getStringExtra(BT_COMMAND_KEY);
        switch(command){
            case REQUEST_CONNECT:
                String deviceMac = intent.getStringExtra(BT_DEVICE_MAC_KEY);
                connect(deviceMac);
                break;
            case REQUEST_DISCONNECT:
                disconnect();
                break;
            case REQUEST_SEND:
                int[] colors = intent.getIntArrayExtra(BT_DATA_KEY);
                send(colors);
                break;
            default:
                Log.e("BluetoothService", String.format(
                        "The command \"%s\" not supported.", command));
                break;
        }

        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // TODO: disconnect device when the app is terminated
    }

    private void connect(String deviceMac){
        if(connectedDevice != null){
            disconnect();
        }

        connectedDevice = getDeviceWithMac(deviceMac);
        if(connectedDevice == null){
            connectionFailedMessage(deviceMac);
            return;
        }

        try {
            // UUID: 00001101-0000-1000-8000-00805f9b34fb
            // UUID: 00000000-0000-1000-8000-00805f9b34fb
            UUID uuid = connectedDevice.getUuids()[0].getUuid();
            Log.i("UUID", uuid.toString());
            connectedSocket = connectedDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            try {
                connectedSocket.connect();
            } catch (IOException e) { // FIXME: nasty fallback  :-(
                // HACK by http://stackoverflow.com/questions/18657427/ioexception-read-failed-socket-might-closed-bluetooth-on-android-4-3
                Log.e("BluetoothService", e.getMessage());
                try {
                    Log.i("BluetoothService", "trying fallback...");

                    connectedSocket = (BluetoothSocket) connectedDevice.getClass()
                            .getMethod("createInsecureRfcommSocket", new Class[]{int.class})
                            .invoke(connectedDevice, 1);
                    connectedSocket.connect();
                } catch (Exception e2) {
                    connectionFailedMessage(deviceMac);
                    return;
                }
            }
            connectedOut = connectedSocket.getOutputStream();
            connectedMessage();
        } catch (IOException e) {
            Log.e("BluetoothActivity", "Can't communicate to device " + deviceMac);
            Log.e("BluetoothActivity", e.getMessage());
            connectionFailedMessage(deviceMac);
        }

    }

    private void disconnect(){
        if(connectedDevice == null || connectedSocket == null || connectedOut == null){
            connectionLostMessage();
            return;
        }
        try {
            connectedOut.close();
            connectedOut = null;
        } catch (IOException e){
            Log.e("BluetoothService", e.getMessage());
        }
        try {
            connectedSocket.close();
            connectedSocket = null;
        } catch (IOException e){
            Log.e("BluetoothService", e.getMessage());
        }

        disconnectedMessage();
        connectedDevice = null;
    }

    private void send(int[] colors){
        if(connectedOut == null){
            if(connectedDevice != null) {
                // Try to reconnect
                String deviceMac = connectedDevice.getAddress();
                disconnect();
                connect(deviceMac);
            }
            if(connectedOut == null) {
                connectionLostMessage();
                return;
            }
        }

        try{
            connectedOut.write(colors2message(colors));
            Log.v("BluetoothService", "Data sent");
        } catch(IOException e){
            Log.e("BluetoothService", e.getMessage());
            sendFailedMessage();
        }
    }

    private BluetoothDevice getDeviceWithMac(String deviceMac){
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if(deviceMac == null || btAdapter == null || !btAdapter.isEnabled()){
            return null;
        }

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getAddress().equals(deviceMac)) {
                return device;
            }
        }
        return null;
    }

    private void connectionFailedMessage(String deviceMac){
        String errMsg = String.format("Couldn't connect to device with MAC: %s", deviceMac);
        Log.e("BluetoothService", errMsg);
        Toast.makeText(this, errMsg, Toast.LENGTH_SHORT).show();
    }

    private void connectedMessage(){
        if(connectedDevice != null){
            String msg = String.format("%s connected", connectedDevice.getName());
            Log.i("BluetoothService", msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("BluetoothService",
                    "Trying to log \"connected\" message, but connectedDevice is null.");
        }
    }

    private void disconnectedMessage(){
        if(connectedDevice != null){
            String msg = String.format("%s disconnected", connectedDevice.getName());
            Log.i("BluetoothService", msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("BluetoothService",
                    "Trying to log \"disconnected\" message, but connectedDevice is null.");
        }
    }

    private void connectionLostMessage(){
        if(connectedDevice != null){
            String msg = String.format("Connection to %s lost", connectedDevice.getName());
            Log.i("BluetoothService", msg);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        } else {
            Log.e("BluetoothService",
                    "Trying to log \"connection lost\" message, but connectedDevice is null.");
        }
    }

    private void sendFailedMessage(){
        if(connectedDevice != null){
            String msg = String.format("Sending data to %s failed", connectedDevice.getName());
            Log.i("BluetoothService", msg);
            Calendar now = Calendar.getInstance();
            if(now.getTimeInMillis() - lastToast > SEND_FAILED_MSG_DELAY) {
                lastToast = now.getTimeInMillis();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Log.e("BluetoothService",
                    "Trying to log \"send failed\" message, but connectedDevice is null.");
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
