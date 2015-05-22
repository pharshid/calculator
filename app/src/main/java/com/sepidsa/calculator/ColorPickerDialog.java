/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sepidsa.calculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ViewSwitcher;

import info.hoang8f.android.segmented.SegmentedGroup;


/**
 * A dialog which takes in as input an array of colors and creates a palette allowing the user to
 * select a specific color swatch, which invokes a listener.
 */
public class ColorPickerDialog extends DialogFragment implements ColorPickerSwatch.OnColorSelectedListener,RadioGroup.OnCheckedChangeListener {

    public static final int SIZE_LARGE = 1;
    public static final int SIZE_SMALL = 2;
    private static final String KEY_COLORS_KEYPAD = "color_keypad" ;
    protected static final String KEY_SELECTED_COLOR_KEYPAD = "selected_color_keypad";

    protected AlertDialog mAlertDialog;

    protected static final String KEY_TITLE_ID = "title_id";
    protected static final String KEY_COLORS = "colors";
    protected static final String KEY_COLOR_CONTENT_DESCRIPTIONS = "color_content_descriptions";
    protected static final String KEY_SELECTED_COLOR = "selected_color";
    protected static final String KEY_COLUMNS = "columns";
    protected static final String KEY_SIZE = "size";

    protected int mTitleResId = R.string.color_picker_default_title;
    protected int[] mColors = null;
    protected int[] mColorsKeypad = null;

    protected String[] mColorContentDescriptions = null;
    protected int mSelectedColor;
    protected int mColumns;
    protected int mSize;
    private int mSelectedKeypadBackgroundColor;

    private ColorPickerPalette mPaletteMaintheme;
    private ColorPickerPalette mPalettekeypad;

    private ProgressBar mProgress;

    protected ColorPickerSwatch.OnColorSelectedListener mListener;
    private ViewSwitcher mViewSwitcher;

    public ColorPickerDialog() {
        // Empty constructor required for dialog fragments.
    }

    public static ColorPickerDialog newInstance(int titleResId, int selectedColor, int[] colors, int[] keypadColors,
                                                int selectedkeypadColor, int size, int columns) {
        ColorPickerDialog ret = new ColorPickerDialog();
        ret.initialize(titleResId, colors, selectedColor,keypadColors,selectedkeypadColor, columns, size);
        return ret;
    }

    public void initialize(int titleResId, int[] colors,int selectedColor, int[] keypadColors, int selectedkeypadColor, int columns, int size) {
        setArguments(titleResId, columns, size);
        setColors(colors, selectedColor);
        setKeypadColors(keypadColors, selectedkeypadColor);
    }


    public void setArguments(int titleResId, int columns, int size) {
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TITLE_ID, titleResId);
        bundle.putInt(KEY_COLUMNS, columns);
        bundle.putInt(KEY_SIZE, size);
        setArguments(bundle);
    }

    public void setOnColorSelectedListener(ColorPickerSwatch.OnColorSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mTitleResId = getArguments().getInt(KEY_TITLE_ID);
            mColumns = getArguments().getInt(KEY_COLUMNS);
            mSize = getArguments().getInt(KEY_SIZE);
        }

        if (savedInstanceState != null) {
            mColors = savedInstanceState.getIntArray(KEY_COLORS);
            mColorsKeypad = savedInstanceState.getIntArray(KEY_COLORS_KEYPAD);
            mSelectedColor = (Integer) savedInstanceState.getSerializable(KEY_SELECTED_COLOR);
            mSelectedKeypadBackgroundColor = (Integer) savedInstanceState.getSerializable(KEY_SELECTED_COLOR_KEYPAD);
            mColorContentDescriptions = savedInstanceState.getStringArray(
                    KEY_COLOR_CONTENT_DESCRIPTIONS);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Activity activity = getActivity();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.color_picker_dialog, null);
