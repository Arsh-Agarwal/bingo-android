package com.example.bingo.fragments;

import android.content.Context;
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

public class SPWinFragment extends Fragment {

    private static final String TAG = "WinFragment";
    private MainInterface mainInterface;
    private Button btnRestart;
    private Button btnQuit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainInterface.setToolbarTitle(getTag());
        View view = inflater.inflate(R.layout.fragment_win,container,false);
        btnQuit = view.findViewById(R.id.btnQuit);
        btnRestart = view.findViewById(R.id.btnRestart);

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.onSPGameRestart();
            }
        });

        btnQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.onQuit();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainInterface = (MainInterface) getActivity();
    }

}
