package com.example.bingo.adapters;

import android.bluetooth.BluetoothDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bingo.R;
import com.example.bingo.interfaces.MainInterface;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private static final String TAG = "MyAdapter";
    private ArrayList<BluetoothDevice> btDevices = new ArrayList<>();
    private MainInterface mainInterface;

    public MyAdapter(ArrayList<BluetoothDevice> btDevices,MainInterface mainInterface) {
        this.btDevices = btDevices;
        this.mainInterface = mainInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,parent,false);
        return new ViewHolder(view,mainInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.deviceName.setText(btDevices.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return btDevices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView deviceName;
        private MainInterface mainInterface;

        public ViewHolder(@NonNull View itemView,MainInterface mainInterface) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.deviceName);
            itemView.setOnClickListener(this);
            this.mainInterface = mainInterface;
        }

        @Override
        public void onClick(View v) {
            mainInterface.onDeviceClick(getAdapterPosition());
        }
    }
}
