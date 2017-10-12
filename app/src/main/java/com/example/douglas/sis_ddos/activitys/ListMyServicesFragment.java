package com.example.douglas.sis_ddos.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.adapters.ListMyServiceAdapter;
import com.example.douglas.sis_ddos.app.AppConfig;
import com.example.douglas.sis_ddos.app.AppController;
import com.example.douglas.sis_ddos.controler.ServPendenteCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListMyServicesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListMyServicesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListMyServicesFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener  {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ListMyServicesFragment.class.getSimpleName();
    private ListView lvMyServ;
    private ProgressDialog pDialog;
    private SQLiteHandler db;
    private List<ServPendenteCtrl> myServPens;

    public static int servPenPosition = 0;
    public static ServPendenteCtrl servMyPen;
    private Toolbar listMyServToolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListMyServiceAdapter listServApd;
    private AlertDialog alerta;
    private boolean confirExcluir = false;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ListMyServicesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListMyServicesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListMyServicesFragment newInstance(String param1, String param2) {
        ListMyServicesFragment fragment = new ListMyServicesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_my_services, container, false);


        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_my_serv);
        lvMyServ = (ListView) view.findViewById(R.id.lvMyService);


        //cria o AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        alerta = builder.create();


        // Progress dialog
        pDialog = new ProgressDialog(getContext());
        pDialog.setCancelable(false);

        db = new SQLiteHandler(getContext());

        servMyPen = new ServPendenteCtrl();

        myServPens = new ArrayList<ServPendenteCtrl>();

        listServApd = new ListMyServiceAdapter(getContext(), myServPens);
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


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myListaServPen();

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onRefresh() {
        myListaServPen();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if((adapterView.getAdapter().getItemId(position) != -1) && !alerta.isShowing()) {
            servPenPosition = (int) adapterView.getAdapter().getItemId(position);
            servMyPen = myServPens.get(position);
            Toast.makeText(getContext(), "ServPen position: " + servPenPosition, Toast.LENGTH_LONG).show();
            Log.e("Click lista ", "Posição do click" + servPenPosition);
            Intent it = new Intent(getContext(), DetalhesMyService.class);
            startActivity(it);
            //getActivity().finish();
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(adapterView.getAdapter().getItemId(position) != -1) {
            //lvMyServ.removeViewAt(position);
            confirmCancelServ(myServPens.get(position), position);


            Log.e("Click Longo lista ", "Posição do click" + position);
        }
        return false;
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
                    } else {
                        // Error in login. Get the error message
                        Log.e("Errro in new Status: ", "Erro ao atualizar novo status!!!" );
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "New Status Error: " + error.getMessage());
                Toast.makeText(getContext(),
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

    private void updateMatriFunc(final int id_serv, final String matriFunc) {
        Log.d(TAG, ">>>>>>>: "+id_serv +" -- "+matriFunc );
        // Tag used to cancel the request
        String tag_string_req = "req_matri_func";


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_MATRI_FUNC, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Update Status Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        Log.e("Update Status: ", "Matricula do funcionario atualizada com sucesso no Serviço!!!" );
                    } else {
                        // Error in login. Get the error message
                        Log.e("Errro in new Status: ", "Erro ao atualizar Matricula do funcionario no Serviço!!!" );
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "New Status Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        "Erro ao ATUALIZAÇÃO DO MatriFUNCTec", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_serv", String.valueOf(id_serv));
                params.put("newMatriFunc", matriFunc);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void confirmCancelServ(final ServPendenteCtrl servPen, final int position){
        //Cria o gerador do AlertDialog
        db = new SQLiteHandler(getContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        //define o titulo
        builder.setTitle("Confirmar Cancelamento de Serviço");
        //define a mensagem
        builder.setMessage("Você deseja Cancelar este Serviço?");
        //define um botão como positivo
        builder.setPositiveButton("Sim",new DialogInterface.OnClickListener()

        {
            public void onClick (DialogInterface arg0, int arg1){
                //Toast.makeText(getApplicationContext(), "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                    updateStatus(servPen.getId_serv_pen(), "Pendente");
                    updateMatriFunc(servPen.getId_serv_pen(), "");
                    db.deleteMyServPen(servPen.getId_serv_pen());
                    db.deleteUniRefrigera(servPen.getId_refriCli());

                    myServPens.remove(position);
                    listServApd.notifyDataSetChanged();


            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não",new DialogInterface.OnClickListener()

        {
            public void onClick (DialogInterface arg0, int arg1){
                Toast.makeText(getContext(), "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
}
