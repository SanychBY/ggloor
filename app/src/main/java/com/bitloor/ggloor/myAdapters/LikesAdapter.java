package com.bitloor.ggloor.myAdapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitloor.ggloor.LikesActivity;
import com.bitloor.ggloor.R;
import com.bitloor.ggloor.model.Likes;
import com.bitloor.ggloor.rest.DelLike;
import com.bitloor.ggloor.settings.SettingsData;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;


import java.util.ArrayList;

/**
 * Created by ssaan on 28.05.2017.
 **/

public class LikesAdapter extends BaseAdapter {
    private Context ctx;
    private ArrayList<Likes> objects;
    private LayoutInflater lInflater;
    AlertDialog.Builder ad;
    LikesActivity act;
    SQLiteDatabase sqLiteDatabase;

    public LikesAdapter(Context ctx, ArrayList<Likes> objects, LikesActivity act, SQLiteDatabase sqLiteDatabase) {
        this.ctx = ctx;
        this.objects = objects;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ad = new AlertDialog.Builder(act);
        this.act = act;
        this.sqLiteDatabase = sqLiteDatabase;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(view == null) {
            view = lInflater.inflate(R.layout.item_likes_list, parent, false);
        }
        Log.d("ggloor_msg", "getView: view");
        ImageView image = (ImageView)view.findViewById(R.id.img_team);
        final Likes like = (Likes) getItem(position);
        loadImage(like.team.cover, image);
        TextView nameTeam = (TextView) view.findViewById(R.id.team_name);
        nameTeam.setText(like.team.name);
        Button delButton = (Button)view.findViewById(R.id.delButton);
        delButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                ad.setTitle("Удалить?");
                ad.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Cursor c = sqLiteDatabase.rawQuery("select * from Login", null);
                        if(c.moveToFirst()) {
                            final DelLike delLike = new DelLike();
                            delLike.DelLike(like.team.id,c.getString(1),ctx, new Handler(){
                                @Override
                                public void handleMessage(Message msg) {
                                    if(delLike.data != null && delLike.data.equals("ok")){
                                        act.updateLikes();
                                    }else {
                                        Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                        c.close();
                    }
                });
                ad.setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                ad.setCancelable(true);
                ad.show();
            }
        });
        return view;
    }
}
