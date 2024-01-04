package com.example.bingo.interfaces;

public interface MainInterface {

    void setToolbarTitle(String title);
    void enableBT();
    void enableDiscoverable();
    void onDeviceClick(int position);
    void becomeClient();
    void becomeHost();
    void onRequestNewGrid(Integer gridOrder);
    void onConnectionAccept();
    void onInputRead(String input);
    void onConnectionConnect();
    void onInputClientGO(int order);
    void onItemClick(String number);
    void onGameActivityMake(SecondaryInterface secondaryInterface);
    void onWin();
    void onQuit();
    void onGameRestart();
    void onSinglePlayer();
    void onMultiPlayer();
    void onSPRequestNewGrid(Integer gridOrder);
    void onSPWin(String status);
    void onSPGameRestart();
    void onMicPressed();
    void onMicUnPressed();
    void checkMicPermissions();

}
