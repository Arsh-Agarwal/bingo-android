package com.example.bingo.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.example.bingo.CustomButton;
import com.example.bingo.R;

import java.util.ArrayList;
import java.util.Random;

public class GridAdapter extends BaseAdapter {

    ArrayList<Integer> integers = new ArrayList<>();
    private static final String TAG = "GridAdapter";
    private ItemClickListener mItemClickListener;
    ArrayList<CustomButton> buttons = new ArrayList<>();
    private int gridOrder;

    public GridAdapter(ItemClickListener itemClickListener, int gridOrder) {
        this.mItemClickListener = itemClickListener;
        this.gridOrder = gridOrder;
        for(int i = 0; i < gridOrder*gridOrder-1 ; i++){
            integers.add(i);
        }
    }

    @Override
    public int getCount() {
        return gridOrder*gridOrder;
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
            integerButton =(CustomButton) LayoutInflater.from(parent.getContext()).inflate(R.layout.integer_button,parent,false);
        }else{
            integerButton = (CustomButton) convertView;
        }

        Random rand = new Random();
        if (position == 0){
            integerButton.setText(String.valueOf(gridOrder*gridOrder));
        } else {
            int temp;
            if (gridOrder*gridOrder-2 - position > 0) {
                temp = rand.nextInt(gridOrder*gridOrder-1 - position);
            } else {
                temp = 0;
            }
            int temp_int = integers.remove(temp);
            integerButton.setText(String.valueOf(temp_int+1));
        }
        integerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemClickListener.onItemClick(position);
            }
        });
        buttons.add(integerButton);
        return integerButton;
    }



    public ArrayList<CustomButton> getButtons(){
        return buttons;
    }

    public interface ItemClickListener{
        void onItemClick(int position);
    }
}
