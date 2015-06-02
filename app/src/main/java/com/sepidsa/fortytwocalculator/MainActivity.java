package com.sepidsa.fortytwocalculator;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.sepidsa.fortytwocalculator.data.LogContract;
import com.sepidsa.fortytwocalculator.util.IabHelper;
import com.sepidsa.fortytwocalculator.util.IabResult;
import com.sepidsa.fortytwocalculator.util.Inventory;
import com.sepidsa.fortytwocalculator.util.Purchase;
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



public class MainActivity extends FragmentActivity implements View.OnClickListener,  CompoundButton.OnCheckedChangeListener, IabHelper.OnIabSetupFinishedListener , IabHelper.OnIabPurchaseFinishedListener, IabHelper.QueryInventoryFinishedListener{

    private static final String FRAGMENT_TAG_LOG_ = "log fragment";
    private static final byte LANGUAGE_ARABIC =0 ;

    private static final String LOG_DATA_KEY = "log data";
    private static final int FONT_DIGITAL_7 = 3;
    private static final int FONT_YEKAN = 4;
    private static final int FONT_MAJALLA = 7;
    private long mLatestInsertedId;
    Log_Adapter mLogAdapter;
    Serializable mListView ;
    //TextSwitcher mResultTextSwitcher = null;
    //the reason it's an editText and not a TextView is solely for supporting the scrolling function
    private AutoResizeTextView mTranslationBox;

    Button mButton ;

    ViewPager mViewPager;
    private static final byte TOTAL_PAGE_COUNT = 3;
    private byte mDefaultPage;

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

    int mSelectedColorCal0 = 0;

    private static final int FONT_ROBOTO_THIN = 0;
    private static final int FONT_ROBOTO_LIGHT = 1;
    private static final int FONT_ROBOTO_REGULAR = 2;
    private static final int FONT_MITRA = 5;
    private static final int FONT_DASTNEVIS = 6;
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
    Typeface mTranslationBoxNumericFont = null;
    //    this typeface will be used for translation box
    private Typeface mTranslationBoxLetterFont = null;
    // Languages supported for "number to word" translation . will use theme Later for Localization
    static final byte LANGUAGE_PERSIAN = 3
            , LANGUAGE_ENGLISH = 1
            , LANGUAGE_FRENCH = 2;
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
    private Typeface mFlatIcon;
    private String mDecimal_fraction = "";
    private TextView mScientificModeTextView;
    private Typeface mshekari;
    private Typeface mMajalla;
    private Typeface mMitra;
    private Typeface mDastnevis;
    //    PopoverView popoverView;
    Button mFavoritesList;
    private boolean mShowingBack = false;
//    EditText mTagHimself;




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

    // SKUs for our products: the premium upgrade (non-consumable)
    static final String SKU_PREMIUM = "golden_upgrade";
    static final String SKU_CLASSIC_THEME = "classic_theme_upgrade";

    // Does the user have the premium upgrade?
    boolean mIsPremium = false;
    boolean mHasPuyrchasedClassicTheme = false;

    // (arbitrary) request code for the purchase flow
    static final int RC_REQUEST = 2;
    static final String TAG = "";



    // The helper object
    com.sepidsa.fortytwocalculator.util.IabHelper mHelper;
    IabHelper.QueryInventoryFinishedListener
            mGotInventoryListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        TinyDB db = new TinyDB(getApplicationContext());

//        String base64EncodedPublicKey = getTheCannoli();
//        // You can find it in your Bazaar console, in the Dealers section.
//        // It is recommended to add more security than just pasting it in your source code;
//        mHelper = new IabHelper(this, base64EncodedPublicKey);
//        //setting up inab helper
//        Log.d(TAG, "Starting setup.");
//        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
//            public void onIabSetupFinished(IabResult result) {
//                Log.d(TAG, "Setup finished.");
//
//                if (!result.isSuccess()) {
//                    // Oh noes, there was a problem.
//                    Log.d(TAG, "Problem setting up In-app Billing: " + result);
//                }
//                // Hooray, IAB is fully set up!
//                mHelper.queryInventoryAsync(getInventoryFinishedListener());
//            }
//        });
//
//        getListofAvaiablePurchases();
        mListView = db.getListString(LOG_DATA_KEY);
        setTypeFaces();

