package com.sepidsa.calculator;

import android.support.v4.app.Fragment;
import android.os.Bundle;

import java.util.List;

/**
 * Created by Farshid on 5/10/2015.
 */
public class ERVExpandableDataProviderFragment extends Fragment {
    private ERVExpandableDataProvider mDataProvider;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        //TODO read from sql here
        List<Item> data =(List<Item>) ((MainActivity)getActivity()).getmListView();
        mDataProvider = new ERVExpandableDataProvider(data);

    }

    public AbstractExpandableDataProvider getDataProvider() { return mDataProvider; }

}
