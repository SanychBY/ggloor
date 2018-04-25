package com.bitloor.ggloor.helpers.helperDB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by ssaan on 21.05.2017.
 **/

public class HelperDB extends SQLiteOpenHelper {
    public HelperDB(Context context, String name) {
        super(context, name, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("log_ggloor", "onCreate: table create");
        db.execSQL("PRAGMA foreign_keys=ON;");
        db.execSQL("create table KeyLogger ("
                + "id integer primary key autoincrement,"
                + "key text);");
        db.execSQL("create table Teams ("
                + "id integer primary key autoincrement,"
                + "name text," +
                " cover text);");
        db.execSQL("create table Matches ("
                + "id integer primary key autoincrement,"
                + " team1 integer," +
                " team2 integer," +
                " dateMatch long," +
                " status integer," +
                " colGames integer," +
                " FOREIGN KEY(team1) REFERENCES Teams(id) ON DELETE CASCADE," +
                " FOREIGN KEY(team2) REFERENCES Teams(id) ON DELETE CASCADE);");
        db.execSQL("create table Login (" +
                "id integer primary key autoincrement," +
                " key Text, dateIn Text);");
        db.execSQL("create table Likes(" +
        "id integer primary key autoincrement," +
        " team integer);");
        db.execSQL("create table Notify (" +
                "id integer primary key autoincrement," +
                " idMatch integer);");

    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
