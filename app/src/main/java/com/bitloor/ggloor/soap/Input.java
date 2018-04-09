package com.bitloor.ggloor.soap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.bitloor.ggloor.settings.SettingsData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/*
import pt.joaocruz04.lib.SOAPManager;
import pt.joaocruz04.lib.misc.JSoapCallback;
import pt.joaocruz04.lib.misc.JsoapError;

import static android.content.ContentValues.TAG;
*/


/**
 * Created by ssaan on 24.05.2017.
 **/

public class Input extends AsyncTask<String, String, String> {
    public Handler h;
    public String result;
    private static String SOAP_ACTION = SettingsData.SOAP_URL + "/ws/mysoap";

    private static String NAMESPACE = "http://soap.testgrails12/";
    private static String METHOD_NAME = "Auth";

    private static String URL = SettingsData.SOAP_URL + "/ws/mysoap?wsdl";

    private String inputKSOAP(String nick, String password){
        try {
            //Initialize soap request + add parameters

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            //Use this to add parameters
            request.addProperty("arg0",nick);
            request.addProperty("arg1",password);
            Log.d("ggloor_msg", "doInBackground: " + request.toString());
            //Declare the version of the SOAP request
            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);

            //Needed to make the internet call
            HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                //this is the actual part that will call the webservice
                androidHttpTransport.call(SOAP_ACTION, envelope);
                Log.d("ggloor_msg", "exInput: end transport");


            // Get the SoapResult from the envelope body.
            SoapObject result = (SoapObject)envelope.bodyIn;

            if(result != null){
                Log.d("ggloor_msg", "exInput: result ok = "  + result.getProperty(0).toString());
                return result.getProperty(0).toString();
            }else {
                Log.d("ggloor_msg", "exInput: result = null");
                return null;
            }
        } catch (Exception e) {
            Log.e("ggloor_error", "exInput: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        result = inputKSOAP(strings[0], strings[1]);
        h.sendEmptyMessage(1);
        return result;
    }
}
