package com.sepidsa.calculator;

import java.io.Serializable;

/**
 * Created by ehsanparhizkar on 4/10/15.
 */
public class Item implements Serializable {


    String Operation;
    String Result;
    boolean Starred ;
    String Tag;

    Item (){
        Operation = "";
        Result = "";
        Starred = false;
        Tag = "";
    }
    Item(String _operation , String _description, boolean _starred, String _tag){

        Operation = _operation;
        Result = _description;
        Starred = _starred;
        Tag = _tag;
    }

}
