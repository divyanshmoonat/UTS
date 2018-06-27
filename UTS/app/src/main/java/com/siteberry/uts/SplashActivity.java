package com.siteberry.uts;
//THIS ACTIVITY WILL CHECK INTERNET CONNECTIVITY AND ALSO CHECKS WEATHER USER IS REGISTERED OR NOT ACCORDING TO WHICH IT JUMPS ON DASHBOARD OR  LOGIN/SIGNUP PAGE.
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.siteberry.helper.MyOpenSQLiteHelper;

public class SplashActivity extends Activity{
    private boolean check=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = null;
                MyOpenSQLiteHelper mosh = new MyOpenSQLiteHelper(SplashActivity.this,"user",null,1);
                SQLiteDatabase db = mosh.getReadableDatabase();
                Cursor c = db.query("user",new String[]{"userName","password"},null,null,null,null,null);
                while (c.moveToNext())
                    check=true;
                if(!Utility.isNetworkAvailable(SplashActivity.this)){
                    showDialog();
                }
                if (check){
                    //IF USER IS ALREADY REGISTERED IN LOCAL DATABASE
                    intent= new Intent(SplashActivity.this,null);//JUMP TO HOME PAGE/DASHBOARD OF APP
                }
                else {
                    intent = new Intent(SplashActivity.this,LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        },2000);
    }
    public static class Utility {
        public static boolean isNetworkAvailable(Context context) {
            ConnectivityManager connectivityManager
                    = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
    }
    public void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
        builder.setTitle("Alert!");
        builder.setMessage("No active internet connection in this device! Turn Internet on?");
        builder.setNegativeButton("Cancel",new  DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                System.exit(0);
            }
        });
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName("com.android.settings","com.android.settings.Settings$WirelessSettingsActivity"));
                startActivity(intent);//SETTINGS WILL OPEN ON DEVICE TO TURN INTERNET CONNECTION ON
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

