package com.example.bingo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.bingo.CustomButton;
import com.example.bingo.R;
import com.example.bingo.fragments.VirtualGridElement;

import java.util.ArrayList;

public class CpuGridAdapter extends BaseAdapter {

    private int gridOrder;
    private ArrayList<VirtualGridElement> virtualGridElements = new ArrayList<>();

    public CpuGridAdapter(int gridOrder,ArrayList<VirtualGridElement> virtualGridElements){
        this.gridOrder = gridOrder;
        this.virtualGridElements = virtualGridElements;
    }

    @Override
    public int getCount() {
        return virtualGridElements.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CustomButton integerButton;
        if(convertView == null){
            integerButton = (CustomButton) LayoutInflater.from(parent.getContext()).inflate(R.layout.cpu_grid_integer_button,parent,false);
        }else{
            integerButton = (CustomButton) convertView;
        }

        if(virtualGridElements.get(position).isSelected()){
            integerButton.setText(virtualGridElements.get(position).getStringValue());
        }else{
            integerButton.setBackgroundResource(R.drawable.default_grey_background);
            integerButton.setText(" ");
        }

        return integerButton;
    }
}
