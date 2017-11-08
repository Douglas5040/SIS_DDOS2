package com.example.douglas.sis_ddos.activitys;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListServPendenteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListServPendenteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListServPendenteFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String TAG = ListServPendenteFragment.class.getSimpleName();
    private ListView lvServPen;
    private SQLiteHandler db;
    public static int servPenPosition = 0;
    public static int servPenPositionList = 0;
    public List<ServPendenteCtrl> servPens;
    public static ServPendenteCtrl servPen;

    private Toolbar listServToolbar;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListServPenAdapter listServApd;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ListServPendenteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListServPendenteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListServPendenteFragment newInstance(String param1, String param2) {
        ListServPendenteFragment fragment = new ListServPendenteFragment();
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
        //view.destroyDrawingCache();
        
        View view = inflater.inflate(R.layout.fragment_list_serv_pendente, container, false);

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        lvServPen = (ListView) view.findViewById(R.id.lvServPen);



        db = new SQLiteHandler(getContext());

        servPens = new ArrayList<ServPendenteCtrl>();

        listServApd = new ListServPenAdapter(getContext(), servPens);
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
                                        listServApd.notifyDataSetChanged();
                                    }
                                }
        );

        lvServPen.setOnItemClickListener(this);

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        listaServPen("Pendente"," ");
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
        listaServPen("Pendente"," ");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        if(adapterView.getAdapter().getItemId(position) != -1) {
            servPen = servPens.get(position);
            Toast.makeText(getContext(), "ServPen position nome: " + servPen.getNomeCli(), Toast.LENGTH_LONG).show();
            Log.e("Click lista ", "Posição do click: " + position);
            Intent it = new Intent(getContext(), DetalhesServPen.class);
            startActivity(it);
            //getActivity().finish();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void listaServPen(final String status, final String matriFunc) {

        // showing refresh animation before making http call
        swipeRefreshLayout.setRefreshing(true);

        // Tag used to cancel the request
        String tag_string_req = "req_listaServPen";
        //final List<ServPendenteCtrl> listSerPen =null;


        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_SERV_PEN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados: " + response.toString());

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
                                objetoServPen.setId_refriCli(serv_penObj.getInt("id_refriCli"));
                                objetoServPen.setUid_cliente(serv_penObj.getString("unique_id"));

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
                        Toast.makeText(getContext(), "Erro AQUI "+errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Log.e("ERRORRRRR","Json error: " + e.getMessage());
                    Toast.makeText(getContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
                // stopping swipe refresh
                swipeRefreshLayout.setRefreshing(false);

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Lista Service Error: " + error.getMessage());
                ServPendenteCtrl objetoServPen = new ServPendenteCtrl();
                objetoServPen.setTipoCli(":SEM SERVIÇOS PENDENTES NO MOMENTO");
                objetoServPen.setData_serv("");
                objetoServPen.setHora_serv("");
                objetoServPen.setEnder("");
                objetoServPen.setNomeCli("  ");
                objetoServPen.setId_serv_pen(-1);

                servPens.add(objetoServPen);
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


}
