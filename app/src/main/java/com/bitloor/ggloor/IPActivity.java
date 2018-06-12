package com.bitloor.ggloor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bitloor.ggloor.model.Settings;
import com.bitloor.ggloor.settings.SettingsData;

public class IPActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ip);
    }

    public void onSave(View view) {
        EditText editText = (EditText)findViewById(R.id.ip_text);
        String ip = editText.getText().toString();
        SettingsData.SITE_URL = ip + "8080";
        SettingsData.SOAP_URL = ip + "9999";
        Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show();
    }
}
