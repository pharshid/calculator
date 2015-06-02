/**
 *
 */
package com.sepidsa.fortytwocalculator;

import android.content.BroadcastReceiver;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


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
