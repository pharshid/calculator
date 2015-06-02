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

package com.sepidsa.fortytwocalculator;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;


/**
 * A dialog which takes in as input an array of colors and creates a palette allowing the user to
 * select a specific color swatch, which invokes a listener.
 */
public class PurchaseDialog extends DialogFragment {

    protected AlertDialog mAlertDialog;


    public PurchaseDialog() {
        // Empty constructor required for dialog fragments.
    }

//    public static PurchaseDialog newInstance(int titleResId, int selectedColor, int[] colors, int[] keypadColors,
//                                                int selectedkeypadColor, int size, int columns) {
//        PurchaseDialog ret = new PurchaseDialog();
//        ret.initialize(titleResId, colors, selectedColor,keypadColors,selectedkeypadColor, columns, size);
//        return ret;
//    }
//


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final Activity activity = getActivity();
        Typeface mMitra = Typeface.createFromAsset(getActivity().getAssets(), "mitra.ttf");
        Typeface mFlaticon = Typeface.createFromAsset(getActivity().getAssets(), "flaticon.ttf");

        View view = inflater.inflate(R.layout.fragment_purchase_dialog, container);
        int [] mitraFontViewIds = new int[]{

                R.id.tv_access_new_scientific_desc, R.id.tv_access_new_themes_desc, R.id.tv_infinite_stars_desc,
                R.id.tv_persistent_history_tape_desc,R.id.tv_why_premium
        };

        for(int id:mitraFontViewIds) {
            TextView tempest = (TextView)view.findViewById(id);
            tempest.setTypeface(mMitra);

        }

        int [] iconFontViewIds = new int[]{
                R.id.ic_access_new_scientific, R.id.ic_access_new_themes, R.id.ic_infinite_stars,
                R.id.ic_persistent_history_tape
        };
        for(int id:iconFontViewIds) {
            TextView tempest = (TextView)view.findViewById(id);
            tempest.setTypeface(mFlaticon);

        }
//        Button tempest = (Button)view.findViewById(R.id.btn_buy_classic_theme);
//        tempest.setTypeface(mMitra);
//         tempest = (Button)view.findViewById(R.id.btn_buy_premium);
//        tempest.setTypeface(mMitra);

        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return view;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }





}
