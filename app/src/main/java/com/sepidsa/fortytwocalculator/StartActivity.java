package com.sepidsa.fortytwocalculator;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MotionEvent;

/**
 * Created by Farshid on 6/10/2015.
 */
public class StartActivity extends Activity {
    public static Activity fa;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fa = this;
        setContentView(R.layout.activity_start);
        showSplashAndTour();


    }

    void showSplashAndTour(){
        if(!getSplashAndTourViewed()){
            setSplashAndTourViewed(true);
            Intent intent = new Intent(this, SplashScreen.class);
            overridePendingTransition(R.anim.appear, R.anim.disappear);
            startActivity(intent);


        } else {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
//            overridePendingTransition(R.anim.appear, R.anim.disappear);
            finish();

        }
    }

    private boolean getSplashAndTourViewed() {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        return  appPreferences.getBoolean("hasViewedTour",false);
    }

    private void setSplashAndTourViewed(boolean hasViewed) {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("APP", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();

        editor.putBoolean("hasViewedTour", hasViewed);


        editor.apply();
    }

}
