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

public class GetSettings {
    public String data;
    public String getSettings(String key, Context context, final Handler hendler){
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SettingsData.SITE_URL + "/Rest/getSettings?key=" + key;
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        data = response;
                        Log.d("GEtSettings", "onResponse: " + response);
                        hendler.sendEmptyMessage(1);
                        // mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                data = null;
                hendler.sendEmptyMessage(2);
                Log.d("GetSettings", "onResponse error: " + error.getMessage());

            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return data;
    }
}
