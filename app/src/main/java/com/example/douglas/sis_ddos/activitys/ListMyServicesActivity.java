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

import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.adapters.ListMyServiceAdapter;
import com.example.douglas.sis_ddos.controler.ServPendenteCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;

import java.util.ArrayList;
import java.util.List;

public class ListMyServicesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private static final String TAG = ListServPendenteActivity.class.getSimpleName();
    private ListView lvMyServ;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private List<ServPendenteCtrl> myServPens;

    public static int servPenPosition = 0;
    private Toolbar listMyServToolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListMyServiceAdapter listServApd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_my_services);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_my_serv);
        lvMyServ = (ListView) findViewById(R.id.lvMyService);

        listMyServToolbar = (Toolbar) findViewById(R.id.tb_list_my_serv);
        listMyServToolbar.setTitle("Lista dos Meus Serviços ");
        //listMyServToolbar.setSubtitle("Subtitulo");
        listMyServToolbar.setLogo(R.drawable.googleg_standard_color_18);
        setSupportActionBar(listMyServToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);


        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        myServPens = new ArrayList<ServPendenteCtrl>();

        listServApd = new ListMyServiceAdapter(getApplicationContext(), myServPens);
        lvMyServ.setAdapter(listServApd);
        Log.e("BOTAO","ENTROU AQUI: "+ myServPens.size());

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

                                        db.getAllMyServPen();
                                        myListaServPen();
                                    }
                                }
        );

        lvMyServ.setOnItemClickListener(this);
        lvMyServ.setOnItemLongClickListener(this);

    }

    @Override
    public void onRefresh() {
        myListaServPen();
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

    @Override
    protected void onStart() {
        super.onStart();
        myListaServPen();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        listMyServToolbar.setTitle("Lista dos Meus Serviços ");
        myListaServPen();
    }

    private void myListaServPen() {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        pDialog.setMessage("Carregando...");
        showDialog();

        myServPens.clear();
        myServPens.addAll(db.getAllMyServPen());
        if(myServPens.isEmpty()) {
            ServPendenteCtrl objetoServPen = new ServPendenteCtrl();
            objetoServPen.setTipoCli("SEM SERVIÇOS PENDENTES NO MOMENTO");
            objetoServPen.setData_serv("");
            objetoServPen.setHora_serv("");
            objetoServPen.setNomeCli("  ");
            objetoServPen.setEnder("");
            objetoServPen.setId_serv_pen(-1);

            myServPens.add(objetoServPen);
            hideDialog();
            // stopping swipe refresh

            listServApd.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
        //myServPens = db.getMyServPen(db.getUserDetails().getName());


//        Log.e("ITEM LISTA: ",""+db.getAllMyServPen().get(1).getNomeCli());
//        Log.e("ITEM LISTA: ",""+db.getAllMyServPen().get(2).getNomeCli());
//        Log.e("ITEM LISTA: ",""+db.getAllMyServPen().get(3).getNomeCli());
//        Log.e("ITEM LISTA: ",""+db.getAllMyServPen().get(4).getNomeCli());
//        Log.e("Dados sqlite: ", ""+db.getAllMyServPen().size());
        //db.getAllMyServPen();
        listServApd.notifyDataSetChanged();

        // stopping swipe refresh
        swipeRefreshLayout.setRefreshing(false);
        hideDialog();
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
        if(adapterView.getAdapter().getItemId(position) != -1) {
            servPenPosition = (int) adapterView.getAdapter().getItemId(position);
            Toast.makeText(getApplicationContext(), "ServPen position: " + servPenPosition, Toast.LENGTH_LONG).show();
            Log.e("Click lista ", "Posição do click" + servPenPosition);
            Intent it = new Intent(getApplicationContext(), DetalhesMyService.class);
            startActivity(it);
            finish();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView.getAdapter().getItemId(position) != -1) {
            lvMyServ.removeView(view);
            Log.e("Click Longo lista ", "Posição do click" + position);
        }
        return false;
    }
}
