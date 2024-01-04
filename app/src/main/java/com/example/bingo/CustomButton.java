package com.example.bingo;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomButton extends androidx.appcompat.widget.AppCompatButton {

    private boolean selected = false;

    public CustomButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setSelected(boolean value){
        selected = value;
    }

    public boolean isSelected(){
        return selected;
    }
}
