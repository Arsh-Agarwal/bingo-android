package com.example.bingo.fragments;

public class VirtualGridElement {

    private Integer value;
    private boolean selected = false;

    public Integer getValue() {
        return value;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getStringValue(){
        return value.toString();
    }

    public void setValueFromString(String sValue){
        value = Integer.parseInt(sValue);
    }
}
