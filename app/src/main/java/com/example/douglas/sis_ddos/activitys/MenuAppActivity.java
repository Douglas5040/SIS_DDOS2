package com.example.douglas.sis_ddos.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.app.AppConfig;
import com.example.douglas.sis_ddos.app.AppController;
import com.example.douglas.sis_ddos.controler.PecsCtrl;
import com.example.douglas.sis_ddos.controler.ServicesCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;
import com.example.douglas.sis_ddos.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MenuAppActivity extends AppCompatActivity {
    private static final String TAG = MenuAppActivity.class.getSimpleName();
    private Button btnServPen;
    private Button btnMyServ;
    private Button btnNotifi;
    private Button btnLogout;
    private ProgressDialog pDialog;

    private SQLiteHandler db;

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_app);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        btnServPen = (Button) findViewById(R.id.btn_ServPen);
        btnMyServ = (Button) findViewById(R.id.btn_MyServ);
        btnNotifi = (Button) findViewById(R.id.btn_Notifi);
        btnLogout = (Button) findViewById(R.id.btn_desconectar);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            session.setLogin(true);
        }

        // Link to Register Screen
        btnServPen.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ListServPendenteActivity.class);
                startActivity(i);
                //finish();
            }
        });

        btnMyServ.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ListMyServicesActivity.class);
                startActivity(i);
                //finish();
            }
        });

        btnNotifi.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        ListServPendenteActivity.class);
                startActivity(i);
                //finish();
            }
        });

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        pDialog.setMessage("CARREGANDO DADOS ...");
        showDialog();
            //botar tudo em segundo plano usando Multcast e Services
            listaPecs();
            listaServices();
            listaMarcas();
            listaModelos();
            listaBtus();
            listaNvEcon();
            listaTencao();

        hideDialog();
    }

    private void logoutUser() {
        session.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(MenuAppActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void listaServices() {


        // Tag used to cancel the request
        String tag_string_req = "req_listaService";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_SERVICES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());
                db = new SQLiteHandler(getApplicationContext());
                db.deleteServices();
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray serviceArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU SERVICES: "+serviceArray.length());
                        for (int i = 0; i < serviceArray.length(); i++) {
                            try {
                                JSONObject serv_penObj = new JSONObject(serviceArray.get(i).toString());

                                Log.e("Serviço ENCONTRADO: ", "Entrou no for");

                                ServicesCtrl objetoServ = new ServicesCtrl();

                                objetoServ.setId_service(serv_penObj.getInt("id_service"));
                                objetoServ.setNome(serv_penObj.getString("nome"));
                                objetoServ.setDescri(serv_penObj.getString("descri"));
                                objetoServ.setTempo(serv_penObj.getString("tempo"));

                                db.addServices(objetoServ);

                            } catch (JSONException e) {
                                Log.e(TAG, "JSON erro ao consultar dados: " + e.getMessage());
                            }
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_list");
                        Toast.makeText(getApplicationContext(), "Erro AQUI "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("ERRORRRRR","Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem Serviços", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void listaPecs() {

        // Tag used to cancel the request
        String tag_string_req = "req_listaPecs";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_PECS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray pecsArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU: "+pecsArray.length());
                        db = new SQLiteHandler(getApplicationContext());
                        db.deletePecs();
                        for (int i = 0; i < pecsArray.length(); i++) {
                            try {
                                JSONObject serv_penObj = new JSONObject(pecsArray.get(i).toString());

                                Log.e("Serviço ENCONTRADO: ", "Entrou no for");

                                PecsCtrl objetoPecs = new PecsCtrl();

                                objetoPecs.setId_pc(serv_penObj.getInt("id_pc"));
                                objetoPecs.setNome(serv_penObj.getString("nome"));
                                objetoPecs.setModelo(serv_penObj.getString("modelo"));
                                objetoPecs.setMarca(serv_penObj.getString("marca"));

                                db.addPecs(objetoPecs);

                            } catch (JSONException e) {
                                Log.e(TAG, "JSON erro ao consultar dados: " + e.getMessage());
                            }
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_list");
                        Toast.makeText(getApplicationContext(), "Erro AQUI "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("ERRORRRRR","Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem Peças no Banco", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void listaMarcas() {

        // Tag used to cancel the request
        String tag_string_req = "req_listMARCAS";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_MARCA_AR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray marcasArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU: "+marcasArray.length());
                        db = new SQLiteHandler(getApplicationContext());
                        db.deleteMarcas();
                        for (int i = 0; i < marcasArray.length(); i++) {
                            try {
                                JSONObject marcaObj = new JSONObject(marcasArray.get(i).toString());

                                Log.e("Serviço ENCONTRADO: ", "Entrou no for");

                                db.addMarca(marcaObj.getInt("id_marca"), marcaObj.getString("marca"));

                            } catch (JSONException e) {
                                Log.e(TAG, "JSON erro ao consultar dados: " + e.getMessage());
                            }
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_list");
                        Toast.makeText(getApplicationContext(), "Erro AQUI "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("ERRORRRRR","Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem Marcas no Banco", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void listaModelos() {

        // Tag used to cancel the request
        String tag_string_req = "req_listModelo";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_MODELO_AR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray modeloArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU: "+modeloArray.length());
                        db = new SQLiteHandler(getApplicationContext());
                        db.deleteModelos();
                        for (int i = 0; i < modeloArray.length(); i++) {
                            try {
                                JSONObject modeloObj = new JSONObject(modeloArray.get(i).toString());

                                Log.e("Serviço ENCONTRADO: ", "Entrou no for");

                                db.addModelo(modeloObj.getInt("id_modelo"), modeloObj.getString("modelo"));

                            } catch (JSONException e) {
                                Log.e(TAG, "JSON erro ao consultar dados: " + e.getMessage());
                            }
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_list");
                        Toast.makeText(getApplicationContext(), "Erro AQUI "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("ERRORRRRR","Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem Modelos no Banco", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void listaBtus() {

        // Tag used to cancel the request
        String tag_string_req = "req_listBTUs";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_BTUS_AR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray btusArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU: "+btusArray.length());
                        db = new SQLiteHandler(getApplicationContext());
                        db.deleteBTU();
                        for (int i = 0; i < btusArray.length(); i++) {
                            try {
                                JSONObject btuObj = new JSONObject(btusArray.get(i).toString());

                                Log.e("Serviço ENCONTRADO: ", "Entrou no for");

                                db.addBtu(btuObj.getInt("id_capaci_termi"), btuObj.getString("capaci_termica"));

                            } catch (JSONException e) {
                                Log.e(TAG, "JSON erro ao consultar dados: " + e.getMessage());
                            }
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_list");
                        Toast.makeText(getApplicationContext(), "Erro AQUI "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("ERRORRRRR","Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem BTUs no Banco", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void listaNvEcon() {

        // Tag used to cancel the request
        String tag_string_req = "req_listNvEcon";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_NV_ECON_AR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray nvEconArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU: "+nvEconArray.length());
                        db = new SQLiteHandler(getApplicationContext());
                        db.deleteNvEcon();
                        for (int i = 0; i < nvEconArray.length(); i++) {
                            try {
                                JSONObject nvEconObj = new JSONObject(nvEconArray.get(i).toString());

                                Log.e("Serviço ENCONTRADO: ", "Entrou no for");

                                db.addNvEcon(nvEconObj.getInt("id_nv_econ"), nvEconObj.getString("nivel_econo"));

                            } catch (JSONException e) {
                                Log.e(TAG, "JSON erro ao consultar dados: " + e.getMessage());
                            }
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_list");
                        Toast.makeText(getApplicationContext(), "Erro AQUI "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("ERRORRRRR","Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem Marcas no Banco", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void listaTencao() {

        // Tag used to cancel the request
        String tag_string_req = "req_listTencao";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_GET_TENCAO_AR, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray tencaoArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU: "+tencaoArray.length());
                        db = new SQLiteHandler(getApplicationContext());
                        db.deleteTencao();
                        for (int i = 0; i < tencaoArray.length(); i++) {
                            try {
                                JSONObject tencaoObj = new JSONObject(tencaoArray.get(i).toString());

                                Log.e("Serviço ENCONTRADO: ", "Entrou no for");

                                db.addTencaoTomada(tencaoObj.getInt("id_tensao"), tencaoObj.getString("tencao_tomada"));

                            } catch (JSONException e) {
                                Log.e(TAG, "JSON erro ao consultar dados: " + e.getMessage());
                            }
                        }


                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("error_list");
                        Toast.makeText(getApplicationContext(), "Erro AQUI "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("ERRORRRRR","Json error: " + e.getMessage());
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem Marcas no Banco", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}

