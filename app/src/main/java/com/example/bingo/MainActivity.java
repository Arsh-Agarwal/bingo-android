package com.example.bingo;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.bingo.fragments.DeviceListFragment;
import com.example.bingo.fragments.EnableBTFragment;
import com.example.bingo.fragments.EnableDiscoverableFragment;
import com.example.bingo.fragments.GameFragment;
import com.example.bingo.fragments.GridSelectorFragment;
import com.example.bingo.fragments.HostClientFragment;
import com.example.bingo.fragments.SPGameFragment;
import com.example.bingo.fragments.SPGridSelectorFragment;
import com.example.bingo.fragments.SPWinFragment;
import com.example.bingo.fragments.SingleMultiPlayerFragment;
import com.example.bingo.fragments.WinFragment;
import com.example.bingo.interfaces.MainInterface;
import com.example.bingo.interfaces.SecondaryInterface;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements MainInterface {

    private TextView toolbarTextView;
    private BluetoothAdapter bluetoothAdapter;
    private static final String TAG = "MainActivity";
    private ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private DeviceListFragment deviceListFragment;
    private BluetoothDevice mDevice;
    private static final UUID MY_INSECURE_UUID = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private BluetoothConnectionService bluetoothConnectionService;
    private static final int PERM_REQUEST_CODE = 1330;
    private ProgressDialog progressDialog;
    private Integer gridOrder = 5;
    private String STATUS;
    private SecondaryInterface secondaryInterface;
    private MediaPlayer player;
    private static final String WIN = "99";
    private static final String RESTART = "98";
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;

    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)){
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,BluetoothAdapter.ERROR);
                switch (state){
                    case BluetoothAdapter.STATE_OFF:
                        Log.d(TAG, "onReceive: STATE_OFF");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.d(TAG, "onReceive: STATE_ON");
                        if(!bluetoothAdapter.isDiscovering()){
                            EnableDiscoverableFragment enableDiscoverableFragment = new EnableDiscoverableFragment();
                            doFragmentTransaction(enableDiscoverableFragment,"Enable Discoverablility",false);
                        }else{
                            inflateDeviceListFragment();
                        }
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.d(TAG, "onReceive: STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.d(TAG, "onReceive: STATE_TURNING_ON");
                        break;

                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED)){
                int mode = intent.getIntExtra(BluetoothAdapter.EXTRA_SCAN_MODE,BluetoothAdapter.ERROR);
                switch (mode){
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE:
                        Log.d(TAG, "onReceive: discoverable and connectable");
                        inflateDeviceListFragment();
                        break;
                    case BluetoothAdapter.SCAN_MODE_CONNECTABLE:
                        Log.d(TAG, "onReceive: connectable but not discoverable");
                        break;
                    case BluetoothAdapter.SCAN_MODE_NONE:
                        Log.d(TAG, "onReceive: not connectable not discoverable");
                        break;
                    case BluetoothAdapter.STATE_CONNECTING:
                        Log.d(TAG, "onReceive: connecting");
                        break;
                    case BluetoothAdapter.STATE_CONNECTED:
                        Log.d(TAG, "onReceive: connected");
                        break;
                }
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_FOUND)){
                Log.d(TAG, "onReceive: found a new device");
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                btDevices.add(device);
                deviceListFragment.deviceAdded();
            }
        }
    };

    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                int bondState = device.getBondState();
                switch (bondState){
                    case BluetoothDevice.BOND_BONDED:
                        Log.d(TAG, "onReceive: bond bonded");
                        HostClientFragment hostClientFragment = new HostClientFragment();
                        doFragmentTransaction(hostClientFragment,"Choose One",false);
                        instantiateBCS();
                        break;
                    case BluetoothDevice.BOND_BONDING:
                        Log.d(TAG, "onReceive: bond bonding");
                        break;
                    case BluetoothDevice.BOND_NONE:
                        Log.d(TAG, "onReceive: bond none");
                        break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = MediaPlayer.create(this,R.raw.click);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {

            }

            @Override
            public void onBeginningOfSpeech() {
                Log.d(TAG, "onBeginningOfSpeech: entered");
            }

            @Override
            public void onRmsChanged(float rmsdB) {

            }

            @Override
            public void onBufferReceived(byte[] buffer) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int error) {

            }

            @Override
            public void onResults(Bundle results) {
                ArrayList<String> words = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                try {
                    Log.d(TAG, "onResults: checking entered");
                    Integer tempChecker = Integer.parseInt(words.get(0));
                    secondaryInterface.onSpeechResult(words.get(0));
                }catch (Exception e){
                    Log.d(TAG, "onResults: "+e.getMessage());
                    switch (words.get(0)){
                        case "one":{
                            secondaryInterface.onSpeechResult("1");
                            break;
                        }
                        case "two":{
                            secondaryInterface.onSpeechResult("2");
                            break;
                        }
                        case "three":{
                            secondaryInterface.onSpeechResult("3");
                            break;
                        }
                        case "for":{
                            secondaryInterface.onSpeechResult("4");
                            break;
                        }

                        case "four":{
                            secondaryInterface.onSpeechResult("4");
                            break;
                        }
                        case "five":{
                            secondaryInterface.onSpeechResult("5");
                            break;
                        }
                        case "six":{
                            secondaryInterface.onSpeechResult("6");
                            break;
                        }
                        case "seven":{
                            secondaryInterface.onSpeechResult("7");
                            break;
                        }
                        case "eight":{
                            secondaryInterface.onSpeechResult("8");
                            break;
                        }
                        case "nine":{
                            secondaryInterface.onSpeechResult("9");
                            break;
                        }
                        case "ten":{
                            secondaryInterface.onSpeechResult("10");
                            break;
                        }
                    }
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }

            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        toolbarTextView = findViewById(R.id.toolbarTextView);
        registerReceivers();
        deviceListFragment = new DeviceListFragment(this,btDevices);

        SingleMultiPlayerFragment singleMultiPlayerFragment = new SingleMultiPlayerFragment();
        doFragmentTransaction(singleMultiPlayerFragment,"Play Bingo",false);
    }

    private void inflateDeviceListFragment(){
        doFragmentTransaction(deviceListFragment,"Choose a Device",false);
        checkPermissions();
        boolean success = bluetoothAdapter.startDiscovery();
        Log.d(TAG, "onCreate: success: "+success);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERM_REQUEST_CODE:
                if(canAccessFineLocation()){
                    Log.d(TAG, "onRequestPermissionsResult: can access coarse and fine location");
                }else{
                    Log.d(TAG, "onRequestPermissionsResult: cannot access coarse and fine locaion");
                }
        }
    }

    private void checkPermissions(){
        while(!(canAccessCoarseLoaction()&&canAccessFineLocation())){
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION},PERM_REQUEST_CODE);
        }
    }

    private boolean canAccessCoarseLoaction(){
        return hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    private boolean canAccessFineLocation(){
        return hasPermission(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    private boolean hasPermission(String perm){
        return (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,perm));
    }

    private void registerReceivers(){
        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1,filter1);
        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);
        registerReceiver(mBroadcastReceiver2,filter2);
        IntentFilter filter3 = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mBroadcastReceiver3,filter3);
        IntentFilter filter4 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver4,filter4);
    }

    @Override
    public void setToolbarTitle(String title) {
        toolbarTextView.setText(title);
    }

    @Override
    public void enableBT() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableBtIntent);
        playClickSound();
    }

    @Override
    public void enableDiscoverable() {
        Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,200);
        startActivity(discoverableIntent);
        playClickSound();
    }

    @Override
    public void onDeviceClick(int position) {
        playClickSound();
        bluetoothAdapter.cancelDiscovery();
        mDevice = btDevices.get(position);
        int state = mDevice.getBondState();
        if(state == BluetoothDevice.BOND_BONDED){
            HostClientFragment hostClientFragment = new HostClientFragment();
            doFragmentTransaction(hostClientFragment,"Choose One",false);
            instantiateBCS();
        }else{
            mDevice.createBond();
        }
    }

    private void instantiateBCS(){
        Log.d(TAG, "instantiateBCS: entered");
        bluetoothConnectionService = new BluetoothConnectionService(this,this);
        if(bluetoothConnectionService == null){
            Log.d(TAG, "instantiateBCS: bcs is null");
        }
    }

    @Override
    public void becomeClient() {
        STATUS = "CLIENT";
        playClickSound();
        progressDialog = ProgressDialog.show(this,"Host is setting up game","Please Wait...",true);
    }

    @Override
    public void becomeHost() {
        STATUS = "HOST";
        GridSelectorFragment gridSelector = new GridSelectorFragment();
        doFragmentTransaction(gridSelector,"Select Grid Order",false);
        playClickSound();
    }



    @Override
    public void onConnectionAccept() {
        progressDialog.dismiss();
    }

    @Override
    public void onConnectionConnect() {
        if(STATUS.equals("HOST")){
            Log.d(TAG, "onConnectionConnect: entered");
            String temp = gridOrder.toString();
            temp = "GO" + temp;
            if(bluetoothConnectionService == null){
                Log.d(TAG, "onConnectionConnect: bcs is null");
            }
            try {
                bluetoothConnectionService.write(temp.getBytes(Charset.defaultCharset()));
            } catch (Exception e) {
                Log.d(TAG, "onConnectionConnect: "+e.getMessage());
            }
            GameFragment gameFragment = new GameFragment(gridOrder,this);
            Bundle args = new Bundle();
            args.putInt("GridOrder",gridOrder);
            doFragmentTransaction(gameFragment,"BINGO",false);
        }
    }

    @Override
    public void onInputRead(String input) {
        switch (input){
            case WIN:{
                playLoseSound();
                WinFragment winFragment = new WinFragment();
                doFragmentTransaction(winFragment,"You Lose",false);
                return;
            }
            case RESTART:{
                playClickSound();
                HostClientFragment hostClientFragment = new HostClientFragment();
                doFragmentTransaction(hostClientFragment,"Choose One",false);
                return;
            }
            default:{
                secondaryInterface.onClickReceive(input);
            }
        }
    }

    @Override
    public void onInputClientGO(int order) {
        gridOrder = order;
        inflateGameFragmentForClient();
    }

    @Override
    public void onItemClick(String number) {
        bluetoothConnectionService.write(number.getBytes(Charset.defaultCharset()));
    }

    @Override
    public void onGameActivityMake(SecondaryInterface secondaryInterface) {
        this.secondaryInterface = secondaryInterface;
    }

    @Override
    public void onWin() {
        playWinSound();
        bluetoothConnectionService.write(WIN.getBytes(Charset.defaultCharset()));
        WinFragment winFragment = new WinFragment();
        doFragmentTransaction(winFragment,"You Win",false);
    }

    @Override
    public void onQuit() {
        playClickSound();
        finish();
        System.exit(0);
    }

    @Override
    public void onGameRestart() {
        playClickSound();
        bluetoothConnectionService.write(RESTART.getBytes(Charset.defaultCharset()));
        HostClientFragment hostClientFragment = new HostClientFragment();
        doFragmentTransaction(hostClientFragment,"Choose One",false);
    }



    private void inflateGameFragmentForClient(){
        GameFragment gameFragmentClient = new GameFragment(gridOrder,this);
        Log.d(TAG, "inflateGameFragmentForClient: "+gridOrder);
        Bundle args = new Bundle();
        args.putInt("GridOrder",gridOrder);
        doFragmentTransaction(gameFragmentClient,"BINGO",false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver1);
            unregisterReceiver(mBroadcastReceiver2);
            unregisterReceiver(mBroadcastReceiver3);
            unregisterReceiver(mBroadcastReceiver4);
        } catch (Exception e) {
            Log.d(TAG, "onDestroy: " +e.getMessage());
        }
    }

    private void doFragmentTransaction(Fragment fragment, String tag, boolean addToBackStack){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.main_container,fragment,tag);
        if(addToBackStack){
            transaction.addToBackStack(tag);
        }
        transaction.commit();
    }

    private void playClickSound(){
        player.reset();
        player = MediaPlayer.create(this,R.raw.click);
        player.start();
    }

    private void playWinSound(){
        player.reset();
        player = MediaPlayer.create(this,R.raw.win);
        player.start();
    }

    private void playLoseSound(){
        player.reset();
        player = MediaPlayer.create(this,R.raw.game_lose);
        player.start();
    }

    private void playTieSound(){
        player.reset();
        player = MediaPlayer.create(this,R.raw.game_tie);
        player.start();
    }

    private void toastMessage(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMultiPlayer() {
        if(!bluetoothAdapter.isEnabled()){
            EnableBTFragment enableBTFragment = new EnableBTFragment();
            doFragmentTransaction(enableBTFragment,"Enable Bluetooth",false);
        }else if(!bluetoothAdapter.isDiscovering()){
            EnableDiscoverableFragment enableDiscoverableFragment = new EnableDiscoverableFragment();
            doFragmentTransaction(enableDiscoverableFragment,"Enable Discoverablility",false);
        }else{
            inflateDeviceListFragment();
        }
    }

    @Override
    public void onSPRequestNewGrid(Integer gridOrder) {
        this.gridOrder = gridOrder;
        playClickSound();
        SPGameFragment spGameFragment = new SPGameFragment(gridOrder,this);
        doFragmentTransaction(spGameFragment,"Your Turn",false);
    }

    @Override
    public void onSPWin(String status) {
        SPWinFragment spWinFragment = new SPWinFragment();
        doFragmentTransaction(spWinFragment,status,false);
        switch (status){
            case "You Win":{
                playWinSound();
                break;
            }
            case "It's a Tie":{
                playTieSound();
                break;
            }
            case "You Lose":{
                playLoseSound();
                break;
            }
        }
    }

    @Override
    public void onSPGameRestart() {
        SPGridSelectorFragment spGridSelectorFragment = new SPGridSelectorFragment();
        doFragmentTransaction(spGridSelectorFragment,"Choose Grid Order",false);
    }

    @Override
    public void onRequestNewGrid(Integer gridOrder) {
        Log.d(TAG, "onRequestNewGrid:  "+gridOrder);
        this.gridOrder = gridOrder;
        bluetoothConnectionService.startConnectThread(mDevice,MY_INSECURE_UUID);
        playClickSound();
    }

    @Override
    public void onSinglePlayer() {
        Log.d(TAG, "onSinglePlayer: single player chosen");
        SPGridSelectorFragment spGridSelectorFragment = new SPGridSelectorFragment();
        doFragmentTransaction(spGridSelectorFragment,"Choose Grid Order",false);
    }

    @Override
    public void onMicPressed() {
        speechRecognizer.startListening(speechRecognizerIntent);
        Log.d(TAG, "onMicPressed: mainActivity entered");
    }

    @Override
    public void onMicUnPressed() {
        speechRecognizer.stopListening();
    }

    @Override
    public void checkMicPermissions() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},0);
        }
    }
}