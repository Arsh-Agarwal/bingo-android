package com.example.bingo.fragments;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bingo.interfaces.MainInterface;
import com.example.bingo.adapters.MyAdapter;
import com.example.bingo.R;

import java.util.ArrayList;

public class DeviceListFragment extends Fragment {

    private static final String TAG = "DeviceListFragment";
    private RecyclerView recyclerView;
    private MyAdapter adapter;
    private ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private MainInterface mainInterface;

    public DeviceListFragment(){

    }

    public DeviceListFragment(MainInterface mainInterface, ArrayList<BluetoothDevice> btDevices){
        this.mainInterface = mainInterface;
        this.btDevices = btDevices;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainInterface.setToolbarTitle(getTag());
        View view = inflater.inflate(R.layout.fragment_device_list,container,false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter = new MyAdapter(btDevices,mainInterface);
        recyclerView.setAdapter(adapter);
        return view;
    }

    public void deviceAdded(){
        adapter.notifyDataSetChanged();
    }
}
