package com.example.douglas.sis_ddos.activitys;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.renderscript.Byte2;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.adapters.ListServPenAdapter;
import com.example.douglas.sis_ddos.app.AppConfig;
import com.example.douglas.sis_ddos.app.AppController;
import com.example.douglas.sis_ddos.controler.RefrigeradorCtrl;
import com.example.douglas.sis_ddos.controler.ServPendenteCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.lang.Runnable;

public class DetalhesServPen extends AppCompatActivity implements Runnable{
    private static final String TAG = DetalhesServPen.class.getSimpleName();
    private TextView txtData;
    private TextView txtHora;
    private TextView txtEnder;
    private TextView txtComplemento;
    private TextView txtCep;
    private TextView txtDescriTec;
    private TextView txtDescriCli;
    private TextView txtDescriRefri;
    private Button btnFone1;
    private Button btnFone2;
    private Button btnMapa;
    private ImageView imgViewFoto1;
    private ImageView imgViewFoto2;
    private ImageView imgViewFoto3;
    private SQLiteHandler db;
    private Toolbar mToolbar;
    private Toolbar mToobarBotton;
    private AlertDialog alerta;
    private String urlFoto1;
    private String urlFoto2;
    private String urlFoto3;
    private Handler handler = new Handler();
    ListServPenAdapter listServApd;
    RefrigeradorCtrl refriCli;

    public DetalhesServPen() throws IOException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_serv_pen);

        db = new SQLiteHandler(getApplicationContext());
        refriCli = new RefrigeradorCtrl();
        //alerta = new AlertDialog.Builder(getApplicationContext()).create();

        txtData = (TextView) findViewById(R.id.dataView);
        txtHora = (TextView) findViewById(R.id.horaView);
        txtEnder = (TextView) findViewById(R.id.tvEnder);
        txtComplemento = (TextView) findViewById(R.id.tvComplem);
        txtCep = (TextView) findViewById(R.id.tvCep);
        txtDescriTec = (TextView) findViewById(R.id.tvDescrTec);
        txtDescriCli = (TextView) findViewById(R.id.tvDescriCli);
        txtDescriRefri = (TextView) findViewById(R.id.tvDEscriRefri);
        btnFone1 = (Button) findViewById(R.id.btnFone1);
        btnFone2 = (Button) findViewById(R.id.btnFone2);
        btnMapa = (Button) findViewById(R.id.btnMapServPen);

        imgViewFoto1 = (ImageView) findViewById(R.id.imageView1FotoServPen);
        imgViewFoto2 = (ImageView) findViewById(R.id.imageView2FotoServPen);
        imgViewFoto3 = (ImageView) findViewById(R.id.imageView3FotoServPen);


        mToolbar = (Toolbar) findViewById(R.id.tb_detal_serv);
        mToolbar.setTitle("Lista de Serviços Pendentes");
        //mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mToobarBotton = (Toolbar) findViewById(R.id.inc_tb_botton);
        mToobarBotton.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //updateStatus(servPen.getId_serv_pen(), db.getUserDetails().getName());
                confirmRealizarServ(ListServPendenteFragment.servPen, db.getUserDetails().getMatricula());

//                Intent it = null;
//
//                if(item.getItemId() ==  R.id.action_settings){
//                        it = new Intent(Intent.ACTION_VIEW);
//                        it.setData(Uri.parse("http://www.google.com"));
//                        startActivity(it);
//                }
                return true;
            }
        });
        mToobarBotton.inflateMenu(R.menu.menu_botton);

        btnMapa.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LocalServActivity.class);
                startActivity(i);

                //finish();
            }
        });
        detalhesServPens();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
