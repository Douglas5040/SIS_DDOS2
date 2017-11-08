package com.example.douglas.sis_ddos.activitys;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.app.AppConfig;
import com.example.douglas.sis_ddos.app.AppController;
import com.example.douglas.sis_ddos.app.DataHoraNow;
import com.example.douglas.sis_ddos.controler.RefrigeradorCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdemServiceActivity extends AppCompatActivity {
    private static final String TAG = OrdemServiceActivity.class.getSimpleName();
    private TextView txtNameCli;
    private RadioButton rbPreven;
    private RadioButton rbCorret;
    private Spinner spiMarca;
    private Spinner spiBTU;
    private Spinner spiService;
    private Spinner spiPecaPro;
    private ImageButton btnEditAr;
    private ImageButton btnAddServ;
    private ImageButton btnAddPecs;
    private CheckBox cbSPLIT;
    private CheckBox cbACJ;
    private EditText etOBS;
    private SQLiteHandler db;
    private ProgressDialog pDialog;
    private Toolbar mToolbar;
    private Toolbar mToobarBotton;
    private Spinner spinnerArCli;

    int tipo_manu;
    public static RefrigeradorCtrl arCliSpinnerOS;
    public static ArrayList<String> listPecs;
    public static ArrayList<String> listServices;

     RefrigeradorCtrl refrigeradoresCli;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordem_service);

        spinnerArCli = (Spinner) findViewById(R.id.spinnerArCliente);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(OrdemServiceActivity.this);
        arCliSpinnerOS = null;
        listPecs = new ArrayList<String>();
        listServices = new ArrayList<String>();
        refrigeradoresCli = db.getArCli(DetalhesMyService.servPen.getId_refriCli());

        txtNameCli = (TextView) findViewById(R.id.tvNameCli);
        txtNameCli.setText(DetalhesMyService.servPen.getNomeCli()+" - "+DetalhesMyService.servPen.getTipoCli());

        Log.e(TAG,"REfrigerador Contexto: "+refrigeradoresCli.toString());
        if(refrigeradoresCli.getId_refri() != 0){
            List<String> refrigeraCli = new ArrayList<String>();
                refrigeraCli.add(db.getNomeMaca(refrigeradoresCli.getMarca())+" / "+
                        db.getNomeBTU(refrigeradoresCli.getCapaci_termica())+" BTUs - " +
                                DetalhesMyService.servPen.getLotacionamento());

            ArrayAdapter<String> arrayAdapterArCli = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_spinner_item,refrigeraCli);
            //arrayAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerArCli.setAdapter(arrayAdapterArCli);
            spinnerArCli.setSelection(0);

        }else{

            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_spinner_item, new String[]{"Não cadastrado"});
            //arrayAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerArCli.setAdapter(arrayAdapter1);
            spinnerArCli.setSelection(0);
        }
        rbPreven = (RadioButton) findViewById(R.id.rbPreventiva);
        rbCorret = (RadioButton) findViewById(R.id.rbCorretiva);
        spiService = (Spinner) findViewById(R.id.spinnerService);
        spiPecaPro = (Spinner) findViewById(R.id.spinnerPecaPro);
        etOBS = (EditText) findViewById(R.id.etObs);
        btnEditAr = (ImageButton) findViewById(R.id.btnEditAr);
        btnAddPecs = (ImageButton) findViewById(R.id.btnAddPecs);
        btnAddServ = (ImageButton) findViewById(R.id.btnAddServ);

        mToolbar = (Toolbar) findViewById(R.id.tb_os);
        mToolbar.setTitle("ORDEM DE SERVIÇO");
        //mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mToobarBotton = (Toolbar) findViewById(R.id.inc_tb_botton);
        mToobarBotton.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                //Inicializando o QRcode zxing
                IntentIntegrator integrator = new IntentIntegrator(OrdemServiceActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Autenticando Assinatura Cliente!!!");
                integrator.setCameraId(0);  // Use a specific camera of the device
                integrator.setBeepEnabled(true);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();


                return true;
            }
        });

        mToobarBotton.inflateMenu(R.menu.menu_botton_os);

        rbPreven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbCorret.setChecked(false);
                tipo_manu = 0;
            }
        });
        rbCorret.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rbPreven.setChecked(false);
                tipo_manu = 1;
            }
        });

        btnEditAr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!refrigeradoresCli.equals(null)) {
                    arCliSpinnerOS =  refrigeradoresCli;
                }
                Intent i = new Intent(getApplicationContext(), EditArActivity.class);  //your class
                startActivity(i);
                finish();
            }
        });

        btnAddPecs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PecsActivity.class);  //your class
                startActivity(i);
            }
        });
        btnAddServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ServicesDescriActivity.class);  //your class
                startActivity(i);
            }
        });
        cacheDadosOS();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Log.e("MainActivity", "Leitura Cancelada");
                Toast.makeText(getApplicationContext(), "Leitura Cancelada", Toast.LENGTH_LONG).show();
            } else {
                Log.e("MainActivity", "Scanned "+ result.getContents());
                Toast.makeText(getApplicationContext(), "Codigo Lido: " + result.getContents(), Toast.LENGTH_LONG).show();


                if(DetalhesMyService.servPen.getCliente_id() == Integer.parseInt(result.getContents())) {
                    DataHoraNow dataNow = new DataHoraNow();

                    pDialog.setMessage("Finalizando...");
                    showDialog();

                    addOrdemService(DetalhesMyService.servPen.getCliente_id(), db.getUserDetails().getMatricula(), tipo_manu, etOBS.getText().toString(),
                            dataNow.getDataNow().substring(6, 10) + "-" +
                                    dataNow.getDataNow().substring(3, 5) + "-" +
                                    dataNow.getDataNow().substring(0, 2), DetalhesMyService.servPen.getHora_serv(), dataNow.getHoraNow().substring(0, 2) + ":" +
                                    dataNow.getHoraNow().substring(3, 5) + ":" +
                                    dataNow.getHoraNow().substring(6, 8));

                    updateStatus(DetalhesMyService.servPen.getId_serv_pen(), "Realizado");
                    db.deleteDadosOScahe(DetalhesMyService.servPen.getCliente_id());

                    Log.e(TAG, "----------------------Fim ADD OS  " + dataNow.getDataNow().substring(6, 10));
                    // Launch DetalhesMyService activity
                }else Toast.makeText(getApplicationContext(),"Autenticação Invalida!!!",Toast.LENGTH_LONG).show();

            }
        } else {
            // This is important, otherwise the result will not be passed to the fragment
            Log.e(TAG,"erro ----- null");
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            db.deleteDadosOScahe(DetalhesMyService.servPen.getCliente_id());
            int tipoManu;
            if(rbCorret.isChecked()){
                tipoManu = 2;
            }else tipoManu = 1;
            db.addDadosOScache(DetalhesMyService.servPen.getCliente_id(),tipoManu,spiService.getSelectedItemPosition(),spiPecaPro.getSelectedItemPosition(),etOBS.getText().toString());

            Intent i = new Intent(getApplicationContext(), DetalhesMyService.class);  //your class
            startActivity(i);
            finish();
        }

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        db = new SQLiteHandler(getApplicationContext());

        refrigeradoresCli = db.getArCli(DetalhesMyService.servPen.getId_refriCli());
        if(refrigeradoresCli.getId_refri() != 0){
            List<String> refrigeraCli = new ArrayList<String>();
            refrigeraCli.add(db.getNomeMaca(refrigeradoresCli.getMarca())+" / "+
                    db.getNomeBTU(refrigeradoresCli.getCapaci_termica())+" BTUs - " +
                    DetalhesMyService.servPen.getLotacionamento());

            ArrayAdapter<String> arrayAdapterArCli = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_spinner_item,refrigeraCli);
            //arrayAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerArCli.setAdapter(arrayAdapterArCli);
            spinnerArCli.setSelection(0);

        }else{

            ArrayAdapter<String> arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(),R.layout.simple_spinner_item, new String[]{"Não cadastrado"});
            //arrayAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spinnerArCli.setAdapter(arrayAdapter1);
            spinnerArCli.setSelection(0);
        }

        if(!listServices.isEmpty()){
            ArrayAdapter<String> arrayAdapterService = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, listServices);
            //arrayAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spiService.setAdapter(arrayAdapterService);
        }
        if(!listPecs.isEmpty()) {
            ArrayAdapter<String> arrayAdapterPecs = new ArrayAdapter<String>(getApplicationContext(), R.layout.simple_spinner_item, listPecs);
            //arrayAdapter1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spiPecaPro.setAdapter(arrayAdapterPecs);
        }
    }


    private void cacheDadosOS(){
        String[] dadosCacheOS = db.getDadosOScache(DetalhesMyService.servPen.getCliente_id());
        if(dadosCacheOS != null) {
            int tipoManu = Integer.parseInt(dadosCacheOS[1]);
            if (tipoManu == 2) {
                rbCorret.setChecked(true);
                rbPreven.setChecked(false);
            } else {
                rbPreven.setChecked(true);
                rbCorret.setChecked(false);
            }
            spiService.setSelection(Integer.parseInt(dadosCacheOS[2]));
            spiPecaPro.setSelection(Integer.parseInt(dadosCacheOS[3]));
            etOBS.setText(dadosCacheOS[4]);
        }
    }

    private void addOrdemService(final int id_cli, final String matri_func, final int tipo_manu, final String obs,
                                 final String data, final String hora_ini, final String hora_fin) {
        // Tag used to cancel the request
        String tag_string_req = "req_os";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERIR_OS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Ordem de Serviço: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    int lastID = jObj.getInt("last_os");
                    if (!error) {
                        // User successfully stored in MySQL


                        // Inserting row in users table
                        db.addOS(lastID, id_cli, matri_func, tipo_manu, obs, data, hora_ini, hora_fin);
                        for(int x=0; x < listPecs.size(); x++) {
                            addPecsProbOs(db.getIdPecs(listPecs.get(x)), lastID);
                        }
                        for(int x=0; x < listServices.size(); x++) {
                            addServFuncOS(db.getIdServices(listServices.get(x)), lastID);
                        }
                        Toast.makeText(getApplicationContext(), "Ordem de Serviço salva no Servidor!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "-------------------------add OS" );
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Erro ao salvar Ordem de Serviço: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_cliente", String.valueOf(id_cli));
                params.put("matri_func", matri_func);
                params.put("tipo_manu", String.valueOf(tipo_manu));
                params.put("obs", obs);
                params.put("data", data);
                params.put("hora_ini", hora_ini);
                params.put("hora_fin", hora_fin);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addPecsProbOs(final int id_pc, final int id_pc_os) {
        // Tag used to cancel the request
        String tag_string_req = "req_pecs_prob_os";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERIR_PCS_PROB, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Peças com problema da Os: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL


                        // Inserting row in users table
                        db.addPcsProbleOS(id_pc, id_pc_os);

                        Toast.makeText(getApplicationContext(), "peças com problema salvas no Servidor!", Toast.LENGTH_LONG).show();

                        Log.e(TAG, "-------------------------addPcsProbleOS" );
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Erro ao salvar Ordem de Serviço: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_pc", String.valueOf(id_pc));
                params.put("id_os", String.valueOf(id_pc_os));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void addServFuncOS(final int id_service, final int id_service_os) {
        // Tag used to cancel the request
        String tag_string_req = "req_serv_func_os";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_INSERIR_SERV_FUNC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Serviços da Os: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        // User successfully stored in MySQL


                        // Inserting row in users table
                        db.addServicesFuncOS(id_service, id_service_os);

                        Toast.makeText(getApplicationContext(), "Serviços do funcionario salvas no Servidor!", Toast.LENGTH_LONG).show();
                        Log.e(TAG, "-------------------------addServFuncOS" );
                    } else {

                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Erro ao salvar serviços do funcionario: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_service", String.valueOf(id_service));
                params.put("id_os", String.valueOf(id_service_os));

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void updateStatus(final int id_serv, final String new_status) {
        Log.d(TAG, ">>>>>>>: "+id_serv +" -- "+new_status );
        // Tag used to cancel the request
        String tag_string_req = "req_new_status";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_STATUS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Status Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.e("Update Status: ", "Status Atualizado com sucesso!!!" );
                        db = new SQLiteHandler(getApplicationContext());

                        db.updateStatusServ(id_serv, new_status);
                        //db.deleteMyServPen(DetalhesMyService.servPen.getId_serv_pen());

                        hideDialog();
                        Intent intent = new Intent(
                                getApplicationContext(),
                                ListMyServicesActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in login. Get the error message
                        Log.e("Errro in new Status: ", "Erro ao atualizar novo status!!!" );
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "New Status Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Erro ao ATUALIZAÇÃO DO STATUS", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_serv", String.valueOf(id_serv));
                params.put("newStatus", new_status);

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
