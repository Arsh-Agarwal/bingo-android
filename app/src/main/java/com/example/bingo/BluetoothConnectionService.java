package com.example.bingo;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;

import com.example.bingo.interfaces.MainInterface;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

public class BluetoothConnectionService {

    private static final String TAG = "BluetoothConnectionServ";
    private static final String apppName = "MYAPP";
    private static final UUID MY_INSECURE_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private final BluetoothAdapter bluetoothAdapter;
    private Context mContext;
    private BluetoothDevice mDevice;
    private UUID mUUID;
    private ProgressDialog progressDialog;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private MainInterface mainInterface;

    public BluetoothConnectionService(Context mContext,MainInterface mainInterface) {
        this.mContext = mContext;
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mainInterface = mainInterface;
        initiateAcceptThread();
    }

    public class ConnectedThread extends Thread{
        private final BluetoothSocket socket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket){

            Log.d(TAG, "ConnectedThread: Connected thread starting");
            this.socket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                progressDialog.dismiss();
            } catch (NullPointerException e) {
                Log.d(TAG, "ConnectedThread: NullPointerException");
            }

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.d(TAG, "ConnectedThread: error getting input and output stream from socket "+e.getMessage());
            }

            inputStream = tmpIn;
            outputStream = tmpOut;

        }

        public void run(){
            byte[] buffer = new byte[1024];
            int bytes;

            while(true){
                try {
                    bytes = inputStream.read(buffer);
                    String incomingMessage = new String(buffer,0,bytes);
                    Log.d(TAG, "run: incomingMessage: "+incomingMessage);
                    if(incomingMessage.length() == 3 && incomingMessage.substring(0,2).equals("GO")){
                        String temp = incomingMessage.substring(2);
                        Log.d(TAG, "onInputRead: "+temp);
                        int tempGridOrder = Integer.parseInt(temp);
                        Log.d(TAG, "onInputRead: "+tempGridOrder);
                        mainInterface.onInputClientGO(tempGridOrder);
                    }else{
                        mainInterface.onInputRead(incomingMessage);
                    }

                } catch (IOException e) {
                    Log.d(TAG, "run: error reading input stream "+e.getMessage());
                    break;
                }
            }
        }

        public void write(byte[] bytes){
            String text = new String(bytes, Charset.defaultCharset());
            Log.d(TAG, "write: writing to output stream "+text);

            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                Log.d(TAG, "write: error writing to output stream "+e.getMessage());
            }
        }

        public void cancel(){
            try {
                socket.close();
            } catch (IOException e) {
                Log.d(TAG, "cancel: "+e.getMessage());
            }
        }
    }

    public class ConnectThread extends Thread{

        private BluetoothSocket socket;

        private ConnectThread(BluetoothDevice device,UUID uuid){
            Log.d(TAG, "ConnectThread: connect thread started");
            mDevice = device;
            mUUID = uuid;
        }

        public void run(){
            BluetoothSocket tmp = null;
            Log.d(TAG, "run: Connect thread running");
            try {
                Log.d(TAG, "run: trying to create insecure rfcom socket using uuid: "+MY_INSECURE_UUID);
                tmp = mDevice.createRfcommSocketToServiceRecord(mUUID);
            } catch (IOException e) {
                Log.d(TAG, "run: could not create rfcom socket "+e.getMessage());
            }
            socket = tmp;
            bluetoothAdapter.cancelDiscovery();
            Log.d(TAG, "run: cancelling discovery");

            try {
                socket.connect();
                Log.d(TAG, "run: ConnectThread Connected");
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException ioException) {
                    Log.d(TAG, "run: unable to close connection in socket "+e.getMessage());
                }
                Log.d(TAG, "run: socket was closed");
                Log.d(TAG, "run: could not connect to UUID: "+MY_INSECURE_UUID);
            }
            connected(socket,mDevice);
        }

        public void cancel(){
            try {
                Log.d(TAG, "cancel: closing client socket");
                socket.close();
            } catch (IOException e) {
                Log.d(TAG, "cancel: unable to close client socket");
            }
        }
    }

    public class AcceptThread extends Thread{
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread(){
            BluetoothServerSocket tmp = null;

            try {
                Log.d(TAG, "AcceptThread: "+bluetoothAdapter.toString());
                tmp = bluetoothAdapter.listenUsingInsecureRfcommWithServiceRecord(apppName,MY_INSECURE_UUID);
                Log.d(TAG, "AcceptThread: setting up insercure RFCOMM server using "+MY_INSECURE_UUID);
            } catch (IOException e) {
                Log.d(TAG, "AcceptThread: "+e.getLocalizedMessage());
            }
            mmServerSocket = tmp;
        }

        public void run(){
            Log.d(TAG, "run: Accept Thread running...");
            BluetoothSocket socket = null;

            try {
                Log.d(TAG, "run: RFcom server socket start.");
                socket = mmServerSocket.accept();
                mainInterface.onConnectionAccept();
                Log.d(TAG, "run: RFCOM server socket accepted connection");

            } catch (IOException e) {
                Log.d(TAG, "run: "+e.getMessage());
            }

            if(socket != null){
                connected(socket,mDevice);
            }
        }

        public void cancel(){
            Log.d(TAG, "cancel: cancelling accept thread");

            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.d(TAG, "cancel: Close of Accept THread server socket failed  "+e.getMessage());
            }
        }
    }

    public void startConnectThread(BluetoothDevice device,UUID uuid){
        Log.d(TAG, "startConnectThread: started");
        progressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth","Please wait...",true);
        mConnectThread = new ConnectThread(device,uuid);
        mConnectThread.start();
    }

    public synchronized void initiateAcceptThread(){
        Log.d(TAG, "initiateAcceptThread: entered");
        if(mAcceptThread == null){
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        if(mConnectThread != null){
            mConnectThread.cancel();
            mConnectThread = null;
        }
    }

    public void write(byte[] bytes){

        try {
            if(mConnectedThread == null){
                Log.d(TAG, "write: mConnectedThread is null");
            }
            mConnectedThread.write(bytes);
        } catch (Exception e) {
            Log.d(TAG, "write: "+e.getMessage());
        }
    }

    public void connected(BluetoothSocket socket,BluetoothDevice device){
        Log.d(TAG, "connected: entered");
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        mainInterface.onConnectionConnect();
    }
}
