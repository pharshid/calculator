package com.sepidsa.fortytwocalculator;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;


public class WhatsNewDialogClass extends Dialog implements View.OnClickListener {


    Typeface mMitra ;



    public WhatsNewDialogClass(Context context, int theme) {
        super(context, theme);
        mMitra = Typeface.createFromAsset(context.getAssets(), "mitra.ttf");

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setContentView(R.layout.whats_new_dialog);

        TextView textView =  (TextView) findViewById(R.id.whats_new_text);
        textView.setTypeface(mMitra);

         textView =  (TextView) findViewById(R.id.whats_new_title);
        textView.setTypeface(mMitra);

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }
}


