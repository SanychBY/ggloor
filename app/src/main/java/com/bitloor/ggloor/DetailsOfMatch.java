package com.bitloor.ggloor;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitloor.ggloor.helpers.helperDB.HelperDB;
import com.bitloor.ggloor.helpers.listViewScrollHelper.ListViewScrollHelper;
import com.bitloor.ggloor.model.Comments;
import com.bitloor.ggloor.model.Game;
import com.bitloor.ggloor.myAdapters.CommentsAdapter;
import com.bitloor.ggloor.rest.CommentSend;
import com.bitloor.ggloor.rest.GetComments;
import com.bitloor.ggloor.rest.GetMatchDetails;
import com.bitloor.ggloor.settings.SettingsData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class DetailsOfMatch extends AppCompatActivity {
    GetMatchDetails getMatchDetails;
    GetComments getComments;
    ArrayList<Game> games = null;
    ArrayList<Comments> comments = null;
    ImageView img1;
    ImageView img2;
    TextView gameNotStart;
    TextView gameIsOn;
    TabLayout tabLayout;
    LinearLayout scoreLayout;
    Button watchStreamButton;
    Button watchRecordingMatch;
    TextView score1;
    TextView score2;
    EditText commentEditText;
    int matchId = 0;
    int tabSelected = 0;
    HelperDB DB;
    SQLiteDatabase dbC;
    ListView commentsListView;

    private void loadImage(String nameImg, ImageView imageView){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .cacheInMemory()
                .cacheOnDisc()
                .build();
        imageLoader.displayImage(SettingsData.SITE_URL + "/static/images/teamcover/" + nameImg, imageView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {

            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });
    }



    private void updatemMatchDetails(){
        img1 = (ImageView)findViewById(R.id.img_team1);
        img2 = (ImageView)findViewById(R.id.img_team2);
        gameNotStart = (TextView)findViewById(R.id.gameNotStart);
        gameIsOn = (TextView)findViewById(R.id.gameIsOn);
        scoreLayout = (LinearLayout) findViewById(R.id.scoreLayout);
        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        watchStreamButton = (Button) findViewById(R.id.watchStreamButton);
        watchRecordingMatch = (Button) findViewById(R.id.watchRecordingMatch);
        score1 = (TextView)findViewById(R.id.score1);
        score2 = (TextView)findViewById(R.id.score2);
        getMatchDetails = new GetMatchDetails();
        @SuppressLint("HandlerLeak") Handler h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                Type listType = new TypeToken<ArrayList<Game>>() {
                }.getType();
                if(getMatchDetails.data != null) {
                    games = new Gson().fromJson(getMatchDetails.data, listType);
                    if (games != null && games.size() != 0) {
                        for (int i = 0; i < games.size(); i++) {
                            tabLayout.addTab(tabLayout.newTab().setText("Игра " + (i + 1)));
                        }
                        loadImage(games.get(0).match.team1.cover, img1);
                        loadImage(games.get(0).match.team2.cover, img2);
                        TextView dataTeam1 = (TextView) findViewById(R.id.data_team1);
                        dataTeam1.setText(String.valueOf(games.get(0).match.team1.id));
                        TextView dataTeam2 = (TextView) findViewById(R.id.data_team2);
                        dataTeam2.setText(String.valueOf(games.get(0).match.team2.id));
                        TextView textDataTime = (TextView) findViewById(R.id.MatchesDataTime);
                        TextView textDataColGame = (TextView) findViewById(R.id.MatchesDataColGame);
                        TextView textDataActiv = (TextView) findViewById(R.id.MatchesDataActiv);
                        textDataTime.setText(games.get(0).match.dateMatch.getHours() + ":" + games.get(0).match.dateMatch.getMinutes() + " " + games.get(0).match.dateMatch.getDate()
                                + "." + (games.get(0).match.dateMatch.getMonth() + 1) + "." + (games.get(0).match.dateMatch.getYear() + 1900));
                        textDataColGame.setText("BO " + games.get(0).match.colGames);
                        if (games.get(0).match.dateMatch.getTime() < new Date().getTime() && games.get(0).match.status == null) {
                            textDataActiv.setText("LIVE");
                            textDataActiv.setTextColor(getResources().getColor(R.color.colorLiveMatch));
                        }
                        if (games.get(0).match.status != null && games.get(0).match.status == 1) {
                            textDataActiv.setText("Завершен");
                            textDataActiv.setTextColor(getResources().getColor(R.color.colorWhiteSmoke));
                        }
                        TabSelected(0);
                    } else {
                        Toast.makeText(DetailsOfMatch.this, "No data", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(DetailsOfMatch.this, "No data", Toast.LENGTH_SHORT).show();
                }

            }
        };

        getMatchDetails.getMatches(matchId, this, h);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Toast.makeText(DetailsOfMatch.this, Integer.toString(tab.getPosition()), Toast.LENGTH_SHORT).show();
                TabSelected(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void updateComments(){

        getComments = new GetComments();
        Handler h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if(getComments.data != null){
                    Type listType = new TypeToken<ArrayList<Comments>>() {
                    }.getType();
                    comments = new Gson().fromJson(getComments.data, listType);
                    if(comments != null && comments.size() != 0){
                        Log.d("updateComments", "handleMessage: create adapter " +  comments.size() + comments.get(0).user.nick);
                        CommentsAdapter commentsAdapter = new CommentsAdapter(getApplicationContext(), comments, dbC);
                        commentsListView.setAdapter(commentsAdapter);
                        ListViewScrollHelper  listViewScrollHelper = new ListViewScrollHelper();
                        listViewScrollHelper.setListViewHeightBasedOnChildren(commentsListView);
                    }
                }
            }
        };
        getComments.getComments(matchId,this,h);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_match);
        Intent intent = getIntent();
        matchId = intent.getIntExtra("EXTRA_MESSAGE", 0);
        DB = new HelperDB(this,"ggdb");
        // подключаемся к БД
        dbC = DB.getWritableDatabase();
        commentsListView = (ListView)findViewById(R.id.commentListView);
        updatemMatchDetails();
        updateComments();
    }
    private void TabSelected(int index){
        gameNotStart.setVisibility(View.GONE);
        gameIsOn.setVisibility(View.GONE);
        scoreLayout.setVisibility(View.GONE);
        watchRecordingMatch.setVisibility(View.GONE);
        watchStreamButton.setVisibility(View.GONE);
        if(games.get(index).status == 0){
            gameNotStart.setVisibility(View.VISIBLE);
        }
        if(games.get(index).status == 1){
            gameIsOn.setVisibility(View.VISIBLE);
        }
        if(games.get(index).status == 2){
            scoreLayout.setVisibility(View.VISIBLE);
            score1.setText(games.get(index).match.team1.name + " : " + games.get(index).scor1);
            score2.setText(games.get(index).match.team2.name + " : " + games.get(index).scor2);
            if(games.get(index).teamWin.id == games.get(index).match.team1.id){
                score1.setTextColor(getResources().getColor(R.color.colorGreenWinTeam));
            }
            if(games.get(index).teamWin.id == games.get(index).match.team2.id){
                score2.setTextColor(getResources().getColor(R.color.colorGreenWinTeam));
            }
            if(games.get(index).teamWin.id != games.get(index).match.team1.id){
                score1.setTextColor(getResources().getColor(R.color.colorLiveMatch));
            }
            if(games.get(index).teamWin.id != games.get(index).match.team2.id){
                score2.setTextColor(getResources().getColor(R.color.colorLiveMatch));
            }

        }
        if(games.get(index).status != 2 && games.get(index).match.streamUrl != null && !games.get(index).match.streamUrl.equals("")){
            watchStreamButton.setVisibility(View.VISIBLE);
        }
        if(games.get(index).status == 2 && games.get(index).recordingLink != null && !games.get(index).recordingLink.equals("")){
            watchRecordingMatch.setVisibility(View.VISIBLE);
        }
    }

    public void onSendNewComment(View view) {
        commentEditText = (EditText)findViewById(R.id.editTextComment);
        if(!commentEditText.getText().toString().equals("")){
            Cursor c = dbC.rawQuery("select * from Login", null);
            if(c.moveToFirst()) {
                final CommentSend commentSend = new CommentSend();
                commentSend.CommentSend(matchId,c.getString(1), commentEditText.getText().toString(),this, new Handler(){
                    @Override
                    public void handleMessage(android.os.Message msg) {
                        if(commentSend.data != null && commentSend.data.equals("ok")){
                            updateComments();
                            Toast.makeText(DetailsOfMatch.this, "Комментарий добавлен", Toast.LENGTH_SHORT).show();
                            commentEditText.setText("");
                        }else {
                            Toast.makeText(DetailsOfMatch.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }
}
