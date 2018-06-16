package com.knk.kruszwilclicker;

import android.content.SharedPreferences;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    long prestiz;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Every clicker's must have
    Button glowka;
    TextView prestizCounter;

    //Buttons with powerups
    Button overtimeButton;
    Button perClickButton;

    //Variables for powerups
    int clickValue;
    int overTimeValue;


    //Associates buttons with PowerUps
    Map<Integer, PowerUp> powerUps;

    //PowerUp views
    View overTimeMenuView;
    View perClickMenuView;

    final int WHISKYJURA_PRICE = 5,
            WHISKYJURA_MODIFIER = 5,
            KAMERZYSTA_PRICE = 10,
            KAMERZYSTA_MODIFIER = 1;

    final int TYPE_OVERTIME = 0,
            TYPE_PERCLICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("prefsy", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Load everything
        prestiz = sharedPreferences.getLong("prestiz", 0);
        clickValue = sharedPreferences.getInt("clickValue", 1);
        overTimeValue = sharedPreferences.getInt("overTimeValue", 0);


        powerUps = new HashMap<Integer, PowerUp>();

        //Associate buttons
        glowka = findViewById(R.id.button2);
        prestizCounter = findViewById(R.id.textView2);
        overtimeButton = findViewById(R.id.overTimeButton);
        perClickButton = findViewById(R.id.perClickButton);

        //Add powerups to the map and associate them with their buttons
        addPowerUp(R.id.whiskyJuraButton, R.id.whiskyJuraCount, WHISKYJURA_MODIFIER, WHISKYJURA_PRICE, TYPE_OVERTIME);
        addPowerUp(R.id.kamerzystaButton, R.id.kamerzystaCount, KAMERZYSTA_MODIFIER, KAMERZYSTA_PRICE, TYPE_PERCLICK);

        //Load amount of powerups
        powerUps.get(R.id.whiskyJuraButton).count = sharedPreferences.getInt("whiskyJuraCount",0);
        powerUps.get(R.id.kamerzystaButton).count = sharedPreferences.getInt("kamerzystaCount", 0);

        //Inflate menus
        overTimeMenuView = getLayoutInflater().inflate(R.layout.overtime_menu, null);
        perClickMenuView = getLayoutInflater().inflate(R.layout.perclick_menu, null);

        //Some onClicks
        glowka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prestiz += clickValue;
                prestizCounter.setText(String.valueOf(prestiz));
            }
        });

        overtimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOverTimeMenu();
            }
        });
        perClickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPerClickMenu();
            }
        });


        //Timer for the over-time prestiż
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        prestiz += overTimeValue;
                        prestizCounter.setText(String.valueOf(prestiz));
                    }
                });


            }
        };
        timer.schedule(timerTask,0,1000);

        Timer saveTimer = new Timer();
        TimerTask saveTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        save();
                    }
                });
            }
        };
        saveTimer.schedule(saveTask, 0, 300000);



    }

    //Save everything
    @Override
    protected void onPause() {
        super.onPause();
        save();
    }



    public void addPowerUp(int buttonId, int counterId, int modifier, int price, int type){
        PowerUp powerUp = null;
        if(type == TYPE_OVERTIME){
            powerUp = new OverTime(modifier,price,counterId, buttonId);
        }else if(type == TYPE_PERCLICK){
            powerUp = new PerClick(modifier, price, counterId, buttonId);
        }

        powerUps.put(buttonId,powerUp);
    }

    public void powerUpClick(View view){
        buy(powerUps.get(view.getId()),1.1);
    }


    //Create menus with powerUps
    private void createOverTimeMenu(){
        overTimeMenuView = getLayoutInflater().inflate(R.layout.overtime_menu, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(overTimeMenuView);


        for(Map.Entry<Integer, PowerUp> entry : powerUps.entrySet()) {
            setCounter(entry.getValue());
        }


        builder.show();
    }

    private void createPerClickMenu() {
        perClickMenuView = getLayoutInflater().inflate(R.layout.perclick_menu, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(perClickMenuView);


        for(Map.Entry<Integer, PowerUp> entry : powerUps.entrySet()) {
            setCounter(entry.getValue());
        }




        builder.show();

    }


    public void buy(PowerUp powerUp, double priceModifier){
        powerUp.count++;
        powerUp.price *= priceModifier;
        prestiz-=powerUp.price;

        if(powerUp instanceof PerClick){
            clickValue += powerUp.modifier;
        }else{
            overTimeValue += powerUp.modifier;
        }
        prestizCounter.setText(String.valueOf(prestiz));

        //Amount of powerUps
        setCounter(powerUp);
    }

    private void save() {
        editor.putLong("prestiz",prestiz);
        editor.putInt("overTimeValue", overTimeValue);
        editor.putInt("clickValue", clickValue);

        //PowerUps' counts
        editor.putInt("whiskyJuraCount", powerUps.get(R.id.whiskyJuraButton).count );
        editor.putInt("kamerzystaCount", powerUps.get(R.id.kamerzystaButton).count);

        editor.commit();

    }

    private void setCounter(PowerUp powerUp) {
        if(powerUp instanceof OverTime) {
            ((TextView) overTimeMenuView.findViewById(powerUp.counterId)).setText(String.valueOf(powerUp.count));
        } else {
            ((TextView) perClickMenuView.findViewById(powerUp.counterId)).setText(String.valueOf(powerUp.count));

        }
    }

}
