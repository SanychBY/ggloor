package com.bitloor.ggloor.soap;

import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.bitloor.ggloor.settings.SettingsData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

/**
 * Created by ssaan on 25.05.2017.
 **/

public class AuthTest extends AsyncTask<String, String, String>{
    public Handler h;
    public String data = null;

    private static String SOAP_ACTION = SettingsData.SOAP_URL + "/ws/mysoap";

    private static String NAMESPACE = "http://soap.testgrails12/";
    private static String METHOD_NAME = "AuthTest";

    private static String URL = SettingsData.SOAP_URL + "/ws/mysoap?wsdl";

    private String testKSOAP(String key){
        try {
            //Initialize soap request + add parameters

            SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);

            //Use this to add parameters
            /*request.addAttribute("arg0", strings[0]);
            request.addAttribute("arg1", strings[1]);*/
            request.addProperty("arg0",key);
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
                Log.d("ggloor_msg", "exInput: "  + result.getProperty(0).toString());
                return result.getProperty(0).toString();
            }else {
                Log.d("ggloor_msg", "exInput: result = null");
                return null;
            }
        } catch (Exception e) {
            Log.e("ggloor_error", "exInput: " + e.getMessage());
            e.printStackTrace();
            return null;
        }finally {
            data = null;

        }
    }
    protected String doInBackground(String... strings) {
        data = testKSOAP(strings[0]);
        h.sendEmptyMessage(1);
        return data;
    }
}
