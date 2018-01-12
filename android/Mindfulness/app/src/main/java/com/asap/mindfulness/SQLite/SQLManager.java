package com.asap.mindfulness.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
SQL Manager Class Module
ASAP Media Services
Author: Jason Dignan

Description: The SQLManager is the direct interface for managing the Android's SQL database system.
    It is designed to create an easy to use method calling system to execute SQL commands and to perform
    complex actions.
 */

public class SQLManager
{
    Map<String, DatabaseClass> databasestorage = new HashMap<String, DatabaseClass>();      //Stores database objects with an associated string
    List<TableColumnClass> constructor = new ArrayList<TableColumnClass>();
    Context passedContext;                                              //Stores the context that is passed on to other methods
    TableClass tc;                                                      //creates a table class, which is used to modify tables

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// CONSTRUCTOR
    /*
    Constructor for this class
    Takes a context object passed from the activity that calls on this class
     */

    public SQLManager(Context context)
    {
        passedContext=context;          //grabs the context passed
        tc = new TableClass();          //Instantiates a table class object
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// CREATE DATABASE
    /*
    Creates a database
    Takes a string for what the database will be named

    Creates a databaseclass object that is called on to perform specific tasks associated with that database
     */
    public void createDatabase(String dbname)
    {
        databasestorage.put(dbname, new DatabaseClass(passedContext,dbname));   //stores a newly created database in our hashmap
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// DELETE DATABASE
    /*
    Deletes a database.
    Takes a string that identifies which database to delete
     */
    public void deleteDatabase(String dbname)
    {
        passedContext.deleteDatabase(dbname);
        try
        {
            databasestorage.remove(dbname);
        }
        catch(Exception e)
        {
            return;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// CREATE COLUMN
    /*
    Creates a Column to be used in the creation of a table
    Takes a String name of the column, type of data the column will hold, a boolean that is true if it is the primary key, and a boolean that if true autoincriments the values

    OVERLOADED FUNCTION
    Includes two versions - One accepts a string variable of flags. These would be items such as autoincriment, primary key and the sort. The other method does not require this.
     */
    public void createColumn(String name, String type, String flags)
    {
        ContentValues column = new ContentValues();         //creates a ContentValue item that will store our values
        column.put("name", name);                           //Populates the values we need in order to create the column
        column.put("type", type);
        column.put("flags", flags);
        constructor.add(new TableColumnClass(column));      //adds a tablecolumnclass object to our constructor array and passes our Content Value to it.
    }

    public void createColumn(String name, String type)
    {
        ContentValues column = new ContentValues();         //creates a ContentValue item that will store our values
        column.put("name", name);                           //Populates the values we need in order to create the column
        column.put("type", type);
        column.put("flags", "");                            //While we don't need this for this version, we still populate a field for when we construct the table
        constructor.add(new TableColumnClass(column));      //adds a tablecolumnclass object to our constructor array and passes our Content Value to it.
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// CREATE TABLE
    /*
    Creates a table
    Takes the string name of the table, and the string name of the database. Columns must be predefined prior to invoking this method.
     */
    public void createTable(String db, String tableName)
    {
        int size = constructor.size();                                  //captures how many columns are stored in our constructor array
        TableColumnClass exportable[] = new TableColumnClass[size];     //Creates an array of tablecolumn class objects to the size of items we have in our constructor
        for(int i = 0; i < size; i++)
        {
            exportable[i] = constructor.get(i);                         //loops through our constructor array and stores it in to an array.
        }

        tc.createTable(tableName, exportable, databasestorage.get(db).getSQLiteDatabase()); //tells the tableclass object to create a table with the values collected
        constructor= null;                                              //Clears the constructor in case we need to make another table with different stuff.
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// DELETE TABLE
    /*
    Deletes a table
    Takes a string of the table to be deleted, and a string of the database the table resides in
     */
    public void deleteTable(String tableName, String db)
    {
        tc.deleteTable(tableName, databasestorage.get(db).getSQLiteDatabase());
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// UPDATE TABLE
    /*
    Updates a table
    Takes a string database name, string table name, comma seperated string of columns, comma seperated string of values, and a string that defines where the action takes place
     */
    public void updateTable(String db, String tableName, String colName, String newVals, String CompareCol, String CompareVal)
    {
        ContentValues values = new ContentValues();                     //creates a ContentValues object
        List<String> columnNames = Arrays.asList(colName.trim().split(","));   //creates an arraylist of the column names list by splitting it by commas
        List<String> newValues  = Arrays.asList(newVals.trim().split(","));    //creates an arraylist of the new values list by splitting it by commas
        for(int i = 0; i < columnNames.size(); i++)                     //Loops through the lists, and assembles a key/value ContentValue for each paring
            values.put(columnNames.get(i), newValues.get(i));
        Log.d("TELLMESTUFF", (String) values.toString());
        databasestorage.get(db).getSQLiteDatabase().update(tableName, values, CompareCol + "= ?", new String[]{CompareVal}); //Pulls the database out of the database storage, and executes the update function
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////// INSERT ROW
    /*
    Insert Row
    Takes a string of database name, comma seperated string of column names, comma seperated string of values, and string of the table's name
     */
    public void insertRow(String db, String tabName, String colName, String colval)
    {
        ContentValues values = new ContentValues();                             //creates a contentValues object
        List<String> columnList = Arrays.asList(colName.split(","));            //Creates an arraylist of the column names by splitting it by commas
        List<String> columnValues = Arrays.asList(colval.split(","));           //Creates an arraylist of the new values by splitting it by commas
        for(int i = 0; i < columnList.size(); i++)                              //Loops through the lists, and assembles a key/value content value for each pairing
            values.put(columnList.get(i), columnValues.get(i));
        try {
            databasestorage.get(db).getSQLiteDatabase().insert(tabName,null,values);
        }
        catch(Exception e)
        {
            Log.d("OMG AN ERROR", e.toString());
        }
    }

    public void deleteRow(String db, String tabname, String where)
    {
        databasestorage.get(db).getSQLiteDatabase().delete(tabname, where, null);

    }
}
