package com.knk.kruszwilclicker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.LinkedHashMap;

import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.plattysoft.leonids.ParticleSystem;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.plattysoft.leonids.ParticleSystem;

import co.infinum.princeofversions.LoaderFactory;
import co.infinum.princeofversions.PrinceOfVersions;
import co.infinum.princeofversions.UpdaterResult;
import co.infinum.princeofversions.callbacks.UpdaterCallback;


public class MainActivity extends AppCompatActivity {
    float prestiz;
    int prestizMultiplier = 1;

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
    float clickValue;
    float overTimeValue;


    //Associates buttons with PowerUps
    Map<View, PowerUp> powerUps;

    //PowerUp views
    View overTimeMenuView;
    View perClickMenuView;


    LinearLayout overtimeLayout;
    LinearLayout perClickLayout;

    final int KAWIOR_PRICE = 100,
            KAWIOR_MAX = 500,
            WHISKYJURA_PRICE = 500,
            WHISKYJURA_MAX = 300,
            SVALBARDI_PRICE = 2000,
            SVALBARDI_MAX = 200,
            ZLOTO_PRICE = 5000,
            ZLOTO_MAX = 1000,
            DONPERIGNON_PRICE = 100000,
            DONPERIGNON_MAX = 10,


            KAMERZYSTA_PRICE = 15,
            KAMERZYSTA_MAX = 500,
            SLUZACY_PRICE = 100,
            SLUZACY_MAX = 450,
            AUDIA7_PRICE = 1000,
            AUDIA7_MAX = 300,
            WILLA_PRICE = 150000,
            WILLA_MAX = 100,
            GIELDA_PRICE = 1000000,
            GIELDA_MAX = 50;


    final float KAWIOR_MODIFIER = 0.1f,
            WHISKYJURA_MODIFIER = 2f,
            SVALBARDI_MODIFIER = 5f,
            ZLOTO_MODIFIER = 10f,
            DONPERIGNON_MODIFIER = 20f,

            KAMERZYSTA_MODIFIER = 0.1f,
            SLUZACY_MODIFIER = 2f,
            AUDIA7_MODIFIER = 200f,
            WILLA_MODIFIER = 500f,
            GIELDA_MODIFIER = 9999f;



    final int TYPE_OVERTIME = 0,
            TYPE_PERCLICK = 1;


    //Updater
    Intent serviceIntent;


    // ;)
    private static final String TAG = "MainActivity";
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    boolean wasAdActivated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                if(wasAdActivated) {
                    mRewardedVideoAd.show();
                    wasAdActivated = false;
                }
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
                wasAdActivated = false;
                loadRewardedVideoAd();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                prestizMultiplier = 3;
                overtimeCounter.setText(getString(R.string.counterBottom, overTimeValue*prestizMultiplier));


                new CountDownTimer(60000,1000) {
                    @Override
                    public void onTick(long l) {
                        ((Button)findViewById(R.id.button)).setText(String.valueOf(l/1000));
                    }

                    @Override
                    public void onFinish() {
                        ((Button)findViewById(R.id.button)).setText("X3");
                        prestizMultiplier = 1;
                        overtimeCounter.setText(getString(R.string.counterBottom, overTimeValue*prestizMultiplier));
                    }
                }.start();

