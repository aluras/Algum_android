package br.com.algum.algum_android.customAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import br.com.algum.algum_android.R;
import br.com.algum.algum_android.entities.Lancamento;

/**
 * Created by sn1007071 on 10/03/2016.
 */
public class ExtratoAdapter extends ArrayAdapter {

    Context context;
    int layoutResourceId;
    Lancamento data[] = null;

    public ExtratoAdapter(Context context, int resource, Lancamento[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LancamentoHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent,false);

            holder = new LancamentoHolder();
            //holder.txtData = (TextView)row.findViewById(R.id.txtData);
            //holder.txtValor = (TextView)row.findViewById(R.id.txtValor);
            //holder.txtGrupo = (TextView)row.findViewById(R.id.txtGrupo);

            row.setTag(holder);
        }
        else
        {
            holder = (LancamentoHolder)row.getTag();
        }

        Lancamento lancamento = data[position];

        if(position != 0 && data[position-1].getData().equals(lancamento.getData())){
            holder.txtData.setVisibility(View.GONE);
        }else{
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            holder.txtData.setText(format.format(lancamento.getData()));
            holder.txtData.setVisibility(View.VISIBLE);
        }

        holder.txtGrupo.setText(lancamento.getGrupo());
        holder.txtValor.setText("R$ "+ String.format("%.2f", lancamento.getValor()));
        if(lancamento.getValor()>=0){
            holder.txtValor.setTextColor(context.getResources().getColor(R.color.receita));
        }else{
            holder.txtValor.setTextColor(context.getResources().getColor(R.color.despesa));
        }

        return row;
    }

    static class LancamentoHolder {
        TextView txtData;
        TextView txtGrupo;
        TextView txtValor;
    }
}
