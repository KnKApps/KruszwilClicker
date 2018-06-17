package com.knk.kruszwilclicker;

import android.view.View;

public abstract class PowerUp {
    private int count;
    private int price;
    private int modifier;
    private int max;
    private String name;
    private View view;

    public PowerUp(int modifier, int price, View view, String name) {
        this.count = 0;
        this.modifier = modifier;
        this.price = price;
        this.view = view;
        this.name = name;
    }
    public void increment(){
        this.count++;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setModifier(int modifier) {
        this.modifier = modifier;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setView(View view) {
        this.view = view;
    }

    public int getCount() {
        return count;
    }

    public int getPrice() {
        return price;
    }

    public int getModifier() {
        return modifier;
    }

    public int getMax() {
        return max;
    }

    public String getName() {
        return name;
    }

    public View getView() {
        return view;
    }
}
