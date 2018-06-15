package com.knk.kruszwilclicker;

import android.content.SharedPreferences;
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

    Button glowka;
    TextView prestizCounter;


    Button overtimeButton;

    int clickValue;
    int overTimeValue;

    PowerUp dziewice;
    PowerUp kamerzysta;

    Map<Integer, PowerUp> powerUps;

    View overTimeMenuView;

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

        prestiz = sharedPreferences.getLong("prestiz", 0);
        clickValue = sharedPreferences.getInt("clickValue", 1);
        overTimeValue = sharedPreferences.getInt("overTimeValue", 0);

        powerUps = new HashMap<Integer, PowerUp>();


        glowka = findViewById(R.id.button2);
        prestizCounter = findViewById(R.id.textView2);
        overtimeButton = findViewById(R.id.overTimeButton);

        addPowerUp(R.id.whiskyJuraButton, R.id.whiskyJuraCount, WHISKYJURA_MODIFIER, WHISKYJURA_PRICE, TYPE_OVERTIME);

        overTimeMenuView = getLayoutInflater().inflate(R.layout.overtime_menu, null);

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

    }

    @Override
    protected void onPause() {
        super.onPause();
        editor.putLong("prestiz",prestiz);
        editor.putInt("overTimeValue", overTimeValue);
        editor.putInt("clickValue", clickValue);
        editor.commit();
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

    private void createOverTimeMenu(){
        overTimeMenuView = getLayoutInflater().inflate(R.layout.overtime_menu, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(overTimeMenuView);

       // AlertDialog alertDialog = builder.create();
       // alertDialog.show();
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
        //powerUp.counter.setText(powerUp.count);
        prestizCounter.setText(String.valueOf(prestiz));
        ((TextView)overTimeMenuView.findViewById(powerUp.counterId)).setText(String.valueOf(powerUp.count));
    }


}
