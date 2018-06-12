package com.bitloor.ggloor;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.bitloor.ggloor.helpers.helperDB.HelperDB;
import com.bitloor.ggloor.model.Settings;
import com.bitloor.ggloor.rest.GetSettings;
import com.bitloor.ggloor.rest.SaveSettingsData;
import com.bitloor.ggloor.rest.SaveVkData;
import com.google.gson.Gson;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import pl.droidsonroids.gif.GifImageView;

public class SettingsActivity extends AppCompatActivity {
    SQLiteDatabase dbC;
    HelperDB DB;
    GifImageView gifImageView;
    ScrollView scrollView;
    RadioGroup radioGroup;
    RadioButton [] rbs = new RadioButton[4];
    private static final String[] sMyScope = new String[]{
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.OFFLINE
    };

    private void updateSettings(int vk_id, String vk_access_token, int f, boolean vk_notify ){
        ContentValues cv = new ContentValues();
        if(vk_id != 0){
            cv.put("vk_id", vk_id);
        }
        if(vk_access_token != null){
            cv.put("vk_access_token", vk_access_token);
        }
        if(f != 0){
            cv.put("frequency",f);
        }
        cv.put("vk_notify", vk_notify );
        dbC.update("Settings", cv, "id = 1", null);
    }
    @SuppressLint("HandlerLeak")
    private void getSettings(){
        try {
            final GetSettings getSettings = new GetSettings();
            Cursor c = dbC.rawQuery("select * from Login", null);
            if (c.moveToFirst()) {
                getSettings.getSettings(c.getString(1), this, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        if (getSettings.data != null && !getSettings.data.equals("")) {
                            gifImageView.setVisibility(View.GONE);
                            scrollView.setVisibility(View.VISIBLE);
                            Settings settings = new Gson().fromJson(getSettings.data, Settings.class);
                            switch (settings.frequency) {
                                case 600: {
                                    rbs[0].setChecked(true);
                                    break;
                                }

                                case 3600: {
                                    rbs[1].setChecked(true);
                                    break;
                                }

                                case 43200: {
                                    rbs[2].setChecked(true);
                                    break;
                                }

                                case 86400: {
                                    rbs[3].setChecked(true);
                                    break;
                                }
                            }
                            updateSettings(settings.vk_id,settings.vk_access_token,settings.frequency, settings.vk_notify );
                        } else {
                            gifImageView.setVisibility(View.GONE);
                            Toast.makeText(SettingsActivity.this, "Не удалось получить настройки", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
            c.close();
        }catch (Exception e){
            Log.e("getSettings", "getSettings: ",e );
            Toast.makeText(SettingsActivity.this, "Не удалось получить настройки", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        DB = new HelperDB(this,"ggdb");
        // подключаемся к БД
        dbC = DB.getWritableDatabase();
        gifImageView = (GifImageView)findViewById(R.id.gif_load);
        scrollView = (ScrollView)findViewById(R.id.myview);
        radioGroup = (RadioGroup)findViewById(R.id.rg);
        rbs[0] = (RadioButton) findViewById(R.id.rb10min);
        rbs[1] = (RadioButton)findViewById(R.id.rb1h);
        rbs[2] = (RadioButton)findViewById(R.id.rb12h);
        rbs[3] = (RadioButton)findViewById(R.id.rb1d);

        getSettings();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("HandlerLeak")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                sendSettingsData(i);
            }
        });
    }
    private void sendSettingsData(int i){

                int f = 600;
                switch (i) {
                    case -1:
                        f = 600;
                        break;
                    case R.id.rb10min:
                        f = 600;
                        break;
                    case R.id.rb1h:
                        f = 3600;
                        break;
                    case R.id.rb12h:
                        f = 43200;
                        break;
                    case R.id.rb1d:
                        f = 86400;
                        break;

                    default:
                        break;
                }
                final SaveSettingsData saveSettingsData = new SaveSettingsData();
                Cursor c = dbC.rawQuery("select * from Login", null);
                if (c.moveToFirst()) {
                    final int finalF = f;
                    saveSettingsData.saveSettingsData(f,true, c.getString(1), this, new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            if(saveSettingsData.data != null && saveSettingsData.data.equals("ok")){
                                updateSettings(0,null, finalF,true);
                                Toast.makeText(SettingsActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText(SettingsActivity.this, "Ошибка, попробуйте еще раз", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                c.close();

    }

    @SuppressLint("HandlerLeak")
    private void sendVKData(){
        final SaveVkData saveVkData = new SaveVkData();
        Cursor c = dbC.rawQuery("select * from Login", null);
        if (c.moveToFirst()) {
            saveVkData.saveVKdata(Integer.valueOf(VKAccessToken.currentToken().userId), VKAccessToken.currentToken().accessToken,c.getString(1), this, new Handler(){
                        @Override
                        public void handleMessage(Message msg) {
                            if(!saveVkData.data.equals("ok")){
                                Toast.makeText(SettingsActivity.this, "Не удалось сохранить. Попробуйте еще раз", Toast.LENGTH_SHORT).show();
                                updateSettings(Integer.valueOf(VKAccessToken.USER_ID), VKAccessToken.currentToken().accessToken, 0, true);
                            }else {
                                Toast.makeText(SettingsActivity.this, "Сохранено", Toast.LENGTH_SHORT).show();
                            }
                        }

            });
        }
        c.close();
    }

    public void VKSignIn(View view) {
        VKSdk.login(this, sMyScope);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Log.w("ggloor", "onResult: " + res.accessToken );
// Пользователь успешно авторизовался
                Toast.makeText(SettingsActivity.this, "Пользователь успешно авторизовался", Toast.LENGTH_SHORT).show();
                sendVKData();
            }
            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
