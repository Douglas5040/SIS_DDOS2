package com.example.douglas.sis_ddos.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.adapters.ListServPenAdapter;
import com.example.douglas.sis_ddos.app.AppConfig;
import com.example.douglas.sis_ddos.app.AppController;
import com.example.douglas.sis_ddos.controler.ServPendenteCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ListServPendenteActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    private static final String TAG = ListServPendenteActivity.class.getSimpleName();
    private ListView lvServPen;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    public static int servPenPosition = 0;
    public static int servPenPositionList = 0;
    public List<ServPendenteCtrl> servPens;
    public static ServPendenteCtrl servPen;

    private Toolbar listServToolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListServPenAdapter listServApd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_serv_pendente);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        lvServPen = (ListView) findViewById(R.id.lvServPen);

        listServToolbar = (Toolbar) findViewById(R.id.tb_list_serv);
        listServToolbar.setTitle("Lista de Serviços Pendentes");
        //listServToolbar.setSubtitle("Subtitulo");
        listServToolbar.setLogo(R.drawable.googleg_standard_color_18);
        setSupportActionBar(listServToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        servPens = new ArrayList<ServPendenteCtrl>();

        listServApd = new ListServPenAdapter(getApplicationContext(), servPens);
        lvServPen.setAdapter(listServApd);

        Log.e("BOTAO","ENTROU AQUI: "+ servPens.size());

        swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        //listaServPen();
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        listaServPen("Pendente"," ");
                                    }
                                }
        );

        lvServPen.setOnItemClickListener(this);

    }
    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {
        listaServPen("Pendente"," ");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
//            Intent it = new Intent(getApplicationContext(), MenuAppActivity.class);
//            startActivity(it);
            finish();
        }

        return true;
    }
    /**
     * function to verify login details in mysql db
     * */
    private void listaServPen(final String status, final String matriFunc) {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // Tag used to cancel the request
        String tag_string_req = "req_listaServPen";
        //final List<ServPendenteCtrl> listSerPen =null;

        pDialog.setMessage("Carregando...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SERV_PEN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray serv_penArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU: "+serv_penArray.length());
                        servPens.clear();
                        for (int i = 0; i < serv_penArray.length(); i++) {
                            try {
                                JSONObject serv_penObj = new JSONObject(serv_penArray.get(i).toString());

                                Log.e("Serviço ENCONTRADO: ", "Entrou no for");

                                ServPendenteCtrl objetoServPen = new ServPendenteCtrl();

                                objetoServPen.setId_serv_pen(serv_penObj.getInt("uid"));
                                objetoServPen.setLatitude(Double.valueOf(serv_penObj.getString("latitude")));
                                objetoServPen.setLongitude(Double.valueOf(serv_penObj.getString("longitude")));
                                objetoServPen.setCliente_id(Integer.valueOf(serv_penObj.getString("cliente")));
                                objetoServPen.setLotacionamento(serv_penObj.getString("lotacionamento"));
                                objetoServPen.setEnder(serv_penObj.getString("ender"));
                                objetoServPen.setComplemento(serv_penObj.getString("complemento"));
                                objetoServPen.setCep(serv_penObj.getString("cep"));
                                objetoServPen.setData_serv(serv_penObj.getString("data_serv"));
                                objetoServPen.setHora_serv(serv_penObj.getString("hora_serv"));
                                objetoServPen.setDescri_cli_problem(serv_penObj.getString("descriCliProblem"));
                                objetoServPen.setDescri_tecni_problem(serv_penObj.getString("descriTecniProblem"));
                                objetoServPen.setDescri_cli_refrigera(serv_penObj.getString("descriCliRefrigera"));
                                objetoServPen.setStatus_serv(serv_penObj.getString("statusServ"));
                                objetoServPen.setNomeCli(serv_penObj.getString("nome"));
                                objetoServPen.setTipoCli(serv_penObj.getString("tipo"));
                                objetoServPen.setFone1(serv_penObj.getString("fone1"));
                                objetoServPen.setFone2(serv_penObj.getString("fone2"));

                                servPens.add(objetoServPen);

                                Log.e("LISTA",""+servPens.size());
                                Log.e("Dados sqlite: ", ""+db.getAllMyServPen().size());
                            } catch (JSONException e) {
                                Log.e(TAG, "JSON erro ao consultar dados: " + e.getMessage());
                            }
                        }
                        listServApd.notifyDataSetChanged();


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
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem Serviço pendente para Realizar", Toast.LENGTH_LONG).show();
                ServPendenteCtrl objetoServPen = new ServPendenteCtrl();
                objetoServPen.setTipoCli("SEM SERVIÇOS PENDENTES NO MOMENTO");
                objetoServPen.setData_serv("");
                objetoServPen.setHora_serv("");
                objetoServPen.setEnder("");
                objetoServPen.setNomeCli("  ");
                objetoServPen.setId_serv_pen(-1);

                servPens.add(objetoServPen);
                hideDialog();
                // stopping swipe refresh

                listServApd.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("status", status);
                params.put("matriFunc", matriFunc);
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
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//        servPenPosition = (int) adapterView.getAdapter().getItemId(position);
//        servPenPositionList = position;
        if(adapterView.getAdapter().getItemId(position) != -1) {
            servPen = servPens.get(position);
            Toast.makeText(getApplicationContext(), "ServPen position nome: " + servPen.getNomeCli(), Toast.LENGTH_LONG).show();
            Log.e("Click lista ", "Posição do click: " + position);
            Intent it = new Intent(getApplicationContext(), DetalhesServPen.class);
            startActivity(it);
            finish();
        }
    }
}
