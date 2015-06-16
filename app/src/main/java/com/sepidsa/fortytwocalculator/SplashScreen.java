package com.sepidsa.fortytwocalculator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Farshid on 6/10/2015.
 */
public class SplashScreen extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                Intent intent = new Intent(SplashScreen.this,ParallaxPagerActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.appear, R.anim.disappear);
                finish();
            }
        }, 2000);

    }


}
