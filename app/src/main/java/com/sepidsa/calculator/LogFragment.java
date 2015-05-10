/**
 *
 */
package com.sepidsa.calculator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ehsan
 *
 */
public class LogFragment extends android.support.v4.app.ListFragment {

    private static final int DIALPAD_FONT_ROBOTO_THIN = 0 ;
    Log_Adapter mSummaryAdapter;
    private BroadcastReceiver mLogReceiver;
    public ListView mListView;
    String operation = "";
    String result = "";
    boolean starred = false;
    String numberTag;

    private SwipeRefreshLayout swipeLayout;
    private Typeface defaultFont;
    private BroadcastReceiver mDialPadTypeFaceChangedReceiver;
    private BroadcastReceiver mThemeChangedReciever;


    public LogFragment() {
        // TODO Auto-generated constructor stub

    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        this.setRetainInstance(true);
        defaultFont =  Typeface.createFromAsset( ((MainActivity)getActivity()).getAssets(), "roboto_light.ttf");
        return  inflater.inflate(R.layout.fragment_log, container, false);
    }



    @Override
    public void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mLogReceiver, new IntentFilter("LogIntent"));
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(mThemeChangedReciever, new IntentFilter("theme_change_intent"));

    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
        outState.putSerializable("logAdapterData",((Serializable)( (Log_Adapter)mListView.getAdapter()).getData()) );
        ((MainActivity)getActivity()).setMListView((Serializable) ((Log_Adapter) mListView.getAdapter()).getData());

    }

    @Override
    public void onPause() {
        super.onPause();
        ((MainActivity)getActivity()).setMListView((Serializable) ((Log_Adapter) mListView.getAdapter()).getData());

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mLogReceiver);
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(mThemeChangedReciever);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        mListView = getListView();
        mListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        mListView.setStackFromBottom(false);
        switch ( ((MainActivity)getActivity()). getCurrentThemePreference() ) {
            case 0:
                //Light theme

                mListView.setBackgroundColor(Color.WHITE);
                mListView.setCacheColorHint(Color.WHITE);
                break;

            case 1:
                //Dark theme

                mListView.setBackgroundColor(Color.parseColor("#1c1c1c"));
                mListView.setCacheColorHint(Color.parseColor("#1c1c1c"));
                break;
        }



        List <Item> data =(List<Item>) ((MainActivity)getActivity()).getmListView();
        if ( data != null) {
            mSummaryAdapter = new Log_Adapter(getActivity().getApplicationContext(), ((MainActivity) getActivity()).getThemeColorCode(),data,defaultFont,getListView());
        }

        else    if(savedInstanceState !=null  ){
            data =(List<Item>) savedInstanceState.getSerializable("logAdapterData");
            mSummaryAdapter = new Log_Adapter(getActivity().getApplicationContext(), ((MainActivity) getActivity()).getThemeColorCode(),data,defaultFont,getListView());
        }
//
        else   {
            mSummaryAdapter = new Log_Adapter(getActivity().getApplicationContext(), ((MainActivity) getActivity()).getThemeColorCode(),defaultFont,getListView());
        }

        setListAdapter(mSummaryAdapter);


        mThemeChangedReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent
                switch ( intent.getIntExtra("theme",0) ) {
                    case 0:

                        //Light theme
                        mListView.setBackgroundColor(Color.WHITE);
                        mListView.setCacheColorHint(Color.WHITE);
                        break;

                    case 1:

                        //Dark theme
                        mListView.setBackgroundColor(Color.parseColor("#1c1c1c"));
                        mListView.setCacheColorHint(Color.parseColor("#1c1c1c"));
                        break;
                }
            }
        };

        mLogReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // Extract data included in the Intent todo
                operation = intent.getStringExtra("OPERATION");
                result = intent.getStringExtra("RESULT");
                starred = intent.getBooleanExtra("STARRED", false);
                numberTag = intent.getStringExtra("TAG");

                if(numberTag.equals("")!= true ){
                    starred = true ;
                }

                mSummaryAdapter.add(new Item(operation, result,starred,numberTag));
                ((MainActivity)getActivity()).setMListView((Serializable) ((Log_Adapter) mListView.getAdapter()).getData());

                return;
            }
        };


        super.onActivityCreated(savedInstanceState);
    }











}