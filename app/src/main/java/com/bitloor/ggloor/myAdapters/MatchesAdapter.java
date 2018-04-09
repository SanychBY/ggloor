package com.bitloor.ggloor.myAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitloor.ggloor.DetailsOfMatch;
import com.bitloor.ggloor.MainActivity;
import com.bitloor.ggloor.R;
import com.bitloor.ggloor.model.Matches;
import com.bitloor.ggloor.myProgressDialog.MyProgressDialog;
import com.bitloor.ggloor.rest.Like;
import com.bitloor.ggloor.settings.SettingsData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

import static android.provider.AlarmClock.EXTRA_MESSAGE;

/**
 * Created by ssaan on 22.05.2017.
 **/

public class MatchesAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Matches> objects;
    //public String key = null;
    SQLiteDatabase dbC;
    AlertDialog.Builder ad;

    public MatchesAdapter(Context ctx, SQLiteDatabase dbC, ArrayList<Matches> objects, Activity act) {
        this.ctx = ctx;
        this.objects = objects;
        this.dbC = dbC;
        ad = new AlertDialog.Builder(act);
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int i) {
        return objects.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    private void loadImage(String nameImg, ImageView imageView){
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(ctx));

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
    private int idTeam = -1;
    private String key;
    private void clickGoMatchDetails(TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View pv = (View) view.getParent();
                TextView tv = (TextView) pv.findViewById(R.id.data_matchId);
                int idMatch = Integer.valueOf(tv.getText().toString());
                Intent intent = new Intent(ctx, DetailsOfMatch.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("EXTRA_MESSAGE", idMatch);
                ctx.startActivity(intent);
            }
        });
    }
    private void clickImage(ImageView img){
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Cursor c = dbC.rawQuery("select * from Login", null);
                if(c.moveToFirst()) {
                    key =  c.getString(1);
                    View pv = (View) view.getParent();
                    if (view.getId() == R.id.img_team1) {
                        TextView tv = (TextView) pv.findViewById(R.id.data_team1);
                        idTeam = Integer.valueOf(tv.getText().toString());
                    } else {
                        TextView tv = (TextView) pv.findViewById(R.id.data_team2);
                        idTeam = Integer.valueOf(tv.getText().toString());
                    }
                    Log.d("ggloor_msg", "onClick: " + idTeam);
                    if (idTeam != -1) {
                        Cursor ct = dbC.rawQuery("select * from Teams where id = ?", new String[]{String.valueOf(idTeam)});
                        Log.d("ggmess", "onClick: " + idTeam);
                        if(ct.moveToFirst()){
                            Log.d("ggmess", "onClick: ");
                            ad.setMessage("Следить за " + ct.getString(1) + "?");
                            ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int arg1) {
                                    final Like like = new Like();
                                    like.addLike(idTeam, key, ctx, new Handler(){
                                        @Override
                                        public void handleMessage(Message msg) {
                                            if(like.data != null){
                                                if(like.data.equals("1")) {
                                                    Toast.makeText(ctx, "ok", Toast.LENGTH_SHORT).show();
                                                }
                                                if(like.data.equals("2")){
                                                    Toast.makeText(ctx, "Уже была добалена", Toast.LENGTH_SHORT).show();
                                                }
                                            }else {
                                                Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                            ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int arg1) {

                                }
                            });
                            ad.setCancelable(true);
                            ad.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                public void onCancel(DialogInterface dialog) {

                                }
                            });
                            ad.show();
                        }
                        ct.close();

                    }
                }else {
                    //Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
                }
                c.close();
            }
        });
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_matches_list, parent, false);
        }
        ImageView img1 = (ImageView)view.findViewById(R.id.img_team1);
        ImageView img2 = (ImageView)view.findViewById(R.id.img_team2);
        Matches match = (Matches) getItem(position);
        loadImage(match.team1.cover, img1);
        loadImage(match.team2.cover, img2);
        clickImage(img1);
        clickImage(img2);
        TextView dataTeam1 = (TextView)view.findViewById(R.id.data_team1);
        dataTeam1.setText(String.valueOf(match.team1.id));
        TextView dataTeam2 = (TextView)view.findViewById(R.id.data_team2);
        dataTeam2.setText(String.valueOf(match.team2.id));
        TextView textDataTime = (TextView)view.findViewById(R.id.MatchesDataTime);
        TextView textDataColGame = (TextView)view.findViewById(R.id.MatchesDataColGame);
        TextView textDataActiv = (TextView)view.findViewById(R.id.MatchesDataActiv);
        textDataTime.setText(match.dateMatch.getHours()+ ":" + match.dateMatch.getMinutes() + " " + match.dateMatch.getDate()
                + "." + (match.dateMatch.getMonth() + 1) + "." + (match.dateMatch.getYear() + 1900));
        textDataColGame.setText("BO " + match.colGames);
        clickGoMatchDetails(textDataColGame);
        if(match.dateMatch.getTime() < new Date().getTime() && match.status == null){
            textDataActiv.setText("LIVE");
            textDataActiv.setTextColor(ctx.getResources().getColor(R.color.colorLiveMatch));
        }
        if(match.status!=null && match.status == 1){
            textDataActiv.setText("Завершен");
            textDataActiv.setTextColor(ctx.getResources().getColor(R.color.colorWhiteSmoke));
        }
        TextView dataMatchId = (TextView)view.findViewById(R.id.data_matchId);
        dataMatchId.setText(match.id + "");
        return view;
    }
}
