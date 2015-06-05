package com.sepidsa.fortytwocalculator;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class ColorPickerActivity extends FragmentActivity {

    boolean mIsPremium;
    boolean mIsRetroThemeSelected;
    int mAccentcolorCode;
    int mKeypadBackgroundColorCode;
    View mAccentLayout;
    View mKeypadLayout;
    ColorPickerPalette mACcentPallete;
    ColorPickerPalette mKeypadPallete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_color_picker);
        mAccentLayout =  findViewById (R.id.layout_color_picker_accent);
        mKeypadLayout =  findViewById(R.id.layout_color_picker_keypad);
        mIsPremium  = getIntent().getBooleanExtra("isPremium",false);
        mIsRetroThemeSelected = getIntent().getBooleanExtra("isRetroTheme",false);
        mAccentcolorCode = getIntent().getIntExtra("accentColor", Color.parseColor("#1abc9c"));
        mKeypadBackgroundColorCode =getIntent().getIntExtra("keyPadColor",Color.WHITE);

        mAccentLayout.setBackgroundColor(mAccentcolorCode);
        mKeypadLayout.setBackgroundColor(mAccentcolorCode);
    }

    @Override
    public Intent getIntent() {
        return super.getIntent();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
