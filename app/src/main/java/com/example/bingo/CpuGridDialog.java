package com.example.bingo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.bingo.adapters.CpuGridAdapter;
import com.example.bingo.fragments.VirtualGridElement;

import java.util.ArrayList;

public class CpuGridDialog extends DialogFragment {

    private static final String TAG = "CpuGridDialog";

    private CpuGridAdapter cpuGridAdapter;
    private Button btnBack;
    private GridView cpuGrid;
    private int gridOrder;
    private ArrayList<VirtualGridElement> virtualGridElements = new ArrayList<>();

    public CpuGridDialog(int gridOrder,ArrayList<VirtualGridElement> virtualGridElements){
        this.gridOrder = gridOrder;
        this.virtualGridElements = virtualGridElements;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        cpuGridAdapter = new CpuGridAdapter(gridOrder,virtualGridElements);
        cpuGrid.setNumColumns(gridOrder);
        cpuGrid.setAdapter(cpuGridAdapter);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_cpu_grid,container,false);
        btnBack = view.findViewById(R.id.backBtn);
        cpuGrid = view.findViewById(R.id.cpuGrid);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });
        return view;
    }
}
