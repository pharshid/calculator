package com.sepidsa.fortytwocalculator;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sepidsa.fortytwocalculator.data.CurrencyContract;
import com.sepidsa.fortytwocalculator.CurrencyUseAdapter;
import com.sepidsa.fortytwocalculator.sync.CurrencySyncAdapter;

/**
 * Created by Farshid on 5/20/2015.
 */
public class CurrencyUseFragment  extends DialogFragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private CurrencyUseAdapter mCurrencyUseAdapter;
    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private static final String SELECTED_KEY = "selected_position";
    private static final int CURRENCY_LOADER = 0;
    private ProgressBar mProgress;
    private ProgressBar mProgress2;

//    private static final String[] CURRENCY_COLUMNS = {
//            CurrencyContract.CurrencyEntry.TABLE_NAME + "." + CurrencyContract.CurrencyEntry._ID,
//            CurrencyContract.CurrencyEntry.COLUMN_KEY,
//            CurrencyContract.CurrencyEntry.COLUMN_VALUE,
//    };

    public CurrencyUseFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(CURRENCY_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mCurrencyUseAdapter = new CurrencyUseAdapter(getActivity(), null, 0);
        View rootView = inflater.inflate(R.layout.fragment_currency_use, container, false);


        mListView = (ListView) rootView.findViewById(R.id.listview_currency);
        TextView empty = (TextView) rootView.findViewById(R.id.empty_list);
        mListView.setEmptyView(empty);
        mListView.setAdapter(mCurrencyUseAdapter);
        mProgress = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mProgress.setVisibility(View.GONE);
        mProgress.setIndeterminate(true);
        mProgress2 = (ProgressBar) rootView.findViewById(R.id.progressBar2);
        mProgress2.setVisibility(View.GONE);
        mProgress2.setIndeterminate(false);
        mProgress2.setMax(24);
        mProgress2.setProgress(0);


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView adapterView, final View view, final int position, long l) {
                TextView resultView = (TextView) view.findViewById(R.id.currency_value);
                String result = resultView.getText().toString();
                ((MainActivity) getActivity()).addNumberToCalculation(result);
                ((MainActivity) getActivity()).switchToMainFragment();
                dismiss();


            }

        });


        return rootView;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCurrencyUseAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {
            mListView.smoothScrollToPosition(mPosition);
        }
        if(mCurrencyUseAdapter.getCount() != 24) {
            mProgress.setVisibility(View.VISIBLE);
            mProgress2.setVisibility(View.VISIBLE);
            mProgress2.setProgress(mCurrencyUseAdapter.getCount());
        } else {
            mProgress.setVisibility(View.GONE);
            mProgress2.setVisibility(View.GONE);
            mProgress2.setProgress(24);
                    }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCurrencyUseAdapter.swapCursor(null);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getActivity(),
                CurrencyContract.CurrencyEntry.CONTENT_URI,
                null,
                null,
                null,
                CurrencyContract.CurrencyEntry.COLUMN_PRIORITY + " ASC");
    }

}