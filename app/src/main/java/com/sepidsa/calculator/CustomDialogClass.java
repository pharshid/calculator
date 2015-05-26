package com.sepidsa.calculator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class CustomDialogClass extends Dialog implements View.OnClickListener {

    private final Typeface mRobotoLight;
    private final Typeface mRobotoRegular;
    private final Typeface mshekari;
    public Button sendFeedBack;
    Context mContext;
    Typeface  mRobotoThin ;
    Typeface mDastnevis ;
    Typeface mMitra ;
    private EditText feedbackEditText;
    private Button dialpad_textSize_Button ;
    private static final int DIALPAD_FONT_ROBOTO_THIN = 0;
    private static final int DIALPAD_FONT_ROBOTO_LIGHT = 1;
    private static final int DIALPAD_FONT_ROBOTO_REGULAR = 2;
    private static final int PERSIAN_TRANSLATION_FONT_MITRA = 5;
    private static final int PERSIAN_TRANSLATION_FONT_DASTNEVIS = 6;
    private AutoResizeTextView fortyTwoSample, fortyTwoSampleTranslation;


    /**
     * Create a Dialog window that uses a custom dialog style.
     *
     * @param context The Context in which the Dialog should run. In particular, it
     *                uses the window manager and theme from this context to
     *                present its UI.
     * @param theme   A style resource describing the theme to use for the
     *                window. See <a href="{@docRoot}guide/topics/resources/available-resources.html#stylesandthemes">Style
     *                and Theme Resources</a> for more information about defining and using
     *                styles.  This theme is applied on top of the current theme in
     */
    public CustomDialogClass(Context context, int theme) {
        super(context, theme);
        mRobotoThin = Typeface.createFromAsset(context.getAssets(), "roboto_thin.ttf");
        mRobotoLight = Typeface.createFromAsset(context.getAssets(), "roboto_light.ttf");
        mRobotoRegular =  Typeface.createFromAsset(context.getAssets(), "roboto_regular.ttf");
        mDastnevis = Typeface.createFromAsset(context.getAssets(), "dastnevis.otf");
        mMitra = Typeface.createFromAsset(context.getAssets(), "mitra.ttf");
        mshekari = Typeface.createFromAsset(context.getAssets(), "shekari.ttf");
        mContext = context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
        Button contact_us_Button =  (Button) findViewById(R.id.btn_CHANGE_TRANSLATION_FONT);
        contact_us_Button.setOnClickListener(this);
        contact_us_Button.setTypeface(mDastnevis);

        dialpad_textSize_Button =  (Button) findViewById(R.id.dialpad_text_size);
        dialpad_textSize_Button.setTypeface(mDastnevis);
        dialpad_textSize_Button.setOnClickListener(this);

        Button give_Stars = (Button) findViewById(R.id.give_stars) ;
        give_Stars.setOnClickListener(this);
        give_Stars.setTypeface(mDastnevis);

        fortyTwoSample = (AutoResizeTextView) findViewById(R.id.fortyTwoSample) ;
        fortyTwoSample.setOnClickListener(this);
        fortyTwoSampleTranslation = (AutoResizeTextView) findViewById(R.id.fortyTwoSampleTranslation) ;
        fortyTwoSampleTranslation.setTypeface(getDefaultPersianTranslationFont());
        set_dialpad_textSize_Button_typeface();
        set_persian_translation_Button_typeface();
        fortyTwoSampleTranslation.setTypeface(getDefaultPersianTranslationFont());
        Button tips = (Button) findViewById(R.id.tips) ;
        tips.setOnClickListener(this);
        tips.setTypeface(mDastnevis);



    }

    private void set_dialpad_textSize_Button_typeface() {
        SharedPreferences appPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
        switch (appPreferences.getInt("DIALPAD_FONT",DIALPAD_FONT_ROBOTO_THIN)) {
            case 0 :
                fortyTwoSample.setTypeface(mRobotoThin);
                break;

            case 1 :
                fortyTwoSample.setTypeface(mRobotoLight);
                break;
            case 2 :
                fortyTwoSample.setTypeface(mRobotoRegular);
                break;
        }
    }

    private void set_persian_translation_Button_typeface() {
        SharedPreferences appPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
        switch (appPreferences.getInt("PERSIAN_FONT_PREFERENCE",PERSIAN_TRANSLATION_FONT_MITRA)) {
            case 0 :
                fortyTwoSampleTranslation.setTypeface(mMitra);
                break;
            case 1 :
                fortyTwoSampleTranslation.setTypeface(mDastnevis);
                break;
            default:
                fortyTwoSampleTranslation.setTypeface(mMitra);
                break;
        }

    }


    // Send an Intent with an action named "my-event".
    void sendChangeDialpadTypefaceMessage(int font) {
        Intent intent = new Intent("themeIntent");
       ((MainActivity) mContext).setFontForComponent("DIALPAD_FONT",font);

        // add data
        intent.putExtra("message", "changeDialpadFont");
        //   intent.putExtra("detail",getMTranslationEditText().toString());
        LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fortyTwoSample:
            case R.id.give_stars:

                Intent giveStarsIntent = new Intent(
                        Intent.ACTION_EDIT,
                        Uri.parse("http://cafebazaar.ir/app/com.sepidsa.calculator/?l=fa"));
                mContext.startActivity(giveStarsIntent);
                dismiss();
                break;

            case R.id.tips:


                AlertDialog dialog = new AlertDialog.Builder(mContext).setIcon(android.R.drawable.ic_dialog_alert).setTitle("").setMessage("- جهت پاک کردن کل عبارت دکمه C  را نگه دارید\n" +
                                "- جهت پاک کردن خلاصه محاسبات در ابتدای لیست انگشت تون رو به پایین بکشید\n" +

                                "- درصد اینطوری کار می کنه\n" +
                                "50%200  = 100\n" +
                                "50% * 200  = 100\n" +
                                "200 * 50%  = 100\n\n" +

                                "100 + 20% = 120\n" +
                                "100 - 20% = 80\n" +
                                "100 ÷ 20% = 500" + "\n\n" +

                                " حافظه ماشین حساب یه چیز خیلی بدرد بخوره که به خاطر پیچیدگیش معمولا ازش کمتر استفاده می شه" +
                                "\nما سعی کردیم کار با حافظه رو آسون تر کنیم" + "\n\n" +
                                " فرض کن قراره هزینه خرید ۴ تا لامپ ۲۰ تومنی و ۵ کیلو گوجه ۷ تومنی رو حساب کنی" + "\n" +
                                " حافظه در ابتدا صفر هست  " + "\n" +
                                "اول ۴ ضربدر ۲۰ رو می نویسی و دکمه جمع رو نگه می داری. این مقدار به حافظه اضافه شد" + "\n" +
                                "حالا ۵ رو ضربدر ۷ کن و باز دوباره دکمه ی جمع رو نگه دار" + "\n" +
                                "محاسبه تمام شد.اگر بخوای مقدار حافظه رو واضح ببینی یا باهاش کار کنی دکمه مساوی رو نگه می داری" + "\n" +
                                "برای  خالی کردن حافظه دکمه ضرب رو نگه دار تا مقدارش صفر شه" + "\n" +
                                "ساده بود نه ؟"

                ).setPositiveButton(
                        "باشه", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                TextView textView = (TextView) dialog.findViewById(android.R.id.message);
                textView.setTypeface(mshekari);


                Button button1 = (Button) dialog.findViewById(android.R.id.button1);
                button1.setTypeface(mshekari);
                break;

            case R.id.dialpad_text_size:
                SharedPreferences fontSizePreference = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
                SharedPreferences.Editor fontSizeeditor = fontSizePreference.edit();
                switch (fontSizePreference.getInt("DIALPAD_FONT", DIALPAD_FONT_ROBOTO_THIN)) {
                    case 0:
                        fontSizeeditor.putInt("DIALPAD_FONT", DIALPAD_FONT_ROBOTO_THIN);
                        fontSizeeditor.apply();
                        fortyTwoSample.setTypeface(mRobotoThin);
                        sendChangeDialpadTypefaceMessage(0);
                        break;

                    case 1:
                        fortyTwoSample.setTypeface(mRobotoLight);
                        fontSizeeditor.putInt("DIALPAD_FONT", DIALPAD_FONT_ROBOTO_LIGHT);
                        fontSizeeditor.apply();
                        sendChangeDialpadTypefaceMessage(1);
                        break;

                    case 2:
                        fortyTwoSample.setTypeface(mRobotoRegular);
                        fontSizeeditor.putInt("DIALPAD_FONT", DIALPAD_FONT_ROBOTO_REGULAR);
                        fontSizeeditor.apply();
                        sendChangeDialpadTypefaceMessage(2);
                        break;



                }

                break;


            case R.id.btn_CHANGE_TRANSLATION_FONT:

                SharedPreferences typographyPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
                SharedPreferences.Editor typographyeditor = typographyPreferences.edit();
                switch (typographyPreferences.getInt("PERSIAN_FONT_PREFERENCE", PERSIAN_TRANSLATION_FONT_MITRA)) {
                    case 5:
                        fortyTwoSampleTranslation.setTypeface(mDastnevis);
                        typographyeditor.putInt("PERSIAN_FONT_PREFERENCE",PERSIAN_TRANSLATION_FONT_DASTNEVIS );
                        typographyeditor.apply();
                        if(!((MainActivity) mContext).isRetroThemeSelected()){
                            ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT",PERSIAN_TRANSLATION_FONT_DASTNEVIS);
                        }
                        break;

                    case 6:
                        fortyTwoSampleTranslation.setTypeface(mMitra);
                        typographyeditor.putInt("PERSIAN_FONT_PREFERENCE",PERSIAN_TRANSLATION_FONT_MITRA );
                        typographyeditor.apply();
                        if(!((MainActivity) mContext).isRetroThemeSelected()){
                            ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT",PERSIAN_TRANSLATION_FONT_MITRA);
                        }
                        break;

                    default:
                        fortyTwoSampleTranslation.setTypeface(mDastnevis);
                        typographyeditor.putInt("PERSIAN_FONT_PREFERENCE", PERSIAN_TRANSLATION_FONT_DASTNEVIS);
                        typographyeditor.apply();
                        if(!((MainActivity) mContext).isRetroThemeSelected()){
                            ((MainActivity) mContext).setFontForComponent("TRANSLATION_LITERAL_FONT",PERSIAN_TRANSLATION_FONT_DASTNEVIS);
                        }
                        break;
                }
//                set_persian_translation_Button_typeface();
                ((MainActivity) mContext).refreshFonts();


                // todo send email to info@sepidsa.com
//                Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//                emailIntent.setType("message/rfc822");
////                emailIntent.setData(Uri.parse("mailto:" + "recipient@example.com"));
//                String aEmailList[] = { "settings@sepidsa.com"};
//
//                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
//                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "راجع به ۴۲");
//                try {
//                    mContext.startActivity(Intent.createChooser(emailIntent, "Choose your Email app"));
//                } catch (android.content.ActivityNotFoundException ex) {
//                    Toast.makeText(mContext, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//                }
                break;

        }
    }


        Typeface getDefaultPersianTranslationFont(){


            int PersianTranslationFont = ((MainActivity) mContext).getPersianFontPreference();
            switch (PersianTranslationFont){
                case PERSIAN_TRANSLATION_FONT_MITRA:
                    return Typeface.createFromAsset(mContext.getAssets(), "mitra.ttf");

                case PERSIAN_TRANSLATION_FONT_DASTNEVIS:
                    return Typeface.createFromAsset(mContext.getAssets(), "dastnevis.otf");
                default:
                    return Typeface.createFromAsset(mContext.getAssets(), "mitra.ttf");

            }


        }
}


