package com.bitloor.ggloor;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitloor.ggloor.broadcast.UpdateData;
import com.bitloor.ggloor.helpers.helperDB.HelperDB;
import com.bitloor.ggloor.model.Matches;
import com.bitloor.ggloor.model.Teams;
import com.bitloor.ggloor.myAdapters.MatchesAdapter;
import com.bitloor.ggloor.rest.AuthTest;
import com.bitloor.ggloor.rest.GetMatches;
import com.bitloor.ggloor.settings.SettingsData;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    HelperDB DB;
    TextView text;
    GetMatches gm;
    ArrayList<Matches> listMatches = new ArrayList<>();
    MatchesAdapter matchesAdapter;
    SQLiteDatabase dbC;
    SwipeRefreshLayout mSwipeRefreshLayout;
    ListView lv;
    int scrollListView = 0;
    int oldSizeList = 0;
    int flagGetData;
    Menu menu;
    public static final int EXTRA_MESSAGE = 0;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        testAuth();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // получим идентификатор выбранного пункта меню
        int id = item.getItemId();
        // Операции для выбранного пункта меню
        switch (id) {
            case R.id.input_acc:
                startActivity(new Intent(this, InputActivity.class));
                return true;
            case R.id.reg_acc:
                startActivity(new Intent(this, RegActivity.class));
                return true;
            case R.id.exit_acc:
                deleteAuth();
                testAuth();
                return true;
            case R.id.room_acc:
                startActivity(new Intent(this, LikesActivity.class));
                return true;
            case R.id.settings_acc:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.ip_edit:
                startActivity(new Intent(this, IPActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DB = new HelperDB(this,"ggdb");
        // подключаемся к БД
        dbC = DB.getWritableDatabase();
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        startService(new Intent(this, UpdateData.class));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                SettingsData.page = 1;
                scrollListView = 0;
                oldSizeList = 0;
                updateMatches();
                Log.d("ggloor_msg", "onRefresh: end refresh ");

            }
        });
        lv = (ListView) findViewById(R.id.listViewMatches);
        lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            int last;
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if(i == 0){
                    if(absListView.getCount() == last && last >9){
                        if(flagGetData == 1){
                            SettingsData.page++;
                            scrollListView = lv.getFirstVisiblePosition();
                           // Log.d("ggloor_msg", "onScrollStateChanged: -------- " + scrollListView);
                            updateMatches();
                        }
                    }
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                last = firstVisibleItem + visibleItemCount;
            }
        });
        updateMatches();
    }
    private  void deleteAuth(){
        dbC.execSQL("delete from Login");
    }
    private void testAuth(){
        Cursor c = dbC.rawQuery("select * from Login", null);
        if(c.moveToFirst()){
            final AuthTest authTest = new AuthTest();
            Handler h = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if(authTest.data != null && authTest.data.equals("1")){
                        menu.findItem(R.id.input_acc).setVisible(false);
                        menu.findItem(R.id.reg_acc).setVisible(false);
                        menu.findItem(R.id.room_acc).setVisible(true);
                        menu.findItem(R.id.settings_acc).setVisible(true);
                        menu.findItem(R.id.exit_acc).setVisible(true);
                    }else {
                        if(authTest.data != null && authTest.data.equals("0")) {
                            deleteAuth();
                        }
                        menu.findItem(R.id.input_acc).setVisible(true);
                        menu.findItem(R.id.reg_acc).setVisible(true);
                        menu.findItem(R.id.room_acc).setVisible(false);
                        menu.findItem(R.id.settings_acc).setVisible(false);
                        menu.findItem(R.id.exit_acc).setVisible(false);
                    }
                }
            };
            authTest.authTest(c.getString(1), this, h);
        }else {
            menu.findItem(R.id.input_acc).setVisible(true);
            menu.findItem(R.id.reg_acc).setVisible(true);
            menu.findItem(R.id.room_acc).setVisible(false);
            menu.findItem(R.id.settings_acc).setVisible(false);
            menu.findItem(R.id.exit_acc).setVisible(false);
        }
        c.close();
    }
    private void insertTeam(Teams team){
        try {
            Cursor c = dbC.rawQuery("select name from Teams where id = ?", new String[]{String.valueOf(team.id)});
           // Log.d("ggloor_msg", "insertTeam: guuuuu-------------");
            if (!c.moveToFirst()) {
                dbC.execSQL("insert into Teams (name, cover, id) values (?,?,?)", new String[]{team.name, team.cover, String.valueOf(team.id)});
            }
            c.close();
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ggloor_error", "insertTeam: " + Arrays.toString(e.getStackTrace()));
        }
    }
    private void deleteMatches(){
        dbC.execSQL("delete from Matches");
    }
    private void insertMatch(Matches match){
        try {
            ContentValues cv = new ContentValues();
            cv.put("id", match.id);
            cv.put("team1", match.team1.id);
            cv.put("team2", match.team2.id);
            //Log.d("ggloor_testData", "insertMatch: " + new SimpleDateFormat().format(match.dateMatch));
            cv.put("dateMatch", match.dateMatch.getTime());
            cv.put("status", match.status);
            cv.put("colGames", match.colGames);
            // вставляем запись и получаем ее ID
            long rowID = dbC.insert("Matches", null, cv);
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ggloor_error", "insertMatch: " + Arrays.toString(e.getStackTrace()));
        }
    }
    private Teams getTeamFromBd(Integer id){
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
    private void selectCacheMatches(){
        try {
            if (SettingsData.page == 1) {
                listMatches = new ArrayList<>();
                Cursor cm = dbC.rawQuery("Select * from Matches order by dateMatch desc", new String[]{});
                boolean flag = cm.moveToFirst();
                while (flag) {
                    Matches match = new Matches();
                    match.id = cm.getInt(0);
                    match.team1 = getTeamFromBd(cm.getInt(1));
                    match.team2 = getTeamFromBd(cm.getInt(2));
                    match.dateMatch = new Date(cm.getLong(3));
                    match.status = cm.getInt(4);
                    match.colGames = cm.getInt(5);
                    listMatches.add(match);
                    flag = cm.moveToNext();
                    Log.d("ggloor_msg", "selectCacheMatches: one match id " + match.id);
                }
                matchesAdapter = new MatchesAdapter(getApplicationContext(), dbC, listMatches, MainActivity.this);
                lv.setAdapter(matchesAdapter);
                if(!cm.moveToFirst()){
                    Toast.makeText(this, "В бд пусто", Toast.LENGTH_SHORT).show();
                }
                cm.close();
                Log.d("ggloor_msg", "selectCacheMatches: end select");

            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("ggloor_error", "selectCacheMatches: " + e.getMessage());
            Log.e("ggloor_error", "selectCacheMatches: " + Arrays.toString(e.getStackTrace()));

        }
    }
    private void updateMatches(){
        try {
            mSwipeRefreshLayout.setRefreshing(true);
            gm = new GetMatches();
            text = (TextView) findViewById(R.id.text);
            Handler h = new Handler() {
                public void handleMessage(android.os.Message msg) {
                    Log.d("ggloor_msg", "handleMessage: handler ex start");
                    if (gm.data != null) {
                        Type type = new TypeToken<List<Matches>>() {
                        }.getType();
                        listMatches = new Gson().fromJson(gm.data, type);
                        flagGetData = 1;
                        deleteMatches();
                        matchesAdapter = new MatchesAdapter(getApplicationContext(), dbC, listMatches, MainActivity.this);
                        if (oldSizeList == listMatches.size()) {
                            Toast.makeText(MainActivity.this, "Все матчи были получены", Toast.LENGTH_SHORT).show();
                            SettingsData.page--;
                        }
                        oldSizeList = listMatches.size();
                        // ArrayAdapter<Matches> listAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, listMatches);
                        lv.setAdapter(matchesAdapter);
                        Log.d("ggloor_msg", "handleMessage: " + scrollListView);
                        lv.setSelection(scrollListView);
                        for (int i = 0; i < listMatches.size() && i < 10; i++) {
                            Matches match = listMatches.get(i);
                            insertTeam(match.team1);
                            insertTeam(match.team2);
                            insertMatch(match);
                        }
                    } else {
                        flagGetData = 2;
                        selectCacheMatches();
                        Toast.makeText(MainActivity.this, "Проверьте соединение с интернетом, либо сервис не доступен", Toast.LENGTH_SHORT).show();
                    }
                    mSwipeRefreshLayout.setRefreshing(false);

                }
            };
            gm.getMatches(SettingsData.page, this, h);
        }catch (Exception e){
            mSwipeRefreshLayout.setRefreshing(false);
            Toast.makeText(this, "Error updateMatches", Toast.LENGTH_SHORT).show();
        }
    }
}