//        mProgress = (ProgressBar) view.findViewById(android.R.id.progress);


        mPalettekeypad = (ColorPickerPalette) view.findViewById(R.id.color_picker_keypad);
        mPalettekeypad.init(mSize, mColumns, this);
        mPaletteMaintheme = (ColorPickerPalette) view.findViewById(R.id.color_picker_theme);
        mPaletteMaintheme.init(mSize, mColumns, this);

        mViewSwitcher = (ViewSwitcher)view.findViewById(R.id.color_picker_holder);
        Animation slide_in_left, slide_out_right;

        slide_in_left = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_in);
        slide_out_right = AnimationUtils.loadAnimation(getActivity(),
                android.R.anim.fade_out);

        mViewSwitcher.setInAnimation(slide_in_left);
        mViewSwitcher.setOutAnimation(slide_out_right);

        //        mColorsKeypad = Utils.ColorUtils.colorChoiceForKeypad(getActivity().getApplicationContext());

        if (mColors != null) {
//            refreshPalette(mPaletteMaintheme,mColors,mSelectedColor,mColorContentDescriptions);

            prepareThemeSegmentedControl(view);
        }

        mAlertDialog = new AlertDialog.Builder(activity)
                .setTitle(mTitleResId)
                .setView(view)
                .create();

        return mAlertDialog;
    }

    @Override
    public void onColorSelected(int color) {
        if (mListener != null) {
            mListener.onColorSelected(color);
        }

        if (getTargetFragment() instanceof ColorPickerSwatch.OnColorSelectedListener) {
            final ColorPickerSwatch.OnColorSelectedListener listener =
                    (ColorPickerSwatch.OnColorSelectedListener) getTargetFragment();
            listener.onColorSelected(color);
        }
        if(mViewSwitcher.getCurrentView().getTag().equals("theme")) {
            if (color != mSelectedColor) {
                mSelectedColor = color;
                // Redraw palette to show checkmark on newly selected color before dismissing.
                refreshPalette(mPaletteMaintheme, mColors, mSelectedColor, mColorContentDescriptions);
                ((MainActivity)getActivity()).changeAccentColor(mSelectedColor);
                //save accent color
                //redraw theme
            }
        }else {
            if (color != mSelectedKeypadBackgroundColor) {
                mSelectedKeypadBackgroundColor = color;
                // Redraw palette to show checkmark on newly selected color before dismissing.
                refreshPalette(mPalettekeypad, mColorsKeypad, mSelectedKeypadBackgroundColor, mColorContentDescriptions);
                ((MainActivity)getActivity()).changeKeypadBackgroundColor(mSelectedKeypadBackgroundColor);
            }
        }
        dismiss();
    }




    public void setColors(int[] colors, int selectedColor) {
        if (mColors != colors || mSelectedColor != selectedColor) {
            mColors = colors;
            mSelectedColor = selectedColor;
//            refreshPalette(mPaletteMaintheme,mColors,mSelectedColor,mColorContentDescriptions);
        }
    }

    private void setKeypadColors(int[] colors, int selectedColor) {
        if (mColorsKeypad != colors || mSelectedKeypadBackgroundColor != selectedColor) {
            mColorsKeypad = colors;
            mSelectedKeypadBackgroundColor = selectedColor;
//            refreshPalette(mPalettekeypad,mColorsKeypad,mSelectedKeypadBackgroundColor,mColorContentDescriptions);
        }
    }

    private void refreshPalette(ColorPickerPalette pallette, int[] colors , int selectedColor,String [] colorDescriptions) {
        if (pallette != null && colors != null) {
            pallette.drawPalette(colors, selectedColor, colorDescriptions);
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putIntArray(KEY_COLORS, mColors);
        outState.putSerializable(KEY_SELECTED_COLOR, mSelectedColor);
        outState.putStringArray(KEY_COLOR_CONTENT_DESCRIPTIONS, mColorContentDescriptions);

        outState.putIntArray(KEY_COLORS_KEYPAD, mColorsKeypad);
        outState.putSerializable(KEY_SELECTED_COLOR_KEYPAD, mSelectedKeypadBackgroundColor);
    }

    private void prepareThemeSegmentedControl(View view) {
        RadioButton buttonLightTheme = (RadioButton) view.findViewById(R.id.accent_color_button);
        RadioButton buttonDarkTheme = (RadioButton) view.findViewById(R.id.keypad_theme_button);

        SegmentedGroup themeSegment = (SegmentedGroup) view.findViewById(R.id.segmented);
        themeSegment.setOnCheckedChangeListener(this);
        themeSegment.setTintColor(((MainActivity)getActivity()).getAccentColorCode());
        switch ( 0) {
            case 0 : // light theme

                buttonLightTheme.setChecked(true);
                buttonDarkTheme.setChecked(false);
                break;

            case 1 : // dark theme
                buttonLightTheme.setChecked(false);
                buttonDarkTheme.setChecked(true);
                break;
        }

    }



    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        switch (i) {

            case R.id.accent_color_button:
                //change accent color
                if(((MainActivity)getActivity()).isRetroThemeSelected() == true){
                    ((MainActivity) getActivity()).setRetrothemeSelected(false);
                    getActivity().recreate();
                }
//                ((MainActivity)getActivity()).changeKeypadBackgroundColor(mSelectedColor);

                refreshPalette(mPaletteMaintheme,mColors,mSelectedColor,mColorContentDescriptions);

                if ( mViewSwitcher.getCurrentView().getTag().equals("keypad")){
                    mViewSwitcher.showNext();
                }
                break;

            case R.id.keypad_theme_button:
                //Dark Theme
                if(((MainActivity)getActivity()).isRetroThemeSelected() == true){
                    ((MainActivity) getActivity()).setRetrothemeSelected(false);

                    getActivity().recreate();
                }
//                ((MainActivity)getActivity()). changeKeypadBackgroundColor(mSelectedKeypadBackgroundColor);
                refreshPalette(mPalettekeypad, mColorsKeypad, mSelectedKeypadBackgroundColor, mColorContentDescriptions);
                if ( ((String)mViewSwitcher.getCurrentView().getTag()).equals("theme")){
                    mViewSwitcher.showNext();
                }

                break;
            //Dark Theme
            case R.id.retro_theme_button :
                if(((MainActivity)getActivity()).isRetroThemeSelected() == false){
                    ((MainActivity) getActivity()).setRetrothemeSelected(true);

                    ((MainActivity)getActivity()).recreate();

                }
                ((MainActivity)getActivity()).changeKeypadBackgroundColor(-1);

                break;
        }
    }
}
