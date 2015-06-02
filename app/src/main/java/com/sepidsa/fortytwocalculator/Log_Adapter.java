package com.sepidsa.fortytwocalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ehsanparhizkar on 4/26/15.
 */
class Log_Adapter extends BaseAdapter implements Serializable, CompoundButton.OnCheckedChangeListener {

    private static final int PERSIAN_TRANSLATION_FONT_MITRA = 0 ;
    private static final int PERSIAN_TRANSLATION_FONT_DASTNEVIS = 1 ;
    // List of ToDoItems
    List<Item> mItems = new ArrayList<Item>();
    Typeface mDefaultFont, mSummaryFont;
    protected int mThemeColor ;
    private Context mContext = null;
    ListView mParentListView;
    public Log_Adapter(Context context, int _themeColor,Typeface _defaultFont,ListView _listView) {
        mParentListView =  _listView;
        mContext = context;
        mDefaultFont = _defaultFont;
        mThemeColor = _themeColor ;
//        if(mItems != null) {
//            add("first");
//        }
        mSummaryFont = getDefaultPersianTranslationFont();

    }

    public Log_Adapter(Context context, int _themeColor, List<Item> _mItems, Typeface _defaultFont,ListView _listView) {
        mParentListView =  _listView;

        mContext = context;
        mDefaultFont = _defaultFont;
        mThemeColor = _themeColor ;
        mItems = _mItems;
        notifyDataSetChanged();

    }

    Typeface getDefaultPersianTranslationFont(){

        SharedPreferences typographyPreferences = mContext.getSharedPreferences("typography", Context.MODE_PRIVATE);
        int PersianTranslationFont = typographyPreferences.getInt("PERSIAN_TRANSLATION_TYPEFACE",PERSIAN_TRANSLATION_FONT_MITRA);
        switch (PersianTranslationFont){
            case PERSIAN_TRANSLATION_FONT_MITRA:
                return Typeface.createFromAsset(mContext.getAssets(), "mitra.ttf");

            case PERSIAN_TRANSLATION_FONT_DASTNEVIS:
                return Typeface.createFromAsset(mContext.getAssets(), "dastnevis.otf");
            default:
                return Typeface.createFromAsset(mContext.getAssets(), "mitra.ttf");

        }


    }

    List<Item> getData(){
        return mItems;

    }
    void setThemeColor(int colorCode){
        mThemeColor = colorCode;
    }

    // Add a ToDoItem to the adapter
    // Notify observers that the data set has changed

    public void add(Item _item) {
        //TextView dummy = new TextView(mContext);
        //dummy.setText(item);
        mItems.add(_item);
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
        RelativeLayout itemLayout;

        if (convertView == null) {

            itemLayout = (RelativeLayout) LayoutInflater
                    .from(mContext)
                    .inflate(R.layout.log_item, parent, false);
        } else {
            itemLayout = (RelativeLayout) convertView;
        }


         Item item = (Item) getItem(position);
        String Operation = item.Operation;
        String Result = item.Result;
        TextView view;
        String numberTag = item.Tag;



        view = (TextView) itemLayout.findViewById(R.id.constant_name);
        view.setText(Operation);
        view.setTypeface(mDefaultFont);
        view.setTextColor(mThemeColor);

        view = (TextView) itemLayout.findViewById(R.id.constant_number);
        view.setText(Result);
        view.setTypeface(mDefaultFont);

        CheckBox starred = (CheckBox) itemLayout.findViewById(R.id.constant_selected);
        starred.setOnCheckedChangeListener(null);
        starred.setChecked(item.Starred);
        starred.setOnCheckedChangeListener(this);

        if(numberTag.equals("")!= true ){
        view = (TextView) itemLayout.findViewById(R.id.LOG_tag);
        view.setText(numberTag);
        view.setTypeface(mDefaultFont);

            view.setVisibility(View.VISIBLE);
        }
        return itemLayout;

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
         int position = mParentListView.getPositionForView(compoundButton);
        if (position != ListView.INVALID_POSITION) {
            mItems.get(position).Starred = b;
        }
    }
}
