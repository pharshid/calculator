package com.sepidsa.fortytwocalculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;


public class ColorPickerActivity extends FragmentActivity implements ColorPickerSwatch.OnColorSelectedListener, CompoundButton.OnCheckedChangeListener {
    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;

    private static final String KEY_COLORS_KEYPAD = "color_keypad" ;
    protected static final String KEY_SELECTED_COLOR_KEYPAD = "selected_color_keypad";


    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions";
    protected static final String KEY_SELECTED_COLOR = "selected_color";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";

    protected int mTitleResId = R.string.color_picker_default_title;
    protected int[] mAccentColors = null;
    protected int[] mColorsKeypad = null;

    protected String[] mColorContentDescriptions = null;
    protected int mSelectedColor;
    protected int mColumns;
    protected int mSize;
    private int mSelectedKeypadBackgroundColor;




    boolean mIsPremium;
    boolean mIsRetroThemeSelected;
    int mAccentcolorCode;
    int mKeypadBackgroundColorCode;
    View mAccentLayout;
    View mKeypadLayout;
    ColorPickerPalette mACcentPallete;
    ColorPickerPalette mKeypadPallete;
    Typeface mYekanFont;
    protected ColorPickerSwatch.OnColorSelectedListener mListener;
    Switch mClassicthemeSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mYekanFont = Typeface.createFromAsset(getAssets(), "yekan.ttf");

        setContentView(R.layout.activity_color_picker);
        mAccentLayout =  findViewById(R.id.layout_color_picker_accent);
        mKeypadLayout =  findViewById(R.id.layout_color_picker_keypad);

       setTypefaces();

        mIsPremium  = getIntent().getBooleanExtra("isPremium", false);
        mIsRetroThemeSelected = getIntent().getBooleanExtra("isRetroTheme", false);
        mAccentcolorCode = getIntent().getIntExtra("accentColor", Color.parseColor("#1abc9c"));
        mKeypadBackgroundColorCode =getIntent().getIntExtra("keyPadColor", Color.WHITE);
        mClassicthemeSwitch = (Switch)findViewById(R.id.switch_classic_theme);
        mClassicthemeSwitch.setOnCheckedChangeListener(this);
        mClassicthemeSwitch.setChecked(isRetroThemeSelected());

        mACcentPallete = (ColorPickerPalette)findViewById(R.id.color_picker_accent);
        mACcentPallete.init(16, 6, this);
        mACcentPallete.setSelected(true);


        refreshPalette(mACcentPallete, Utils.ColorUtils.colorChoice(getApplicationContext()), indexOf(Utils.ColorUtils.colorChoice(getApplicationContext()), getAccentColorCode()));
        onColorSelected(getAccentColorCode());

        mKeypadPallete = (ColorPickerPalette)findViewById(R.id.color_picker_keypad);
        mKeypadPallete.init(18, 6, this);
        mKeypadPallete.setSelected(true);

        refreshPalette(mKeypadPallete, Utils.ColorUtils.colorChoiceForKeypad(getApplicationContext()), indexOf(Utils.ColorUtils.colorChoiceForKeypad(getApplicationContext()), getKeypadBackgroundColorCode()));
        onColorSelected(getAccentColorCode());

        mAccentLayout.setBackgroundColor(getAccentColorCode());
        mKeypadLayout.setBackgroundColor(getKeypadBackgroundColorCode());
    }

    private void setTypefaces() {
        TextView fontChaange = (TextView) findViewById(R.id.textview_accent);
        fontChaange.setTypeface(mYekanFont);
           fontChaange = (TextView) findViewById(R.id.textview_use_classic);
        fontChaange.setTypeface(mYekanFont);
           fontChaange = (TextView) findViewById(R.id.textView_keypad);
        fontChaange.setTypeface(mYekanFont);

    }


    @Override
    public Intent getIntent() {
        return super.getIntent();
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void refreshPalette(ColorPickerPalette pallette, int[] colors , int selectedColor) {
        if (pallette != null && colors != null) {
            pallette.drawPalette(colors, selectedColor);
        }
    }


    boolean arrayContains(int[] parent,int child){

        for(int index:parent){
            if(index == child){return true;}
        }
        return false;
    }

    int indexOf(int[] parent,int child){

        for(int index=0; index <parent.length;index++){
            if(parent[index] == child) {
                return index;
            }
        }
        return 0;
    }

    @Override
    public void onColorSelected(int color) {

        boolean selectedAccentColor = true;
        int [] temp = Utils.ColorUtils.colorChoice(getApplicationContext());
        selectedAccentColor =  arrayContains(temp,color);
        if(selectedAccentColor){
            saveAccentColorCode(color);
            mAccentLayout.setBackgroundColor(getAccentColorCode());
            mACcentPallete.drawPalette(Utils.ColorUtils.colorChoice(getApplicationContext()), color, null);
        }else{
            saveKeypadBackgroundColorCode(color);
            mKeypadLayout.setBackgroundColor(getKeypadBackgroundColorCode());
            mKeypadPallete.drawPalette(Utils.ColorUtils.colorChoiceForKeypad(getApplicationContext()), color, null);


        }
    }


    // Saving the selected color theme to prefrence
    public void saveAccentColorCode(int colorCode){
        SharedPreferences appPreferences =getApplicationContext().getSharedPreferences("THEME",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt("ACCENT_COLOR_CODE", colorCode);
        editor.commit();
    }

    public int getAccentColorCode(){
        //TODO set a cool default theme color
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("THEME", MODE_PRIVATE);
        return appPreferences.getInt("ACCENT_COLOR_CODE", Color.parseColor("#1abc9c"));
    }

    public int getKeypadBackgroundColorCode(){
        //TODO set a cool default theme color
        int  currentThemeNumber = -1;
        SharedPreferences appPreferences =getApplicationContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        currentThemeNumber = appPreferences.getInt("KEYPAD_BACKGROUND_COLOR_CODE",Color.WHITE);

        return currentThemeNumber;
    }




    private void saveKeypadBackgroundColorCode(int themeNumber) {
        SharedPreferences appPreferences =getApplicationContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt("KEYPAD_BACKGROUND_COLOR_CODE", themeNumber);
        editor.commit();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(buttonView != null) {
//        if(((MainActivity)getParent()).getPremiumPreference()){
            if (isChecked) {
                setRetrothemeSelected(true);
            } else {
                setRetrothemeSelected(false);
            }
            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("switchTheme", true);
            setResult(2, resultIntent);
//        getParent().recreate();
        }
    }
    public boolean getPremiumPreference(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("purchases", Context.MODE_PRIVATE);
        return  appPreferences.getBoolean("isPremium",false);
    }

    boolean isRetroThemeSelected(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("THEME", MODE_PRIVATE);
        return appPreferences.getBoolean("is_retro_theme_selected", false);
    }

    void setRetrothemeSelected(boolean _isSelected) {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("THEME", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putBoolean("is_retro_theme_selected", _isSelected);
        editor.apply();

    }

}
