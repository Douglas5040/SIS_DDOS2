package com.example.douglas.sis_ddos.activitys;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.douglas.sis_ddos.app.AppConfig;
import com.example.douglas.sis_ddos.app.AppController;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Douglas on 18/08/2017.
 */

public class ReceberAlarmeBroadcast extends BroadcastReceiver {
    private static final String TAG = ReceberAlarmeBroadcast.class.getSimpleName();
    private GoogleApiClient googleApiClient;

    private SQLiteHandler db;
    Date dataNow = new Date();
    private String dia = ""+dataNow.getDate(),
            mes = ""+(dataNow.getMonth()+1),
            ano = ""+(dataNow.getYear()+2000-100),
            hora = ""+dataNow.getHours(),
            minutos= ""+dataNow.getMinutes(),
            segundos= ""+ dataNow.getSeconds();
    @Override
    public void onReceive(Context context, Intent intent) {


        if(Integer.parseInt(mes)<10) mes = "0"+mes;
        //Aqui pode iniciar uma activity, serviço ou exibir notificações
        Log.e("ALARME ----- ", "Alarme disparado AS "+dia+"/"+mes+"/"+ano);
        Log.d("ALARME ----- ", "Alarme disparado AS "+hora+":"+minutos+":"+segundos);

        db = new SQLiteHandler(context);
        MyLocation.LocationResult locationResult = new MyLocation.LocationResult(){
            @Override
            public void gotLocation(Location location){
                Log.e("POSIÇÃO ALARME----- ", "LATITUDE: "+location.getLatitude()+"LONGITUDE: "+ String.valueOf(location.getLongitude()));
                Log.i("POSIÇÃO ALARME----- ", "LATITUDE: "+location.getLatitude()+"LONGITUDE: "+ String.valueOf(location.getLongitude()));
                registrarPOSI(db.getUserDetails().getMatricula(),location.getLatitude(),location.getLongitude(),ano+"-"+mes+"-"+dia,hora+":"+minutos+":"+segundos);
            }
        };
        MyLocation myLocation = new MyLocation();
        myLocation.getLocation(context, locationResult);


    }

    private void registrarPOSI(final String matriFunc, final double latitude,
                               final double longitude, final String dataPosi, final String horaPosi) {
        // Tag used to cancel the request
        final String tag_string_req = "req_register_posi";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERIR_POSI_FUNC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Register POSI: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL
                        // Now store the user in sqlite
                        String msg = jObj.getString("error_msg");

                        Log.e("<<>> Retorno Servidor: ",msg);
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Log.e("Retorno Servidor: ",errorMsg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());

            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("matriFunc", matriFunc);
                params.put("latitude", ""+latitude);
                params.put("longitude", ""+longitude);
                params.put("dataPosi", dataPosi);
                params.put("horaPosi", horaPosi);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

}
