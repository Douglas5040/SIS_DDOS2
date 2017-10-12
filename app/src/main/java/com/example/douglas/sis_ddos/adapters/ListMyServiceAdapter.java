package com.example.douglas.sis_ddos.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.douglas.sis_ddos.R;
import com.example.douglas.sis_ddos.controler.ServPendenteCtrl;

import java.util.List;

public class ListMyServiceAdapter extends BaseAdapter {

    private List<ServPendenteCtrl> myServs;
    private Context context;

    public ListMyServiceAdapter(){}
    public ListMyServiceAdapter(Context context, List<ServPendenteCtrl> serPens) {
        this.myServs = serPens;
        this.context = context;
    }
    @Override
    public int getCount() {
        return myServs.size();
    }

    @Override
    public Object getItem(int position) {
        return myServs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return myServs.get(position).getId_serv_pen();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_list_my_servce_adapter, parent, false);

        TextView tvNomeLocalServ = (TextView) rootView.findViewById(R.id.nomeLocalServ);
        TextView tvHoraServ = (TextView) rootView.findViewById(R.id.horaServ);
        TextView tvDataServ = (TextView) rootView.findViewById(R.id.dataServ);
        TextView tvLocalizaServ = (TextView) rootView.findViewById(R.id.localizaServ);

//ajeitar o listview para listar os itens do banco na sequencia certa
        ServPendenteCtrl msgDaVez = myServs.get(position);


        tvNomeLocalServ.setText(msgDaVez.getTipoCli()+": "+msgDaVez.getNomeCli());
        tvHoraServ.setText(msgDaVez.getHora_serv().toString());
        tvDataServ.setText(msgDaVez.getData_serv().toString());
        tvLocalizaServ.setText(msgDaVez.getEnder().toString());

        return rootView;
    }
}
