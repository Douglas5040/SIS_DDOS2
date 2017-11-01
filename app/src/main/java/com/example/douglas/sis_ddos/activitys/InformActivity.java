package com.example.douglas.sis_ddos.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.controler.UserFuncionarioCtrl;
import com.example.douglas.sis_ddos.helper.SQLiteHandler;

public class InformActivity extends AppCompatActivity {

    private TextView txtMatri;
    private TextView txtNome;
    private TextView txtEmail;

    private Toolbar tbInfo;
    private SQLiteHandler db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inform);

        txtMatri = (TextView)findViewById(R.id.textMatricula);
        txtNome = (TextView)findViewById(R.id.textNome);
        txtEmail = (TextView)findViewById(R.id.textEmail);

        db = new SQLiteHandler(getApplicationContext());

        UserFuncionarioCtrl userFunc = new UserFuncionarioCtrl();
        userFunc = db.getUserDetails();
        txtMatri.setText(userFunc.getMatricula());
        txtNome.setText(userFunc.getName());
        txtEmail.setText(userFunc.getEmail());

        tbInfo = (Toolbar) findViewById(R.id.tb_info_func);
        tbInfo.setTitle("INFORMAÇÕES");
        //mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(tbInfo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            Intent i = new Intent(getApplicationContext(), MenuDrawerActivity.class);  //your class
            startActivity(i);
            finish();
        }

        return true;
    }
}
