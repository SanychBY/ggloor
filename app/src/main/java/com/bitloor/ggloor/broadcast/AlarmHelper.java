package com.bitloor.ggloor.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.bitloor.ggloor.helpers.helperDB.HelperDB;
import com.bitloor.ggloor.model.Likes;
import com.bitloor.ggloor.model.Matches;
import com.bitloor.ggloor.model.Teams;
import com.bitloor.ggloor.rest.GetLikes;
import com.bitloor.ggloor.rest.GetMatches;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * Created by ssaan on 29.05.2017.
 **/

public class AlarmHelper extends BroadcastReceiver {
    Context ctx;
    AlarmManager am;

    public AlarmHelper() {
    }

    public AlarmHelper(Context ctx) {
        this.ctx = ctx;
    }

    private void insertTeam(Teams team, SQLiteDatabase dbC){
        try {
            Cursor c = dbC.rawQuery("select name from Teams where id = ?", new String[]{String.valueOf(team.id)});
            // Log.d("ggloor_msg", "insertTeam: guuuuu-------------");
            if (!c.moveToFirst()) {
                dbC.execSQL("insert into Teams (name, cover, id) values (?,?,?)", new String[]{team.name, team.cover, String.valueOf(team.id)});
            }
            c.close();
        }catch (Exception e){
            Log.e("ggloor_error", "insertTeam: " + Arrays.toString(e.getStackTrace()));
        }
    }

    private void insertMatch(Matches match, SQLiteDatabase dbC){
        try {
            insertTeam(match.team1, dbC);
            insertTeam(match.team2, dbC);
            ContentValues cv = new ContentValues();
            cv.put("id", match.id);
            cv.put("team1", match.team1.id);
            cv.put("team2", match.team2.id);
            Log.d("ggloor_testData", "insertMatch: " + new SimpleDateFormat().format(match.dateMatch));
            cv.put("dateMatch", match.dateMatch.getTime());
            cv.put("status", match.status);
            cv.put("colGames", match.colGames);
            // вставляем запись и получаем ее ID
            long rowID = dbC.insert("Matches", null, cv);
        }catch (Exception e){
           // Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ggloor_error", "insertMatch: " + Arrays.toString(e.getStackTrace()));
        }
    }

    ArrayList<Matches> listMatches;
    ArrayList<Likes> listLikes;

    @Override
    public void onReceive(Context context, final Intent intent) {
        HelperDB DB = new HelperDB(context,"ggdb");
        // подключаемся к БД
        final SQLiteDatabase dbC = DB.getWritableDatabase();
        Log.d("ggloor_msg", "onReceive: start");
        final GetMatches getMatches =  new GetMatches();
        getMatches.getMatches(1,context, new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if (getMatches.data != null) {
                    Type type = new TypeToken<List<Matches>>() {
                    }.getType();
                    listMatches = new Gson().fromJson(getMatches.data, type);
                    Log.d("ggloor_msg", "handleMessage: broadcast get matches data success");
                    dbC.execSQL("delete from Matches");
                    for(int i = 0; i < listMatches.size(); i++){
                        insertMatch(listMatches.get(i), dbC);
                    }
                }
            }
        });

        final GetLikes getLikes = new GetLikes();
        Cursor c = dbC.rawQuery("Select * from Login", null);
        if(c.moveToFirst()) {
            getLikes.getLikes(c.getString(1), context, new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if(getLikes.data == null){
                        Log.d("ggloor_msg", "handleMessage: likes date = null");
                        return;
                    }
                    if (!getLikes.data.equals("0")) {
                        Type type = new TypeToken<List<Likes>>() {
                        }.getType();
                        listLikes = new Gson().fromJson(getLikes.data, type);
                        Log.d("ggloor_msg", "handleMessage: likes date get success");
                        dbC.execSQL("delete from Likes");
                        for(int i = 0; i < listLikes.size() ; i++){
                            dbC.execSQL("insert into Likes (id, team) values (?,?)",
                                    new String[]{String.valueOf(listLikes.get(i).id), String.valueOf(listLikes.get(i).team.id)});
                        }
                    } else {
                        Log.d("ggloor_msg", "handleMessage: invalid key");
                    }
                }
            });
        }
        c.close();
        /*try {
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        testNotify(dbC, context);
    }

    private Teams getTeamFromBd(Integer id, SQLiteDatabase dbC){
        Teams team1= new Teams();
        Cursor ct1 = dbC.rawQuery("Select * from Teams where id = ?", new String[]{String.valueOf(id)});
        if (!ct1.moveToFirst()) {
            return null;
        } else {
            team1.id = ct1.getInt(0);
            team1.name = ct1.getString(1);
            team1.cover = ct1.getString(2);
        }
        ct1.close();
        Log.d("ggloor_msg", "getTeamFromBd: one team id " + team1.id);
        return team1;
    }
    private boolean LikeOrNot(Matches match, Teams team, SQLiteDatabase sqLiteDatabase, Context context){
        Cursor c2 = sqLiteDatabase.rawQuery("select * from Likes where team = ?", new String[]{String.valueOf(team.id)});
        if(c2.moveToFirst()){
            Cursor c3 = sqLiteDatabase.rawQuery("select * from Notify where idMatch = ?", new String[]{String.valueOf(match.id)});
            if(!c3.moveToFirst()){
                NotificationHelper notificationHelper = new NotificationHelper();
                notificationHelper.notify(match, team, context);
                sqLiteDatabase.execSQL("insert into Notify (idMatch) values (?)", new String[]{String.valueOf(match.id)});
                Log.d("ggloor_msg", "LikeOrNot: TRUE");
                return true;
            }
            c3.close();
            Log.d("ggloor_msg", "LikeOrNot: FALSE");
        }
        c2.close();
        return false;
    }
    private void testNotify(SQLiteDatabase sqLiteDatabase, Context context){
         Cursor cm = sqLiteDatabase.rawQuery("select * from Matches", null);
        if (cm.moveToFirst()){
            boolean flag = cm.moveToFirst();
            while (flag) {
                Matches match = new Matches();
                match.id = cm.getInt(0);
                match.team1 = getTeamFromBd(cm.getInt(1), sqLiteDatabase);
                match.team2 = getTeamFromBd(cm.getInt(2), sqLiteDatabase);
                match.dateMatch = new Date(cm.getLong(3));
                match.status = cm.getInt(4);
                match.colGames = cm.getInt(5);
               // listMatches.add(match);
                flag = cm.moveToNext();
                long t = match.dateMatch.getTime() - System.currentTimeMillis();
                Log.d("ggloor_msg", "testNotify: " + t + " " + (1000 * 60 *10));
                if(t < 1000 * 60 *10 && t > 0){
                    if(!LikeOrNot(match, match.team1, sqLiteDatabase, context)){
                        LikeOrNot(match, match.team2, sqLiteDatabase, context);
                    }
                }
            }
        }
        cm.close();
    }

    public void Start(){
        am = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(ctx, AlarmHelper.class);
        intent.putExtra("onetime", Boolean.FALSE);//Задаем параметр интента
        PendingIntent pi= PendingIntent.getBroadcast(ctx,0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 60, pi);
        Log.d("ggloor_msg", "Start: alarm start");
    }
    public void cancel(){
        Intent intent=new Intent(ctx, AlarmHelper.class);
        PendingIntent sender= PendingIntent.getBroadcast(ctx,0, intent,0);
        AlarmManager alarmManager=(AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);//Отменяем будильник, связанный с интентом данного класса
    }
}
