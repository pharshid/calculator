package com.sepidsa.calculator;

import android.graphics.Rect;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;

/**
 * This animation class is animating the expanding and reducing the size of a view.
 * The animation toggles between the Expand and Reduce, depending on the current state of the view
 * @author Udinic
 *
 */
public class ExpandAnimation extends Animation {
    private View mAnimatedView;
    private ListView mListView;
    private View mParentView;
    private LayoutParams mViewLayoutParams;
    private int mMarginStart, mMarginEnd;
    private boolean mIsVisibleAfter = false;
    private boolean mWasEndedAlready = false;

    /**
     * Initialize the animation
     * @param view The layout we want to animate
     * @param duration The duration of the animation, in ms
     */
    public ExpandAnimation(ListView listView, View parent, View view, int duration) {

        setDuration(duration);
        mAnimatedView = view;
        mViewLayoutParams = (LayoutParams) view.getLayoutParams();
        mListView = listView;
        mParentView = parent;

        // decide to show or hide the view
        mIsVisibleAfter = (view.getVisibility() == View.VISIBLE);

        mMarginStart = mViewLayoutParams.bottomMargin;
        mMarginEnd = (mMarginStart == 0 ? (0- view.getHeight()) : 0);

        view.setVisibility(View.VISIBLE);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);

        if ((interpolatedTime < 1.0f)) {
            mIsVisibleAfter = false;
            mAnimatedView.setVisibility(View.VISIBLE);
            // Calculating the new bottom margin, and setting it
            mViewLayoutParams.bottomMargin = mMarginStart
                    + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);

            // Invalidating the layout, making us seeing the changes we made
            mAnimatedView.requestLayout();
            mListView.post(new Runnable() {

                @Override
                public void run() {
                    Rect rect = new Rect(mAnimatedView.getLeft(), mParentView.getTop(), mAnimatedView.getRight(), mAnimatedView.getBottom());
                    mListView.requestChildRectangleOnScreen(mParentView,
                            rect, false);
                }
            });

            // Making sure we didn't run the ending before (it happens!)
        } else if (!mWasEndedAlready) {
            mViewLayoutParams.bottomMargin = mMarginEnd;
            mAnimatedView.requestLayout();

            if (mIsVisibleAfter) {
                mAnimatedView.setVisibility(View.GONE);
            }
            mWasEndedAlready = true;
        }
    }
}
