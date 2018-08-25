package com.knk.kruszwilclicker;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class EndActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Gratulacje!");
        builder.setMessage("Zostałeś najbardziej prestiżowym człowiekiem na świecie!\nJesteś nawet bardziej prestiżowy niż sam Lord Kruszwil!");
        builder.setPositiveButton("Super!", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            }
        });
        dialog.show();
    }


    public void onResetClick(View view) {
        SharedPreferences preferences = getSharedPreferences("inneścoś", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
        onPlayClick(view);
    }

    public void onPlayClick(View view) {
        finish();
        startActivity(new Intent(EndActivity.this, MainActivity.class));
    }
}



