package com.bitloor.ggloor;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bitloor.ggloor.settings.RegExHelper;
import com.bitloor.ggloor.soap.Reg;

import org.w3c.dom.Text;

import pl.droidsonroids.gif.GifImageView;

public class RegActivity extends AppCompatActivity {
    TextView errorNick;
    TextView errorEmail;
    TextView errorPassword;
    EditText nickText;
    EditText emailText;
    EditText passwordText;
    EditText repPasswordText;
    GifImageView gif;
    Reg reg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        nickText = (EditText)findViewById(R.id.nickText);
        emailText = (EditText)findViewById(R.id.emaitText);
        passwordText = (EditText)findViewById(R.id.passwordText);
        repPasswordText = (EditText)findViewById(R.id.repPasswordText);
        errorNick = (TextView)findViewById(R.id.error_nick_reg);
        errorEmail = (TextView)findViewById(R.id.error_email_reg);
        errorPassword = (TextView)findViewById(R.id.error_password_reg);
        gif = (GifImageView)findViewById(R.id.gif_load);
    }

    public void regEx(View view) {
        errorNick.setVisibility(View.GONE);
        errorPassword.setVisibility(View.GONE);
        errorEmail.setVisibility(View.GONE);
        String nick = nickText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String repPassword = repPasswordText.getText().toString();
        final RegExHelper reh = new RegExHelper();
        boolean flag = true;
        if(nick.length() < 3){
            errorNick.setText(R.string.error_nick_2);
            errorNick.setVisibility(View.VISIBLE);
            flag = false;
        }
        if(nick.length() > 9){
            errorNick.setText(R.string.error_nick_3);
            errorNick.setVisibility(View.VISIBLE);
            flag = false;
        }
        Log.d("ggloor_msg", ("regEx: " + reh.Test(nick, "^[1-9a-zA-Z_-]+")) + " " + nick);
        if(flag && !reh.Test(nick, "^[1-9a-zA-Z_-]+")){
            errorNick.setText(R.string.error_nick_1);
            errorNick.setVisibility(View.VISIBLE);
            flag = false;
        }
        Log.d("ggloor_msg", "regEx: " + email.contains("@"));
        if(!email.contains("@")){
            errorEmail.setText(R.string.error_email_1);
            errorEmail.setVisibility(View.VISIBLE);
            flag = false;
        }
        if(password.length() < 5){
            errorPassword.setText(R.string.error_password_2);
            errorPassword.setVisibility(View.VISIBLE);
            flag = false;
        }else {
            if(!password.equals(repPassword)){
                errorPassword.setText(R.string.error_password_1);
                errorPassword.setVisibility(View.VISIBLE);
                flag = false;
            }
        }
        if(flag){
            reg = new Reg();
            gif.setVisibility(View.VISIBLE);
            reg.h = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    if(reg.result != null){
                        boolean _flag = true;
                        if(reg.result.equals("2"))
                        {
                            _flag = false;
                            errorNick.setText(R.string.error_nick_4);
                            errorNick.setVisibility(View.VISIBLE);
                        }
                        if(reg.result.equals("4")){
                            _flag = false;
                            errorNick.setText(R.string.error_nick_4);
                            errorNick.setVisibility(View.VISIBLE);
                        }
                        if(reg.result.equals("ok")){
                            Log.d("ggloor_msg", "handleMessage: OK");
                            LinearLayout llForm = (LinearLayout)findViewById(R.id.reg_form);
                            llForm.setVisibility(View.GONE);
                            LinearLayout llRegText = (LinearLayout)findViewById(R.id.reg_res_text);
                            llRegText.setVisibility(View.VISIBLE);
                        }else {
                            if(_flag){
                                Toast.makeText(RegActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else {
                        Toast.makeText(RegActivity.this, "Сервис временно недоступен", Toast.LENGTH_SHORT).show();
                    }
                    gif.setVisibility(View.GONE);
                }
            };
            reg.execute(nick, email, password, repPassword);
        }
    }
}
