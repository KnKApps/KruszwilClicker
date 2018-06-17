package com.knk.kruszwilclicker;

import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.plattysoft.leonids.ParticleSystem;



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
    TextView prestizCounter;
    TextView overtimeCounter;

    //Buttons with powerups
    Button overtimeButton;
    Button perClickButton;

    //Variables for powerups
    int clickValue;
    int overTimeValue;


    //Associates buttons with PowerUps
    Map<View, PowerUp> powerUps;

    //PowerUp views
    View overTimeMenuView;
    View perClickMenuView;

    LinearLayout overtimeLayout;
    LinearLayout perClickLayout;

    final int KAWIOR_PRICE = 100,
            KAWIOR_MODIFIER = 1,
            KAWIOR_MAX = 1000,
            WHISKYJURA_PRICE = 500,
            WHISKYJURA_MODIFIER = 5,
            WHISKYJURA_MAX = 1000,
            SVALBARDI_PRICE = 2000,
            SVALBARDI_MODIFIER = 20,
            SVALBARDI_MAX = 1000,
            ZLOTO_PRICE = 50000,
            ZLOTO_MODIFIER = 50,
            ZLOTO_MAX = 1000,
            DONPERIGNON_PRICE = 1000000,
            DONPERIGNON_MODIFIER = 100,
            DONPERIGNON_MAX = 1000,


            KAMERZYSTA_PRICE = 20,
            KAMERZYSTA_MODIFIER = 1,
            KAMERZYSTA_MAX = 1000,
            SLUZACY_PRICE = 100,
            SLUZACY_MODIFIER = 10,
            SLUZACY_MAX = 1000,
            AUDIA7_PRICE = 1000,
            AUDIA7_MODIFIER = 50,
            AUDIA7_MAX = 1000,
            WILLA_PRICE = 10000,
            WILLA_MODIFIER = 100,
            WILLA_MAX = 1000,
            GIELDA_PRICE = 100000,
            GIELDA_MODIFIER = 500,
            GIELDA_MAX = 1000;



    final int TYPE_OVERTIME = 0,
            TYPE_PERCLICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("prefsy", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //Associate buttons
        prestizCounter = findViewById(R.id.counterTop);
        overtimeCounter = findViewById(R.id.counterBottom);
        overtimeButton = findViewById(R.id.overTimeButton);
        perClickButton = findViewById(R.id.perClickButton);

        //Load everything
        prestiz = sharedPreferences.getLong("prestiz", 0);
        clickValue = sharedPreferences.getInt("clickValue", 1);
        overTimeValue = sharedPreferences.getInt("overTimeValue", 0);
        prestizCounter.setText(getString(R.string.counterTop, prestiz));
        overtimeCounter.setText(getString(R.string.counterBottom, overTimeValue));


        powerUps = new LinkedHashMap<View, PowerUp>();



        //Add powerups to the map and associate them with their buttons
        //PerClick
        addPowerUp(KAWIOR_MODIFIER, KAWIOR_PRICE, TYPE_PERCLICK, KAWIOR_MAX, "Kawior");
        addPowerUp(WHISKYJURA_MODIFIER, WHISKYJURA_PRICE, TYPE_PERCLICK, WHISKYJURA_MAX, "Whisky Jura");
        addPowerUp(SVALBARDI_MODIFIER, SVALBARDI_PRICE, TYPE_PERCLICK, SVALBARDI_MAX, "Svalbardi");
        addPowerUp(ZLOTO_MODIFIER, ZLOTO_PRICE, TYPE_PERCLICK, ZLOTO_MAX, "Złoto");
        addPowerUp(DONPERIGNON_MODIFIER, DONPERIGNON_PRICE, TYPE_PERCLICK, DONPERIGNON_MAX, "Don Perignon");

        //OvertTime
        addPowerUp(KAMERZYSTA_MODIFIER, KAMERZYSTA_PRICE, TYPE_OVERTIME, KAMERZYSTA_MAX, "Kamerzysta");
        addPowerUp(SLUZACY_MODIFIER, SLUZACY_PRICE, TYPE_OVERTIME, SLUZACY_MAX, "Służący");
        addPowerUp(AUDIA7_MODIFIER, AUDIA7_PRICE, TYPE_OVERTIME, AUDIA7_MAX, "Audi A7");
        addPowerUp(WILLA_MODIFIER, WILLA_PRICE, TYPE_OVERTIME, WILLA_MAX, "Willa");
        addPowerUp(GIELDA_MODIFIER, GIELDA_PRICE, TYPE_OVERTIME, GIELDA_MAX, "Giełda Kryptowalut");

        //Load amount of powerups
        load();

        //Some onClicks
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

    public void mainClick(View view){
        prestiz += clickValue;
        prestizCounter.setText(getString(R.string.counterTop, prestiz));

        new ParticleSystem(MainActivity.this, 50, R.drawable.kruszwilek, 500)
                .setSpeedRange(0.3f, 0.7f)
                .setRotationSpeed(1000f)
                .emit(view, 1, 500);
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
                        prestizCounter.setText(getString(R.string.counterTop, prestiz));
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

    public void addPowerUp(int modifier, int price, int type, int max, String name){
        PowerUp powerUp = null;

        View view = getLayoutInflater().inflate(R.layout.menu_item, null);
        ((TextView)view.findViewById(R.id.menu_name)).setText(name);
        ((TextView)view.findViewById(R.id.menu_description)).setText(
                (type == TYPE_OVERTIME)
                        ?getString(R.string.overTimeMenuString, String.valueOf(modifier))
                        :getString(R.string.perClickMenuString, String.valueOf(modifier))
        );

        ((Button)view.findViewById(R.id.menu_button)).setText(String.valueOf(price));
        ((ProgressBar)view.findViewById(R.id.menu_progress)).setMax(max);
        ((ProgressBar)view.findViewById(R.id.menu_progress)).setProgress(sharedPreferences.getInt(name, 0));

        if(type == TYPE_OVERTIME){
            powerUp = new OverTime(modifier,price,view, name);
        }else{
            powerUp = new PerClick(modifier,price,view, name);
        }
        powerUps.put(view,powerUp);

    }

    public void powerUpClick(View view){
        buy(powerUps.get(view.getParent().getParent()),1.1f);
    }


    //Create menus with powerUps
    private void createOverTimeMenu(){
        overTimeMenuView = getLayoutInflater().inflate(R.layout.overtime_menu, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(overTimeMenuView);

        overtimeLayout = (LinearLayout) overTimeMenuView.findViewById(R.id.overtimeLinearLayout);

        for(Map.Entry<View, PowerUp> entry : powerUps.entrySet()) {
            if (entry.getValue() instanceof OverTime) {
                if (entry.getKey().getParent() != null) {
                    ((LinearLayout) entry.getKey().getParent()).removeView(entry.getKey());
                    ((ProgressBar) entry.getKey().findViewById(R.id.menu_progress)).setProgress(entry.getValue().getCount());
                }
                overtimeLayout.addView(entry.getKey());
            }
        }
        View dismissButton = getLayoutInflater().inflate(R.layout.dismiss_button, null);

        overtimeLayout.addView(dismissButton);

        final AlertDialog alertDialog = builder.create();
        dismissButton.findViewById(R.id.dismissButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void createPerClickMenu() {
        perClickMenuView = getLayoutInflater().inflate(R.layout.perclick_menu, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(perClickMenuView);

        perClickLayout = (LinearLayout) perClickMenuView.findViewById(R.id.perClickLinearLayout);

        for(Map.Entry<View, PowerUp> entry : powerUps.entrySet()) {
            if(entry.getValue() instanceof PerClick){
                if (entry.getKey().getParent() != null) {
                    ((LinearLayout) entry.getKey().getParent()).removeView(entry.getKey());
                    ((ProgressBar) entry.getKey().findViewById(R.id.menu_progress)).setProgress(entry.getValue().getCount());
                }
                perClickLayout.addView(entry.getKey());
            }
        }
        View dismissButton = getLayoutInflater().inflate(R.layout.dismiss_button, null);

        perClickLayout.addView(dismissButton);

        final AlertDialog alertDialog = builder.create();
        dismissButton.findViewById(R.id.dismissButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
    public void buy(PowerUp powerUp, float priceModifier){

            powerUp.increment();
            powerUp.setPrice(Math.round((powerUp.getPrice() * priceModifier)));
            prestiz-=powerUp.getPrice();

            if(powerUp instanceof PerClick){
                clickValue += powerUp.getModifier();
            }else{
                overTimeValue += powerUp.getModifier();
            }
            prestizCounter.setText(getString(R.string.counterTop, prestiz));
            overtimeCounter.setText(getString(R.string.counterBottom, overTimeValue));
            ((Button)powerUp.getView().findViewById(R.id.menu_button)).setText(String.valueOf(powerUp.getPrice()));
            ((ProgressBar)powerUp.getView().findViewById(R.id.menu_progress)).setProgress(powerUp.getCount());


    }

    private void save() {
        editor.putLong("prestiz",prestiz);
        editor.putInt("overTimeValue", overTimeValue);
        editor.putInt("clickValue", clickValue);

        for(Map.Entry<View, PowerUp> entry : powerUps.entrySet()){
            editor.putInt(entry.getValue().getName(), entry.getValue().getCount());
        }

        editor.commit();
    }

    private void load() {
        for(Map.Entry<View, PowerUp> entry : powerUps.entrySet()){
            entry.getValue().setCount(sharedPreferences.getInt(entry.getValue().getName(), 0));
        }
    }
}