        if(isRetroThemeSelected()){
            setContentView(R.layout.activity_main_retro);
        }else {
            setContentView(R.layout.activity_main);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {

            mJustPressedExecuteButton = true;
        }
        setMViewPager(fragmentManager);
        setMViewPagerIndicator();

        mMemoryVariableTextView =  (TextView) findViewById(R.id.m_vaiable_textview);
        mTranslationBox = (AutoResizeTextView) findViewById(R.id.translationEditText);
        resultTextView = (AutoResizeTextView) findViewById(R.id.result);
        mScientificModeTextView = (TextView) findViewById(R.id.scientific_mode_textview);
        result_textView_holder = findViewById(R.id.MotherTop);
        mFavoritesList = (Button)findViewById(R.id.favorites_list);
        mAddToFavorites = (Button)findViewById(R.id.constant_selected);
        mAddLabel = (Button)findViewById(R.id.add_label);
        mFavoritesList.setOnClickListener(this);
        mAddToFavorites.setOnClickListener(this);
        mAddLabel.setOnClickListener(this);


//        If it's a new instance of application i.e. Not because of rotation or configuration changes =================
        prepareBottomIcons();



        if(!watchedIntro()){



//            AlertDialog.Builder adb = new AlertDialog.Builder(this);
//            Dialog d = adb.setView(new View(this)).create();
//            // (That new View is just there to have something inside the dialog that can grow big enough to cover the whole screen.)
//
//            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
//            lp.copyFrom(d.getWindow().getAttributes());
//            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//            lp.height = WindowManager.LayoutParams.MATCH_PARENT;
//            d.show();
//            d.getWindow().setAttributes(lp);


//            String mytext = Html.fromHtml("<h2>Title</h2><br><p>Description here</p>");


//            WhatsNewDialogClass cdc = new WhatsNewDialogClass(this , android.R.style.Theme_Holo_Light_Dialog_MinWidth);
//            cdc.show();

//
//            AlertDialog dialog =   new AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("در نسخه جدید ").setMessage(
//
//
//            getResources().getString(R.string.str1)
//
//            ) .setPositiveButton(
//                    "باشه", new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                            setWatcheIntroPreference(true);
//                            dialog.dismiss();
//                        }
//                    }).create();
//            DisplayMetrics displaymetrics = new DisplayMetrics();
//            getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
//            float height = displaymetrics.density;
//            float width = displaymetrics.density;
//
//
//            dialog.getWindow().setLayout((int)width, (int)height/2);
//            dialog.show();
//            TextView textView = (TextView) dialog.findViewById(android.R.id.message);
//            textView.setTypeface(mMitra);
//
//
//            Button button1 = (Button) dialog.findViewById(android.R.id.button1);
//            button1.setTypeface(mMitra);

        }
        refreshFonts();
        setIconButtons();

    }

    private String getTheCannoli() {
        return null;
    }

    private void getListofAvaiablePurchases() {

        List additionalSkuList = new ArrayList();
        additionalSkuList.add(SKU_PREMIUM);
        additionalSkuList.add(SKU_CLASSIC_THEME);
        mHelper.queryInventoryAsync(true, additionalSkuList,
                this);
    // returning the result to query finished listner (this)

    }


