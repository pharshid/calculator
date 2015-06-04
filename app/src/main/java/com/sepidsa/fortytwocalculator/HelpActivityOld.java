package com.sepidsa.fortytwocalculator;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;


public class HelpActivityOld extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

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
    private CirclePageIndicator mViewPagerIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_old);

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

        setMViewPagerIndicator();
        mViewPagerIndicator.setOnPageChangeListener(this);


    }



            private void setMViewPagerIndicator() {
        mViewPagerIndicator = (CirclePageIndicator) findViewById(R.id.help_view_pager_indicator);
        if (mViewPagerIndicator  != null) {
            mViewPagerIndicator.setViewPager(mViewPager);
            mViewPagerIndicator.setCurrentItem(0);
            mViewPagerIndicator.setFillColor(Color.WHITE);

            //  mViewPagerIndicator.setFades(false);
//            mViewPagerIndicator.setSelectedColor(getAccentColorCode());
//        mViewPagerIndicator.setFillColor(getAccentColorCode());
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.next_help_page:
                if(mViewPager.getCurrentItem() !=2) {
                    mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
                }else{
                    this.finish();
                }

        }
    }

    /**
     * This method will be invoked when the current page is scrolled, either as part
     * of a programmatically initiated smooth scroll or a user initiated touch scroll.
     *
     * @param position             Position index of the first page currently being displayed.
     *                             Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset       Value from [0, 1) indicating the offset from the page at position.
     * @param positionOffsetPixels Value in pixels indicating the offset from position.
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    int h;
    }

    /**
     * This method will be invoked when a new page becomes selected. Animation is not
     * necessarily complete.
     *
     * @param position Position index of the new selected page.
     */
    @Override
    public void onPageSelected(int position) {
        if(position == 2){
            Button navButton = (Button)findViewById(R.id.next_help_page);
            navButton.setText("خروج");
            navButton.setTypeface(mMitra);
        }
    }

    /**
     * Called when the scroll state changes. Useful for discovering when the user
     * begins dragging, when the pager is automatically settling to the current page,
     * or when it is fully stopped/idle.
     *
     * @param state The new scroll state.
     * @see android.support.v4.view.ViewPager#SCROLL_STATE_IDLE
     * @see android.support.v4.view.ViewPager#SCROLL_STATE_DRAGGING
     * @see android.support.v4.view.ViewPager#SCROLL_STATE_SETTLING
     */
    @Override
    public void onPageScrollStateChanged(int state) {

    }




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
            switch (position){
                case 0:
               return new HelpPageOneFragment();

                case 1:
                return    new HelpPageTwoFragment();

                case 2:
                return    new HelpPageThreeFragment();
            }
            return new HelpPageOneFragment();
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
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


        public HelpPageOneFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_help_one, container, false);

           Typeface mMitra = Typeface.createFromAsset(getActivity().getAssets(), "mitra.ttf");
            Typeface  mDastnevis = Typeface.createFromAsset(getActivity().getAssets(), "dastnevis.otf");
            Typeface  mYekan = Typeface.createFromAsset(getActivity().getAssets(), "yekan.ttf");



           TextView tv = (TextView) rootView.findViewById(R.id.passage);
            tv.setTypeface(mDastnevis);

            tv = (TextView) rootView.findViewById(R.id.highlight);
            tv.setTypeface(mDastnevis);

//            tv = (TextView) rootView.findViewById(R.id.highlight_explanation);
//            tv.setTypeface(mMitra);
            return rootView;
        }
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class HelpPageTwoFragment extends HelpPageOneFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreateView(inflater,container,savedInstanceState);
            View rootView = inflater.inflate(R.layout.fragment_help_two, container, false);
            Typeface mMitra = Typeface.createFromAsset(getActivity().getAssets(), "mitra.ttf");
            Typeface  mDastnevis = Typeface.createFromAsset(getActivity().getAssets(), "dastnevis.otf");


            TextView tv = (TextView) rootView.findViewById(R.id.passage);
            tv.setTypeface(mDastnevis);

            tv = (TextView) rootView.findViewById(R.id.highlight);
            tv.setTypeface(mMitra);
            return rootView;
        }
    }
  public static class HelpPageThreeFragment extends HelpPageOneFragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            super.onCreateView(inflater,container,savedInstanceState);
            View rootView = inflater.inflate(R.layout.fragment_help_three, container, false);
            Typeface mMitra = Typeface.createFromAsset(getActivity().getAssets(), "mitra.ttf");
            Typeface  mDastnevis = Typeface.createFromAsset(getActivity().getAssets(), "dastnevis.otf");


            TextView tv = (TextView) rootView.findViewById(R.id.passage);
            tv.setTypeface(mDastnevis);

            tv = (TextView) rootView.findViewById(R.id.highlight);
            tv.setTypeface(mMitra);
            return rootView;
        }
    }

}
