package com.asap.mindfulness.SQLite;

import android.content.ContentValues;


public class TableColumnClass {

    public String  Type        =   "";         //While necessary at declaration, any type may be stored
    public String  ColName     =   "";         //This will be the name of the column;
    public String  Flags       = "";

    /*
    ALLOWABLE FLAG TYPES:
    NOT NULL
    UNIQUE
    CHECK
    PRIMARY KEY
    FOREIGN KEY
    AUTO INCREMENT
     */


    public TableColumnClass(ContentValues input)
    {
        Type    = (String)input.get("type");
        ColName = (String)input.get("name");
        Flags   = (String)input.get("flags");
    }

    public String builder()                        //When called, this method returns a string that can
    {                                               //      be added to a table builder SQLite string
        String returnable = ColName + " " + Type + " " + Flags;
        return returnable;
    }
}