    com.sepidsa.fortytwocalculator.util.IabHelper.QueryInventoryFinishedListener getInventoryFinishedListener(){
        mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
            public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
                Log.d(TAG, "Query inventory finished.");
                if (result.isFailure()) {
                    Log.d(TAG, "Failed to query inventory: " + result);
                    return;
                }
                else {
                    Log.d(TAG, "Query inventory was successful.");
                    // does the user have the premium upgrade?
                    mIsPremium = inventory.hasPurchase(SKU_PREMIUM);

                    // update UI accordingly

                    Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
                }

                Log.d(TAG, "Initial inventory query finished; enabling main UI.");
            }
        };
        return mGotInventoryListener;
    }


    void purchaseContent(String SKU){



        IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener
                = new IabHelper.OnIabPurchaseFinishedListener() {
            public void onIabPurchaseFinished(IabResult result, Purchase purchase)
            {
                if (result.isFailure()) {
                    Log.d(TAG, "Error purchasing: " + result);
                    return;
                }
                else if (purchase.getSku().equals(SKU_PREMIUM)) {
                    // consume the gas and update the UI
                }
                else if (purchase.getSku().equals(SKU_CLASSIC_THEME)) {
                    // give user access to premium content and update the UI

                    // update UI
                }
            }
        };
        mHelper.launchPurchaseFlow(this, SKU, 10001,
                mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
    }

    void checkPurchases() {
        mHelper.queryInventoryAsync(mGotInventoryListener);
        // todo update ui

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
        outState.putBoolean("mISRetroThemeOn", isRetroThemeSelected());
        outState.putSerializable("mMemoryVariable", mMemoryVariable);
        outState.putString("mTemp",mTemp);
        outState.putString("mDecimal_fraction", mDecimal_fraction);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        // super.onRestoreInstanceState(savedInstanceState);
        // todo mtranslationbox restore

        if(savedInstanceState!= null) {
            mTranslationBox = getMTranslationEditText();
            mTranslationBox.setText(savedInstanceState.getString("mTranslationText"));
            mJustPressedExecuteButton = savedInstanceState.getBoolean("mJustPressedExecuteButton");
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
                mTranslationBox.setTypeface(mTranslationBoxNumericFont);
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
                getMTranslationEditText().setTypeface(mTranslationBoxLetterFont);
                break;

            default:
                getMTranslationEditText().setTypeface(getFontForComponent("RESULT_FONT"));
        }

        if(!isRetroThemeSelected()) {
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
            mTranslationBox.setTypeface(mTranslationBoxNumericFont);
        }
        redrawAccent();
        redrawKeypadBackground();



        super.onResume();
    }

    private void prepareBottomIcons() {

        ( (Button)findViewById(R.id.buttonMute)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonMute)).setTextColor(Color.LTGRAY);
        findViewById(R.id.buttonMute).setOnClickListener(this);
        if(getVolumeFromPreference()) {
            ((Button) findViewById(R.id.buttonMute)).setText(getResources().getText(R.string.volume_high));
        }else {
            ( (Button)findViewById(R.id.buttonMute)).setText(getResources().getText(R.string.volume_off));
        }

        ( (Button)findViewById(R.id.buttonSettings)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonSettings)).setTextColor(Color.LTGRAY);
        ( findViewById(R.id.buttonSettings)).setOnClickListener(this);

        ( (Button)findViewById(R.id.buttonLanguage)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonLanguage)).setTextColor(Color.LTGRAY);
        ( findViewById(R.id.buttonLanguage)).setOnClickListener(this);
