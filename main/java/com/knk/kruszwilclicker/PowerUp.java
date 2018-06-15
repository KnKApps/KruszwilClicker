package com.knk.kruszwilclicker;

import android.widget.TextView;


public abstract class PowerUp {
    int count;
    int price;
    int modifier;
    int buttonId;
    int counterId;

    public PowerUp(int modifier, int price, int counterId, int buttonId) {
        this.count = 0;
        this.modifier = modifier;
        this.price = price;
        this.counterId = counterId;
        this.buttonId = buttonId;
    }


}
