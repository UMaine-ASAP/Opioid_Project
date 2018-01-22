package com.asap.mindfulness.SQLite;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

public class TableClass
{
    boolean Indexed = true; //If false, will add "WITHOUT ROWID" to end of table creation string

    TableClass()
    {

    }
    //pbulic void createTable()

    public void createTable(String tableName, TableColumnClass col[], SQLiteDatabase db)
    {
        //Implement notnull, unique, primary key, foreign key, check, default, index, auto-increment
        // Cursor result = db.query(tableName, colName, null, null, null, null, null, null);
        //Cursor result = db.rawQuery("SELECT" + colName[0] +" FROM " + tableName, null);
        //if(result.getCount() < 1) {
            StringBuilder query = new StringBuilder("CREATE TABLE \"" + tableName + "\" (");
            int listLength = col.length;
            for (int i = 0; i < listLength; i++) {
                query.append(col[i].builder());
                if (i < listLength-1) {
                    query.append(", ");
                } else
                    query.append(")");
            }
            try
            {
                db.execSQL(query.toString());
            }
            catch (SQLiteException e)
            {
                Log.e("TableClass", e.getMessage());
                return;
            }
    }

    public void deleteTable(String tableName, SQLiteDatabase db)
    {
        db.execSQL("DROP TABLE IF EXISTS "+ tableName);
    }
}
