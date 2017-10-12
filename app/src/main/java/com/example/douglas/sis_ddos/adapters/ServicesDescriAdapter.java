package com.example.douglas.sis_ddos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.activitys.OrdemServiceActivity;
import com.example.douglas.sis_ddos.controler.ServicesCtrl;

import java.util.List;

public class ServicesDescriAdapter extends BaseAdapter {

    private List<ServicesCtrl> services;
    private Context context;

    public ServicesDescriAdapter(){}
    public ServicesDescriAdapter(Context context, List<ServicesCtrl> services) {
        this.services = services;
        this.context = context;
    }
    @Override
    public int getCount() {
        return services.size();
    }

    @Override
    public Object getItem(int position) {
        return services.get(position);
    }

    @Override
    public long getItemId(int position) {
        return services.get(position).getId_service();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_services_adapter, parent, false);

        TextView tvNomeServ = (TextView) rootView.findViewById(R.id.nomeService);
        TextView tvDescriServ = (TextView) rootView.findViewById(R.id.descriService);
        CheckBox cbServices = (CheckBox) rootView.findViewById(R.id.checkBoxService);

//ajeitar o listview para listar os itens do banco na sequencia certa
        ServicesCtrl msgDaVez = services.get(position);


        tvNomeServ.setText(msgDaVez.getNome());
        tvDescriServ.setText(msgDaVez.getDescri());
        if(!OrdemServiceActivity.listServices.isEmpty()){
            for(int x = 0; x< OrdemServiceActivity.listServices.size(); x++){
                if(OrdemServiceActivity.listServices.get(x).equals(msgDaVez.getNome())) cbServices.setChecked(true);
            }
        }else cbServices.setChecked(false);

        return rootView;
    }
}
