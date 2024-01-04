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

public class HostClientFragment extends Fragment {

    private static final String TAG = "HostClientFragment";
    private Button btnHost;
    private Button btnClient;
    private MainInterface mainInterface;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_host_client,container,false);
        btnClient = view.findViewById(R.id.btnClient);
        btnHost = view.findViewById(R.id.btnHost);
        mainInterface.setToolbarTitle(getTag());

        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.becomeHost();
            }
        });

        btnClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainInterface.becomeClient();
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
