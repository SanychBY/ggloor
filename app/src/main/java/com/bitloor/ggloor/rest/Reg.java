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

public class Reg {
    public String data;
    private String NICK;
    private String PASSWORD;
    private String EMAIL;
    private String PASSWORDREP;

    public String reg(String nick,String email, String password,  String passwordRep, Context context, final Handler handler){

        NICK = nick;
        PASSWORD = password;
        PASSWORDREP = passwordRep;
        EMAIL = email;

        RequestQueue queue = Volley.newRequestQueue(context);
        String url = SettingsData.SITE_URL + "/Rest/Reg";
// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        data = response;
                        Log.d("REG", "onResponse: " + response);
                        handler.sendEmptyMessage(1);
                        // mTextView.setText("Response is: "+ response.substring(0,500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                data = null;
                handler.sendEmptyMessage(2);
                Log.d("REG", "onResponse error: " + error.getMessage());

            }
        }){
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String> params = new HashMap<>();

                params.put("nick", NICK);
                params.put("email", EMAIL);
                params.put("password", PASSWORD);
                params.put("passwordRep", PASSWORDREP);

                return params;
            }
        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);
        return data;
    }
}
