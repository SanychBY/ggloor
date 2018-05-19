package com.bitloor.ggloor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

public class SettingsActivity extends AppCompatActivity {

    private static final String[] sMyScope = new String[]{
            VKScope.NOHTTPS,
            VKScope.MESSAGES,
            VKScope.OFFLINE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    public void SaveIpClick(View view) {
    }

    public void VKSignIn(View view) {
        VKSdk.login(this, sMyScope);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Log.w("ggloor", "onResult: " + res.accessToken );
// Пользователь успешно авторизовался
                Toast.makeText(SettingsActivity.this, "Пользователь успешно авторизовался", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onError(VKError error) {
// Произошла ошибка авторизации (например, пользователь запретил авторизацию)
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
