package com.bitloor.ggloor.myAdapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.bitloor.ggloor.R;
import com.bitloor.ggloor.model.Comments;

import java.util.ArrayList;

public class CommentsAdapter extends BaseAdapter {
    private Context ctx;
    private ArrayList<Comments> comments;
    private SQLiteDatabase sqLiteDatabase;
    private LayoutInflater lInflater;
    public CommentsAdapter(Context ctx, ArrayList<Comments> objects, SQLiteDatabase sqLiteDatabase){
        this.ctx = ctx;
        comments = objects;
        this.sqLiteDatabase = sqLiteDatabase;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        if(comments != null)
            return comments.size();
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        return comments.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = lInflater.inflate(R.layout.item_comments_list, viewGroup, false);
        }
        Log.d("CommentsAdapter", "getView: " + comments.get(i).user.nick + i);
        TextView nick = (TextView)view.findViewById(R.id.userNameComment);
        Comments comment = comments.get(i);
        nick.setText(comment.user.nick);
        TextView text = (TextView)view.findViewById(R.id.textComment);
        text.setText(comment.text);
        TextView textDataTime = (TextView)view.findViewById(R.id.dateComment);
        textDataTime.setText(comments.get(i).match.dateMatch.getHours() + ":" + comments.get(i).match.dateMatch.getMinutes() + " " + comments.get(i).match.dateMatch.getDate()
                + "." + (comments.get(i).match.dateMatch.getMonth() + 1) + "." + (comments.get(i).match.dateMatch.getYear() + 1900));
        return view;
    }
}
