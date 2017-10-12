package com.example.douglas.sis_ddos.activitys;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.adapters.ListServPenAdapter;
import com.example.douglas.sis_ddos.controler.RefrigeradorCtrl;
import com.example.douglas.sis_ddos.controler.ServPendenteCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;


public class DetalhesMyService extends AppCompatActivity {
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
    ListServPenAdapter listServApd;

    public static ServPendenteCtrl servPen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes_my_service);

        db = new SQLiteHandler(getApplicationContext());
        servPen = db.getMyServiceUidade(ListMyServicesFragment.servPenPosition);

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

        imgViewFoto1 = (ImageView)findViewById(R.id.imgViewFoto1);
        imgViewFoto2 = (ImageView)findViewById(R.id.imgViewFoto2);
        imgViewFoto3 = (ImageView)findViewById(R.id.imgViewFoto3);

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
                //confirmRealizarServ(servPen, db.getUserDetails().getName());

                Intent it = new Intent(getApplicationContext(), OrdemServiceActivity.class);
                startActivity(it);
                finish();
                return true;
            }
        });
        mToobarBotton.inflateMenu(R.menu.menu_botton_detal_my_serv);

        btnMapa.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        LocalServActivity.class);
                startActivity(i);
                //finish();
            }
        });

        btnFone1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + btnFone1.getText().toString());
                Intent i = new Intent(Intent.ACTION_DIAL, uri);

                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    //return;
                    startActivity(i);
                }
                //finish();
            }
        });

        btnFone2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + btnFone2.getText().toString());
                Intent i = new Intent(Intent.ACTION_DIAL, uri);

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
            //Intent i = new Intent(getApplicationContext(), MenuDrawerActivity.class);  //your class
            //startActivity(i);
            finish();
        }

        return true;
    }

    private void detalhesServPens() {

        Log.e("REfrigerador: ", "-->>---" + ListMyServicesFragment.servMyPen.getId_refriCli());
        RefrigeradorCtrl refri = null;
        try {
            refri = db.getArCli(servPen.getId_refriCli());
        }catch (Exception ex){
            Log.e("|Sem Refrigerador","Sem REfrigerador cadastrado");
        }
        Log.e("SercPen DATA: ", ">>>> " + servPen.getData_serv());
        Log.e("SercPen HORA: ", ">>>> " + servPen.getHora_serv());

        mToolbar.setTitle(servPen.getNomeCli() + ": " + servPen.getTipoCli());

        txtData.setText(servPen.getData_serv());
        txtHora.setText(servPen.getHora_serv());
        txtEnder.setText(servPen.getEnder());
        txtComplemento.setText(servPen.getComplemento());
        txtCep.setText(servPen.getCep());
        txtDescriTec.setText(servPen.getDescri_tecni_problem());
        txtDescriCli.setText(servPen.getDescri_cli_problem());
        txtDescriRefri.setText(servPen.getDescri_cli_refrigera());
        btnFone1.setText(servPen.getFone1());
        btnFone2.setText(servPen.getFone2());
        try {
            if (refri != null && refri.getFoto1().length > 0) {
                //Fotos
                Bitmap bitmap1 = BitmapFactory.decodeByteArray(refri.getFoto1(), 0, refri.getFoto1().length);
                imgViewFoto1.setImageBitmap(resizeImage(this, bitmap1, 600, 500));
                Bitmap bitmap2 = BitmapFactory.decodeByteArray(refri.getFoto2(), 0, refri.getFoto2().length);
                imgViewFoto2.setImageBitmap(resizeImage(this, bitmap2, 600, 500));
                Bitmap bitmap3 = BitmapFactory.decodeByteArray(refri.getFoto3(), 0, refri.getFoto3().length);
                imgViewFoto3.setImageBitmap(resizeImage(this, bitmap3, 600, 500));
            }
        }catch(Exception e){Log.e(TAG,"Error  --  :"+e);}
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


    private void confirmRealizarServ(final ServPendenteCtrl servPen, final String new_status){
        //Cria o gerador do AlertDialog
        db = new SQLiteHandler(getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //define o titulo
        builder.setTitle("Confirmação de Serviço");
        //define a mensagem
        builder.setMessage("Você deseja Realizar este Serviço?");
        //define um botão como positivo
        builder.setPositiveButton("Sim",new DialogInterface.OnClickListener()

        {
            public void onClick (DialogInterface arg0, int arg1){
                Toast.makeText(getApplicationContext(), "positivo=" + arg1, Toast.LENGTH_SHORT).show();

                //updateStatus(servPen.getId_serv_pen(), new_status);
                //db.addMyServPen(servPen, new_status);
                //ListServPendenteActivity.servPens.remove(ListServPendenteActivity.servPenPositionList);
                //listServApd.notifyDataSetChanged();
//                Intent it = new Intent(getApplicationContext(), ListServPendenteActivity.class);
//                startActivity(it);
//                finish();
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
}
