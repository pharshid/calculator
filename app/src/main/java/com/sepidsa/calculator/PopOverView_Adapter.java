package com.sepidsa.calculator;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PopOverView_Adapter extends Log_Adapter implements Serializable {

    private static final int PERSIAN_TRANSLATION_FONT_MITRA = 0 ;
    private static final int PERSIAN_TRANSLATION_FONT_DASTNEVIS = 1 ;
    // List of ToDoItems
    List<Item> mItems = new ArrayList<Item>();
    Typeface mDefaultFont, mSummaryFont;
    protected int mThemeColor ;
    private  Context mContext = null;

    public PopOverView_Adapter(Context context, int _themeColor, Typeface _defaultFont,ListView _listView) {
        super(context, _themeColor, _defaultFont,_listView);
    }

    public PopOverView_Adapter(Context context, int _themeColor, List<Item> _mItems, Typeface _defaultFont,ListView _listView) {
        super(context, _themeColor, _mItems, _defaultFont,_listView);
    }




    List<Item> getData(){
        return mItems;

    }
    void setThemeColor(int colorCode){
        mThemeColor = colorCode;
    }

	// Add a ToDoItem to the adapter
	// Notify observers that the data set has changed

	public void add(String Result) {
		//TextView dummy = new TextView(mContext);
		//dummy.setText(item);
		mItems.add(new Item("",Result,false,""));
//        ((MainActivity)mContext).setMListView((Serializable) (mItems));

        notifyDataSetChanged();

    }

	// Clears the list adapter of all items.

	public void clear(){

		mItems.clear();
		notifyDataSetChanged();

	}

	// Returns the number of ToDoItems

	@Override
	public int getCount() {

		return mItems.size();

	}

	// Retrieve the number of ToDoItems

	@Override
	public Object getItem(int pos) {

		return mItems.get(pos);

	}

	// Get the ID for the ToDoItem
	// In this case it's just the position

	@Override
	public long getItemId(int pos) {

		return pos;

	}

	//Create a View to display the Item
	// at specified position in mItems

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout itemLayout = null;

        if (convertView == null) {

            itemLayout = (RelativeLayout) LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.log_item, parent, false);
        } else {
            itemLayout = (RelativeLayout) convertView;
        }

        if(position == 0 ) {




             TextView view = (TextView) itemLayout.findViewById(R.id.LOG_detail);
             view.setText("اعداد ستاره دار");
             view.setTypeface(mSummaryFont);


            view = (TextView) itemLayout.findViewById(R.id.Log_title);
            view.setText(" ");
            view.setTypeface(mDefaultFont);
            view.setTextColor(mThemeColor);


         }else
         {
             final String text = (String) getItem(position);
             String title = text.substring(0, text.indexOf(';'));
             String detail = text.substring(text.indexOf(';') + 1);
             TextView view;



             view = (TextView) itemLayout.findViewById(R.id.Log_title);
             view.setText(title);
             view.setTypeface(mDefaultFont);
             view.setTextColor(mThemeColor);

             view = (TextView) itemLayout.findViewById(R.id.LOG_detail);
             view.setText(detail);
             view.setTypeface(mDefaultFont);

         }
		// Return the View you just created
		return itemLayout;

	}


}
