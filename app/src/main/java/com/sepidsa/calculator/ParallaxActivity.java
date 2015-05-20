package com.sepidsa.calculator;

/**
 * Created by Farshid on 5/20/2015.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.xgc1986.parallaxPagerTransformer.ParallaxPagerTransformer;

public class ParallaxActivity extends FragmentActivity {

    ViewPager mPager;
    Button mButton;
    ParallaxAdapter mAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parallax);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setBackgroundColor(0xFF000000);

        ParallaxPagerTransformer pt = new ParallaxPagerTransformer((R.id.image));
        pt.setBorder(20);
        //pt.setSpeed(0.2f);
        mPager.setPageTransformer(false, pt);

        mAdapter = new ParallaxAdapter(getSupportFragmentManager());
        mAdapter.setPager(mPager); //only for this transformer

        Bundle bNina = new Bundle();
        bNina.putInt("image", R.mipmap.bg_nina);
        bNina.putString("name", "Nina");
        ParallaxFragment pfNina = new ParallaxFragment();
        pfNina.setArguments(bNina);

        Bundle bNiju = new Bundle();
        bNiju.putInt("image", R.mipmap.bg_niju);
        bNiju.putString("name", "Ninu Junior");
        ParallaxFragment pfNiju = new ParallaxFragment();
        pfNiju.setArguments(bNiju);

        Bundle bYuki = new Bundle();
        bYuki.putInt("image", R.mipmap.bg_yuki);
        bYuki.putString("name", "Yuki");
        ParallaxFragment pfYuki = new ParallaxFragment();
        pfYuki.setArguments(bYuki);

        Bundle bKero = new Bundle();
        bKero.putInt("image", R.mipmap.bg_kero);
        bKero.putString("name", "Kero");
        ParallaxFragment pfKero = new ParallaxFragment();
        pfKero.setArguments(bKero);

        mAdapter.add(pfNina);
        mAdapter.add(pfNiju);
        mAdapter.add(pfYuki);
        mAdapter.add(pfKero);
        mPager.setAdapter(mAdapter);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().show();
        }

        mButton = (Button) findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO intent to main activity
                Intent myIntent = new Intent(ParallaxActivity.this, MainActivity.class);
                ParallaxActivity.this.startActivity(myIntent);
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();

        return super.onOptionsItemSelected(item);
    }
}
