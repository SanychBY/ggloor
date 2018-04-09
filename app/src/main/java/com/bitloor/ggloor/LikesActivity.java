package com.bitloor.ggloor;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.bitloor.ggloor.helperDB.HelperDB;
import com.bitloor.ggloor.model.Likes;
import com.bitloor.ggloor.model.Matches;
import com.bitloor.ggloor.myAdapters.LikesAdapter;
import com.bitloor.ggloor.rest.GetLikes;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LikesActivity extends AppCompatActivity {
    SwipeRefreshLayout mSwipeRefreshLayout;
    HelperDB DB;
    ArrayList<Likes> listLikes = new ArrayList<>();
    SQLiteDatabase dbC;
    LikesAdapter likesAdapter;
    ListView lv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);
        DB = new HelperDB(this,"ggdb");
        // подключаемся к БД
        dbC = DB.getWritableDatabase();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayoutLikes);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                updateLikes();
            }
        });
        lv = (ListView)findViewById(R.id.listViewLikes);

        updateLikes();
    }
    public void updateLikes(){
        try {
            mSwipeRefreshLayout.setRefreshing(true);
            Cursor c = dbC.rawQuery("select * from Login", null);
            if (c.moveToFirst()) {
                final GetLikes getLikes = new GetLikes();
                getLikes.getLikes(c.getString(1), this, new Handler() {
                    @Override
                    public void handleMessage(Message msg) {
                        mSwipeRefreshLayout.setRefreshing(false);
                        if(getLikes.data == null){
                            Toast.makeText(LikesActivity.this, "Сервис недоступен. Проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        if (!getLikes.data.equals("0")) {
                            Type type = new TypeToken<List<Likes>>() {
                            }.getType();
                            listLikes = new Gson().fromJson(getLikes.data, type);
                            dbC.execSQL("delete from Likes");
                            for(int i = 0; i < listLikes.size() ; i++){
                                dbC.execSQL("insert into Likes (id, team) values (?,?)",
                                        new String[]{String.valueOf(listLikes.get(i).id), String.valueOf(listLikes.get(i).team.id)});
                            }
                            likesAdapter = new LikesAdapter(getApplicationContext(),listLikes, LikesActivity.this, dbC);
                            lv.setAdapter(likesAdapter);
                        } else {
                            Toast.makeText(LikesActivity.this, "Error. Invalid key", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
            }
            c.close();
        }catch (Exception e){
            mSwipeRefreshLayout.setRefreshing(false);
            Log.e("ggloor_error", "updateLikes: " + e.getMessage());
            Toast.makeText(this, "Error updateLikes", Toast.LENGTH_SHORT).show();
        }
    }
}
