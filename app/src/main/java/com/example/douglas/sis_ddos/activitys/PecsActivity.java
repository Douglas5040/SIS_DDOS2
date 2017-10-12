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
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.adapters.PecsAdapter;
import com.example.douglas.sis_ddos.controler.PecsCtrl;
import com.example.douglas.sis_ddos.controler.ServicesCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;

public class PecsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener,  AdapterView.OnItemLongClickListener{
    private static final String TAG = PecsActivity.class.getSimpleName();
    private ListView lvPecs;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private CheckBox checkBoxPecs;
    private TextView txtNomePecs;
    public List<PecsCtrl> pecs;
    public ServicesCtrl pecUNI;

    private Toolbar pecsToolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    PecsAdapter pecsApd;
    AdapterView<?> pcsAdpt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pecs);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_pecs);

        lvPecs = (ListView) findViewById(R.id.lvPecs);

        pecsToolbar = (Toolbar) findViewById(R.id.tb_pecs);
        pecsToolbar.setTitle("Lista de Peças");
        pecsToolbar.setSubtitle("Subtitulo");
        pecsToolbar.setLogo(R.drawable.googleg_standard_color_18);
        setSupportActionBar(pecsToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //checkBoxService = (CheckBox) findViewById(R.id.checkBoxService);
        //checkBoxService.set

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);


        pecs = new ArrayList<PecsCtrl>();
        listaPecs();
        pecsApd = new PecsAdapter(getApplicationContext(), pecs);
        lvPecs.setAdapter(pecsApd);

        Log.e("TAMANHO PECS","ENTROU AQUI: "+ pecs.size());

        swipeRefreshLayout.setOnRefreshListener(this);

        //listaServPen();
        swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        swipeRefreshLayout.setRefreshing(true);
                                        listaPecs();
                                    }
                                }
        );
        lvPecs.setOnItemClickListener(this);


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        if(id == android.R.id.home){
            //pcsAdpt = new AdapterView<?>();
            if(pcsAdpt != null) {
                OrdemServiceActivity.listPecs.clear();
                for (int x = 0; x < pecs.size(); x++) {
                    Log.e("Entrou no FOR", " " + x);
                    checkBoxPecs = (CheckBox) pcsAdpt.getChildAt(x).findViewById(R.id.checkBoxPecs);
                    Log.e("CheckBox", "Valor Checkbox: " + checkBoxPecs.isChecked());

                    if (checkBoxPecs.isChecked()) {
                        OrdemServiceActivity.listPecs.add(pecs.get(x).getNome());
                    }
                }
            }
                finish();
            Log.e("Tamanho da lista","Lista de PEcs retornada: "+ OrdemServiceActivity.listPecs.size());
        }

        return true;
    }
    @Override
    public void onRefresh() {
        listaPecs();
    }

    @Override
    public void onItemClick(AdapterView<?> pcAdpt, View view, int position, long id) {
        checkBoxPecs = (CheckBox) pcAdpt.getChildAt(position).findViewById(R.id.checkBoxPecs);

        if(checkBoxPecs.isChecked()){
            checkBoxPecs.setChecked(false);
        }else checkBoxPecs.setChecked(true);
        pcsAdpt = pcAdpt;
        Toast.makeText(getApplicationContext(), "Service position nome: "+pecs.get(position).getNome(), Toast.LENGTH_LONG).show();

    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        return false;
    }

    private void listaPecs() {

        db = new SQLiteHandler(getApplicationContext());
        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);
        pDialog.setMessage("Carregando...");
        showDialog();
        try{
            pecs = db.getPecs();
            Log.e("Tamanho lis pecs: ",""+pecs.size());

        }catch (Exception ex){
            Toast.makeText(this, "Erro ao retornar lista de PEÇAS: "+ex, Toast.LENGTH_SHORT).show();
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
