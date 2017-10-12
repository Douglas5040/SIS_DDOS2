package com.example.douglas.sis_ddos.activitys;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.adapters.ServicesDescriAdapter;
import com.example.douglas.sis_ddos.controler.ServicesCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;

public class ServicesDescriActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,  AdapterView.OnItemLongClickListener{
    private static final String TAG = ServicesDescriActivity.class.getSimpleName();
    private ListView lvServices;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private CheckBox checkBoxService;
    public List<ServicesCtrl> services;
    public ServicesCtrl serviceUNI;


    private Toolbar ServicesToolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    ServicesDescriAdapter ServicesApd;
    AdapterView<?> ServicesAdpt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_descri);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_services);

        lvServices = (ListView) findViewById(R.id.lvServices);

        ServicesToolbar = (Toolbar) findViewById(R.id.tb_services);
        ServicesToolbar.setTitle("Lista de Serviços a fazer");
        ServicesToolbar.setSubtitle("Subtitulo");
        ServicesToolbar.setLogo(R.drawable.googleg_standard_color_18);
        setSupportActionBar(ServicesToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //checkBoxService = (CheckBox) findViewById(R.id.checkBoxService);
        //checkBoxService.set

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        services = new ArrayList<ServicesCtrl>();
        listaServices();

        ServicesApd = new ServicesDescriAdapter(getApplicationContext(), services);
        lvServices.setAdapter(ServicesApd);

        Log.e("BOTAO","ENTROU AQUI: "+ services.size());

        swipeRefreshLayout.setOnRefreshListener(this);

        //listaServPen();
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        listaServices();
                                    }
                                }
        );
        lvServices.setOnItemClickListener(this);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            if(ServicesAdpt != null) {
                OrdemServiceActivity.listServices.clear();
                for (int x = 0; x < services.size(); x++) {
                    Log.e("Entrou no FOR", " " + x);
                    checkBoxService = (CheckBox) ServicesAdpt.getChildAt(x).findViewById(R.id.checkBoxService);
                    Log.e("CheckBox", "Valor Checkbox: " + checkBoxService.isChecked());

                    if (checkBoxService.isChecked()) {
                        OrdemServiceActivity.listServices.add(services.get(x).getNome());
                    }
                }
            }
            finish();
            Log.e("Tamanho da lista","Lista de PEcs retornada: "+ OrdemServiceActivity.listPecs.size());
            finish();
        }

        return true;
    }

    @Override
    public void onRefresh() {
        listaServices();
    }

    @Override
    public void onItemClick(AdapterView<?> serviceAdpt, View view, int position, long id) {
        checkBoxService = (CheckBox) serviceAdpt.getChildAt(position).findViewById(R.id.checkBoxService);
        if(checkBoxService.isChecked()){
            checkBoxService.setChecked(false);
        }else checkBoxService.setChecked(true);
        ServicesAdpt = serviceAdpt;
        Toast.makeText(getApplicationContext(), "Service position nome: "+services.get(position).getNome(), Toast.LENGTH_LONG).show();
        //finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }
    private void listaServices() {

        db = new SQLiteHandler(getApplicationContext());
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        pDialog.setMessage("Carregando...");
        showDialog();
        try{
            services = db.getServices();
        }catch (Exception ex){
            Toast.makeText(this, "Erro ao retornar lista de Serviços: "+ex, Toast.LENGTH_SHORT).show();
            hideDialog();
            swipeRefreshLayout.setRefreshing(false);
        }

        hideDialog();
        swipeRefreshLayout.setRefreshing(false);

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
