package com.example.trigger.bluetooth;

import java.io.IOException;
import java.io.InputStream;

import android.os.AsyncTask;
import android.bluetooth.*;
import android.util.Log;

import com.example.trigger.MainActivity.Action;
import com.example.trigger.DoorReply;
import com.example.trigger.DoorReply.ReplyCode;
import com.example.trigger.OnTaskCompleted;


public class BluetoothRequestHandler extends AsyncTask<Object, Void, DoorReply> {
    private OnTaskCompleted listener;

    public BluetoothRequestHandler(OnTaskCompleted listener) {
        this.listener = listener;
    }

    static class BluetoothDoorSetup {
        BluetoothDoorSetup() {

        }
    }

/*

Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

if (pairedDevices.size() > 0) {
    // There are paired devices. Get the name and address of each paired device.
    for (BluetoothDevice device : pairedDevices) {
        String deviceName = device.getName();
        String deviceHardwareAddress = device.getAddress(); // MAC address
    }
}


*/


    @Override
    protected DoorReply doInBackground(Object... params) {
        if (params.length != 2) {
            Log.e("BluetoothRequestHandler.doInBackGround", "Unexpected number of params.");
            return DoorReply.internal_error();
        }

        if (!(params[0] instanceof Action && params[1] instanceof BluetoothDoorSetup)) {
            Log.e("BluetoothRequestHandler.doInBackground", "Invalid type of params.");
            return DoorReply.internal_error();
        }

        Action action = (Action) params[0];
        SshDoorSetup setup = (BluetoothDoorSetup) params[1];

        if (setup.getId() < 0) {
            return DoorReply.internal_error();
        }

        BluetoothAdapter bluetooth = BluetoothAdapter.getDefaultAdapter();

        try {
            String status;
            if (bluetooth.isEnabled()) {
                String mydeviceaddress = bluetooth.getAddress();
                String mydevicename = bluetooth.getName();
                String state = bluetooth.getState();
                status = mydevicename + " : " + mydeviceaddress + " " + state;
            } else {
                // request to enable
                //Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                //startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);

                status = "Bluetooth is not Enabled.";
            }

            return new DoorReply(ReplyCode.SUCCESS, status);
        } catch (Exception e) {
            return new DoorReply(ReplyCode.LOCAL_ERROR, e.toString());
        }
    }

    protected void onPostExecute(DoorReply result) {
        listener.onTaskCompleted(result);
    }
}
