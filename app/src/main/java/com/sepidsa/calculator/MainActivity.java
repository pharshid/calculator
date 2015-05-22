package com.sepidsa.calculator;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.sepidsa.calculator.data.LogContract;
import com.viewpagerindicator.CirclePageIndicator;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Stack;

// todo for google play
//import com.ibm.icu.text.NumberFormat;
//import com.ibm.icu.text.RuleBasedNumberFormat;
//import com.ibm.icu.util.ULocale;


public class MainActivity extends FragmentActivity implements View.OnClickListener,  CompoundButton.OnCheckedChangeListener, PopoverView.PopoverViewDelegate, TextView.OnEditorActionListener {

    private static final String FRAGMENT_TAG_LOG_ = "log fragment";
    private static final byte LANGUAGE_ARABIC =3 ;
    private static final byte LANGUAGE_GERMAN =4 ;
    private static final byte LANGUAGE_ITALIAN =5 ;
    private static final String LOG_DATA_KEY = "log data";
    Log_Adapter mLogAdapter;
    Serializable mListView ;
    //TextSwitcher mResultTextSwitcher = null;
    //the reason it's an editText and not a TextView is solely for supporting the scrolling function
    private AutoResizeTextView mTranslationBox;

    Button mButton ;
    FragmentStatePagerAdapter mAdapter;

    ViewPager mViewPager;
    private static final byte TOTAL_PAGE_COUNT = 3;
    private byte mDefaultPage;
    private boolean mISRetroThemeOn = false;

//    private byte mDelta = -2;
     /*  Contstant for viewpager page selection
    there's the versions of this values a portrait value and a Wide Tablet value. Instead of
    making two sets of these constant for each mode I devised Another! constant called Delta
    adding these with Delta results in the desired index for viewpager whether it's portrait or
    tablet landscape . for example in portrait viewpager.getItem(Log_Fragment+ Delta) we need
     index 0 so Delta in this mode is Zero.But in Landscape Mode Page for zero'th index is Scientific
     index so here the Delta would be -2 hence viewpager.getItem(SCIENTIFIC_FRAGMENT+ Delta) i.e.
     viewpager.getItem(2 - 2)
    */

    private static final byte
            LOG_FRAGMENT = 0,
            DIALPAD_FRAGMENT = 1,

    SCIENTIFIC_FRAGMENT_LANDSCAPE_TABLET = 0,
            COLOR_PICKER_FRAGMENT_LANDSCAPE_TABLET = 1,
            COLOR_PICKER_FRAGMENT_PORTRAIT = 3 ,
            COLOR_PICKER_FRAGMENT_LANDSCAPE_PHONE = 2 ;

    private byte mColorPage ;
    int[] mColor ;
    int mSelectedColorCal0 = 0;

    private static final int DIALPAD_FONT_ROBOTO_THIN = 0;
    private static final int DIALPAD_FONT_ROBOTO_LIGHT = 1;
    private static final int DIALPAD_FONT_ROBOTO_REGULAR = 2;
    private static final int PERSIAN_TRANSLATION_FONT_MITRA = 0;
    private static final int PERSIAN_TRANSLATION_FONT_DASTNEVIS = 1;
    private Typeface mRobotoLight;
    private Typeface mRobotoRegular;

    private static final byte PORTRAIT_BOTH = 0,
            LANDSCAPE_PHONE = 1,
            LANDSCAPE_TABLET = 2;
    public byte mLayoutState ;
    public boolean mLogAdded = false;
    // This will be synced with the viewpager . and is provided kindly by www.viewpagerindicator.com
    private CirclePageIndicator mViewPagerIndicator = null;

    AutoResizeTextView resultTextView;
    String mTemp = "0";


    // for "2.4+4.5 this variable is 4.right after the first operator we see
//    private byte mPermittedIndexOFDotCharRepetition;
    Boolean mJustPressedExecuteButton = true;
    //This is the string work with during all the calculations.We don't necessarily display it though
    private StringBuilder mExpressionBuffer = new StringBuilder();
    //Mainly used for backspace operations
    private Stack<String> mButtonsStack = new Stack<String>();
    private static final byte DECIMAL_FRACTION_LENGTH = 3;



    /*
    Although this app is ICS+ and ICS supports Roboto out of the box.But robot is attached nevertheless
    because A) it's the latest version included in Android L which has numerous refinements one digits and letters and spacing'
    B) I have a unified vision of how the typeface looks like on various android versions.95% of app uses this typeface
    */
    Typeface mEnglishTypeFace = null;
    Typeface mTranslationBoxTypeface = null;
    //    this typeface will be used for translation box
    private Typeface mPersianTranslationTypeface = null;
    // Languages supported for "number to word" translation . will use theme Later for Localization
    static final byte LANGUAGE_PERSIAN = 0
            , LANGUAGE_ENGLISH = 2
            , LANGUAGE_FRENCH = 1;
    private static final byte DEFAULT_LANGUAGE = LANGUAGE_PERSIAN;



    //These are modes for UI update
    private static final byte DIGIT_BUTTON_PRESSED = 1
            , EXECUTE_BUTTON_PRESSED = 3;
    private TextSwitcher mResult;
    private View result_textView_holder;
    BigDecimal mResultBeforeSignification = new BigDecimal(0);
    BigDecimal mMemoryVariable = new BigDecimal(0);
    TextView mMemoryVariableTextView;
    String mMemoryVariableString = "";
    private Animation mErrorBlink,mBlink,mFadeIn,mFadeOut;

    private SoundPool mSoundPool;
    private boolean mSoundPoolLoaded = false;
    private int numericButtonSoundID
            , executeButtonSoundID
            , clearAllButtonSoundID
            , operatorsButtonSoundID
            , errorSoundID
            , mHasVolumeSoundID
            , backSpaceButtonSoundID ;

    Animation  out_anim,in_anim;
    private Typeface mIconFont;
    private Typeface mFlatIcon;
    private String mDecimal_fraction = "";
    private TextView mScientificModeTextView;
    private Typeface mshekari;
    private Typeface mArabicTypeFace;
    private Typeface mMitra;
    private Typeface mDastnevis;
    //    PopoverView popoverView;
    Button mFavoritesList;
    private boolean mShowingBack = false;
    EditText mTagHimself;




    private boolean doubleBackToExitPressedOnce;
    private Handler mHandler;

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            doubleBackToExitPressedOnce = false;
        }
    };
    private Button mAddToFavorites;
    private Button mAddLabel;
    private Typeface mPhalls,mDigital_7;
    private Typeface mRobotoThin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TinyDB db = new TinyDB(getApplicationContext());
        mListView = db.getListString(LOG_DATA_KEY);
        boolean isnull = false;

        if (getKeypadBackgroundColorCode() == 2) {
            mISRetroThemeOn = true;
        }else {
            mISRetroThemeOn = false;
        }

        if(mISRetroThemeOn){
            setContentView(R.layout.activity_main_retro);
        }else {
            setContentView(R.layout.activity_main);
        }




//        ctx = this;
        //Call the set overlay, you can put the logic of checking if overlay is already called with a simple sharedpreference
//        showOverLay();

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {

            mJustPressedExecuteButton = true;
        } else {
            //        Configuration Change (rotation) occurred so better load the savedinstancestate values
            //        TODO
//            mListView = savedInstanceState.getSerializable("mListView");
        }

        setMViewPager(fragmentManager);
        setMViewPagerIndicator();
        mMemoryVariableTextView =  (TextView) findViewById(R.id.m_vaiable_textview);
        resultTextView = (AutoResizeTextView) findViewById(R.id.result);
//        resultTextView.setText("3\u20444");
//        resultTextView.setText(Html.fromHtml("<sup>5</sup>/<sub>9</sub>"));

        mScientificModeTextView = (TextView) findViewById(R.id.scientific_mode_textview);
        result_textView_holder = findViewById(R.id.MotherTop);
        mFavoritesList = (Button)findViewById(R.id.favorites_list);
        mAddToFavorites = (Button)findViewById(R.id.add_to_favorites);
        mAddLabel = (Button)findViewById(R.id.add_label);
        mTagHimself = (EditText)findViewById(R.id.tag_himself);
        mTagHimself.setOnEditorActionListener(this);
        mFavoritesList.setOnClickListener(this);
        mAddToFavorites.setOnClickListener(this);
        mAddLabel.setOnClickListener(this);

        if(findViewById(R.id.switch_deg_rad) != null) {
            ((Switch) (findViewById(R.id.switch_deg_rad))).setChecked(getAngleMode());
            ((Switch) (findViewById(R.id.switch_deg_rad))).setOnCheckedChangeListener(this);
        }



        setTypeFaces();

