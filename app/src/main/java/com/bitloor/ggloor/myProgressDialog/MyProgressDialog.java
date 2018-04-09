package com.bitloor.ggloor.myProgressDialog;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by ssaan on 25.05.2017.
 **/

public class MyProgressDialog {
    private Context cxt;
    private ProgressDialog pd;

    public MyProgressDialog(Context cxt) {
        this.cxt = cxt;
        pd = new ProgressDialog(cxt);
        pd.setTitle("Загрузка");
        pd.setMessage("Пожалуйста, подождите...");
    }
    public void Show(){
        pd.show();
    }

    public void hide(){
        pd.hide();
    }
}
