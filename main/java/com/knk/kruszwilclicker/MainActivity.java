package com.knk.kruszwilclicker;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.plattysoft.leonids.ParticleSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    long prestiz;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    //Timers and their tasks
    Timer timer;
    Timer saveTimer;
    TimerTask timerTask;
    TimerTask saveTask;

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

    final int KAWIOR_PRICE = 100,
            KAWIOR_MODIFIER = 1,
            WHISKYJURA_PRICE = 500,
            WHISKYJURA_MODIFIER = 5,
            SVALBARDI_PRICE = 2000,
            SVALBARDI_MODIFIER = 20,
            ZLOTO_PRICE = 50000,
            ZLOTO_MODIFIER = 50,
            DONPERIGNON_PRICE = 1000000,
            DONPERIGNON_MODIFIER = 100,


            KAMERZYSTA_PRICE = 20,
            KAMERZYSTA_MODIFIER = 1,
            SLUZACY_PRICE = 100,
            SLUZACY_MODIFIER = 10,
            AUDIA7_PRICE = 1000,
            AUDIA7_MODIFIER = 50,
            WILLA_PRICE = 10000,
            WILLA_MODIFIER = 100,
            GIELDA_PRICE = 100000,
            GIELDA_MODIFIER = 500;



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
        //PerClick
        addPowerUp(R.id.kawiorButton, R.id.kawiorCount, KAWIOR_MODIFIER, KAWIOR_PRICE, TYPE_PERCLICK);
        addPowerUp(R.id.whiskyJuraButton, R.id.whiskyJuraCount, WHISKYJURA_MODIFIER, WHISKYJURA_PRICE, TYPE_PERCLICK);
        addPowerUp(R.id.svalbardiButton, R.id.svalbardiCount, SVALBARDI_MODIFIER, SVALBARDI_PRICE, TYPE_PERCLICK);
        addPowerUp(R.id.zlotoButton, R.id.zlotoCount, ZLOTO_MODIFIER, ZLOTO_PRICE, TYPE_PERCLICK);
        addPowerUp(R.id.donPerignonButton, R.id.donPerignonCount, DONPERIGNON_MODIFIER, DONPERIGNON_PRICE, TYPE_PERCLICK);

        //OvertTime
        addPowerUp(R.id.kamerzystaButton, R.id.kamerzystaCount, KAMERZYSTA_MODIFIER, KAMERZYSTA_PRICE, TYPE_OVERTIME);
        addPowerUp(R.id.sluzacyButton, R.id.sluzacyCount, SLUZACY_MODIFIER, SLUZACY_PRICE, TYPE_OVERTIME);
        addPowerUp(R.id.audia7Button, R.id.audia7Count, AUDIA7_MODIFIER, AUDIA7_PRICE, TYPE_OVERTIME);
        addPowerUp(R.id.willaButton, R.id.willaCount, WILLA_MODIFIER, WILLA_PRICE, TYPE_OVERTIME);
        addPowerUp(R.id.gieldaButton, R.id.gieldaCount, GIELDA_MODIFIER, GIELDA_PRICE, TYPE_OVERTIME);


        //Load amount of powerups
        load();

        //Inflate menus
        overTimeMenuView = getLayoutInflater().inflate(R.layout.overtime_menu, null);
        perClickMenuView = getLayoutInflater().inflate(R.layout.perclick_menu, null);

        //Some onClicks
        glowka.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prestiz += clickValue;
                prestizCounter.setText(String.valueOf(prestiz));

                new ParticleSystem(MainActivity.this, 50, R.drawable.kruszwilek, 500)
                        .setSpeedRange(0.3f, 0.7f)
                        .setRotationSpeed(1000f)
                        .emit(view, 1, 500);
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
    }

    //Save everything and cancel timers
    @Override
    protected void onPause() {
        super.onPause();
        save();
        timer.cancel();
        saveTimer.cancel();
    }

    //Set timers again
    @Override
    protected void onResume() {
        super.onResume();
        //Timer for the over-time prestiż
        timer = new Timer();
        timerTask = new TimerTask() {
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

        //Timer to save every 5 minutes
        saveTimer = new Timer();
        saveTask = new TimerTask() {
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

        timer.schedule(timerTask,0,1000);
        saveTimer.schedule(saveTask, 0, 300000);

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
        //PerClick
        editor.putInt("kawiorCount", powerUps.get(R.id.kawiorButton).count);
        editor.putInt("whiskyJuraCount", powerUps.get(R.id.whiskyJuraButton).count);
        editor.putInt("svalbardiCount", powerUps.get(R.id.svalbardiButton).count);
        editor.putInt("zlotoCount", powerUps.get(R.id.zlotoButton).count);
        editor.putInt("donPerignonCount", powerUps.get(R.id.donPerignonButton).count);

        //OverTime
        editor.putInt("kamerzystaCount", powerUps.get(R.id.kamerzystaButton).count);
        editor.putInt("sluzacyCount", powerUps.get(R.id.sluzacyButton).count);
        editor.putInt("audia7Count", powerUps.get(R.id.audia7Button).count);
        editor.putInt("willaCount", powerUps.get(R.id.willaButton).count);
        editor.putInt("gieldaCount", powerUps.get(R.id.gieldaButton).count);

        editor.commit();

    }

    private void load() {
        //PerClick
        powerUps.get(R.id.kawiorButton).count = sharedPreferences.getInt("kawiorCount",0);
        powerUps.get(R.id.whiskyJuraButton).count = sharedPreferences.getInt("whiskyJuraCount",0);
        powerUps.get(R.id.svalbardiButton).count = sharedPreferences.getInt("svalbardiCount",0);
        powerUps.get(R.id.zlotoButton).count = sharedPreferences.getInt("zlotoCount",0);
        powerUps.get(R.id.donPerignonButton).count = sharedPreferences.getInt("donPerignonCount",0);

        //OverTime
        powerUps.get(R.id.kamerzystaButton).count = sharedPreferences.getInt("kamerzystaCount", 0);
        powerUps.get(R.id.sluzacyButton).count = sharedPreferences.getInt("sluzacyCount",0);
        powerUps.get(R.id.audia7Button).count = sharedPreferences.getInt("audia7Count",0);
        powerUps.get(R.id.willaButton).count = sharedPreferences.getInt("willaCount",0);
        powerUps.get(R.id.gieldaButton).count = sharedPreferences.getInt("gieldaCount",0);

    }

    private void setCounter(PowerUp powerUp) {
        if(powerUp instanceof OverTime) {
            ((TextView) overTimeMenuView.findViewById(powerUp.counterId)).setText(String.valueOf(powerUp.count));
        } else {
            ((TextView) perClickMenuView.findViewById(powerUp.counterId)).setText(String.valueOf(powerUp.count));

        }
    }

}
