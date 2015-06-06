package com.sepidsa.fortytwocalculator;


import android.app.ExpandableListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;

import java.util.ArrayList;

public class HelpActivity extends ExpandableListActivity{

    private ArrayList<String> parentItems = new ArrayList<String>();
    private ArrayList<Object> childItems = new ArrayList<Object>();

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // this is not really  necessary as ExpandableListActivity contains an ExpandableList
        //setContentView(R.layout.activity_help);

        ExpandableListView expandableList = getExpandableListView(); // you can use (ExpandableListView) findViewById(R.id.list)

        expandableList.setDividerHeight(2);
        expandableList.setGroupIndicator(null);
        expandableList.setClickable(true);

        setGroupParents();
        setChildData();

        HelpExpandableAdapter adapter = new HelpExpandableAdapter(parentItems, childItems);

        adapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        expandableList.setAdapter(adapter);
        expandableList.setOnChildClickListener(this);
    }

    public void setGroupParents() {
        String [] helpTopicsArray = getResources().getStringArray(R.array.help_topics);
        for (int index = 0; index < helpTopicsArray.length;index ++){
           parentItems.add(helpTopicsArray[index]);
        }
    }

    public void setChildData() {

        // Android
        ArrayList<String> child;

        String [] helpSubTopicsArray = getResources().getStringArray(R.array.help_sub_topics);
        for (int index = 0; index < helpSubTopicsArray.length;index ++){
            child = new ArrayList<String>();
            child.add(helpSubTopicsArray[index]);

            childItems.add(child);
        }
//        child.add("Core");
//        child.add("Games");
//        childItems.add(child);
//
//        // Core Java
//        child = new ArrayList<String>();
//        child.add("Apache");
//        child.add("Applet");
//        child.add("AspectJ");
//        child.add("Beans");
//        child.add("Crypto");
//        childItems.add(child);
//
//        // Desktop Java
//        child = new ArrayList<String>();
//        child.add("Accessibility");
//        child.add("AWT");
//        child.add("ImageIO");
//        child.add("Print");
//        childItems.add(child);
//
//        // Enterprise Java
//        child = new ArrayList<String>();
//        child.add("EJB3");
//        child.add("GWT");
//        child.add("Hibernate");
//        child.add("JSP");
//        childItems.add(child);
    }

}