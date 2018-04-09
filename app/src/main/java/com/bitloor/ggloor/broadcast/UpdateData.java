package com.bitloor.ggloor.broadcast;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import com.bitloor.ggloor.helperDB.HelperDB;

public class UpdateData extends Service {

    HelperDB DB;
    SQLiteDatabase dbC;
    private AlarmHelper alarmHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        DB = new HelperDB(this,"ggdb");
        // подключаемся к БД
        dbC = DB.getWritableDatabase();
        alarmHelper = new AlarmHelper( this.getApplicationContext());
        alarmHelper.Start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ggloor_msg", "onDestroy : service destroy");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ggloor_msg", "onStartCommand: service is started");
        return super.onStartCommand(intent, flags, startId);
    }

    public UpdateData() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