//        If it's a new instance of application i.e. Not because of rotation or configuration changes =================
        prepareBottomIcons();



        if(!watchedIntro()){

            AlertDialog dialog =   new AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("").setMessage(""+
                            "جدید در این نسخه\n" +
                            "• تم تیره اضافه شده\n" +
                            "• امکان انتخاب فونت فارسی\n" +
                            "• رنگ بندی جدید\n" +
                            "• صداگذاری جدید\n" +
                            "• برای استفاده از حافظه قسمت نکات رو یه نگاهی بنداز\n"
            ) .setPositiveButton(
                    "باشه", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            setWatcheIntroPreference(true);
                            dialog.dismiss();
                        }
                    }).show();

            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
            textView.setTypeface(mshekari);


            Button button1 = (Button) dialog.findViewById(android.R.id.button1);
            button1.setTypeface(mshekari);

        }



    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {

//        super.onSaveInstanceState(outState);


        outState.putSerializable("buttonStack", mButtonsStack);
        outState.putString("mResultText", resultTextView.getText().toString());
        outState.putString("mScientificModeTextView", mScientificModeTextView.getText().toString());

        outState.putCharSequence("mTranslationText", mTranslationBox.getText().toString());
        outState.putSerializable("mButtonStack", mButtonsStack);
        outState.putSerializable("mListView", mListView);
        outState.putString("mExpressionBuffer", mExpressionBuffer.toString());
        outState.putBoolean("mJustPressedExecuteButton", mJustPressedExecuteButton);
        outState.putBoolean("mISRetroThemeOn", mISRetroThemeOn);
        outState.putSerializable("mMemoryVariable", mMemoryVariable);
        outState.putString("mTemp",mTemp);
        outState.putString("mDecimal_fraction", mDecimal_fraction);
        Log.d("sinatra","here in save instance state retro is "+ mISRetroThemeOn);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);


        if(savedInstanceState!= null) {
            mTranslationBox = getMTranslationEditText();
            mTranslationBox.setText(savedInstanceState.getString("mTranslationText"));
            mJustPressedExecuteButton = savedInstanceState.getBoolean("mJustPressedExecuteButton");
            mISRetroThemeOn =  savedInstanceState.getBoolean("mISRetroThemeOn");
            Log.d("sinatra","here in restore instance state retro is "+ mISRetroThemeOn);
            mButtonsStack = new Stack<String>();
            mButtonsStack.addAll((Collection<String>) savedInstanceState.getSerializable("mButtonStack"));

            //TODO
            mListView = savedInstanceState.getSerializable("mListView");
            mExpressionBuffer = new StringBuilder();
            String exTemp = savedInstanceState.getString("mExpressionBuffer");


            mMemoryVariable = (BigDecimal) savedInstanceState.getSerializable("mMemoryVariable");
            mMemoryVariableTextView.setText(" M = " + mMemoryVariable.toString());
            mTemp = savedInstanceState.getString("mTemp");
            mDecimal_fraction = savedInstanceState.getString("mDecimal_fraction");



            if (exTemp != null)
                mExpressionBuffer = new StringBuilder(exTemp);
            else
                mExpressionBuffer = new StringBuilder();

            resultTextView.setText(savedInstanceState.getString("mResultText"));

            if (mJustPressedExecuteButton) {
                calculateResult(null);
                updateUIExecute(false);
            } else {
                mTranslationBox.setTypeface(mTranslationBoxTypeface);
//
                resultTextView.setText(savedInstanceState.getString("mResultText"));
//            mTranslationBox.setText(savedInstanceState.getString("mTranslationText"));
//            updateUIExecute(false);

            }
            checkCLRButtonSendIntent();
        }
    }



    @Override
    protected void onStart() {

        super.onStart();

        setResultTextBox();

        mTranslationBox = (AutoResizeTextView)findViewById(R.id.translationEditText);

        switch (getTranslationLanguage()) {
            case LANGUAGE_PERSIAN:
                getMTranslationEditText().setTypeface(mPersianTranslationTypeface);
                break;

            default:
                getMTranslationEditText().setTypeface(mEnglishTypeFace);
        }


        if(!mISRetroThemeOn) {
            resultTextView.setBackgroundColor(getAccentColorCode());
        }
        if(getAngleMode() == true){
            mScientificModeTextView.setText("DEG");
        }else{
            mScientificModeTextView.setText("RAD");
        }
        Runnable runnable = new Runnable() {
            public void run() {
                prepareAnimationStuff();
                prepareSoundStuff();
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();

        /*if (mJustPressedExecuteButton) {
            updateUIExecute(false);
        }*/


    }

    @Override
    protected void onResume() {
        checkCLRButtonSendIntent();


        if (!mJustPressedExecuteButton) {
            mTranslationBox.setTypeface(mTranslationBoxTypeface);
        }
        redrawAccent();
        redrawKeypadBackground();



        super.onResume();
    }

    private void prepareBottomIcons() {

        ( (Button)findViewById(R.id.buttonMute)).setTypeface(mFlatIcon);
        findViewById(R.id.buttonMute).setOnClickListener(this);
        if(getVolumeFromPreference()) {
            ((Button) findViewById(R.id.buttonMute)).setText(getResources().getText(R.string.volume_high));
        }else {
            ( (Button)findViewById(R.id.buttonMute)).setText(getResources().getText(R.string.volume_off));
        }

        ( (Button)findViewById(R.id.buttonSettings)).setTypeface(mFlatIcon);
        ( findViewById(R.id.buttonSettings)).setOnClickListener(this);

        ( (Button)findViewById(R.id.buttonLanguage)).setTypeface(mFlatIcon);
        ( findViewById(R.id.buttonLanguage)).setOnClickListener(this);
// todo show a settings dialog here

        ( (Button)findViewById(R.id.buttonColors)).setTypeface(mFlatIcon);
        ( findViewById(R.id.buttonColors)).setOnClickListener(this);

        ( (Button)findViewById(R.id.buttonParallax)).setTypeface(mFlatIcon);
        ( findViewById(R.id.buttonParallax)).setOnClickListener(this);


    }

    private boolean getVolumeFromPreference(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("volumeState", Context.MODE_PRIVATE);
        return  appPreferences.getBoolean("hasVolume",true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {

        //TODO check if it works for tablet landscape
        // if we're in default page prompt for exit else switch to default page TODO set default page here

        if ((mViewPager.getCurrentItem() == DIALPAD_FRAGMENT && (mLayoutState != LANDSCAPE_TABLET )) ||
                (mLayoutState == LANDSCAPE_TABLET )) {


            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "یه بار دیگه جهت خروج کامل", Toast.LENGTH_SHORT).show();

            mHandler = new Handler();
            mHandler.postDelayed(mRunnable, 2000);

        } else  {

            mViewPager.setCurrentItem(DIALPAD_FRAGMENT);

        }



    }

    //  Mind casting of viewpagerindicator .there's 6 types of this object in the library availble
    private void setMViewPagerIndicator() {
        mViewPagerIndicator = (CirclePageIndicator) findViewById(R.id.view_pager_indicator);
        if (mViewPagerIndicator  != null) {
            mViewPagerIndicator.setViewPager(mViewPager);
            mViewPagerIndicator.setCurrentItem(mDefaultPage);
            mViewPagerIndicator.setFillColor(getAccentColorCode());

            //  mViewPagerIndicator.setFades(false);
//            mViewPagerIndicator.setSelectedColor(getAccentColorCode());
//        mViewPagerIndicator.setFillColor(getAccentColorCode());
        }
    }

    //    if we're in portrait mode there are 5 pages in the indicator but in tablet mode there are 2 tabs only
    private void setMViewPager(FragmentManager fragmentManager) {
        // This view is only present in the portrait xml file. I use it as measure for portrait/landscape judgement!
        mViewPager = (ViewPager) findViewById(R.id.portrait_viewpager);
        if (mViewPager != null) {


            if (((String) mViewPager.getTag()).equals("portrait_phone") == true) {
                //getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                // if in Portrait Phone
                mColorPage = 3;
                mViewPager.setAdapter(null);
                List<Fragment> fList = new ArrayList<Fragment>();

                //fList.add(new LogFragment());

                fList.add(new AnimatedLogFragment());
                fList.add(new DialpadFragment());
                fList.add(new ScientificFragment());
//                fList.add(new ColorPickerFragment());


                mViewPager.setAdapter(new PortraitPagerAdapter(fragmentManager, fList));
                mLayoutState = PORTRAIT_BOTH;
                mViewPager.setOffscreenPageLimit(0);

                // if todo signs viewed = false
                // {make em visible and
                //   if(!watchedIntro()) {
                //  setIntroButtonsVisibility(true);
                mViewPager.setCurrentItem(DIALPAD_FRAGMENT);
                setmDefaultPage(DIALPAD_FRAGMENT);
                // mViewPager.setOnPageChangeListener(this);
            } else if (((String) mViewPager.getTag()).equals("landscape_phone") == true) {
                // if in Landscape Phone
//                mViewPager.removeAllViews();
//                mViewPager.setAdapter(null);
//                mViewPager = null;
//                mViewPager = (ViewPager) findViewById(R.id.portrait_viewpager);
//                lapd = new LandscapePhonePagerAdapter(fragmentManager);
//            mViewPager = (ViewPager) findViewById(R.id.landscape_viewpager);
//                mAdapter = ;

                List<Fragment> fList = new ArrayList<Fragment>();

                fList.add(new AnimatedLogFragment());
                fList.add(new DialpadFragment());
//                fList.add(new ColorPickerFragment());

                mColorPage = 2;
                mViewPager.setAdapter(new PortraitPagerAdapter(fragmentManager, fList));
                mLayoutState = LANDSCAPE_PHONE;
                mViewPager.setOffscreenPageLimit(0);
                mViewPager.setCurrentItem(DIALPAD_FRAGMENT);
                setmDefaultPage(DIALPAD_FRAGMENT);



                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                ScientificFragment fragment = new ScientificFragment();
                fragmentTransaction.add(R.id.log_container, fragment);
                fragmentTransaction.commit();

            }
            else {
                // mviewpager tag is equal landscape_tablet


                if(fragmentManager.findFragmentByTag("jake") != null){
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    AnimatedLogFragment fragment = new AnimatedLogFragment();
                    fragmentTransaction.replace(R.id.container, fragment);
                    fragmentTransaction.commit();
                }

                // Landscape Tablet
                mLayoutState = LANDSCAPE_TABLET;
                mColorPage = 1;

                List<Fragment> fList = new ArrayList<Fragment>();
                fList.add(new ScientificFragment());
//                fList.add(new ColorPickerFragment());

                mViewPager.setAdapter(new PortraitPagerAdapter(fragmentManager,fList));
                mViewPager.setCurrentItem(SCIENTIFIC_FRAGMENT_LANDSCAPE_TABLET);
                setmDefaultPage(SCIENTIFIC_FRAGMENT_LANDSCAPE_TABLET);
            }
        }
    }


    private void setIntroButtonsVisibility(boolean isVisible) {
        if (isVisible) {
//          ((Button) findViewById(R.id.west_cost)).setEnabled(true);
//          ((Button) findViewById(R.id.west_cost)).setVisibility(View.VISIBLE);
//          ((Button) findViewById(R.id.east_cost)).setEnabled(true);
//          ((Button) findViewById(R.id.east_cost)).setVisibility(View.VISIBLE);
        }else {
            ///   ((Button) findViewById(R.id.west_cost)).setEnabled(false);
            //   ((Button) findViewById(R.id.west_cost)).setVisibility(View.GONE);
//          ((Button) findViewById(R.id.west_cost)).setWidth(1);
            //   ((Button) findViewById(R.id.east_cost)).setEnabled(false);

            //    ((Button) findViewById(R.id.east_cost)).setVisibility(View.GONE);
//          ((Button) findViewById(R.id.east_cost)).setWidth(1);

        }

    }

    private void setTypeFaces() {

        mRobotoLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_light.ttf");
        mRobotoRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_regular.ttf");
        mRobotoThin = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_thin.ttf");

        mArabicTypeFace = Typeface.createFromAsset(getApplicationContext().getAssets(), "majalla.ttf");
        mIconFont = Typeface.createFromAsset(getApplicationContext().getAssets(), "icon_font.ttf");
        mFlatIcon = Typeface.createFromAsset(getApplicationContext().getAssets(), "flaticon.ttf");
        mshekari = Typeface.createFromAsset(getApplicationContext().getAssets(), "shekari.ttf");
        mDastnevis = Typeface.createFromAsset(getApplicationContext().getAssets(), "dastnevis.otf");
        mMitra = Typeface.createFromAsset(getApplicationContext().getAssets(), "mitra.ttf");
        mPhalls =  Typeface.createFromAsset(getApplicationContext().getAssets(), "jozoor.ttf");
        mDigital_7 =  Typeface.createFromAsset(getApplicationContext().getAssets(), "digital_7.ttf");

        if(!mISRetroThemeOn){
            mEnglishTypeFace = mRobotoThin;
            mPersianTranslationTypeface = mMitra;
            mTranslationBoxTypeface = mRobotoLight;
            resultTextView.setTypeface(mRobotoLight);
        }else {
            mEnglishTypeFace = mPhalls;
            mPersianTranslationTypeface = mPhalls;
            mTranslationBoxTypeface = mPhalls;

        }

        //todo refactor code
        mFavoritesList.setTypeface(mFlatIcon);
        mFavoritesList.setText(getResources().getString(R.string.list));
        mFavoritesList.setTextSize(30);
        mFavoritesList.setTextColor(getAccentColorCode());

        mAddLabel.setTypeface(mFlatIcon);
        mAddToFavorites.setTypeface(mFlatIcon);
    }

    private void setResultTextBox(){

        if(!mISRetroThemeOn) {
            resultTextView.setBackgroundColor(getAccentColorCode());
            result_textView_holder.setBackgroundColor(getAccentColorCode());
            resultTextView.setTextColor(Color.WHITE);
            resultTextView.setTypeface(mEnglishTypeFace);
        }else {
            resultTextView.setBackgroundColor(Color.BLACK);
            resultTextView.setTextColor(Color.BLACK);
            resultTextView.setTypeface(mDigital_7);

        }
        resultTextView.setSingleLine();
        resultTextView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
        resultTextView.setInputType(EditorInfo.TYPE_CLASS_TEXT);
    }

    public int getAccentColorCode(){
        //TODO set a cool default theme color
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("THEME", MODE_PRIVATE);
        return appPreferences.getInt("ACCENT_COLOR_CODE", Color.parseColor("#1abc9c"));
    }

    public boolean watchedIntro(){
        //TODO set a cool default theme color
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("watchedIntro", MODE_PRIVATE);
        return appPreferences.getBoolean("hasWatched_v1.71", false);
    }


    public boolean getAngleMode(){
        //TODO set a cool default theme color
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("angleMode", MODE_PRIVATE);
        return appPreferences.getBoolean("isDeg",true);
    }

    void setAngleMode(boolean isDeg){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("angleMode", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        if(isDeg){

            editor.putBoolean("isDeg", true);
            mScientificModeTextView.setText("DEG");

        }else{

            editor.putBoolean("isDeg", false);
            mScientificModeTextView.setText("RAD");

        }
        editor.apply();


    }

    public int getKeypadBackgroundColorCode(){
        //TODO set a cool default theme color
        //light theme is 0
        //dark theme is 1
        //retro theme is 2
        int  currentThemeNumber = 0;
        SharedPreferences appPreferences =getApplicationContext().getSharedPreferences("THEME",Context.MODE_PRIVATE);
        currentThemeNumber = appPreferences.getInt("KEYPAD_BACKGROUND_COLOR_CODE",Color.WHITE);

        return currentThemeNumber;
    }




    // Saving the selected color theme to prefrence
    public void saveAccentColorCode(int colorCode){
        SharedPreferences appPreferences =getApplicationContext().getSharedPreferences("THEME",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt("ACCENT_COLOR_CODE", colorCode);
        editor.commit();
    }


    private void saveKeypadBackgroundColorCode(int themeNumber) {
        SharedPreferences appPreferences =getApplicationContext().getSharedPreferences("THEME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt("KEYPAD_BACKGROUND_COLOR_CODE", themeNumber);
        editor.commit();
    }
    private void saveKeypadFontColorCode(int colorCode) {
        SharedPreferences appPreferences =getApplicationContext().getSharedPreferences("THEME",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt("KEYPAD_FONT_COLOR_CODE", colorCode);
        editor.commit();
    }



    public void changeAccentColor(int colorCode) {
        saveAccentColorCode(colorCode);
        redrawAccent();
    }

    void redrawAccent(){

        if(!mISRetroThemeOn) {
            sendChangeAccentColorIntent();
            setMViewPagerIndicatorColor();
            resultTextView.setBackgroundColor(getAccentColorCode());
            result_textView_holder.setBackgroundColor(getAccentColorCode());
        }else {
            resultTextView.setBackgroundColor(Color.TRANSPARENT);
            result_textView_holder.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    public void changeKeypadBackgroundColor(int backgroundColorCode){
        saveKeypadBackgroundColorCode(backgroundColorCode);
        redrawKeypadBackground();
        sendChangeKeypadFontColorIntent();
    }
    public int getKeypadFontColor() {
        if (getKeypadBackgroundColorCode() != Color.WHITE){
            return  Color.parseColor("#757575");
        }else {
            return  Color.LTGRAY;
        }
    }

    private void redrawKeypadBackground() {
        View activityView = findViewById(R.id.activity_body);
        if (isRetroThemeSelected()){
//                activityView.setBackground(getDrawable(R.drawable.background_normal));
            mTranslationBox.setBackgroundColor(Color.TRANSPARENT);

        }else{
            View fragmentContainer = findViewById(R.id.fragment_container);

//            fragmentContainer.setBackground(key());
            mTranslationBox.setBackgroundColor(getKeypadBackgroundColorCode());
            int testtt = getKeypadBackgroundColorCode();
            mTranslationBox.setTextColor(getKeypadFontColor());
            fragmentContainer = findViewById(R.id.lower_half);
            activityView.setBackgroundColor(getKeypadBackgroundColorCode());

        }
    }

    //
    private void sendChangeAccentColorIntent() {
        Intent intent = new Intent("themeIntent");
        intent.putExtra("message", "changeAccentColor");
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }
    private void sendChangeKeypadFontColorIntent() {
        Intent intent = new Intent("themeIntent");
        intent.putExtra("message", "changeKeypadFontColor");
        intent.putExtra("fontColor",getKeypadFontColor());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public void setMViewPagerIndicatorColor(){
        mViewPagerIndicator.setFillColor(getAccentColorCode());
//        mViewPagerIndicator.setPageColor(getAccentColorCode());

    }

    public int getTranslationLanguage(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("LanguagePreference", MODE_PRIVATE);
        //Todo make sure this works with byte constants
        return appPreferences.getInt("LANGUAGE", DEFAULT_LANGUAGE);
    }
    public AutoResizeTextView getMTranslationEditText(){
        return (AutoResizeTextView) findViewById(R.id.translationEditText);
    }
    public void setTranslationText(String text){
        AutoResizeTextView translationBox;
        translationBox = (AutoResizeTextView) findViewById(R.id.translationEditText);
        translationBox.setText(text);
        translationBox.setMovementMethod(ScrollingMovementMethod.getInstance());
    }



    /// ==================== ViewController implementation

    void aButtonIsPressed(String currentButtonValue){
        // currentButtonValue is tag element of Buttons
        // ordering of this if matters!! don't move it down bellow
        //todo mdelta goes here
        if  (( mViewPager.getCurrentItem()) != this.mDefaultPage) {
            switchToMainFragment();
        }

        if (currentButtonValue.equals(getResources().getString(R.string.clear))) {
            performClearResult();
            return;
        }

        if (currentButtonValue.equals(getResources().getString(R.string.mc))) {
            performMC();
            return;
        }


        if (currentButtonValue.equals(getResources().getString(R.string.mr))) {
            performMR();
            return;
        }

        if (currentButtonValue.equals(getResources().getString(R.string.mplus))) {
            performMPlus();
            return;
        }

        if (currentButtonValue.equals(getResources().getString(R.string.mminus))) {
            performMMinus();
            return;
        }

        if (currentButtonValue.equals("\u2190")) {
            performBackspace();
            return;
        }



        boolean shouldPrevent ;
        if  ( getMJustPressedExecute())
            if ( Character.isDigit(currentButtonValue.charAt(0))
                    || currentButtonValue.equals(getResources().getString(R.string.Pi))
                    || currentButtonValue.equals(getResources().getString(R.string.e))) {
                setMExpressionString("");
                mButtonsStack.clear();
            }

        setMJustPressedExecute(false);
        shouldPrevent = preventCommonErrors(currentButtonValue.charAt(0));
        if(shouldPrevent) {
            playSound(errorSoundID);
            mTranslationBox.startAnimation(mErrorBlink);
            return;
        }


        if( currentButtonValue.equals("=") ) {
            if(getMExpressionString().length() > 0){
                if (calculateResult(null) == 0) {
                    setMJustPressedExecute(true);
                    updateUIExecute(true);
                }
            }
        }
        else
            //TODO Here a button is pressed which we don't want to be evaluated (non DIGIT or Functions)
            if (isNonDigit(currentButtonValue)){
                appendMExpressionString(currentButtonValue);
                pushLastButton(currentButtonValue);
                playSound(operatorsButtonSoundID);
                updateUIOperator();
            }
            else {
                //else it is deffinately a digit
                playSound(numericButtonSoundID);

                if (calculateResult(currentButtonValue) != 2){
                    updateUIDigit();
                }
            }
        checkCLRButtonSendIntent();
    }

    void switchToMainFragment() {
        mViewPager.setCurrentItem(this.mDefaultPage);

    }

    private void performMMinus() {

        mMemoryVariable =  mMemoryVariable.subtract(new BigDecimal(resultTextView.getText().toString().replace(",", "")));

        //todo add mmvariable with what's on display right now
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        df.setMaximumFractionDigits(6);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        String result = df.format(mMemoryVariable);
        result = result.replaceAll("^-(?=0(.0*)?$)", "");
        mButtonsStack.clear();
        mButtonsStack.push(mTemp);
        mJustPressedExecuteButton = true;
        playSound(clearAllButtonSoundID);
        checkCLRButtonSendIntent();
        mMemoryVariableTextView.setText(" M = " + result);

        Toast.makeText(getApplicationContext(),"M-",Toast.LENGTH_SHORT).show();
    }

    private void performMPlus() {


        //todo add mmvariable with what's on display right now
        mMemoryVariable =  mMemoryVariable.add(new BigDecimal(resultTextView.getText().toString().replace(",","")));
        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        df.setMaximumFractionDigits(6);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
        String result = df.format(mMemoryVariable);
        result = result.replaceAll("^-(?=0(.0*)?$)", "");
        mButtonsStack.clear();
        mButtonsStack.push(mTemp);
        mJustPressedExecuteButton = true;
        playSound(clearAllButtonSoundID);
        checkCLRButtonSendIntent();
        mMemoryVariableTextView.setText(" M = " + result);
        Toast.makeText(getApplicationContext(),"M+",Toast.LENGTH_SHORT).show();

    }

    private void performMR() {

        String da = getMExpressionString().toString();
        if( da.length()>0 && (da.charAt(da.length()-1) == '+' ||
                da.charAt(da.length()-1) == '−' ||
                da.charAt(da.length()-1) == '\u00d7' ||
                da.charAt(da.length()-1) == '÷')
                ) {

            setMExpressionString(getMExpressionString().append(mMemoryVariable.toPlainString()).toString());
            Expression expression = new Expression(getMExpressionString().toString(),getAngleMode(),getApplicationContext());
            mTemp = evaluateResult(expression);
            sendLogMessage(getMExpressionString().toString(), mTemp,false, String.valueOf(mTagHimself.getText()));

            mResultBeforeSignification = new BigDecimal(mTemp.replace(",",""));


        }else {

            DecimalFormat df = new DecimalFormat();
            df.setGroupingUsed(true);
            df.setGroupingSize(3);
            df.setMaximumFractionDigits(6);
            df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
            mTemp = df.format(mMemoryVariable);
            mTemp = mTemp.replaceAll("^-(?=0(.0*)?$)", "");

            mResultBeforeSignification = mMemoryVariable;

        }
        if(mTemp.indexOf(".") != -1){
            mDecimal_fraction = mTemp.substring(mTemp.indexOf(".")+1);
        }        resultTextView.startAnimation(out_anim);
        mButtonsStack.clear();
        mButtonsStack.push(mTemp);
        mJustPressedExecuteButton = true;
        setMExpressionString(mTemp);
        displayTranslation();
        playSound(clearAllButtonSoundID);
        checkCLRButtonSendIntent();
        Toast.makeText(getApplicationContext(),"MR",Toast.LENGTH_SHORT).show();

    }

    private void performMC() {
        mMemoryVariable = new BigDecimal(0);
        mMemoryVariableTextView.setText(" M = 0");
        mDecimal_fraction = "";
        Toast.makeText(getApplicationContext(),"MC",Toast.LENGTH_SHORT).show();
        playSound(clearAllButtonSoundID);

    }


    private boolean reverseVolume() {
        //if user has volume ... the mute it
        if (getVolumeFromPreference()) {

            setVolumeInPreference(false);
            //  sendVolumeMessage(false);
            return false;

        }else{
            // turn the volume up
            // sendVolumeMessage(true);
            setVolumeInPreference(true);

            playSound(mHasVolumeSoundID);
            return true;
        }
    }



    private void setVolumeInPreference(boolean hasVolume){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("volumeState", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        if (hasVolume) {

            editor.putBoolean("hasVolume", true);

        } else {

            editor.putBoolean("hasVolume", false);

        }
        editor.apply();


    }

    private void setWatcheIntroPreference(boolean hasWatched){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("watchedIntro", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();

        if (hasWatched) {
            editor.putBoolean("hasWatched_v1.71", true);
        } else {
            editor.putBoolean("hasWatched_v1.71", false);
        }
        editor.commit();


    }



//    private void switchToColorPicker() {
//        int test = mViewPager.getCurrentItem();
//        if (mViewPager.getCurrentItem()!= mColorPage) {
//            switch (mLayoutState){
//                case PORTRAIT_BOTH:
//                    mColorPage = COLOR_PICKER_FRAGMENT_PORTRAIT;
//                    break;
//                case  LANDSCAPE_TABLET:
//                    mColorPage = COLOR_PICKER_FRAGMENT_LANDSCAPE_TABLET;
//                    break;
//
//                case LANDSCAPE_PHONE:
//                    mColorPage = COLOR_PICKER_FRAGMENT_LANDSCAPE_PHONE;
//                    break;
//
//            }
//            mViewPager.setCurrentItem(mColorPage);
//
//        }else {
//            mViewPager.setCurrentItem(mDefaultPage);
//        }
//    }

    private void performBackspace() {

        // First we cut the last button that we pressed


        if (mButtonsStack.size() > 0) {
            this.setMExpressionString(getMExpressionString()
                    .substring(0, getMExpressionString().length() - lastButtonLength()));
            popPressedButton();

            if (isNonDigit(peekLastButton())) {
                //last button was non-Digit so no need to calculate result and face an Error !
                updateUIOperator();

            } else {
                calculateResult(null);
                updateUIDigit();
            }
        }
        playSound(backSpaceButtonSoundID);
        checkCLRButtonSendIntent();

    }
    private int lastButtonLength(){

        return peekLastButton().length();
    }
    public void pushLastButton(String input){
        mButtonsStack.push(input) ;

    }
    public String popPressedButton(){
        return mButtonsStack.pop();

    }
    public String peekLastButton(){
        return mButtonsStack.peek();
    }

    private void performClearResult(){

        setMExpressionString("");
        setTranslationText("");
//        mResult.setText(getResources().getString(R.string.zero)); todo
        mTemp = "0";
        resultTextView.startAnimation(out_anim);

        mButtonsStack.clear();
        playSound(clearAllButtonSoundID);
    }

    int stackSize(){

        return  mButtonsStack.size();
    }


    void checkCLRButtonSendIntent(){
        if(stackSize()<= 1) {
            sendClearButtonMessage(getResources().getString(R.string.clear));
        }
        else{
            sendClearButtonMessage(getResources().getString(R.string.backSpace));

        }
    }



    //            DecimalFormat df = new DecimalFormat("#");
//            df.setMaximumFractionDigits(4);
//              mDecimal_fraction =  df.format(mTemp).toString();

    private boolean updateUIExecute(boolean sendLogMessage) {
//        Log.d("vivz", "Activity - update ui called");

        mDecimal_fraction = "";
        byte decimalIndex = (byte) mTemp.indexOf(".");
        if(decimalIndex != -1){
            mDecimal_fraction = mTemp.substring(mTemp.indexOf(".")+1);
        }

        try {

            resultTextView.setText(mTemp);
            displayTranslation();

        } catch (Exception e) {
            e.printStackTrace();
            playSound(errorSoundID);
            return true;
        }

        if(sendLogMessage) {
            mLogAdded = false;
            sendLogMessage(getMExpressionString().toString(), mTemp,false, String.valueOf(mTagHimself.getText()));
        }
        setMExpressionString(resultTextView.getText().toString().replace(",",""));
        mButtonsStack.clear();

        mButtonsStack.push(resultTextView.getText().toString().replace(",",""));
        playSound(executeButtonSoundID);
        resultTextView.startAnimation(out_anim);

        return false;
    }

    //log added == true
    //
    private void updateUIDigit() {
        if(getMExpressionString().length()>0) {
            //any button But Equal pressed ..refresh result With mMathexpression
            //Also mResult With Value of mExp
            resultTextView.setText(mTemp);
            String gg = getMExpressionString().toString().replaceAll("(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))", "$0,");
            mTranslationBox.setTypeface(mEnglishTypeFace);
            mTranslationBox.setText(gg);

        }
    }

    private byte calculateResult(String currentButtonValue) {
        mTemp = "0";
        String testSubject ;
        testSubject = mExpressionBuffer.toString();
        if(currentButtonValue != null){
            testSubject += currentButtonValue ;
        }
        try {
            String withoutcomas = testSubject.toString().replace(",","");
            Expression expression = new Expression(withoutcomas,getAngleMode(), getApplicationContext());
            mTemp = evaluateResult(expression);
            mExpressionBuffer = new StringBuilder(testSubject);
            if(currentButtonValue != null) {
                pushLastButton(currentButtonValue);
            }


        } catch (ArithmeticException e) {

            mExpressionBuffer = new StringBuilder(testSubject);
            if(currentButtonValue != null) {
                pushLastButton(currentButtonValue);
            }
            mTemp = "∞";

            // Happens if it is devision by zero
            return 1;
        }
        catch (NumberFormatException e) {

            mExpressionBuffer = new StringBuilder(testSubject);
            if(currentButtonValue != null) {
                pushLastButton(currentButtonValue);
            }
            mTemp = "error";
            // Happens if it is devision by zero
            return 1;
        }



        catch (Exception e) {
            e.printStackTrace();
            playSound(errorSoundID);
            setMJustPressedExecute(false);
            //TODO decide the fate of clear button ?
            // Happens for generals exceptions

            return 2;
        }
        // returning zero means there's no problems what so ever
        return 0;
    }

    private void updateUIOperator() {
        String gg = getMExpressionString().toString().replaceAll("(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))", "$0,");
        mTranslationBox.setTypeface(mEnglishTypeFace);
        mTranslationBox.setText(gg);
        return;
    }
    boolean isRetroThemeSelected(){
        return mISRetroThemeOn;
    }

    void setRetrothemeSelected(boolean _isSelected){
        mISRetroThemeOn = _isSelected;
    }

    private void displayTranslation() {


        refreshLanguagefontsBeforeTranslation();
        printResultWithTranslation(mDecimal_fraction, getTranslationLanguage());
        mTranslationBox.startAnimation(mBlink);
    }

    private void refreshLanguagefontsBeforeTranslation() {


        switch (getTranslationLanguage()) {
            //FOR PERSIAN   **************
            case LANGUAGE_PERSIAN:
                getMTranslationEditText().setTypeface(mPersianTranslationTypeface);
                mTranslationBoxTypeface = mPersianTranslationTypeface;
                break;
            case LANGUAGE_ARABIC:
                getMTranslationEditText().setTypeface(mArabicTypeFace);
                mTranslationBoxTypeface = mArabicTypeFace;
                break;

            case LANGUAGE_ENGLISH:
            case LANGUAGE_FRENCH:
            case LANGUAGE_GERMAN:
            case LANGUAGE_ITALIAN:
                getMTranslationEditText().setTypeface(mRobotoLight);
                mTranslationBoxTypeface = mRobotoLight;
                break;
        }
    }


    private void printResultWithTranslation(String decimal_fraction, int Language) {
//        String integerFraction = mResultBeforeSignification.longValue();
        String resultWithoutCommas = mTemp.replace(",","");
        int pointIndex = resultWithoutCommas.indexOf(".");
        String integerFraction = "";
        boolean resultIsNegative = mResultBeforeSignification.compareTo(new BigDecimal(-0.0000009)) < 0;

        if(pointIndex == -1){
            integerFraction = resultWithoutCommas;
        }else {
            integerFraction = resultWithoutCommas.substring(0, pointIndex);
        }
        if(resultIsNegative && integerFraction.length()>0){
            integerFraction = integerFraction.substring(1);
        }
        String partII;


// todo for multilanguage google play
//        NumberFormat formatter;

        switch (Language){
            case LANGUAGE_ENGLISH:


                if(mResultBeforeSignification.longValue()== 42 && decimal_fraction.equals("")& !resultIsNegative) {
                    mTranslationBox.setText("Google \"Answer to the Ultimate Question of Life, The Universe, and Everything\" ");
                    return;
                }
                if(resultIsNegative){
                    mTranslationBox.setText("minus " + NumberConveterAmerican.convert(integerFraction.substring(0)));
                }

                else {
                    mTranslationBox.setText(NumberConveterAmerican.convert(integerFraction));
                }


                if (!decimal_fraction.equals("")) {
                    partII = NumberConveterAmericanPartII.convert(decimal_fraction);

                    mTranslationBox.append(partII);
                }
//                formatter = new RuleBasedNumberFormat(new ULocale("en_US"), RuleBasedNumberFormat.SPELLOUT);
//                mTranslationBox.setText(formatter.format(new BigDecimal(resultWithoutCommas)));
                break;

            case LANGUAGE_FRENCH:
                if(mResultBeforeSignification.longValue()== 42 && decimal_fraction.equals("")& !resultIsNegative) {
                    mTranslationBox.setText("google quelle est la réponse à la vie l'univers et tout");
                    return;
                }


                if(resultIsNegative) {
                    mTranslationBox.setText("moins " + NumberConverterFrench.convert(integerFraction));
                }
                else {
                    mTranslationBox.setText(NumberConverterFrench.convert(integerFraction));
                }

                if (!decimal_fraction.equals("")) {
                    partII = NumberConverterFrenchPartII.convert(decimal_fraction);
                    mTranslationBox.append(partII);
                }

//                formatter = new RuleBasedNumberFormat(new ULocale("fr_FR"), RuleBasedNumberFormat.SPELLOUT);
//                mTranslationBox.setText(formatter.format(new BigDecimal(resultWithoutCommas)));
                break;


            case LANGUAGE_ARABIC:
                if(mResultBeforeSignification.longValue()== 42 && decimal_fraction.equals("")& !resultIsNegative) {
                    mTranslationBox.setText("google quelle est la réponse à la vie l'univers et tout");
                    return;
                }


                if(resultIsNegative) {
                    NumberConverterArabic arabic = new NumberConverterArabic(mResultBeforeSignification.abs());
                    mTranslationBox.setText("ناقص " + arabic.ConvertToArabic());
                }
                else {
                    NumberConverterArabic arabic = new NumberConverterArabic(mResultBeforeSignification);
                    mTranslationBox.setText(arabic.ConvertToArabic());
                }

                break;


            case LANGUAGE_PERSIAN:

                if(mResultBeforeSignification.longValue()== 42 && decimal_fraction.equals("")& !resultIsNegative) {
                    mTranslationBox.setText("از گوگل بپرس what's the answer to life the universe and everything");
                    return;
                }


                mTranslationBox.setText(new NumberConveterPersianPartI().convert(integerFraction));

                if (!decimal_fraction.equals("")) {
                    partII = NumberConverterPersianPartII.convert(decimal_fraction);
                    if (new BigDecimal(resultWithoutCommas).compareTo(BigDecimal.ONE) >= 0 ||new BigDecimal(resultWithoutCommas).compareTo(new BigDecimal(-1)) <= 0 ) {
                        mTranslationBox.append("ممیز " + partII);
                    }   else {
                        mTranslationBox.setText(partII);
                    }
                }
                if(resultIsNegative) {
                    mTranslationBox.setText("منفی " + mTranslationBox.getText());
                }
                break;
        }
    }

    public void setMJustPressedExecute(Boolean pressed){

        mJustPressedExecuteButton = pressed;
    }
    public Boolean getMJustPressedExecute(){

        return mJustPressedExecuteButton;

    }
    private Boolean isOperator(Character inputChar){
        switch (inputChar) {
            case '+':
            case '−':
            case '÷':
            case '*':
            case 'x':
            case '\u00d7':
                return true;

        /*    case '(':
            case ')':*/
            default:
                return false;
        }
    }

    // Error - Fixing Methods

    private boolean preventCommonErrors(char currentChar){

        boolean hasError1 = false, hasError2 = false,hasError3 = false;
        // TODO change char to string
        if (getMExpressionString().length() == 0)
            hasError1 = fixFirstChar(currentChar);
        hasError2 = fixSuccessiveOperators(currentChar);


        if ((currentChar) == '.') {
            hasError3 = fixDoublePoints( currentChar);
        }
        return (hasError1 || hasError2 || hasError3);
    }
    private boolean fixFirstChar(char input) {
        if(getMExpressionString().length() == 0) {
            switch (input) {
                case '+':
                case '÷':
                case 'x':
                case '*':
                case '\u00d7':
                case ')':
                case '=':
                    return true;


            }
        }
        return false;
    }
    private boolean fixSuccessiveOperators(char input) {

        int lastIndex = getMExpressionString().length() - 1;

        // allow 2*-
        if ((getMExpressionString().length() > 1)) {
            if ((getMExpressionString().charAt(lastIndex) == '\u00d7' )||
                    ( getMExpressionString().charAt(lastIndex)== '÷'))
                if (input == '−') {
                    return false;
                }

//            //TODO ok to delete ? 2*-
//            if(getMExpressionString().length()>2)
//                if (isOperator(input))
//                if(isOperator(getMExpressionString().charAt(lastIndex)))
//                    if(isOperator(getMExpressionString().charAt(lastIndex - 1)))
//                    {
//                        return true;
//                    }


            // deny 2++
            if (isOperator(input))
            {
                if (isOperator(getMExpressionString().charAt(lastIndex)))
                    return true;
            }
        }
        return false;
    }
    private boolean fixDoublePoints(char input){
        int legalStart = -1;

        for(int index = 0;index< getMExpressionString().length();index ++){
            if(!Character.isDigit(getMExpressionString().charAt(index))){
                if(getMExpressionString().charAt(index) != '.')
                    legalStart = index;
            }
        }
        int lastOccurrence ;
        lastOccurrence = getMExpressionString().lastIndexOf(".");
        //secondOccurrence = getMExpressionString().length()  ;
        if (lastOccurrence <= legalStart){
            return false;
        }
        return true;
    }
    private void deleteLastChar(){
        int lastIndex = getMExpressionString().length() - 1;
        if(isOperator(getMExpressionString().charAt(lastIndex)))
            deleteMExpressionStringAt(lastIndex);

    }




    private String evaluateResult(Expression exp) {

        String result ="";
        mResultBeforeSignification = exp.evaluate();

        DecimalFormat df = new DecimalFormat();
        df.setGroupingUsed(true);
        df.setGroupingSize(3);
        df.setRoundingMode(RoundingMode.HALF_EVEN);
        df.setMaximumFractionDigits(6);
        df.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        result  =   df.format(mResultBeforeSignification);
//        result = result.replaceAll("^-(?=0(.0*)?$)", "");

        return result;

        // }else{
        //result = String.format(Locale.US, "%.4f", mResultBeforeSignification);
        //  result = Double.toString(mResultBeforeSignification);
        // result = String.format("%."+ DECIMAL_FRACTION_LENGTH +"f",(float) mResultBeforeSignification);
        // }
        //String formatted = df.format(mResultBeforeSignification);

//        result = String.format(Locale.US, "%.6f", result);
//        result = mResultBeforeSignification.toPlainString();
    }

/*    private  String fixRedundantZeroesofFloatNumbers(String input){
        int floatPointPosition ;
        int  counter = input.length()-1;
        floatPointPosition = input.lastIndexOf(".");
        while((counter >floatPointPosition) && (input.charAt(counter)=='0')){
            input = input.substring(0,counter);
            counter--;
        }
        if(input.charAt(input.length()-1)=='.')
            return input.substring(0,input.length()-1);

        return input;
    }*/


    public void playSound(int id) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float actualVolume = (float) audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume;
        // = actualVolume / maxVolume;


        if (getVolumeFromPreference() == true) {
            volume =  maxVolume;
        } else {
            volume = 0;
        }

        // Is the sound mSoundPoolLoaded already?
        if (mSoundPoolLoaded) {
            mSoundPool.play(id, volume, volume, 1, 0, 1f);
        }


    }
    void prepareSoundStuff(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
           mSoundPool =  createNewSoundPool();
        }else{
           mSoundPool =  createOldSoundPool();
        }
        mSoundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId,
                                       int status) {
                mSoundPoolLoaded = true;
            }
        });
       if (mISRetroThemeOn) {
           numericButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.button1, 1);
           executeButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.button28, 1);
           clearAllButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.clear_retro, 1);
           backSpaceButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.backspace, 1);
           operatorsButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.operator_retro, 1);
           errorSoundID = mSoundPool.load(getApplicationContext(), R.raw.error_retro, 1);
           mHasVolumeSoundID = mSoundPool.load(getApplicationContext(), R.raw.backspace, 1);



       }else{


           numericButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.keypress, 1);
           executeButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.equal, 1);
           clearAllButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.clear, 1);
           backSpaceButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.backspace, 1);
           operatorsButtonSoundID = mSoundPool.load(getApplicationContext(), R.raw.keypress, 1);
           errorSoundID = mSoundPool.load(getApplicationContext(), R.raw.error, 1);
           mHasVolumeSoundID = mSoundPool.load(getApplicationContext(), R.raw.backspace, 1);
        }


    }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected SoundPool createNewSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        SoundPool sounds = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
                return sounds;
    }
            @SuppressWarnings("deprecation")
    protected SoundPool createOldSoundPool(){
      SoundPool  sounds = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
                return sounds;
    }

    void prepareAnimationStuff(){
        in_anim = AnimationUtils.loadAnimation(this, android.R.anim.slide_in_left);
        out_anim  = AnimationUtils.loadAnimation(this, android.R.anim.slide_out_right);
        out_anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resultTextView.setText(mTemp);
                resultTextView.startAnimation(in_anim);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        out_anim.setDuration(100);
        prepareBlinkingErrorAnimation();
        prepareBlinkingAnimation();

    }
    void prepareBlinkingErrorAnimation(){

        mErrorBlink = new AlphaAnimation(0.0f, 1.0f);
        mErrorBlink.setDuration(100); //You can manage the time of the blink with this parameter
        // mErrorBlink.setStartOffset(20);
        mErrorBlink.setRepeatMode(Animation.REVERSE);
        mErrorBlink.setRepeatCount(4);

    }
    void prepareBlinkingAnimation(){

        mBlink = new AlphaAnimation(0.0f, 1.0f);
        mBlink.setDuration(100); //You can manage the time of the blink with this parameter
        //  mBlink.setStartOffset(20);
        mBlink.setRepeatMode(Animation.REVERSE);
        mBlink.setRepeatCount(0);

    }
   /* void prepareFadeAnimation(){

        mFadeIn =  AnimationUtils.loadAnimation(this, android.R.anim.fadein);;
        mBlink.setDuration(100); //You can manage the time of the blink with this parameter
        //  mBlink.setStartOffset(20);
        mBlink.setRepeatMode(Animation.REVERSE);
        mBlink.setRepeatCount(0);

    }
*/


    public void setMExpressionString(String input){

        mExpressionBuffer.replace(0, mExpressionBuffer.length(), input);
    }
    public StringBuilder getMExpressionString(){

        return mExpressionBuffer;

    }
    public void appendMExpressionString(CharSequence input){

        mExpressionBuffer.append(input);
    }
    public void deleteMExpressionStringAt(int index){

        mExpressionBuffer.deleteCharAt(index);
    }

    // Send an Intent with an action named "my-event".
    void sendLogMessage(String Operation, String Result, boolean starred, String tagText) {
        Intent intent = new Intent("LogIntent");
        // add data
        intent.putExtra("OPERATION", Operation );
        intent.putExtra("RESULT", Result);
        intent.putExtra("STARRED", starred);
        intent.putExtra("TAG", tagText);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        ContentValues cv = new ContentValues();
        cv.put(LogContract.LogEntry.COLUMN_RESULT,Result);
        cv.put(LogContract.LogEntry.COLUMN_OPERATION,Operation);
        cv.put(LogContract.LogEntry.COLUMN_TAG,tagText);
        cv.put(LogContract.LogEntry.COLUMN_STARRED, 0);

        getContentResolver().insert(LogContract.LogEntry.CONTENT_URI,cv);


//        if(mActivityLogAdapter != null ) {
//            mActivityLogAdapter.notifyDataSetChanged();
//        }
//
//        if(mActivtyLogListView != null ) {
//            mActivtyLogListView.invalidate();
//        }

    }


    private void sendClearButtonMessage(String value) {
        Intent intent = new Intent("clearIntent");
        // add data
        intent.putExtra("buttonValue", value);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }



    private boolean isNonDigit (String currentButtonValue){

        String[] nonDigitStrings = { "(", ")" , getResources().getString(R.string.point),
                getResources().getString(R.string.sin) + "(",
                getResources().getString(R.string.cos) + "(",
                getResources().getString(R.string.tan)+ "(",
                getResources().getString(R.string.cot) + "(",
                getResources().getString(R.string.csc) + "(",
                getResources().getString(R.string.sec)+"(",

                getResources().getString(R.string.asin) + "(" ,
                getResources().getString(R.string.acos) + "("  ,
                getResources().getString(R.string.atan) + "("  ,
                getResources().getString(R.string.acot) + "(" ,
                getResources().getString(R.string.acsc) + "(",
                getResources().getString(R.string.asec) + "(",

                getResources().getString(R.string.sinh) + "(",
                getResources().getString(R.string.cosh) + "(",
                getResources().getString(R.string.tanh) + "(",
                getResources().getString(R.string.coth) + "(",
                getResources().getString(R.string.sech) + "(",
                getResources().getString(R.string.csch) + "(",

                getResources().getString(R.string.asinh) + "(" ,
                getResources().getString(R.string.acosh) + "("  ,
                getResources().getString(R.string.atanh) + "("  ,
                getResources().getString(R.string.acoth) + "(" ,
                getResources().getString(R.string.asech) + "(",
                getResources().getString(R.string.acsch) + "(",

                getResources().getString(R.string.ln_tag), getResources().getString(R.string.power), getResources().getString(R.string.log_tag),
                getResources().getString(R.string.sqrt), getResources().getString(R.string.e),"+","−","-","÷","×"};


        List<String> myList = Arrays.asList(nonDigitStrings);
        if(myList.contains(currentButtonValue)) return true;

        return false;
    }




    Typeface get_Default_Dialpad_Button_typeface() {

        //if retro theme is selected we only need a default (roboto regular) font
        if(getKeypadBackgroundColorCode() == 2){
            return mRobotoLight;
        }
        //else chose from users previous preference (Thin - Light - Regular)
        else {
            SharedPreferences appPreferences =getSharedPreferences("typography", Context.MODE_PRIVATE);
            switch (appPreferences.getInt("DIALPAD_FONT",DIALPAD_FONT_ROBOTO_THIN)) {
                case DIALPAD_FONT_ROBOTO_THIN :
                    return mRobotoThin;
                case DIALPAD_FONT_ROBOTO_LIGHT :
                    return mRobotoLight;
                case DIALPAD_FONT_ROBOTO_REGULAR :
                    return mRobotoRegular;
            }
        }
        return mRobotoThin;


    }


    Typeface change_DialPad_TypeFace(int message) {

        switch (message) {
            case DIALPAD_FONT_ROBOTO_THIN :
                return mRobotoThin;

            case DIALPAD_FONT_ROBOTO_LIGHT :
                return mRobotoLight;

            case DIALPAD_FONT_ROBOTO_REGULAR :
                return mRobotoRegular;

        }
        return mRobotoThin;
    }


    public void setmDefaultPage(byte _defaultPage) {
        this.mDefaultPage = (byte) (_defaultPage);
    }


    public void setMListView(Serializable _listView) {
        mListView = _listView;
    }

    public Serializable getmListView() {
        return mListView;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){


            case R.id.switch_deg_rad:

                boolean on = ((Switch) v).isChecked();

                if (on) {
                    //result in Radian
                    setAngleMode(true);
                } else {
                    //result in Degress
                    setAngleMode(false);

                }
                break;

            case R.id.favorites_list:


//                ViewGroup rootView = (ViewGroup)findViewById(R.id.activity_body);
//                PopoverView popoverView = new PopoverView(this, R.layout.popover_showed_view);
//                popoverView.setContentSizeForViewInPopover(new Point(600, 600));
//                popoverView.setDelegate(this);
//                popoverView.showPopoverFromRectInViewGroup(rootView, PopoverView.getFrameForView(v), PopoverView.PopoverArrowDirectionAny, true);

                FragmentManager fm = getSupportFragmentManager();
                FavoritesFragment favoritesDialog = new FavoritesFragment();
                favoritesDialog.show(fm, "fragment_favorites");
                break;

            case R.id.add_to_favorites:

                sendLogMessage(getMExpressionString().toString(), mTemp, true, String.valueOf(mTagHimself.getText()));






            case R.id.buttonSettings:
                CustomDialogClass cdc = new CustomDialogClass(this , android.R.style.Theme_Holo_Light_Dialog_MinWidth);
                cdc.show();
                setIntroButtonsVisibility(false);
                break;
//            case R.id.buttonPreferences2:
//                Intent intent = new Intent(getApplicationContext(),prefs.class);
//                startActivity(intent);
            case R.id.buttonLanguage:
                mButton = (Button)findViewById(R.id.buttonLanguage);
                showSpinner();
                break;

            case R.id.buttonParallax:
                Intent myIntent = new Intent(MainActivity.this, ParallaxActivity.class);
                MainActivity.this.startActivity(myIntent);
                break;


            case R.id.buttonMute:
                reverseVolume();
                if (getVolumeFromPreference() == true) {
                    ((Button)v).setText(getResources().getText(R.string.volume_high));
                }else {
                    ((Button)v).setText(getResources().getText(R.string.volume_off));
                }
                break;




            case R.id.buttonColors:

                final ColorPickerDialog colorcalendar = ColorPickerDialog.newInstance(
                        R.string.color_picker_default_title,
                        mSelectedColorCal0, Utils.ColorUtils.colorChoice(getApplicationContext()),Utils.ColorUtils.colorChoiceForKeypad(getApplicationContext()),
                        mSelectedColorCal0, Utils.isTablet(this)? ColorPickerDialog.SIZE_LARGE : ColorPickerDialog.SIZE_SMALL, 5
                );

                //Implement listener to get selected color value
                colorcalendar.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener(){

                    @Override
                    public void onColorSelected(int color) {
                        mSelectedColorCal0=color;
                    }

                });

                colorcalendar.show(getFragmentManager(),"cal");

        }
        return;
    }



    void PersianTranslationTypefaceChanged(){
        SharedPreferences typographyPreferences = getSharedPreferences("typography", Context.MODE_PRIVATE);

        switch (typographyPreferences.getInt("PERSIAN_TRANSLATION_TYPEFACE",PERSIAN_TRANSLATION_FONT_MITRA)){
            case PERSIAN_TRANSLATION_FONT_MITRA :
                if(!mISRetroThemeOn) {
                    mPersianTranslationTypeface = mMitra;
                }else {
                    mPersianTranslationTypeface = mPhalls;
                }
                break;
            case PERSIAN_TRANSLATION_FONT_DASTNEVIS :
                mPersianTranslationTypeface = mDastnevis;
                break;

            default:
                mPersianTranslationTypeface = mMitra;
                break;
        }
        getMTranslationEditText().setTypeface(mPersianTranslationTypeface);

    }


    private void showSpinner() {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
// todo google play edition
//        String[] options = {"Arabic","Deutch","English","French","Italian","Persian"};
        String[] options = {"Arabic","English","French","Persian"};
        b.setTitle("Language" );
        b.setSingleChoiceItems(options, -1,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("LanguagePreference", MODE_PRIVATE);
                        SharedPreferences.Editor editor = appPreferences.edit();

                        dialog.dismiss();
                        switch (which) {

                            case 0: {
                                editor.putInt("LANGUAGE", 3);

                                break;
                            }
                            case 1: {
                                editor.putInt("LANGUAGE", 2);

                                break;
                            }

                            case 2: {
                                editor.putInt("LANGUAGE", 1);
                                break;
                            }
                            case 3: {
                                editor.putInt("LANGUAGE", 0);

                                break;
                            }

                        }



                        // todo Google play edition
//                        switch (which) {
//                            case 0: {
//
//                                editor.putInt("LANGUAGE", 3);
//                                // french is 1
//                                break;
//                            }
//                            case 1: {
//                                editor.putInt("LANGUAGE", 4);
//                                // english is 2
//
//                                break;
//                            }
//                            case 2: {
//                                editor.putInt("LANGUAGE", 2);
//                                // arabic is 3
//
//                                break;
//                            }
//                            case 3: {
//                                editor.putInt("LANGUAGE", 1);
//                                // deuth is 4
//
//                                break;
//                            }
//                            case 4: {
//                                editor.putInt("LANGUAGE", 5);
//                                // italian is 5
//                                break;
//                            }
//                            case 5: {
//                                editor.putInt("LANGUAGE", 0);
//                                //persian is 0
//                                break;
//                            }
//
//                        }


                        editor.commit();
                        if (mDecimal_fraction != null) {
                            if (mJustPressedExecuteButton) {
                                displayTranslation();
                            } else {
                                refreshLanguagefontsBeforeTranslation();
                                mTranslationBox.startAnimation(mBlink);

                            }

                        }

                    }
                }
        );
        b.show();
    }



    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean on = ((Switch) buttonView).isChecked();

        if (on) {
            //result in Radian
            setAngleMode(true);
        } else {
            //result in Degress
            setAngleMode(false);

        }
    }


    /**
     * Called when the popover is going to show
     *
     * @param view The whole popover view
     */
    @Override
    public void popoverViewWillShow(PopoverView view) {

    }

    /**
     * Called when the popover did show
     *
     * @param view The whole popover view
     */
    @Override
    public void popoverViewDidShow(PopoverView view) {
        ListView mListView = (ListView)view.findViewById(R.id.listView1);
        List <Item> data =(List<Item>) getmListView();
        Log_Adapter mSummaryAdapter;
        Typeface defaultFont =  Typeface.createFromAsset( getAssets(), "roboto_light.ttf");
        if ( data != null) {
            mSummaryAdapter = new Log_Adapter(getApplicationContext(), getAccentColorCode(),data,defaultFont,mListView);
        }
        else   {
            mSummaryAdapter = new Log_Adapter(getApplicationContext(), getAccentColorCode(),defaultFont,mListView);
        }
        mListView.setAdapter(mSummaryAdapter);

    }

    /**
     * Called when the popover is going to be dismissed
     *
     * @param view The whole popover view
     */
    @Override
    public void popoverViewWillDismiss(PopoverView view) {

    }

    /**
     * Called when the popover was dismissed
     *
     * @param view The whole popover view
     */
    @Override
    public void popoverViewDidDismiss(PopoverView view) {

    }


    public void flipCard(View view) {
        if (mShowingBack) {

            mShowingBack = false;


            getSupportFragmentManager()
                    .beginTransaction()

                            // Replace the default fragment animations with animator resources representing
                            // rotations when switching to the back of the card, as well as animator
                            // resources representing rotations when flipping back to the front (e.g. when
                            // the system Back button is pressed).
                    .setCustomAnimations(
                            android.R.anim.slide_in_left,    android.R.anim.slide_in_left,
                            android.R.anim.slide_in_left,    android.R.anim.slide_in_left)

                            // Replace any fragments currently in the container view with a fragment
                            // representing the next page (indicated by the just-incremented currentPage
                            // variable).
                    .replace(R.id.log_container, new AnimatedLogFragment())

                            // Add this transaction to the back stack, allowing users to press Back
                            // to get to the front of the card.
                    .addToBackStack(null)

                            // Commit the transaction.
                    .commit();



            return;
        }

        // Flip to the back.

        mShowingBack = true;

        // Create and commit a new fragment transaction that adds the fragment for the back of
        // the card, uses custom animations, and is part of the fragment manager's back stack.

        getSupportFragmentManager()
                .beginTransaction()

                        // Replace the default fragment animations with animator resources representing
                        // rotations when switching to the back of the card, as well as animator
                        // resources representing rotations when flipping back to the front (e.g. when
                        // the system Back button is pressed).
                .setCustomAnimations(
                        android.R.anim.slide_out_right,    android.R.anim.slide_out_right,
                        android.R.anim.slide_out_right,    android.R.anim.slide_out_right)

                        // Replace any fragments currently in the container view with a fragment
                        // representing the next page (indicated by the just-incremented currentPage
                        // variable).
                .replace(R.id.log_container, new ScientificFragment())

                        // Add this transaction to the back stack, allowing users to press Back
                        // to get to the front of the card.
                .addToBackStack(null)

                        // Commit the transaction.
                .commit();
    }


    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if(i==EditorInfo.IME_ACTION_DONE){
            //todo test she

            sendLogMessage(getMExpressionString().toString(), mTemp,true, String.valueOf(mTagHimself.getText()));

//
//
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(mTagHimself.getWindowToken(), 0);

//            getWindow().setSoftInputMode(
//                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
//            );
            mTagHimself.setText("");
            mTagHimself.clearFocus();

            hideKeyboard();
            //add label and star to current number on adapter
        }
        return false;
    }


}




//----------------------------------------------------------
class PortraitPagerAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragments;

    public PortraitPagerAdapter(FragmentManager fm ,List<Fragment> fragments ) {
        super(fm);
        this.fragments = fragments;


        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int index) {
        // TODO Auto-generated method stub



        return this.fragments.get(index);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return this.fragments.size();
    }

}

