package com.bitloor.ggloor.rest;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bitloor.ggloor.settings.SettingsData;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ssaan on 28.05.2017.
 **/

public class GetLikes {
    public String data;

    public String getLikes(String key, Context context, final Handler handler){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SettingsData.SITE_URL + "/LikesRestPage/" + key;
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        data = response;
                        Log.d("GetMatches", "onResponse: " + response);
                        handler.sendEmptyMessage(1);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                data = null;
                handler.sendEmptyMessage(2);
                Log.d("GetMatches", "onResponse error: " + error.getMessage());

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return data;
    }
}
