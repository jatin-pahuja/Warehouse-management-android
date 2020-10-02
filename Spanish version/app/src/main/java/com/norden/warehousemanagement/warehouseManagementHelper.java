package com.norden.warehousemanagement;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class warehouseManagementHelper extends SQLiteOpenHelper {

    // database version
    private static final int database_VERSION = 2;

    // database name
    private static final String database_NAME = "warehouseManagementDataBase";

    private String CREATE_WAREHOUSEMANAGEMENT =
            "CREATE TABLE warehouseManagement ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "itemCode TEXT," +
                    "description TEXT," +
                    "pvp INTEGER," +
                    "stock TEXT)";
    private String CREATE_MOVEMENT =
            "CREATE TABLE movement ( _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "itemCode TEXT," +
                    "date TEXT," +
                    "quantity INTEGER," +
                    "type TEXT,"+
                    "warehouseManagementId INTEGER," +
                    "FOREIGN KEY (warehouseManagementId) REFERENCES warehouseManagement (_id) ON DELETE CASCADE)";

    public warehouseManagementHelper(Context context) {
        super(context, database_NAME, null, database_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WAREHOUSEMANAGEMENT);
        db.execSQL(CREATE_MOVEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL(CREATE_MOVEMENT);
        }
    }

}
