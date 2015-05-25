package com.sepidsa.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;


/**
 * Created by Ehsan on 5/29/14.
 */
public class ScientificFragment extends Fragment implements OnClickListener,CompoundButton.OnCheckedChangeListener {
    private static final int DIALPAD_FONT_ROBOTO_THIN = 0 ;
    View mView;
    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private BroadcastReceiver mScientificTypeFaceChangedReceiver;
    private Typeface defaultFont;
    int idList[];
    private boolean inversed = false;
    private boolean arcIsOn = false;
    private boolean mIsRetroOn = false;


    public static final ScientificFragment newInstance(String message)
    {
        ScientificFragment f = new ScientificFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mIsRetroOn = ((MainActivity)getActivity()).isRetroThemeSelected();
        if(mIsRetroOn) {
            mView = inflater.inflate(R.layout.fragment_scientific_retro, container, false);
        }else {
            mView = inflater.inflate(R.layout.fragment_scientific_flat, container, false);

        }        // Set the listener for all the gray_buttons
//        this.setRetainInstance(false);
        idList =getScientificButtonsID();
        defaultFont = ((MainActivity)getActivity()).get_Default_Dialpad_Button_typeface();
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
            ((ToggleButton) (mView.findViewById(R.id.switch_deg_rad))).setChecked(((MainActivity) getActivity()).getAngleMode());
            ((ToggleButton) (mView.findViewById(R.id.switch_deg_rad))).setOnCheckedChangeListener(this);
            ((ToggleButton) (mView.findViewById(R.id.switch_deg_rad))).setOnClickListener(this);


        }
        mScientificTypeFaceChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent
                int message = intent.getIntExtra("dialpad_typeFace",DIALPAD_FONT_ROBOTO_THIN);
                defaultFont =     ((MainActivity)getActivity()).change_DialPad_TypeFace(message);
                for(int id : idList) {
                    View v = mView.findViewById(id);
                    if (v != null && (v instanceof Button)) {
                        ((Button) v).setTypeface(defaultFont);
                    }
                }
            }
        };

        return  mView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            //TODO

            case R.id.buttonInverse:
                if(   ((ToggleButton)view).isChecked() == true){
                    applyInverse(true);
                }else {
                    applyInverse(false);

                }

                break;
            case R.id.buttonARC:
                if(   ((ToggleButton)view).isChecked() == true){
                    arcIsOn = true;
                    applyArc(true);
                }else {
                    arcIsOn = false;
                    applyArc(false);

                }
                break;

            case R.id.switch_deg_rad:

                boolean on = ((ToggleButton) view).isChecked();

                if (on) {
                    //result shown  in Degress
                    ((MainActivity)getActivity()).  setAngleMode(true);
                } else {
                    //result shown in Radian
                    ((MainActivity)getActivity()). setAngleMode(false);

                }
                break;

            case R.id.buttonConstant: {

                FragmentManager fm = getActivity().getSupportFragmentManager();
                ConstantUseFragment constantUseDialog = new ConstantUseFragment();
                constantUseDialog.show(fm, "fragment_constant_use");

            }
                break;

            default:
                if(view.getTag().toString().equals( "trigonomic")){
                    ((MainActivity) getActivity()).aButtonIsPressed(((Button)view).getText().toString()+"(");

                }else {
                    ((MainActivity) getActivity()).aButtonIsPressed(view.getTag().toString());
                }
                sendClearButtonIntent();
        }
    }

    private void applyArc(boolean isArc) {
        if(isArc) {
            ((Button) mView.findViewById(R.id.buttonSinus)).setText("a"+ ((Button) mView.findViewById(R.id.buttonSinus)).getText());
            ((Button) mView.findViewById(R.id.buttonCosinus)).setText("a"+ ((Button) mView.findViewById(R.id.buttonCosinus)).getText());
            ((Button) mView.findViewById(R.id.buttonTan)).setText("a"+ ((Button) mView.findViewById(R.id.buttonTan)).getText());
            ((Button) mView.findViewById(R.id.buttonCot)).setText("a"+ ((Button) mView.findViewById(R.id.buttonCot)).getText());

            ((Button) mView.findViewById(R.id.buttonSinusH)).setText("a"+ ((Button) mView.findViewById(R.id.buttonSinusH)).getText());
            ((Button) mView.findViewById(R.id.buttonCosinusH)).setText("a"+ ((Button) mView.findViewById(R.id.buttonCosinusH)).getText());
            ((Button) mView.findViewById(R.id.buttonTanH)).setText("a"+ ((Button) mView.findViewById(R.id.buttonTanH)).getText());
            ((Button) mView.findViewById(R.id.buttonCotH)).setText("a"+ ((Button) mView.findViewById(R.id.buttonCotH)).getText());
        }else {
            ((Button) mView.findViewById(R.id.buttonSinus)).setText(((Button) mView.findViewById(R.id.buttonSinus)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonCosinus)).setText(((Button) mView.findViewById(R.id.buttonCosinus)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonTan)).setText(((Button) mView.findViewById(R.id.buttonTan)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonCot)).setText(((Button) mView.findViewById(R.id.buttonCot)).getText().toString().substring(1));

            ((Button) mView.findViewById(R.id.buttonSinusH)).setText(((Button) mView.findViewById(R.id.buttonSinusH)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonCosinusH)).setText(((Button) mView.findViewById(R.id.buttonCosinusH)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonTanH)).setText(((Button) mView.findViewById(R.id.buttonTanH)).getText().toString().substring(1));
            ((Button) mView.findViewById(R.id.buttonCotH)).setText(((Button) mView.findViewById(R.id.buttonCotH)).getText().toString().substring(1));
        }
    }

    private void applyInverse(boolean isOn) {
        if(arcIsOn) {
            if (isOn) {
                ((Button) mView.findViewById(R.id.buttonSinus)).setText("a"+ getResources().getString(R.string.csc));
                ((Button) mView.findViewById(R.id.buttonCosinus)).setText("a"+ getResources().getString(R.string.sec));
                ((Button) mView.findViewById(R.id.buttonTan)).setText("a"+ getResources().getString(R.string.taninverse));
                ((Button) mView.findViewById(R.id.buttonCot)).setText("a"+ getResources().getString(R.string.cotinverse));

               ((Button) mView.findViewById(R.id.buttonSinusH)).setText("a"+ getResources().getString(R.string.csch));
                ((Button) mView.findViewById(R.id.buttonCosinusH)).setText("a"+ getResources().getString(R.string.sech));
                ((Button) mView.findViewById(R.id.buttonTanH)).setText("a"+ getResources().getString(R.string.coth));
                ((Button) mView.findViewById(R.id.buttonCotH)).setText("a"+ getResources().getString(R.string.tanh));
            } else {
                ((Button) mView.findViewById(R.id.buttonSinus)).setText("a"+ getResources().getString(R.string.sin));
                ((Button) mView.findViewById(R.id.buttonCosinus)).setText("a"+ getResources().getString(R.string.cos));
                ((Button) mView.findViewById(R.id.buttonTan)).setText("a"+ getResources().getString(R.string.tan));
                ((Button) mView.findViewById(R.id.buttonCot)).setText("a"+ getResources().getString(R.string.cot));

                ((Button) mView.findViewById(R.id.buttonSinusH)).setText("a"+ getResources().getString(R.string.sinh));
                ((Button) mView.findViewById(R.id.buttonCosinusH)).setText("a"+ getResources().getString(R.string.cosh));
                ((Button) mView.findViewById(R.id.buttonTanH)).setText("a"+ getResources().getString(R.string.tanh));
                ((Button) mView.findViewById(R.id.buttonCotH)).setText("a"+ getResources().getString(R.string.coth));
            }
        }else {

            if (isOn) {
                ((Button) mView.findViewById(R.id.buttonSinus)).setText(getResources().getString(R.string.csc));
                ((Button) mView.findViewById(R.id.buttonCosinus)).setText(getResources().getString(R.string.sec));
                ((Button) mView.findViewById(R.id.buttonTan)).setText(getResources().getString(R.string.taninverse));
                ((Button) mView.findViewById(R.id.buttonCot)).setText(getResources().getString(R.string.cotinverse));

                ((Button) mView.findViewById(R.id.buttonSinusH)).setText(getResources().getString(R.string.csch));
                ((Button) mView.findViewById(R.id.buttonCosinusH)).setText(getResources().getString(R.string.sech));
                ((Button) mView.findViewById(R.id.buttonTanH)).setText(getResources().getString(R.string.coth));
                ((Button) mView.findViewById(R.id.buttonCotH)).setText(getResources().getString(R.string.tanh));
            } else {
                ((Button) mView.findViewById(R.id.buttonSinus)).setText(getResources().getString(R.string.sin));
                ((Button) mView.findViewById(R.id.buttonCosinus)).setText(getResources().getString(R.string.cos));
                ((Button) mView.findViewById(R.id.buttonTan)).setText(getResources().getString(R.string.tan));
                ((Button) mView.findViewById(R.id.buttonCot)).setText(getResources().getString(R.string.cot));

                ((Button) mView.findViewById(R.id.buttonSinusH)).setText(getResources().getString(R.string.sinh));
                ((Button) mView.findViewById(R.id.buttonCosinusH)).setText(getResources().getString(R.string.cosh));
                ((Button) mView.findViewById(R.id.buttonTanH)).setText(getResources().getString(R.string.tanh));
                ((Button) mView.findViewById(R.id.buttonCotH)).setText(getResources().getString(R.string.coth));
            }

        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mScientificTypeFaceChangedReceiver, new IntentFilter("typeFaceIntent"));

    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mScientificTypeFaceChangedReceiver);

    }

    // Send an Intent with an action named "clearintent". when a scientific button  is pressed clear button's text should change to <- backspace
    private void sendClearButtonIntent() {
        Intent intent = new Intent("clearIntent");
        // add data
        intent.putExtra("buttonValue", "Check");
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).sendBroadcast(intent);
    }

    int[] getScientificButtonsID(){
        int [] buttonsPointerArray;

        buttonsPointerArray = new int[]{ R.id.buttonSinus , R.id.buttonCosinus , R.id.buttonTan , R.id.buttonln , R.id.buttonlog , R.id.buttonlog , R.id.buttonpie , R.id.buttone, R.id.buttonpower ,
                R.id.buttonrad , R.id.buttonCot , R.id.switch_deg_rad , R.id.buttonPow2 , R.id.buttonPow3 ,R.id.buttonInverse , R.id.buttonSinusH , R.id.buttonCosinusH , R.id.buttonTanH , R.id.buttonCotH
                , R.id.buttonConstant , R.id.buttonARC

        };
        return buttonsPointerArray;

    }
    /**
     * Called when the checked state of a compound button has changed.
     *
     * @param buttonView The compound button view whose state has changed.
     * @param isChecked  The new checked state of buttonView.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        boolean on = ((ToggleButton) buttonView).isChecked();

        if (on) {
            //result in Degress
            ((MainActivity)getActivity()). setAngleMode(true);
        } else {
            //result in Radian
            ((MainActivity)getActivity()). setAngleMode(false);

        }

    }



}
