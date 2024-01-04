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

public class EnableDiscoverableFragment extends Fragment {

    private static final String TAG = "EnableDiscoverableFragm";
    private Button btnEnableDiscoverable;
    private MainInterface mainInterface;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainInterface.setToolbarTitle(getTag());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enable_discoverable,container,false);
        btnEnableDiscoverable = view.findViewById(R.id.btnEnableDiscoverable);
        btnEnableDiscoverable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.enableDiscoverable();
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
