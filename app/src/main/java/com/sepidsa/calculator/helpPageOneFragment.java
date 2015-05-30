/**
 *
 */
package com.sepidsa.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.ToggleButton;


/**
 * @author Ehsan
 *
 */
public class helpPageOneFragment extends android.support.v4.app.Fragment {
    private static final int DIALPAD_FONT_ROBOTO_THIN = 0;
    private static final int DIALPAD_FONT_ROBOTO_LIGHT = 1;
    private static final int DIALPAD_FONT_ROBOTO_REGULAR = 2;
    private boolean arcIsOn = false;

    View mView;
    private Typeface defaultFont;

    private BroadcastReceiver mThemeChangedReciever;
    private BroadcastReceiver mClearButtonChangedReceiver;
    private BroadcastReceiver mDialPadTypeFaceChangedReceiver;


    private int[] idList;
    boolean mIsRetroOn = false ;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return mView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

    }

}
