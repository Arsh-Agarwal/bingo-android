package com.example.bingo.fragments;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bingo.CustomButton;
import com.example.bingo.interfaces.MainInterface;
import com.example.bingo.R;
import com.example.bingo.interfaces.SecondaryInterface;
import com.example.bingo.adapters.GridAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class GameFragment extends Fragment implements GridAdapter.ItemClickListener, SecondaryInterface {
    private static final String TAG = "GameFragment";
    private int gridOrder = 5;
    private TextView[] customTextViews;
    private GridView gridView;
    private ArrayList<CustomButton> buttons;
    private Integer[][] allCombos;
    private boolean firstClick = true;
    private int previousBingoCount = 0;
    private int bingoCount = 0;
    private MainInterface mainInterface;
    private boolean turn = true;
    private MediaPlayer player;
    private FloatingActionButton fab;
    private boolean firstMicClick = true;

    public GameFragment(int gridOrder,MainInterface mainInterface){
        this.gridOrder = gridOrder;
        this.mainInterface = mainInterface;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        mainInterface.setToolbarTitle("Your Turn");
        super.onViewCreated(view, savedInstanceState);
        mainInterface.onGameActivityMake(this);
        GridAdapter adapter = new GridAdapter(this,gridOrder);
        gridView.setNumColumns(gridOrder);
        gridView.setAdapter(adapter);
        buttons = adapter.getButtons();
        allCombos = comboGenerator(gridOrder);
        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if(firstMicClick){
                    mainInterface.checkMicPermissions();
                    firstMicClick = false;
                }else {
                    if(event.getAction() == MotionEvent.ACTION_UP){
                        fab.setImageResource(R.drawable.ic_baseline_mic_24);
                        mainInterface.onMicUnPressed();
                        Log.d(TAG, "onTouch: unpress entered");
                    }
                    if(event.getAction() == MotionEvent.ACTION_DOWN){
                        mainInterface.onMicPressed();
                        Log.d(TAG, "onTouch: pressed entered");
                    }
                }

                return false;
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        player = MediaPlayer.create(getContext(), R.raw.click);
        View view = inflater.inflate(R.layout.game_fragment,container,false);
        fab = view.findViewById(R.id.fab);
        gridView = view.findViewById(R.id.grid);
        customTextViews = new TextView[gridOrder];
        for(int i = 0 ; i < gridOrder ; i++){
            TextView temp = getTextView();
            Log.d(TAG, "onCreate: " + temp);
            customTextViews[i] = temp;
        }
        switch (gridOrder){
            case 3:{
                customTextViews[0].setText("W");
                customTextViews[1].setText("I");
                customTextViews[2].setText("N");
                break;
            }
            case 4:{
                customTextViews[0].setText("P");
                customTextViews[1].setText("L");
                customTextViews[2].setText("A");
                customTextViews[3].setText("Y");
                break;
            }
            case 5:{
                customTextViews[0].setText("B");
                customTextViews[1].setText("I");
                customTextViews[2].setText("N");
                customTextViews[3].setText("G");
                customTextViews[4].setText("O");
                break;
            }
            case 6:{
                customTextViews[0].setText("B");
                customTextViews[1].setText("I");
                customTextViews[2].setText("N");
                customTextViews[3].setText("G");
                customTextViews[4].setText("O");
                customTextViews[5].setText("!");
                break;
            }
            case 7:{
                customTextViews[0].setText("B");
                customTextViews[1].setText("I");
                customTextViews[2].setText("N");
                customTextViews[3].setText("G");
                customTextViews[4].setText("O");
                customTextViews[5].setText("!");
                customTextViews[6].setText("!");
                break;
            }
            case 8:{
                customTextViews[0].setText("B");
                customTextViews[1].setText("I");
                customTextViews[2].setText("N");
                customTextViews[3].setText("G");
                customTextViews[4].setText("O");
                customTextViews[5].setText("!");
                customTextViews[6].setText("!");
                customTextViews[7].setText("!");
                break;
            }
        }
        LinearLayout bingosContainer = view.findViewById(R.id.bingosContainer);
        for(int i = 0 ; i < gridOrder ; i++){
            bingosContainer.addView(customTextViews[i]);
        }
        return view;
    }

    @Override
    public void onItemClick(int position) {

        if(!turn){
            toastMessage("Please wait for your turn");
            playErrorSound();
            return;
        }

        if(firstClick) {
            firstClick = false;
            buttons.remove(0);
        }

        if(buttons.get(position).isSelected()){
            toastMessage("No backsies..");
            playErrorSound();
            return;
        }else{
            turn = false;
            playClickSound();
            mainInterface.setToolbarTitle("Opponent's Turn");
            buttons.get(position).setSelected(true);
            buttons.get(position).setBackgroundResource(R.drawable.letter_background_selected);
        }
        mainInterface.onItemClick(buttons.get(position).getText().toString());
        updateBingo();
    }

    private void completionCheck(ArrayList<CustomButton> buttons){
        boolean array_selected = true;
        for(int i = 0 ; i < gridOrder ; i++){
            if(!buttons.get(i).isSelected()){
                array_selected = false;
                break;
            }
        }
        if(array_selected){
            bingoCount++;
        }
    }

    private ArrayList<CustomButton> makeButtonArrayList(Integer[] values){
        ArrayList<CustomButton> myList = new ArrayList<>();
        for(int i = 0; i < gridOrder ; i++){
            myList.add(buttons.get(values[i]));
        }
        return myList;
    }

    private void unsetBingos(){
        for(int i = 0 ; i < gridOrder ; i ++){
            customTextViews[i].setBackgroundResource(R.drawable.letter_background_default);
        }
    }

    private void setBingo(TextView textView){
        textView.setBackgroundResource(R.drawable.letter_background_selected);
    }

    private void updateBingo(){
        bingoCount = 0;
        for(int i = 0 ; i < 2*(gridOrder+1) ; i++){
            completionCheck(makeButtonArrayList(allCombos[i]));
        }
        unsetBingos();

        for(int i = 0 ; i < bingoCount ; i++){
            setBingo(customTextViews[i]);
        }

        if(bingoCount > gridOrder){
            for(int i = 0 ; i < gridOrder ; i++){
                setBingo(customTextViews[i]);
            }
        }

        if(bingoCount >= gridOrder){
            mainInterface.onWin();
        }

        if(bingoCount > previousBingoCount && bingoCount < gridOrder){
            playLevelUpSound();
        }
        previousBingoCount = bingoCount;
    }

    private Integer[][] comboGenerator(int rowCount){
        Integer[][] combos = new Integer[2*(rowCount + 1)][rowCount];

        for(int i = 0 ; i < rowCount ; i++){
            for(int j = 0 ; j < rowCount ; j++){
                int temp = rowCount*i+j;
                combos[i][j] = temp;
            }
        }

        for(int i = 0 ; i < rowCount ; i++){
            for(int j = 0 ; j < rowCount ; j++){
                int temp = i + rowCount*j;
                combos[i+rowCount][j] = temp;
            }
        }

        for(int  k = 0 ; k < rowCount ; k++){
            combos[2*rowCount][k] = k*(rowCount+1);
        }

        for(int a = 0 ; a < rowCount ; a++){
            combos[2*rowCount + 1][a] = rowCount - 1 + a*(rowCount - 1);
        }
        return combos;
    }

    private TextView getTextView(){
        TextView textView = new TextView(getActivity().getApplicationContext());
        textView.setText("*");
        textView.setTextColor(getResources().getColor(R.color.black));
        textView.setBackgroundResource(R.drawable.letter_background_default);
        textView.setPadding(20,0,20,0);
        textView.setTextSize(30);
        return textView;
    }

    @Override
    public void onClickReceive(String number) {
        turn = true;
        playClickSound();
        mainInterface.setToolbarTitle("Your Turn");
        Button tmpBtn = findButtonWithText(number);
        tmpBtn.setSelected(true);
        tmpBtn.setBackgroundResource(R.drawable.letter_background_selected);
        updateBingo();
    }

    @Override
    public void onSpeechResult(String answer) {
        Log.d(TAG, "onSpeechResult: "+answer);
        fab.setImageResource(R.drawable.ic_baseline_mic_off_24);
        Integer checkerInt = Integer.parseInt(answer);
        if(checkerInt <= gridOrder*gridOrder) {
            CustomButton btnTemp = findButtonWithText(answer);
            if (!turn) {
                toastMessage("Please wait for your turn");
                playErrorSound();
                return;
            }

            if (firstClick) {
                firstClick = false;
                buttons.remove(0);
            }

            if (btnTemp.isSelected()) {
                toastMessage("No backsies..");
                playErrorSound();
                return;
            } else {
                turn = false;
                playClickSound();
                mainInterface.setToolbarTitle("Opponent's Turn");
                btnTemp.setSelected(true);
                btnTemp.setBackgroundResource(R.drawable.letter_background_selected);
            }
            mainInterface.onItemClick(btnTemp.getText().toString());
            updateBingo();
        }
    }

    private CustomButton findButtonWithText(String number){
        Log.d(TAG, "findButtonWithText: enquired string: "+number);
        CustomButton temp = null;
        for(int i = 0 ; i < gridOrder*gridOrder ; i++){
            Log.d(TAG, "findButtonWithText: "+buttons.get(i).getText().toString());
            if(buttons.get(i).getText().toString().equals(number)){
                Log.d(TAG, "findButtonWithText: entered");
                temp = buttons.get(i);
                break;
            }
        }
        return temp;
    }

    private void toastMessage(String message){
        Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void playClickSound(){
        player.reset();
        player = MediaPlayer.create(getContext(),R.raw.click);
        player.start();
    }

    private void playErrorSound() {
        player.reset();
        player = MediaPlayer.create(getContext(),R.raw.game_error);
        player.start();
    }

    private void playLevelUpSound(){
        player.reset();
        player = MediaPlayer.create(getContext(),R.raw.level_up);
        player.start();
    }
}
