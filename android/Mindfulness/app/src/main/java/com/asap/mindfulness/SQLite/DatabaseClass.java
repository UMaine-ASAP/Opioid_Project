package com.asap.mindfulness.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JadnarRoverdin on 3/16/17.
 */

public final class DatabaseClass extends SQLiteOpenHelper
{

    SQLiteDatabase db;
    String filename;
    String currentTable;



    public DatabaseClass(Context context, String name)      //Constructor for this class
    {
        super(context, name, null, 1);                      //creates the database
        filename = name;                                    //Stores the filename
        db = this.getWritableDatabase();                    //Stores the database object
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {

    }

    public String getName()                                 //Returns the string of the name of the database
    {
        return filename;
    }

    public SQLiteDatabase getSQLiteDatabase()               //Returns a database object
    {
        return db;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + currentTable);
        onCreate(db);
    }
}
