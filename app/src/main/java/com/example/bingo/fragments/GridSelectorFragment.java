package com.example.bingo.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bingo.interfaces.MainInterface;
import com.example.bingo.R;

public class GridSelectorFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "GridSelector";
    private Button btnNewGrid;
    private int gridIndex = 5;
    private TextView[] textViews = new TextView[6];
    private MainInterface mainInterface;
    private MediaPlayer player;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        player = MediaPlayer.create(getContext(), R.raw.click);
        mainInterface.setToolbarTitle(getTag());
        View view = inflater.inflate(R.layout.fragment_grid_selector,container,false);
        textViews[0] = view.findViewById(R.id.textView3);
        textViews[1] = view.findViewById(R.id.textView4);
        textViews[2] = view.findViewById(R.id.textView5);
        textViews[3] = view.findViewById(R.id.textView6);
        textViews[4] = view.findViewById(R.id.textView7);
        textViews[5] = view.findViewById(R.id.textView8);
        btnNewGrid = view.findViewById(R.id.btnNewGrid);

        textViews[2].setBackgroundResource(R.drawable.green_letter_background);
        btnNewGrid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.onRequestNewGrid(gridIndex);
            }
        });

        for(int i= 0 ; i < 6 ; i++){
            textViews[i].setOnClickListener(this);
        }

        return view;
    }

    private void unsetAll(){
        for (int i = 0 ; i < 6 ; i++){
            textViews[i].setBackgroundResource(R.drawable.yellow_letter_background);
        }
    }

    @Override
    public void onClick(View v) {
        playClickSound();
        unsetAll();
        switch (v.getId()){
            case R.id.textView3:{
                Log.d(TAG, "onClick: 3");
                textViews[0].setBackgroundResource(R.drawable.green_letter_background);
                gridIndex = 3;
                break;
            }
            case R.id.textView4:{
                textViews[1].setBackgroundResource(R.drawable.green_letter_background);
                gridIndex = 4;
                break;
            }
            case R.id.textView5:{
                textViews[2].setBackgroundResource(R.drawable.green_letter_background);
                gridIndex = 5;
                break;
            }
            case R.id.textView6:{
                textViews[3].setBackgroundResource(R.drawable.green_letter_background);
                gridIndex = 6;
                break;
            }
            case R.id.textView7:{
                textViews[4].setBackgroundResource(R.drawable.green_letter_background);
                gridIndex = 7;
                break;
            }
            case R.id.textView8:{
                textViews[5].setBackgroundResource(R.drawable.green_letter_background);
                gridIndex = 8;
                break;
            }
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainInterface = (MainInterface) getActivity();
    }

    private void playClickSound(){
        if(player.isPlaying()){
            player.stop();
        }
        player.start();
    }
}
