/*
package com.sepidsa.calculator;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.sepidsa.calculator.R;

import info.hoang8f.android.segmented.SegmentedGroup;

*/
/**
 * Created by ehsanparhizkar on 7/8/14.
 *//*

public class ColorPickerFragment extends Fragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {


    public static final String EXTRA_MESSAGE = "EXTRA_MESSAGE";
    private RadioButton buttonLightTheme;
    private RadioButton buttonDarkTheme;

    public static final ColorPickerFragment newInstance(String message)
    {
        ColorPickerFragment f = new ColorPickerFragment();
        Bundle bdl = new Bundle(1);
        bdl.putString(EXTRA_MESSAGE, message);
        f.setArguments(bdl);
        return f;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_colors, container, false);
       Typeface mIconFont = Typeface.createFromAsset( ((MainActivity)getActivity()).getAssets(), "flaticon.ttf");
             buttonLightTheme = (RadioButton)view.findViewById(R.id.light_theme_button);
            buttonLightTheme.setTypeface(mIconFont);
         buttonDarkTheme = (RadioButton)view.findViewById(R.id.dark_theme_button);
            buttonDarkTheme.setTypeface(mIconFont);

        buttonLightTheme.setTypeface(mIconFont);
        SegmentedGroup themeSegment = (SegmentedGroup)view.findViewById(R.id.segmented);
        themeSegment.setOnCheckedChangeListener(this);
        themeSegment.setTintColor(((MainActivity) getActivity()).getThemeColorCode());
        int idList[] = getNumericButtonsID();
        switch (   ((MainActivity) getActivity()).getCurrentThemePreference()) {
            case 0 : // light theme

                buttonLightTheme.setChecked(true);
                buttonDarkTheme.setChecked(false);

                break;
            case 1 : // dark theme
                buttonLightTheme.setChecked(false);
                buttonDarkTheme.setChecked(true);

                break;
        }
        for(int id : idList) {
            View v = view.findViewById(id);
            v.setOnClickListener(this);


//            if( v instanceof Switch){
//                ((Switch) v).setOnCheckedChangeListener(this);


//            }
        }

//        if(view.findViewById(R.id.switch_deg_rad) != null) {
//            ((Switch) (view.findViewById(R.id.switch_light_dark_theme))).setChecked(((MainActivity) getActivity()).getAngleMode());
//            ((Switch) (view.findViewById(R.id.switch_light_dark_theme))).setOnCheckedChangeListener(this);
//            view.findViewById(R.id.switch_light_dark_theme).setOnClickListener(this);
//
//
//        }



        return view;
    }





    int[] getNumericButtonsID(){
        int [] buttonsPointerArray = {  R.id.colora1,R.id.colora2,R.id.colora3,R.id.colora4,
               R.id.colorb1,R.id.colorb2,  R.id.colorb3,R.id.colorb4, R.id.colorc1,R.id.colorc2, R.id.colorc3,R.id.colorc4,
                R.id.colord1,R.id.colord2,R.id.colord3,R.id.colord4
        };
        return buttonsPointerArray;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public void onClick(View v) {
          */
/* *//*


        if (!(v instanceof Switch))
        ((MainActivity) getActivity()).aButtonIsPressed("requestingThemeColorChange",(String)v.getTag() ,v.getContentDescription() );

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        boolean on = ((Switch) buttonView).isChecked();


        if (on) {
            //Light theme

            ((MainActivity)getActivity()). changeTheme(0);
        } else {
            //Dark Theme
            ((MainActivity)getActivity()). changeTheme(1);

        }
//        ((MainActivity)getActivity()).switchToMainFragment();
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        switch (i) {

            case R.id.light_theme_button:
                //Light theme
                ((MainActivity) getActivity()).changeTheme(0);
                break;

            case R.id.dark_theme_button :
                //Dark Theme
                ((MainActivity) getActivity()).changeTheme(1);

            break;
        }
    }
}

*/
