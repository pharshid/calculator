package com.sepidsa.calculator;

import java.util.Locale;

import android.graphics.Typeface;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class HelpActivity extends FragmentActivity implements View.OnClickListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private Typeface mFlatIcon;
    private Typeface mDastnevis;
    private Typeface mMitra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.help_viewpager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

//
//               mRobotoLight = Typeface.createFromAsset(getAssets(), "roboto_light.ttf");
//         mRobotoRegular =  Typeface.createFromAsset(getAssets(), "roboto_regular.ttf");
          mDastnevis = Typeface.createFromAsset(getAssets(), "dastnevis.otf");
         mMitra = Typeface.createFromAsset(getAssets(), "mitra.ttf");
         mFlatIcon = Typeface.createFromAsset(getAssets(), "flaticon.ttf");

        Button navButton = (Button)findViewById(R.id.next_help_page);
        navButton.setOnClickListener(this);
        navButton.setTypeface(mFlatIcon);

         navButton = (Button)findViewById(R.id.previous_help_page);
        navButton.setTypeface(mFlatIcon);
        navButton.setOnClickListener(this);




    }

    @Override
    public void onClick(View v) {

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_help, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }



    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a HelpPageOneFragment (defined as a static inner class below).
            return HelpPageOneFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HelpPageOneFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HelpPageOneFragment newInstance(int sectionNumber) {
            HelpPageOneFragment fragment = new HelpPageOneFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public HelpPageOneFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_help_one, container, false);

           Typeface mMitra = Typeface.createFromAsset(getActivity().getAssets(), "mitra.ttf");
            Typeface  mFlatIcon = Typeface.createFromAsset(getActivity().getAssets(), "flaticon.ttf");
            Typeface  mDastnevis = Typeface.createFromAsset(getActivity().getAssets(), "dastnevis.otf");

            TextView tv = (TextView) rootView.findViewById(R.id.page_logo);
            tv.setTypeface(mFlatIcon);

             tv = (TextView) rootView.findViewById(R.id.page_description);
            tv.setTypeface(mMitra);

            tv = (TextView) rootView.findViewById(R.id.passage);
            tv.setTypeface(mMitra);

            tv = (TextView) rootView.findViewById(R.id.highlight);
            tv.setTypeface(mMitra);

//            tv = (TextView) rootView.findViewById(R.id.highlight_explanation);
//            tv.setTypeface(mMitra);
            return rootView;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HelpPageTwoFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static HelpPageTwoFragment newInstance(int sectionNumber) {
            HelpPageTwoFragment fragment = new HelpPageTwoFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public HelpPageTwoFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_help_one, container, false);
            return rootView;
        }
    }

}
