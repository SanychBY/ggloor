package com.bitloor.ggloor;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bitloor.ggloor.helpers.helperDB.HelperDB;
import com.bitloor.ggloor.rest.Input;

import java.text.SimpleDateFormat;
import java.util.Date;

import pl.droidsonroids.gif.GifImageView;

public class InputActivity extends AppCompatActivity {
    HelperDB DB;
    TextView nickText;
    TextView passwordText;
    SQLiteDatabase dbC;
    GifImageView gif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        nickText = (TextView)findViewById(R.id.input_nick);
        passwordText = (TextView)findViewById(R.id.input_password);
        gif = (GifImageView)findViewById(R.id.gif_load);
        DB = new HelperDB(this,"ggdb");
        // подключаемся к БД
        dbC = DB.getWritableDatabase();
    }

    public void onInputGo(View view) {
        gif.setVisibility(View.VISIBLE);
        Log.d("ggloor_msg", "onInputGo: ef");
        final Input input = new Input();
        Handler h = new Handler(){
            public void handleMessage(android.os.Message msg){
                gif.setVisibility(View.GONE);
                Log.d("ggloor_msg", "handleMessage: result " + input.data);
                if(input.data != null){
                    if(input.data.equals("0")){
                        Toast.makeText(InputActivity.this, "Неверное имя пользователя или пароль", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(input.data.equals("1")){
                        Toast.makeText(InputActivity.this, "Аккаунт не активирован", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    dbC.execSQL("insert into Login (key, dateIn) values (?,?)", new String[]{input.data,  new SimpleDateFormat().format(new Date())});
                    Toast.makeText(InputActivity.this, "Успешный вход", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else {
                    Toast.makeText(InputActivity.this, "Сервис временно не доступен", Toast.LENGTH_SHORT).show();
                }
            }
        };
        input.input(nickText.getText().toString(),passwordText.getText().toString(), this, h);
        //input.exInput("nick","rtnxeg1012");
    }
}
