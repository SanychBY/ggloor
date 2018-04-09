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
 * Created by ssaan on 31.05.2017.
 **/

public class DelLike {
    public String data;
    private int ID;
    private String KEY;
    public String DelLike(int id, String key, Context context, final Handler handler){
        ID = id;
        KEY = key;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SettingsData.SITE_URL + "/MainPage/delLikeTeamApp";
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        data = response;
                        Log.d("DelLike", "onResponse: " + response);
                        handler.sendEmptyMessage(1);
                        // mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                data = null;
                handler.sendEmptyMessage(2);
                Log.d("GetMatches", "onResponse error: " + error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(ID));
                params.put("key", KEY);
                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return data;
    }
}
