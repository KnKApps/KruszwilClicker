package com.knk.kruszwilclicker;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;

import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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


import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends AppCompatActivity {
    float prestiz;
    int prestizMultiplier = 1;

    int[] sounds;
    Random rand;
    int clickCounter = 0;

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
    Button boostButton;

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

    final int
            SUSHI_MAX = 200,
            KAWIOR_MAX = 150,
            HANEBISHO_MAX = 100,
            DZIEWICA_MAX = 80,
            WHISKYJURA_MAX = 60,
            SVALBARDI_MAX = 40,
            ZLOTO_MAX = 25,
            DONPERIGNON_MAX = 10,
            KANAL_MAX = 200,
            YEEZY_MAX = 150,
            IPHONE_MAX = 100,
            KAMERZYSTA_MAX = 80,
            SLUZACY_MAX = 60,
            AUDIA7_MAX = 40,
            WILLA_MAX = 25,
            GIELDA_MAX = 10;


    final long
            SUSHI_PRICE = 100,
            KAWIOR_PRICE = 1000,
            HANEBISHO_PRICE = 5000,
            DZIEWICA_PRICE = 10000,
            WHISKYJURA_PRICE = 50000,
            SVALBARDI_PRICE = 200000,
            ZLOTO_PRICE = 1000000,
            DONPERIGNON_PRICE = 10000000,


            KANAL_PRICE = 15,
            YEEZY_PRICE = 100,
            IPHONE_PRICE = 1100,
            KAMERZYSTA_PRICE = 130000,
            SLUZACY_PRICE = 1400000,
            AUDIA7_PRICE = 20000000,
            WILLA_PRICE = 330000000,
            GIELDA_PRICE = 5100000000L;


    final float
            SUSHI_MODIFIER = 0.2f,
            KAWIOR_MODIFIER = 0.7f,
            HANEBISHO_MODIFIER = 1.5f,
            DZIEWICA_MODIFIER = 4.5f,
            WHISKYJURA_MODIFIER = 7.5f,
            SVALBARDI_MODIFIER = 12,
            ZLOTO_MODIFIER = 18,
            DONPERIGNON_MODIFIER = 30,

            KANAL_MODIFIER = 0.2f,
            YEEZY_MODIFIER = 1.3f,
            IPHONE_MODIFIER = 37f,
            KAMERZYSTA_MODIFIER = 230f,
            SLUZACY_MODIFIER = 1100f,
            AUDIA7_MODIFIER = 6800f,
            WILLA_MODIFIER = 34000f,
            GIELDA_MODIFIER = 210000f;



    final int TYPE_OVERTIME = 0,
            TYPE_PERCLICK = 1;


    //Updater
    Intent serviceIntent;


    // ;)
    private static final String TAG = "MainActivity";
    private AdView mAdView;
    private RewardedVideoAd mRewardedVideoAd;
    boolean wasAdActivated = false;


    MediaPlayer mediaPlayer;


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
                boostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                prestizMultiplier = 2;
                overtimeCounter.setText(getString(R.string.counterBottom, (overTimeValue*prestizMultiplier),(clickValue*prestizMultiplier)));



                new CountDownTimer(60000,1000) {
                    @Override
                    public void onTick(long l) {
                        ((Button)findViewById(R.id.boostButton)).setText(String.valueOf(l/1000));
                    }

                    @Override
                    public void onFinish() {
                        ((Button)findViewById(R.id.boostButton)).setText("X2");
                        prestizMultiplier = 1;
                        overtimeCounter.setText(getString(R.string.counterBottom, (overTimeValue*prestizMultiplier),(clickValue*prestizMultiplier)));

                        boostButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                onBoostClick(view);
                            }
                        });
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


        MobileAds.initialize(this,getString(R.string.admob_app_id));
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        sharedPreferences = getSharedPreferences("inneścoś", MODE_PRIVATE);
        editor = sharedPreferences.edit();


        //Associate buttons
        prestizCounter = findViewById(R.id.counterTop);
        overtimeCounter = findViewById(R.id.counterBottom);
        overtimeButton = findViewById(R.id.overTimeButton);
        perClickButton = findViewById(R.id.perClickButton);
        boostButton = findViewById(R.id.boostButton);

        //Load everything
        if(sharedPreferences.getAll().get("prestiz") instanceof Long || sharedPreferences.getAll().get("prestiz") instanceof Integer){
            editor.remove("prestiz");
            editor.commit();
        }
        prestiz = sharedPreferences.getFloat("prestiz", 0.0f);
        if(sharedPreferences.getAll().get("clickValue") instanceof Long || sharedPreferences.getAll().get("clickValue") instanceof Integer){
            editor.remove("prestiz");
            editor.commit();
        }
        clickValue = sharedPreferences.getFloat("clickValue", 0.1f);
        overTimeValue = sharedPreferences.getFloat("overTimeValue", 0.0f);
        prestizCounter.setText(getString(R.string.counterTop, prestiz));
        overtimeCounter.setText(getString(R.string.counterBottom, (overTimeValue* prestizMultiplier), (clickValue * prestizMultiplier)));


        powerUps = new LinkedHashMap<View, PowerUp>();
        rand = new Random();
        sounds = new int[]{
                R.raw.ekskluzywnewidowisko,
                R.raw.fekalia,
                R.raw.jestemmarek,
                R.raw.niczymlord,
                R.raw.ocochodzi,
                R.raw.parkowacsamochod,
                R.raw.prestizowo,
                R.raw.rolexodmierzaczas,
                R.raw.stosunek,
                R.raw.szacunek,
                R.raw.tanutabuja,
                R.raw.wartoscczlowieka,
                R.raw.wsadzrolexa
        };

        //Add powerups to the map and associate them with their buttons
        //PerClick

        addPowerUp(SUSHI_MODIFIER, SUSHI_PRICE, TYPE_PERCLICK, SUSHI_MAX, getString(R.string.perClick1));
        addPowerUp(KAWIOR_MODIFIER, KAWIOR_PRICE, TYPE_PERCLICK, KAWIOR_MAX, getString(R.string.perClick2));
        addPowerUp(HANEBISHO_MODIFIER, HANEBISHO_PRICE, TYPE_PERCLICK, HANEBISHO_MAX, getString(R.string.perClick3));
        addPowerUp(DZIEWICA_MODIFIER, DZIEWICA_PRICE, TYPE_PERCLICK, DZIEWICA_MAX, getString(R.string.perClick4));
        addPowerUp(WHISKYJURA_MODIFIER, WHISKYJURA_PRICE, TYPE_PERCLICK, WHISKYJURA_MAX, getString(R.string.perClick5));
        addPowerUp(SVALBARDI_MODIFIER, SVALBARDI_PRICE, TYPE_PERCLICK, SVALBARDI_MAX, getString(R.string.perClick6));
        addPowerUp(ZLOTO_MODIFIER, ZLOTO_PRICE, TYPE_PERCLICK, ZLOTO_MAX, getString(R.string.perClick7));
        addPowerUp(DONPERIGNON_MODIFIER, DONPERIGNON_PRICE, TYPE_PERCLICK, DONPERIGNON_MAX, getString(R.string.perClick8));

        //OvertTime
        addPowerUp(KANAL_MODIFIER, KANAL_PRICE, TYPE_OVERTIME, KANAL_MAX, getString(R.string.overtime1));
        addPowerUp(YEEZY_MODIFIER, YEEZY_PRICE, TYPE_OVERTIME, YEEZY_MAX, getString(R.string.overtime2));
        addPowerUp(IPHONE_MODIFIER, IPHONE_PRICE, TYPE_OVERTIME, IPHONE_MAX, getString(R.string.overtime3));
        addPowerUp(KAMERZYSTA_MODIFIER, KAMERZYSTA_PRICE, TYPE_OVERTIME, KAMERZYSTA_MAX, getString(R.string.overtime4));
        addPowerUp(SLUZACY_MODIFIER, SLUZACY_PRICE, TYPE_OVERTIME, SLUZACY_MAX, getString(R.string.overtime5));
        addPowerUp(AUDIA7_MODIFIER, AUDIA7_PRICE, TYPE_OVERTIME, AUDIA7_MAX, getString(R.string.overtime6));
        addPowerUp(WILLA_MODIFIER, WILLA_PRICE, TYPE_OVERTIME, WILLA_MAX, getString(R.string.overtime7));
        addPowerUp(GIELDA_MODIFIER, GIELDA_PRICE, TYPE_OVERTIME, GIELDA_MAX, getString(R.string.overtime8));


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
        boostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBoostClick(view);
            }
        });


        //Updater
        serviceIntent = new Intent(this, UpdateService.class);
        if (!isMyServiceRunning(UpdateService.class)) startService(serviceIntent);
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.rewarded_ad_unit_id),
                new AdRequest.Builder().build());
    }

    private void playRandomSound(){
        int soundId = sounds[rand.nextInt(sounds.length)];

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = MediaPlayer.create(getApplicationContext(), soundId);
        mediaPlayer.start();
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
        clickCounter++;
        if(clickCounter>=100){
            clickCounter = 0;
            playRandomSound();
        }
        prestiz += clickValue*prestizMultiplier;
        prestizCounter.setText(getString(R.string.counterTop, prestiz));



        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.boop_animation);
        findViewById(R.id.imageView).startAnimation(animation);

        new ParticleSystem(MainActivity.this, 50, R.drawable.kruszwilek, 500)
                .setSpeedRange(0.3f, 0.7f)
                .setScaleRange(0.5f, 0.5f)
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
        saveTimer.schedule(saveTask, 300000, 300000);

    }
    public void addPowerUp(float modifier, long price, int type, int max, String name){

        PowerUp powerUp = null;

        View view = getLayoutInflater().inflate(R.layout.menu_item, null);
        ((TextView)view.findViewById(R.id.menu_name)).setText(name);
        ((TextView)view.findViewById(R.id.menu_description)).setText(
                (type == TYPE_OVERTIME)
                        ?getString(R.string.overTimeMenuString, String.valueOf(modifier))
                        :getString(R.string.perClickMenuString, String.valueOf(modifier))
        );

        ((Button)view.findViewById(R.id.menu_button)).setText(getString(R.string.buttonPrice, price));
        ((ProgressBar)view.findViewById(R.id.menu_progress)).setMax(max);
        ((ProgressBar)view.findViewById(R.id.menu_progress)).setProgress(sharedPreferences.getInt(name, 0));

        if(type == TYPE_OVERTIME){
            powerUp = new OverTime(modifier,price,view, name, max);
        }else{
            powerUp = new PerClick(modifier,price,view, name, max);
        }
        powerUps.put(view,powerUp);

    }

    public void powerUpClick(View view){
        buy(powerUps.get(view.getParent().getParent()),1.15);
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
    public void buy(PowerUp powerUp, double priceModifier){
        if(powerUp.getCount() < powerUp.getMax()) {
            if (Math.floor(prestiz) >= powerUp.getPrice()) {
                powerUp.increment();
                if(powerUp.getCount() < powerUp.getMax()) {
                    prestiz -= powerUp.getPrice();
                    powerUp.setPrice(Math.round((powerUp.getPrice() * 1.15)));
                    if (powerUp instanceof PerClick) {
                        clickValue += powerUp.getModifier();
                    } else {
                        overTimeValue += powerUp.getModifier();
                    }
                    prestizCounter.setText(getString(R.string.counterTop, prestiz));
                    overtimeCounter.setText(getString(R.string.counterBottom, (overTimeValue * prestizMultiplier), (clickValue * prestizMultiplier)));
                    ((Button) powerUp.getView().findViewById(R.id.menu_button)).setText(getString(R.string.buttonPrice, powerUp.getPrice()));
                    ((ProgressBar) powerUp.getView().findViewById(R.id.menu_progress)).setProgress(powerUp.getCount());
                }else{
                    ((Button ) powerUp.getView().findViewById(R.id.menu_button)).setText("MAX");
                    ((ProgressBar) powerUp.getView().findViewById(R.id.menu_progress)).setProgress(powerUp.getCount());

                }
            }
        }else{
            ((Button ) powerUp.getView().findViewById(R.id.menu_button)).setText("MAX");
        }


    }

    public void onBoostClick(View view) {
        createRewardedAdDialog();
    }

    private void createRewardedAdDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Prestiż X2!");
        builder.setMessage("Czy chcesz obejrzeć reklamę i przyspieszyć zdobywanie prestiżu?\nPrestiż X2 przez 60 sekund.");
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
            if(entry.getValue().getPrice() <= prestiz && entry.getValue().getCount() < entry.getValue().getMax()) {
                entry.getKey().findViewById(R.id.menu_button).setBackgroundResource(R.drawable.buttonks);
                ((Button)entry.getKey().findViewById(R.id.menu_button)).setTextColor(Color.parseColor("#FFFFFF"));

            } else {
                entry.getKey().findViewById(R.id.menu_button).setBackgroundResource(R.drawable.buttonks_locked);
                ((Button)entry.getKey().findViewById(R.id.menu_button)).setTextColor(Color.parseColor("#CCCCCC"));

            }
        }
    }

    private void save() {
        editor.putFloat("prestiz",(Float)prestiz);
        editor.putFloat("overTimeValue", (Float)overTimeValue);
        editor.putFloat("clickValue", (Float)clickValue);

        for(Map.Entry<View, PowerUp> entry : powerUps.entrySet()){
            editor.putInt(entry.getValue().getName(), entry.getValue().getCount());
        }

        editor.commit();
    }

    private void load() {
        for(Map.Entry<View, PowerUp> entry : powerUps.entrySet()){
            entry.getValue().setCount(sharedPreferences.getInt(entry.getValue().getName(), 0));
            if (entry.getValue().getCount() != 0) {
                Log.d("testoviron", entry.getValue().getName() + " " + entry.getValue().getCount() + " " + entry.getValue().getMax());
                if (entry.getValue().getCount() >= entry.getValue().getMax()) {
                    ((Button) entry.getValue().getView().findViewById(R.id.menu_button)).setText("MAX");
                    ((Button) entry.getValue().getView().findViewById(R.id.menu_button)).setOnClickListener(null);
                }else{
                    entry.getValue().setPrice(Math.round(
                            (entry.getValue().getCount() * entry.getValue().getBasePrice() * 1.15)));
                    ((Button) entry.getValue().getView().findViewById(R.id.menu_button))
                            .setText(getString(R.string.buttonPrice, entry.getValue().getPrice()));
                }
            }
        }
    }
}