//            Intent i = new Intent(getApplicationContext(), ListServPendenteFragment.class);  //your class
//            startActivity(i);
            finish();
        }

        return true;
    }

    private void detalhesServPens() {

        Log.e("SercPen Position: ", "-->>---" + ListServPendenteFragment.servPenPosition);

         getArCli(ListServPendenteFragment.servPen.getId_refriCli());

        Log.e("SercPen DATA: ", ">>>> " + ListServPendenteFragment.servPen.getData_serv());
        Log.e("SercPen HORA: ", ">>>> " + ListServPendenteFragment.servPen.getHora_serv());

        mToolbar.setTitle(ListServPendenteFragment.servPen.getNomeCli() + ": " + ListServPendenteFragment.servPen.getTipoCli());

        txtData.setText(ListServPendenteFragment.servPen.getData_serv());
        txtHora.setText(ListServPendenteFragment.servPen.getHora_serv());
        txtEnder.setText(ListServPendenteFragment.servPen.getEnder());
        txtComplemento.setText(ListServPendenteFragment.servPen.getComplemento());
        txtCep.setText(ListServPendenteFragment.servPen.getCep());
        txtDescriTec.setText(ListServPendenteFragment.servPen.getDescri_tecni_problem());
        txtDescriCli.setText(ListServPendenteFragment.servPen.getDescri_cli_problem());
        txtDescriRefri.setText(ListServPendenteFragment.servPen.getDescri_cli_refrigera());
        btnFone1.setText(ListServPendenteFragment.servPen.getFone1());
        btnFone2.setText(ListServPendenteFragment.servPen.getFone2());


    }

    private static Bitmap resizeImage(Context context, Bitmap bmpOriginal, float newWidth, float newHeight){
        Bitmap novoBmp = null;

        int w = bmpOriginal.getWidth();
        int h = bmpOriginal.getHeight();

        float densityFactor = context.getResources().getDisplayMetrics().density;
        float novoW = newWidth * densityFactor;
        float novoH = newHeight * densityFactor;

        //calcula escala e percentual do tamanho original
        float scalaW = novoW / w;
        float scalaH = novoH / h;

        //Criando uma matrix para manipulação da imagem bitmap
        Matrix matrix = new Matrix();

        //Definindo a proporção da escala para a Matrix
        matrix.postScale(scalaW, scalaH);

        //Criando novo bitmap com o novo tamanho
        novoBmp = Bitmap.createBitmap(bmpOriginal, 0, 0, w, h, matrix, true);

        return  novoBmp;
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

    private void confirmRealizarServ(final ServPendenteCtrl servPen, final String matriFunc){
        //Cria o gerador do AlertDialog
        db = new SQLiteHandler(getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(DetalhesServPen.this);
        //define o titulo
        builder.setTitle("Confirmação de Serviço");
        //define a mensagem
        builder.setMessage("Você deseja Realizar este Serviço?");
        //define um botão como positivo
        builder.setPositiveButton("Sim",new DialogInterface.OnClickListener()

        {
            public void onClick (DialogInterface arg0, int arg1){
                //Toast.makeText(getApplicationContext(), "positivo=" + arg1, Toast.LENGTH_SHORT).show();
                if(!db.verifyDataHoraServIfEquals(txtData.getText().toString(),txtHora.getText().toString())) {
                    updateStatus(servPen.getId_serv_pen(), "Fazendo");
                    updateMatriFunc(servPen.getId_serv_pen(), matriFunc);
                    db.addMyServPen(servPen, "Fazendo");
                    db.addRefrigerador(refriCli);


                    finish();
                }else Toast.makeText(getApplicationContext(), "Voçe ja tem um serviço pendente nesta DATA e HORA", Toast.LENGTH_LONG).show();
            }
        });
        //define um botão como negativo.
        builder.setNegativeButton("Não",new DialogInterface.OnClickListener()

        {
            public void onClick (DialogInterface arg0, int arg1){
                Toast.makeText(getApplicationContext(), "negativo=" + arg1, Toast.LENGTH_SHORT).show();
            }
        });
        //cria o AlertDialog
        alerta = builder.create();
        //Exibe
        alerta.show();
    }


    private void getArCli(final int idRefri) {

        // Tag used to cancel the request
        String tag_string_req = "req_RefrigeradorCliente";

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_RETORNA_AR_CLI, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Carregando dados REFRIGERADOR: " + response.toString());
                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    // Check for error node in json
                    if (!error) {

                        JSONArray arCliArray = jObj.getJSONArray("data");

                        Log.e("TESTE","ENTROU: "+arCliArray.length());
                        for (int i = 0; i < arCliArray.length(); i++) {
                            try {
                                JSONObject arCliObj = new JSONObject(arCliArray.get(i).toString());

                                Log.e("REFRIGERADOR : ", "Entrou no for AR ENCONTRADO");


                                refriCli.setId_refri(arCliObj.getInt("id_refri"));
                                refriCli.setPeso(arCliObj.getInt("peso"));
                                refriCli.setHas_control(arCliObj.getInt("has_control"));
                                refriCli.setHas_exaustor(arCliObj.getInt("has_exaustor"));
                                refriCli.setSaida_ar(arCliObj.getString("saida_ar"));
                                refriCli.setCapaci_termica(arCliObj.getInt("capaci_termica"));
                                refriCli.setTencao_tomada(arCliObj.getInt("tencao_tomada"));
                                refriCli.setHas_timer(arCliObj.getInt("has_timer"));
                                refriCli.setTipo_modelo(arCliObj.getInt("tipo_modelo"));
                                refriCli.setMarca(arCliObj.getInt("marca"));
                                refriCli.setTemp_uso(arCliObj.getInt("temp_uso"));
                                refriCli.setNivel_econo(arCliObj.getInt("nivel_econo"));
                                refriCli.setTamanho(arCliObj.getString("tamanho"));

                                urlFoto1 = arCliObj.getString("foto1");
                                urlFoto2 = arCliObj.getString("foto2");
                                urlFoto3 = arCliObj.getString("foto3");

                                new Thread(DetalhesServPen.this).start();

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
                Log.e(TAG, "Lista Ar Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        "Sem Refrigerador Cadastrado", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_ar",""+idRefri);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }

    public Bitmap downloadIMG(String url) throws IOException{
        try{
            //Cria a URL
            URL u = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            //configura a requesição para "get"
            connection.setRequestProperty("Request-Method","GET");
            connection.setDoInput(true);
            connection.setDoOutput(false);
            connection.connect();
            InputStream in = connection.getInputStream();
            //Arquivo
            byte[] bytes = readBytes(in);
            Log.e("DOWNLOAD IMG","Image Retornada com "+ bytes.length + "bytes");
            connection.disconnect();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }catch (MalformedURLException e){
            Log.e("DOWNLOAD IMG",e.getMessage(),e);
        }catch (IOException e){
            Log.e("DOWNLOAD IMG",e.getMessage(),e);
        }

        return null;
    }

    private byte[] readBytes(InputStream in) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try{
            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0){
                bos.write(buffer, 0, len);
            }
            byte[] bytes = bos.toByteArray();
            return bytes;
        }finally {
            bos.close();
            in.close();;
        }
    }

    @Override
    public void run() {
        try{
            if(!urlFoto1.equals("null") || !urlFoto1.equals(null)) {
                final Bitmap bitmap1 = downloadIMG(AppConfig.URL_DOWNLOAD_IMAGE+urlFoto1+".JPG");
                if(bitmap1 != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ByteArrayOutputStream stream1 = new ByteArrayOutputStream();
                            bitmap1.compress(Bitmap.CompressFormat.JPEG, 100, stream1);
                            byte[] byteArray1 = stream1.toByteArray();
                            refriCli.setFoto1(byteArray1);

                            imgViewFoto1.setImageBitmap(resizeImage(getApplicationContext(), bitmap1, 600, 500));
                        }
                    });
                }else{
                    refriCli.setFoto1(null);
                }
            }
            if(!urlFoto2.equals("null") || !urlFoto2.equals(null)) {
                final Bitmap bitmap2 = downloadIMG(AppConfig.URL_DOWNLOAD_IMAGE+urlFoto2+".JPG");
                if(bitmap2 != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                            bitmap2.compress(Bitmap.CompressFormat.JPEG, 100, stream2);
                            byte[] byteArray2 = stream2.toByteArray();
                            refriCli.setFoto2(byteArray2);

                            imgViewFoto2.setImageBitmap(resizeImage(getApplicationContext(), bitmap2, 600, 500));
                        }
                    });
                }else{
                    refriCli.setFoto2(null);
                }
            }
            if(!urlFoto3.equals("null") || !urlFoto3.equals(null)) {
                final Bitmap bitmap3 = downloadIMG(AppConfig.URL_DOWNLOAD_IMAGE+urlFoto3+".JPG");
                if(bitmap3 != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                            bitmap3.compress(Bitmap.CompressFormat.JPEG, 100, stream3);
                            byte[] byteArray3 = stream3.toByteArray();
                            refriCli.setFoto3(byteArray3);

                            imgViewFoto3.setImageBitmap(resizeImage(getApplicationContext(), bitmap3, 600, 500));
                        }
                    });
                }else{
                    refriCli.setFoto3(null);
                }
            }

        }catch (Throwable e){
            Log.e("DOWNLOAD IMG ",e.getMessage(),e);
        }
    }
}
