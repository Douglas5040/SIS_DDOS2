package com.example.douglas.sis_ddos.activitys;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
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

public class MenuDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                    Runnable,
                    ListServPendenteFragment.OnFragmentInteractionListener,
                    ListMyServicesFragment.OnFragmentInteractionListener{
    private static final String TAG = MenuDrawerActivity.class.getSimpleName();

    private SQLiteHandler db;
    private SessionManager session;
    private ProgressDialog pDialog;
    private AlertDialog alerta;
    Menu menuDrawer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_drawer);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());
        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            session.setLogin(true);
        }



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_viewDrawer);
        navigationView.setNavigationItemSelectedListener(this);

        View viewHeader = navigationView.getHeaderView(0);

        TextView tvNomeUser =  viewHeader.findViewById(R.id.tvNomeUser);
        tvNomeUser.setText(db.getUserDetails().getName());

        TextView tvEmailUser = viewHeader.findViewById(R.id.tvEmailUser);
        tvEmailUser.setText(db.getUserDetails().getEmail());

        TextView tvMatriUser = viewHeader.findViewById(R.id.tvMatriUser);
        tvMatriUser.setText(db.getUserDetails().getMatricula());


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Cria o gerador do AlertDialog
        //confirmarSair();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void confirmarSair(){
        Handler handler = new Handler();
        AlertDialog.Builder builder = new AlertDialog.Builder(MenuDrawerActivity.this);
        //define o titulo
        builder.setTitle("Confirmação Sai do Sistema");
        //define a mensagem
        builder.setMessage("Você deseja mesmo SAIR?");
        //define um botão como positivo
        builder.setPositiveButton("Sim",new DialogInterface.OnClickListener()

        {
            public void onClick (DialogInterface arg0, int arg1){
                //Toast.makeText(getApplicationContext(), "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                finish();

            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não",new DialogInterface.OnClickListener()

        {
            public void onClick (DialogInterface arg0, int arg1){
                Toast.makeText(getApplicationContext(), "negativo=" + arg1, Toast.LENGTH_SHORT).show();
                alerta.cancel();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
        //handler.postDelayed(this, 3000);
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        //confirmarSair();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drawer, menu);
        menuDrawer = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Fragment fragment = null;
        Boolean fragmentoSection = false;
        if (id == R.id.serv_pen) {
            // Handle the camera action
            fragment = new ListServPendenteFragment();
            fragmentoSection = true;

        } else if (id == R.id.my_serv) {
            // Handle the camera action
            fragment = new ListMyServicesFragment();
            fragmentoSection = true;

        } else if (id == R.id.notifica) {

        }if(fragmentoSection)
            getSupportFragmentManager().beginTransaction().replace(R.id.contendor, fragment).commit();

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Fragment fragment = null;
        Boolean fragmentoSection = false;

        if (id == R.id.serv_pen) {
            // Handle the camera action
            fragment = new ListServPendenteFragment();
            fragmentoSection = true;

        } else if (id == R.id.my_serv) {
            // Handle the camera action
            fragment = new ListMyServicesFragment();
            fragmentoSection = true;

        } else if (id == R.id.notifica) {

        } else if (id == R.id.detal_conta) {

        } else if (id == R.id.sair) {

            session.setLogin(false);

            // Launching the login activity
            Intent intent = new Intent(MenuDrawerActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();

        }if(fragmentoSection)
            getSupportFragmentManager().beginTransaction().replace(R.id.contendor, fragment).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

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

    @Override
    public void run() {

    }
}
