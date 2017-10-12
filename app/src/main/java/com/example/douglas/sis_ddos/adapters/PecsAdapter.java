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
import com.example.douglas.sis_ddos.controler.PecsCtrl;

import java.util.List;

public class PecsAdapter extends BaseAdapter {

    private List<PecsCtrl> pecs;
    private Context context;

    public PecsAdapter(){}
    public PecsAdapter(Context context, List<PecsCtrl> pecs) {
        this.pecs = pecs;
        this.context = context;
    }
    @Override
    public int getCount() {
        return pecs.size();
    }

    @Override
    public Object getItem(int position) {
        return pecs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return pecs.get(position).getId_pc();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rootView = LayoutInflater.from(context).inflate(R.layout.activity_pecs_adapter, parent, false);

        TextView tvNomePc = (TextView) rootView.findViewById(R.id.nomePecs);
        TextView tvDescriPc = (TextView) rootView.findViewById(R.id.descriPecs);
        CheckBox cbPecs = (CheckBox) rootView.findViewById(R.id.checkBoxPecs);

//ajeitar o listview para listar os itens do banco na sequencia certa
        PecsCtrl msgDaVez = pecs.get(position);


        tvNomePc.setText(msgDaVez.getNome());
        tvDescriPc.setText(msgDaVez.getModelo()+" / "+msgDaVez.getMarca());

        if(!OrdemServiceActivity.listPecs.isEmpty()){
            for(int x = 0; x< OrdemServiceActivity.listPecs.size(); x++){
                if(OrdemServiceActivity.listPecs.get(x).equals(msgDaVez.getNome())) cbPecs.setChecked(true);
            }
        }else cbPecs.setChecked(false);

        return rootView;
    }
}
