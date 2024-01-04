package com.example.bingo.fragments;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bingo.interfaces.MainInterface;
import com.example.bingo.R;

public class SingleMultiPlayerFragment extends Fragment {

    private MainInterface mainInterface;
    private static final String TAG = "SingleMultiPlayerFragme";
    private Button btnSinglePlayer;
    private Button btnMultiPlayer;
    private MediaPlayer player;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        player = MediaPlayer.create(getContext(), R.raw.click);
        mainInterface.setToolbarTitle(getTag());
        View view = inflater.inflate(R.layout.fragment_single_multipllayer,container,false);
        btnMultiPlayer = view.findViewById(R.id.btnMultiPlayer);
        btnSinglePlayer = view.findViewById(R.id.btnSinglePlayer);

        btnSinglePlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClickSound();
                mainInterface.onSinglePlayer();
            }
        });

        btnMultiPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playClickSound();
                mainInterface.onMultiPlayer();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainInterface = (MainInterface) getActivity();
    }

    private void playClickSound(){
        player.reset();
        player = MediaPlayer.create(getContext(),R.raw.click);
        player.start();
    }
}
