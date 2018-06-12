package com.bitloor.ggloor.helpers.helperDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by ssaan on 21.05.2017.
 **/

public class HelperDB extends SQLiteOpenHelper {
    private Context context;
    public HelperDB(Context context, String name) {
        super(context, name, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d("log_ggloor", "onCreate: table create start");
            db.execSQL("PRAGMA foreign_keys=ON;");
            db.execSQL("create table KeyLogger ("
                    + "id integer primary key autoincrement, "
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
            db.execSQL("create table Settings (" +
                    "id integer primary key autoincrement," +
                    " vk_id integer, " +
                    "vk_access_token text, " +
                    "frequency integer, " +
                    "vk_notify integer" +
                    ");");

            ContentValues values = new ContentValues();
            values.put("vk_id", 0);
            values.put("vk_access_token", "0");
            values.put("frequency", 600);
            values.put("vk_notify", 1);

            db.insert("Settings", null, values);
        }catch (Exception err){
            Log.e(" HelperDB", "onCreate: ", err);
            Toast.makeText(context, "error create database", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConfigure(SQLiteDatabase db){
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