                wasAdActivated = false;
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });

        loadRewardedVideoAd();


        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sharedPreferences = getSharedPreferences("prefsy", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        //Associate buttons
        prestizCounter = findViewById(R.id.counterTop);
        overtimeCounter = findViewById(R.id.counterBottom);
        overtimeButton = findViewById(R.id.overTimeButton);
        perClickButton = findViewById(R.id.perClickButton);

        //Load everything
        prestiz = sharedPreferences.getFloat("prestiz", 0f);
        clickValue = sharedPreferences.getFloat("clickValue", 1f);
        overTimeValue = sharedPreferences.getFloat("overTimeValue", 0.0f);
        prestizCounter.setText(getString(R.string.counterTop, prestiz));
        overtimeCounter.setText(getString(R.string.counterBottom, overTimeValue));


        powerUps = new LinkedHashMap<View, PowerUp>();


        //Add powerups to the map and associate them with their buttons
        //PerClick

        addPowerUp(KAWIOR_MODIFIER, KAWIOR_PRICE, TYPE_PERCLICK, KAWIOR_MAX, getString(R.string.perClick1));
        addPowerUp(WHISKYJURA_MODIFIER, WHISKYJURA_PRICE, TYPE_PERCLICK, WHISKYJURA_MAX, getString(R.string.perClick2));
        addPowerUp(SVALBARDI_MODIFIER, SVALBARDI_PRICE, TYPE_PERCLICK, SVALBARDI_MAX, getString(R.string.perClick3));
        addPowerUp(ZLOTO_MODIFIER, ZLOTO_PRICE, TYPE_PERCLICK, ZLOTO_MAX, getString(R.string.perClick4));
        addPowerUp(DONPERIGNON_MODIFIER, DONPERIGNON_PRICE, TYPE_PERCLICK, DONPERIGNON_MAX, getString(R.string.perClick5));

        //OvertTime
        addPowerUp(KAMERZYSTA_MODIFIER, KAMERZYSTA_PRICE, TYPE_OVERTIME, KAMERZYSTA_MAX, getString(R.string.overtime1));
        addPowerUp(SLUZACY_MODIFIER, SLUZACY_PRICE, TYPE_OVERTIME, SLUZACY_MAX, getString(R.string.overtime2));
        addPowerUp(AUDIA7_MODIFIER, AUDIA7_PRICE, TYPE_OVERTIME, AUDIA7_MAX, getString(R.string.overtime3));
        addPowerUp(WILLA_MODIFIER, WILLA_PRICE, TYPE_OVERTIME, WILLA_MAX, getString(R.string.overtime4));
        addPowerUp(GIELDA_MODIFIER, GIELDA_PRICE, TYPE_OVERTIME, GIELDA_MAX, getString(R.string.overtime5));

      addPowerUp(KAMERZYSTA_MODIFIER, KAMERZYSTA_PRICE, TYPE_OVERTIME, KAMERZYSTA_MAX, getString(R.string.overtime1));
        addPowerUp(SLUZACY_MODIFIER, SLUZACY_PRICE, TYPE_OVERTIME, SLUZACY_MAX, getString(R.string.overtime2));
        addPowerUp(AUDIA7_MODIFIER, AUDIA7_PRICE, TYPE_OVERTIME, AUDIA7_MAX, getString(R.string.overtime3));
        addPowerUp(WILLA_MODIFIER, WILLA_PRICE, TYPE_OVERTIME, WILLA_MAX, getString(R.string.overtime4));
        addPowerUp(GIELDA_MODIFIER, GIELDA_PRICE, TYPE_OVERTIME, GIELDA_MAX, getString(R.string.overtime5));

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
      


        //Updater
        serviceIntent = new Intent(this, UpdateService.class);
        if (!isMyServiceRunning(UpdateService.class)) startService(serviceIntent);
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917",
                new AdRequest.Builder().build());
    }

    //Checks whether service is running
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    public void mainClick(View view){
        prestiz += clickValue*prestizMultiplier;
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


    @Override
    protected void onDestroy() {
        stopService(serviceIntent);
        super.onDestroy();
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
                        setPowerUpsBackground();
                        prestiz += (overTimeValue*prestizMultiplier)/10;
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


        timer.schedule(timerTask, 0, 100);
        saveTimer.schedule(saveTask, 0, 300000);

    }
    public void addPowerUp(float modifier, int price, int type, int max, String name){

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
        buy(powerUps.get(view.getParent().getParent()),1.15f);
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
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                //saving original dialog size, as adding a background drawable changes it
                int h, w;
                if(overTimeMenuView.getHeight() > overtimeLayout.getHeight()){
                     h = overtimeLayout.getHeight();
                     w = overtimeLayout.getWidth();
                }else{
                     h = overTimeMenuView.getHeight();
                     w = overTimeMenuView.getWidth();
                }

                alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg3));

                alertDialog.getWindow().setLayout(w,h);

            }
        });
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
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {

                //saving original dialog size, as adding a background drawable changes it
                int h, w;
                if(perClickMenuView.getHeight() > perClickLayout.getHeight()){
                    h = perClickLayout.getHeight();
                    w = perClickLayout.getWidth();
                }else{
                    h = perClickMenuView.getHeight();
                    w = perClickMenuView.getWidth();
                }

                alertDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(R.drawable.bg3));
                alertDialog.getWindow().setLayout(w,h);
            }
        });
        dismissButton.findViewById(R.id.dismissButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.show();

    }
    public void buy(PowerUp powerUp, float priceModifier){
        if(Math.floor(prestiz) >= powerUp.getPrice()) {
            powerUp.increment();
            prestiz -= powerUp.getPrice();
            powerUp.setPrice(Math.round((powerUp.getPrice() * priceModifier)));

            if (powerUp instanceof PerClick) {
                clickValue += powerUp.getModifier();
            } else {
                overTimeValue += powerUp.getModifier();
            }
            prestizCounter.setText(getString(R.string.counterTop, prestiz));
            overtimeCounter.setText(getString(R.string.counterBottom, overTimeValue * prestizMultiplier));
            ((Button) powerUp.getView().findViewById(R.id.menu_button)).setText(String.valueOf(powerUp.getPrice()));
            ((ProgressBar) powerUp.getView().findViewById(R.id.menu_progress)).setProgress(powerUp.getCount());
        }


    }

    public void onBoostClick(View view) {
        createRewardedAdDialog();
    }

    private void createRewardedAdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Prestiż x3!");
        builder.setMessage("Czy chcesz obejrzeć reklamę i przyspieszyć zdobywanie prestiżu?\nPrestiż x3 przez 60 sekund.");
        builder.setNegativeButton("NIE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.setPositiveButton("TAK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mRewardedVideoAd.isLoaded()) {
                    mRewardedVideoAd.show();
                } else {
                    Toast.makeText(getApplicationContext(), "Poczekaj na wczytanie reklamy", Toast.LENGTH_LONG).show();
                }

                dialogInterface.dismiss();
                wasAdActivated = true;
            }
        });

        final AlertDialog alertDialog = builder.create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));

            }
        });

        alertDialog.show();

    }

    private void setPowerUpsBackground() {
        for (Map.Entry<View, PowerUp> entry : powerUps.entrySet()) {
            if(entry.getValue().getPrice() <= prestiz) {
                entry.getKey().findViewById(R.id.menu_button).setBackgroundResource(R.drawable.buttonks);
                ((Button)entry.getKey().findViewById(R.id.menu_button)).setTextColor(Color.parseColor("#FFFFFF"));

            } else {
                entry.getKey().findViewById(R.id.menu_button).setBackgroundResource(R.drawable.buttonks_locked);
                ((Button)entry.getKey().findViewById(R.id.menu_button)).setTextColor(Color.parseColor("#CCCCCC"));

            }
        }
    }

    private void save() {
        editor.putFloat("prestiz",prestiz);
        editor.putFloat("overTimeValue", overTimeValue);
        editor.putFloat("clickValue", clickValue);

        for(Map.Entry<View, PowerUp> entry : powerUps.entrySet()){
            editor.putInt(entry.getValue().getName(), entry.getValue().getCount());
        }

        editor.commit();
    }

    private void load() {
        for(Map.Entry<View, PowerUp> entry : powerUps.entrySet()){
            entry.getValue().setCount(sharedPreferences.getInt(entry.getValue().getName(), 0));
            if (entry.getValue().getCount() != 0)
            entry.getValue().setPrice(Math.round((entry.getValue().getCount()*entry.getValue().getBasePrice()*1.15f)));
            ((Button) entry.getValue().getView().findViewById(R.id.menu_button)).setText(String.valueOf(entry.getValue().getPrice()));

        }
    }
}