// todo show a settings dialog here

        ( (Button)findViewById(R.id.buttonColors)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonColors)).setTextColor(Color.LTGRAY);
        ( findViewById(R.id.buttonColors)).setOnClickListener(this);

        ( (Button)findViewById(R.id.buttonParallax)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonParallax)).setTextColor(Color.LTGRAY);
        ( findViewById(R.id.buttonParallax)).setOnClickListener(this);

        ( (Button)findViewById(R.id.buttonPurchase)).setTypeface(mFlatIcon);
        ( (Button)findViewById(R.id.buttonPurchase)).setTextColor(Color.LTGRAY);
        ( findViewById(R.id.buttonPurchase)).setOnClickListener(this);


    }

    private boolean getVolumeFromPreference(){
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("volumeState", Context.MODE_PRIVATE);
        return  appPreferences.getBoolean("hasVolume",true);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHelper != null) mHelper.dispose();
        mHelper = null;
        if (mHandler != null) { mHandler.removeCallbacks(mRunnable); }
    }

    @Override
    public void onBackPressed() {

        //TODO check if it works for tablet landscape
        // if we're in default page prompt for exit else switch to default page TODO set default page here

        if ((mLayoutState == LANDSCAPE_TABLET )||
                (mViewPager.getCurrentItem() == DIALPAD_FRAGMENT )) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "جهت خروج یه بار دیگه لطفا", Toast.LENGTH_SHORT).show();

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
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        if (mViewPager != null) {


            if (((String) mViewPager.getTag()).equals("portrait_phone") == true) {
                //getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT
                // if in Portrait Phone
//                mColorPage = 3;
                mViewPager.setAdapter(null);
                List<Fragment> fList = new ArrayList<Fragment>();
                fList.add(new AnimatedLogFragment());
                fList.add(new DialpadFragment());
                fList.add(new ScientificFragment());
                mViewPager.setAdapter(new PortraitPagerAdapter(fragmentManager, fList));
                mLayoutState = PORTRAIT_BOTH;
                mViewPager.setOffscreenPageLimit(2);

                // if todo signs viewed = false
                // {make em visible and
                //   if(!watchedIntro()) {
                //  setIntroButtonsVisibility(true);
                mViewPager.setCurrentItem(DIALPAD_FRAGMENT);
                setmDefaultPage(DIALPAD_FRAGMENT);
                // mViewPager.setOnPageChangeListener(this);
            } else if (((String) mViewPager.getTag()).equals("landscape_phone") == true) {
                // if in Landscape Phone
                List<Fragment> fList = new ArrayList<Fragment>();
                fList.add(new AnimatedLogFragment());
                fList.add(new DialpadFragment());
//                mColorPage = 2;
                mViewPager.setAdapter(new PortraitPagerAdapter(fragmentManager, fList));
                mLayoutState = LANDSCAPE_PHONE;
                mViewPager.setOffscreenPageLimit(2);
                mViewPager.setCurrentItem(DIALPAD_FRAGMENT);
                setmDefaultPage(DIALPAD_FRAGMENT);


            }
        }
        else {

            mLayoutState = LANDSCAPE_TABLET;
        }

    }


    private void setTypeFaces() {

        mRobotoLight = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_light.ttf");
        mRobotoRegular = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_regular.ttf");
        mRobotoThin = Typeface.createFromAsset(getApplicationContext().getAssets(), "roboto_thin.ttf");

        mMajalla = Typeface.createFromAsset(getApplicationContext().getAssets(), "majalla.ttf");
        mFlatIcon = Typeface.createFromAsset(getApplicationContext().getAssets(), "flaticon.ttf");
        mshekari = Typeface.createFromAsset(getApplicationContext().getAssets(), "shekari.ttf");
        mDastnevis = Typeface.createFromAsset(getApplicationContext().getAssets(), "dastnevis.otf");
        mMitra = Typeface.createFromAsset(getApplicationContext().getAssets(), "mitra.ttf");
        mPhalls =  Typeface.createFromAsset(getApplicationContext().getAssets(), "yekan.ttf");
        mDigital_7 =  Typeface.createFromAsset(getApplicationContext().getAssets(), "digital_7.ttf");


    }

    private void setIconButtons() {
        //todo refactor code
        mFavoritesList.setTypeface(mFlatIcon);
        mFavoritesList.setText(getResources().getString(R.string.list));
        mFavoritesList.setTextSize(30);
        mFavoritesList.setTextColor(getAccentColorCode());

        mAddLabel.setTypeface(mFlatIcon);
        mAddToFavorites.setTypeface(mFlatIcon);
    }

    private void setResultTextBox(){

        if(!isRetroThemeSelected()) {
            resultTextView.setBackgroundColor(getAccentColorCode());
            result_textView_holder.setBackgroundColor(getAccentColorCode());
            resultTextView.setTextColor(Color.WHITE);
        }else {
            resultTextView.setBackgroundColor(Color.BLACK);
            resultTextView.setTextColor(Color.BLACK);

        }
        resultTextView.setLines(2);
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
        int  currentThemeNumber = -1;
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

        if(!isRetroThemeSelected()) {
            sendChangeAccentColorIntent();
            setMViewPagerIndicatorColor();
            resultTextView.setBackgroundColor(getAccentColorCode());
            result_textView_holder.setBackgroundColor(getAccentColorCode());
            mFavoritesList.setTextColor(getAccentColorCode());
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
    public int getDialpadFontColor() {
        if (getKeypadBackgroundColorCode() != Color.WHITE){
//            return  Color.LTGRAY;
            return  Color.parseColor("#757575");
        }else {
            return  Color.LTGRAY;
//            return  Color.parseColor("#757575");
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
            mTranslationBox.setTextColor(getDialpadFontColor());
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
        intent.putExtra("fontColor", getDialpadFontColor());
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

    public void setMViewPagerIndicatorColor(){
        if(mViewPagerIndicator!= null)
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

        if  (mViewPager!=null && mViewPager.getCurrentItem() != this.mDefaultPage) {
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
        if  ( mJustPressedExecuteButton)
            if ( Character.isDigit(currentButtonValue.charAt(0))
                    || currentButtonValue.equals(getResources().getString(R.string.Pi))
                    || currentButtonValue.equals(getResources().getString(R.string.e))) {
                setMExpressionString("");
                mButtonsStack.clear();
            }

        mJustPressedExecuteButton = false;
        shouldPrevent = preventCommonErrors(currentButtonValue.charAt(0));
        if(shouldPrevent) {
            playSound(errorSoundID);
            mTranslationBox.startAnimation(mErrorBlink);
            return;
        }


        if( currentButtonValue.equals("=") ) {
            if(getMExpressionString().length() > 0){
                if (calculateResult(null) == 0) {
                    mJustPressedExecuteButton = true;
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

        Toast.makeText(getApplicationContext(), "M-",Toast.LENGTH_SHORT).show();
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
        Toast.makeText(getApplicationContext(), "MR",Toast.LENGTH_SHORT).show();

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



    private void setVolumeInPreference(boolean hasVolume) {
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


    private boolean updateUIExecute(boolean sendLogMessage) {

        mDecimal_fraction = "";
        byte decimalIndex = (byte) mTemp.indexOf(".");
        if(decimalIndex != -1){
            mDecimal_fraction = mTemp.substring(mTemp.indexOf(".")+1);
        }

        try {

            resultTextView.setText(mTemp);
            displayTranslation();
            sendLogMessage(getMExpressionString().toString(), mTemp, false, "");
            mAddToFavorites.setAlpha(1);
            mAddToFavorites.setRotation(0);
            mAddToFavorites.setTextColor(Color.BLACK);
            mAddToFavorites.setScaleX(1);
            mAddToFavorites.setScaleY(1);
            mAddToFavorites.setTranslationY(0);

            mAddToFavorites.setVisibility(View.VISIBLE);


            mAddLabel.setVisibility(View.VISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            playSound(errorSoundID);
            return true;
        }

        if(sendLogMessage) {
            mLogAdded = false;
//            sendLogMessage(getMExpressionString().toString(), mTemp,false, String.valueOf(mTagHimself.getText()));
        }
        setMExpressionString(resultTextView.getText().toString().replace(",", ""));
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
            mTranslationBox.setTypeface(mTranslationBoxNumericFont);
            mTranslationBox.setText(gg);

            mAddToFavorites.setVisibility(View.GONE);
            mAddLabel.setVisibility(View.GONE);

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
            mJustPressedExecuteButton = false;
            //TODO decide the fate of clear button ?
            // Happens for generals exceptions

            return 2;
        }
        // returning zero means there's no problems what so ever
        return 0;
    }

    private void updateUIOperator() {
        String gg = getMExpressionString().toString().replaceAll("(?<!\\.\\d{0,6})\\d+?(?=(?:\\d{3})+(?:\\D|$))", "$0,");
        mTranslationBox.setTypeface(mTranslationBoxNumericFont);
        mTranslationBox.setText(gg);
        mAddToFavorites.setVisibility(View.GONE);
        mAddLabel.setVisibility(View.GONE);
        return;
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

    private void displayTranslation() {


        mTranslationBox.setTypeface(mTranslationBoxLetterFont);
        printResultWithTranslation(mDecimal_fraction, getTranslationLanguage());
        mTranslationBox.startAnimation(mBlink);
    }



    private void printResultWithTranslation(String decimal_fraction, int Language) {
        String resultWithoutCommas = mTemp.replace(",", "");
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


        switch (Language){
            case LANGUAGE_ENGLISH:
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
                break;

            case LANGUAGE_FRENCH:
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

                break;


            case LANGUAGE_ARABIC:
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
            hasError3 = fixDoublePoints(currentChar);
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

    }


    public void playSound(int id) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        float maxVolume = (float) audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float volume;


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
        if (isRetroThemeSelected()) {
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

    void prepareAnimationStuff() {
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
        mErrorBlink.setRepeatMode(Animation.REVERSE);
        mErrorBlink.setRepeatCount(4);

    }
    void prepareBlinkingAnimation(){

        mBlink = new AlphaAnimation(0.0f, 1.0f);
        mBlink.setDuration(100); //You can manage the time of the blink with this parameter
        mBlink.setRepeatMode(Animation.REVERSE);
        mBlink.setRepeatCount(0);

    }


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

        mLatestInsertedId = ContentUris.parseId(getContentResolver().insert(LogContract.LogEntry.CONTENT_URI,cv));

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


    void setFontForComponent(String key, int value) {

        SharedPreferences appPreferences = getSharedPreferences("typography", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
    }
    Typeface getFontForComponent(String key) {

        SharedPreferences appPreferences =getSharedPreferences("typography", Context.MODE_PRIVATE);
        int outputFont = 0 ;

        switch (key) {
            case "DIALPAD_FONT":
                outputFont = appPreferences.getInt(key, FONT_ROBOTO_THIN);
                break;
            case "SCIENTIFIC_FONT":
                outputFont = appPreferences.getInt(key, FONT_ROBOTO_LIGHT);
                break;
            case "RESULT_FONT":
                outputFont = appPreferences.getInt(key, FONT_ROBOTO_THIN);
                break;
            case "TRANSLATION_NUMERIC_FONT":
                outputFont = appPreferences.getInt(key, FONT_ROBOTO_THIN);
                break;
            case "TRANSLATION_LITERAL_FONT":
                outputFont = appPreferences.getInt(key, FONT_MITRA);
                break;

        }

        switch (outputFont) {
            case FONT_ROBOTO_THIN:
                return mRobotoThin;
            case FONT_ROBOTO_LIGHT:
                return mRobotoLight;
            case FONT_ROBOTO_REGULAR:
                return mRobotoRegular;
            case FONT_DIGITAL_7:
                return mDigital_7;
            case FONT_YEKAN:
                return mPhalls;
            case FONT_MITRA:
                return mMitra;
            case FONT_DASTNEVIS:
                return mDastnevis;
            case FONT_MAJALLA:
                return mMajalla;
        }

        return mRobotoThin;
    }


    public void setmDefaultPage(byte _defaultPage) {
        this.mDefaultPage = (byte) (_defaultPage);
    }


    public void switchTheme() {
//        SharedPreferences appPreferences = getSharedPreferences("typography", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = appPreferences.edit();

        //switching from modern theme to clasic theme
        if (isRetroThemeSelected()) {
            switch (getTranslationLanguage()) {
                case LANGUAGE_PERSIAN:
                case LANGUAGE_ARABIC:

                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_YEKAN);
                    break;
                default:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_DIGITAL_7);
            }

            setFontForComponent("TRANSLATION_NUMERIC_FONT", FONT_DIGITAL_7);
            setFontForComponent("RESULT_FONT", FONT_DIGITAL_7);
            setFontForComponent("DIALPAD_FONT", FONT_ROBOTO_LIGHT);

        } else {
            switch (getTranslationLanguage()) {
                case LANGUAGE_PERSIAN:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", getPersianFontPreference());
                    break;
                case LANGUAGE_ARABIC:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_MAJALLA);
                    break;
                default:
                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_ROBOTO_THIN);
            }
            setFontForComponent("TRANSLATION_NUMERIC_FONT", FONT_ROBOTO_THIN);
            setFontForComponent("RESULT_FONT", FONT_ROBOTO_THIN);
            setFontForComponent("DIALPAD_FONT", FONT_ROBOTO_THIN);
        }
        setFontForComponent("SCIENTIFIC_FONT", FONT_ROBOTO_LIGHT);

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

                FragmentManager fm = getSupportFragmentManager();
                FavoritesFragment favoritesDialog = new FavoritesFragment();
                favoritesDialog.show(fm, "fragment_favorites");
                break;

            case R.id.constant_selected: {


                String selection = LogContract.LogEntry._ID + "=?";
                Cursor cursor = getContentResolver().query(LogContract.LogEntry.CONTENT_URI, null, selection, new String[]{Long.toString(mLatestInsertedId)}, null);
                if (cursor.moveToFirst()) {
                    int currentStarredStatus = cursor.getInt(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_STARRED));
                    if (currentStarredStatus == 0) {
                        ContentValues values = new ContentValues();
                        values.put(LogContract.LogEntry.COLUMN_STARRED, 1);
                        getContentResolver().update(LogContract.LogEntry.CONTENT_URI, values, selection, new String[]{Long.toString(mLatestInsertedId)});
                    }
                }
                cursor.close();
                setClipView(mAddToFavorites, false);
                mAddToFavorites.setTextColor(Color.YELLOW);
                mAddToFavorites.animate()
                        .translationY(200)
                        .scaleX(0.5f)
                        .scaleY(0.5f)
                        .alpha(0)
                        .rotation(180)
                        .setDuration(1000);
            }

//                sendLogMessage(getMExpressionString().toString(), mTemp, true, String.valueOf(mTagHimself.getText()));
            break;

            case R.id.add_label: {
                final String  selection = LogContract.LogEntry._ID + "=?";
                Cursor cursor = getContentResolver().query(LogContract.LogEntry.CONTENT_URI, null, selection, new String[]{Long.toString(mLatestInsertedId)}, null);
                if (cursor.moveToFirst()) {
                    String currentLabel = cursor.getString(cursor.getColumnIndex(LogContract.LogEntry.COLUMN_TAG));
                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                    builder.setTitle("Enter a Label:");

                    // Set up the input

                    final EditText input = new EditText(this);
                    final InputMethodManager inputMethodManager = (InputMethodManager)  getSystemService(Activity.INPUT_METHOD_SERVICE);

                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);
                    builder.setCancelable(false);
                    input.setText(currentLabel);
                    input.requestFocus();
                    inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                    // Set up the buttons

                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String newLabel = input.getText().toString();
                            ContentValues values = new ContentValues();
                            values.put(LogContract.LogEntry.COLUMN_TAG, newLabel);
                            getContentResolver().update(
                                    LogContract.LogEntry.CONTENT_URI,
                                    values,
                                    selection,
                                    new String[]{Long.toString(mLatestInsertedId)}
                            );
                            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            inputMethodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            dialog.cancel();

                        }
                    });
                    final android.support.v7.app.AlertDialog dialog = builder.create();
                    dialog.show();
                    EditText.OnKeyListener keyListener = new EditText.OnKeyListener() {
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                                switch (keyCode) {
                                    case KeyEvent.KEYCODE_DPAD_CENTER:
                                    case KeyEvent.KEYCODE_ENTER:
                                        dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).performClick();
                                        return true;
                                    default:
                                        break;
                                }
                            }
                            return false;
                        }
                    };
                    input.setOnKeyListener(keyListener);

                }

                cursor.close();



            }

            break;





            case R.id.buttonSettings:
                CustomDialogClass cdc = new CustomDialogClass(this , android.R.style.Theme_Holo_Light_Dialog_MinWidth);
                cdc.show();
                break;

            case R.id.buttonLanguage:
                mButton = (Button)findViewById(R.id.buttonLanguage);
                showSpinner();
                break;

            case R.id.buttonParallax:
                Intent myIntent = new Intent(MainActivity.this, HelpActivity.class);
                MainActivity.this.startActivity(myIntent);
                break;



            case R.id.buttonPurchase:
                PurchaseDialog myDialog = new PurchaseDialog();
                myDialog.show(getSupportFragmentManager(),"PurchaseDialog");
//                myDialog.show(getFragmentManager(), "PurchaseDialog");
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

    public static void setClipView(View view, boolean clip) {
        if (view != null) {
            ViewParent parent = view.getParent();
            if(parent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view.getParent();
                viewGroup.setClipChildren(clip);
                viewGroup.setClipToPadding(clip);
                setClipView(viewGroup, clip);

            }
        }
    }




    private void showSpinner() {

        AlertDialog.Builder b = new AlertDialog.Builder(this);
// todo google play edition
        String[] options = {"Arabic","English","French","Persian"};
        b.setTitle("Language" );
        b.setSingleChoiceItems(options, -1,new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("LanguagePreference", MODE_PRIVATE);
                        SharedPreferences.Editor editor = appPreferences.edit();

                        dialog.dismiss();
                        switch (which) {

                            case 0: {
                                editor.putInt("LANGUAGE", LANGUAGE_ARABIC);
                                setFontForComponent("TRANSLATION_LITERAL_FONT",FONT_MAJALLA);
                                break;
                            }
                            case 1: {
                                editor.putInt("LANGUAGE", LANGUAGE_ENGLISH);
                                if(isRetroThemeSelected()){
                                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_DIGITAL_7);

                                }else {
                                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_ROBOTO_THIN);
                                }
                                break;
                            }

                            case 2: {
                                editor.putInt("LANGUAGE", LANGUAGE_FRENCH);
                                if(isRetroThemeSelected()){
                                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_DIGITAL_7);

                                }else {
                                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_ROBOTO_THIN);
                                }
                                break;
                            }
                            case 3: {
                                editor.putInt("LANGUAGE", LANGUAGE_PERSIAN);
                                if(isRetroThemeSelected()){
                                    setFontForComponent("TRANSLATION_LITERAL_FONT", FONT_YEKAN);
                                }else {
                                    setFontForComponent("TRANSLATION_LITERAL_FONT", getPersianFontPreference());
                                }
                                break;
                            }

                        }

                        editor.commit();
                        refreshFonts();
                        if (mDecimal_fraction != null) {
                            if (mJustPressedExecuteButton) {
                                displayTranslation();
                            } else {
                                mTranslationBox.startAnimation(mBlink);
                            }
                        }

                    }
                }
        );
        b.show();
    }



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

    public void refreshFonts() {

        resultTextView.setTypeface(getFontForComponent("RESULT_FONT"));
        mTranslationBoxLetterFont = getFontForComponent("TRANSLATION_LITERAL_FONT");
        mTranslationBoxNumericFont = getFontForComponent("TRANSLATION_NUMERIC_FONT");
        if(mJustPressedExecuteButton){
            mTranslationBox.setTypeface(mTranslationBoxLetterFont);
        }else{
            mTranslationBox.setTypeface(mTranslationBoxNumericFont);
        }
    }

    int getPersianFontPreference() {
        SharedPreferences appPreferences = getApplicationContext().getSharedPreferences("typography", MODE_PRIVATE);
        return appPreferences.getInt("PERSIAN_FONT_PREFERENCE",FONT_MITRA);
    }

    void buyIap(String sku){

        mHelper.launchPurchaseFlow(this, sku, RC_REQUEST, this, "payload-string");

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

    @Override
    public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
        if (result.isFailure()) {
            Log.d(TAG, "Error purchasing: " + result);
            return;
        }
        else if (purchase.getSku().equals(SKU_PREMIUM)) {
            // give user access to premium content and update the UI
        }
    }


    @Override
    public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
        Log.d(TAG, "Query inventory finished.");
        if (result.isFailure()) {
            Log.d(TAG, "Failed to query inventory: " + result);
            return;
        }
        else {
            Log.d(TAG, "Query inventory was successful.");
            // does the user have the premium upgrade?
            mIsPremium = inventory.hasPurchase(SKU_PREMIUM);

            // update UI accordingly

            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));
        }

        Log.d(TAG, "Initial inventory query finished; enabling main UI.");
    }

    @Override
    public void onIabSetupFinished(IabResult result) {

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

