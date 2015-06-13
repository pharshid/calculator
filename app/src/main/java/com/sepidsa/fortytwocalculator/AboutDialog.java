package com.sepidsa.fortytwocalculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by ehsanparhizkar on 6/9/15 AD.
 */
public class AboutDialog extends Activity {
    /**
     * Create a Dialog window that uses the default dialog frame style.
     *
     * @param context The Context the Dialog is to run it.  In particular, it
     *                uses the window manager and theme in this context to
     *                present its UI.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about_dialog);

//        TextView app_version = (TextView)findViewById(R.id.scientific_mode_textview);
//        Typeface mRobotoThin = Typeface.createFromAsset(getOwnerActivity().getAssets(), "roboto_thin.ttf");
//
//

    }
}
