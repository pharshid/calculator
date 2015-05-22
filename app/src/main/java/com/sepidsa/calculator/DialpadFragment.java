/**
 *
 */
package com.sepidsa.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;


/**
 * @author Ehsan
 *
 */
public class DialpadFragment extends android.support.v4.app.Fragment implements OnClickListener,  CompoundButton.OnCheckedChangeListener {
    private static final int DIALPAD_FONT_ROBOTO_THIN = 0;
    private static final int DIALPAD_FONT_ROBOTO_LIGHT = 1;
    private static final int DIALPAD_FONT_ROBOTO_REGULAR = 2;
    View mView;
    private Typeface defaultFont;

    private BroadcastReceiver mThemeChangedReciever;
    private BroadcastReceiver mClearButtonChangedReceiver;
    private BroadcastReceiver mDialPadTypeFaceChangedReceiver;


    private int[] idList;
    private Typeface mRobotoThin;
    private Typeface mRobotoLight;
    private Typeface mRobotoRegular;
    boolean mIsRetroOn = false ;
    private BroadcastReceiver mDialPadTypeFaceColorChangedReceiver;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsRetroOn = ((MainActivity)getActivity()).isRetroThemeSelected();
        if(mIsRetroOn) {
            mView = inflater.inflate(R.layout.fragment_dialpad_retro, container, false);
        }else {
            mView = inflater.inflate(R.layout.fragment_dialpad_flat, container, false);

        }
        // Set the listener for all the gray_buttons
        idList = getNumericButtonsID();

        defaultFont = ((MainActivity)getActivity()).get_Default_Dialpad_Button_typeface();

        //If no retro theme is selected apply flat theme colors to keys
        if(((MainActivity) getActivity()).isRetroThemeSelected() != true){
            redrawKeypad();
        }


        mThemeChangedReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent
                String message = intent.getStringExtra("message");
                switch (message) {
                    case "changeAccentColor":
                        redrawKeypad();
//                        setTextColorState(getOperatorButtonsID(), getThemeColorStateList());
//                        setBackgroundColorForOperators();
                        break;

                    case "changeKeypadFontColor":

                        setTextColorState(getNonAccentButtonsID(), getNonAccentColorStateList());
                        break;

                    case "changeDialpadFont":
                        int font = intent.getIntExtra("dialpad_typeFace", DIALPAD_FONT_ROBOTO_THIN);
                        defaultFont = ((MainActivity) getActivity()).change_DialPad_TypeFace(font);
                        for (int id : idList) {
                            View v = mView.findViewById(id);
                            if (v != null && (v instanceof Button)) {
                                ((Button) v).setTypeface(defaultFont);
                            }
                        }
                        break;

                }
            }

        };






        mClearButtonChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent
                String message = intent.getStringExtra("buttonValue");
                setClearButtonText(message);
            }
        };


        for(int id : idList) {
            View v = mView.findViewById(id);
            if (v != null) {
                v.setOnClickListener(this);
                if( v instanceof Button){
                    ((Button) v).setTypeface(defaultFont);
                }
            }
        }

        if(mView.findViewById(R.id.switch_deg_rad) != null) {
            ((Switch) (mView.findViewById(R.id.switch_deg_rad))).setChecked(((MainActivity) getActivity()).getAngleMode());
            ((Switch) (mView.findViewById(R.id.switch_deg_rad))).setOnCheckedChangeListener(this);
            mView.findViewById(R.id.switch_deg_rad).setOnClickListener(this);


        }
        (mView.findViewById(R.id.buttonClear)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("C");
                setClearButtonText(getResources().getString(R.string.clear));

                return true;
            }
        });

        (mView.findViewById(R.id.buttonEquals)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("MR");

                return true;
            }
        });

        (mView.findViewById(R.id.buttonTimes)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("MC");

                return true;
            }
        });


        (mView.findViewById(R.id.buttonMinus)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("M-");
                setClearButtonText(getResources().getString(R.string.clear));

                return true;
            }
        });
        (mView.findViewById(R.id.buttonPlus)).setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {

                ((MainActivity)getActivity()).aButtonIsPressed("M+");

                return true;
            }
        });



        if(((MainActivity)getActivity()).stackSize() <= 1){
            setClearButtonText(getResources().getString(R.string.clear));
        }else {
            setClearButtonText(getResources().getString(R.string.backSpace));

        }
        return mView;
    }



    Typeface prepare_Default_typeface() {
        mRobotoLight = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "roboto_light.ttf");
        mRobotoRegular = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "roboto_regular.ttf");
        mRobotoThin = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "roboto_thin.ttf");
        if(((MainActivity) getActivity()).getKeypadBackgroundColorCode() == 2){
            mIsRetroOn = true;
        }

        //if retro theme is selected we only need a default (roboto regular) font
        if(mIsRetroOn){
            return mRobotoLight;
        }
        //else chose from users previous preference (Thin - Light - Regular)
        else {
//            redrawKeypad();
            SharedPreferences appPreferences = getActivity().getSharedPreferences("typography", Context.MODE_PRIVATE);
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

    @Override
    public void onStart() {
        super.onStart();


        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mThemeChangedReciever, new IntentFilter("themeIntent"));
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mClearButtonChangedReceiver, new IntentFilter("clearIntent"));
    }


    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.switch_deg_rad:

                boolean on = ((Switch) view).isChecked();

                if (on) {
                    //result in Degree
                    ((MainActivity)getActivity()).  setAngleMode(true);
                } else {
                    //result in Radian
                    ((MainActivity)getActivity()). setAngleMode(false);

                }
                break;



            default:
                if(view.getTag().toString().equals( "trigonomic")){
                    ((MainActivity) getActivity()).aButtonIsPressed(((Button)view).getText().toString()+"(");

                }else {
                    ((MainActivity) getActivity()).aButtonIsPressed(((Button)view).getText().toString());
                }        }

    }



    int[] getNumericButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{R.id.button9, R.id.button8, R.id.button7,
                R.id.button6, R.id.button5, R.id.button4, R.id.button1, R.id.button2, R.id.button3, R.id.button0,
                R.id.buttonPlus, R.id.buttonMinus, R.id.buttonDevide, R.id.buttonTimes, R.id.buttonEquals, R.id.buttonClear, R.id.buttonPoint
                , R.id.openParen, R.id.closeParen, R.id.buttonPercent,
                R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog,  R.id.buttonpie, R.id.buttone, R.id.buttonpower,
                R.id.buttonrad , R.id.buttonCot  , R.id.switch_deg_rad


        };
        return buttonsPointerArray;

    }
    int[] getOperatorButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{
                R.id.buttonPlus, R.id.buttonMinus, R.id.buttonDevide, R.id.buttonTimes
                , R.id.openParen, R.id.closeParen, R.id.buttonPercent
        };
        return buttonsPointerArray;

    }

    int[] getNonAccentButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{R.id.button9, R.id.button8, R.id.button7,
                R.id.button6, R.id.button5, R.id.button4, R.id.button1, R.id.button2, R.id.button3, R.id.button0,
                R.id.buttonPoint,

                R.id.buttonSinus, R.id.buttonCosinus, R.id.buttonTan, R.id.buttonln, R.id.buttonlog, R.id.buttonlog,  R.id.buttonpie, R.id.buttone, R.id.buttonpower,
                R.id.buttonrad , R.id.buttonCot


        };
        return buttonsPointerArray;

    }


    void redrawKeypad(){

        setTextColorState(getOperatorButtonsID(), getThemeColorStateList());
        setTextColorState(getNonAccentButtonsID(), getNonAccentColorStateList());
        setBackgroundColorForOperators();
    }

    private ColorStateList getThemeColorStateList() {
        int[][] states = getStateAray();
        int[] colors = new int[] {
                Color.WHITE,
                ((MainActivity) getActivity()).getAccentColorCode()
        };

        return new ColorStateList(states, colors);
    }

    private ColorStateList getNonAccentColorStateList() {
        int[][] states = getStateAray();
        int[] colors;
        if(((MainActivity) getActivity()).getKeypadFontColor() == Color.LTGRAY) {
           colors = new int[]{
                   ((MainActivity) getActivity()).getAccentColorCode(),Color.LTGRAY,

           };
        }else{
             colors = new int[]{
                     ((MainActivity) getActivity()).getAccentColorCode(),Color.parseColor("#BDBDBD")

            };
        }
        return new ColorStateList(states, colors);
    }

    int[][] getStateAray (){
        return new int[][] {

                new int[] { android.R.attr.state_pressed} , // pressed
                new int[] { -android.R.attr.state_pressed}  // pressed
        };

    }

    private void setTextColorState(int[] buttonsIdArray, ColorStateList textColor) {
        for(int buttonId:buttonsIdArray){
            if((Button) mView.findViewById(buttonId)!= null)
            ((Button) mView.findViewById(buttonId)).setTextColor(textColor);
        }
    }

    private void setBackgroundColorForOperators() {

        mView.findViewById(R.id.buttonTimes).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.buttonDevide).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.buttonPlus).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.buttonMinus).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.openParen).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.closeParen).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);
        mView.findViewById(R.id.buttonPercent).getBackground().setColorFilter(((MainActivity) getActivity()).getAccentColorCode(), PorterDuff.Mode.SRC_ATOP);

    }

    void setClearButtonText(String input){

        if(input.equals("\u2190"))
        {
            //TODO
            //Change Clear Button's Text to Backspace
            ((Button) mView.findViewById(R.id.buttonClear)).setText("\u2190");
            mView.findViewById(R.id.buttonClear).setTag("\u2190");
        }
        if(input.equals("C"))
        {
            ((Button) mView.findViewById(R.id.buttonClear)).setTypeface(defaultFont);
            ((Button) mView.findViewById(R.id.buttonClear)).setText("C");
            mView.findViewById(R.id.buttonClear).setTag("C");

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mThemeChangedReciever);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mClearButtonChangedReceiver);
        super.onPause();
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean on = ((Switch) buttonView).isChecked();


        if (on) {
            //result in Radian
            ((MainActivity)getActivity()). setAngleMode(true);
        } else {
            //result in Degress
            ((MainActivity)getActivity()). setAngleMode(false);

        }
    }
}
