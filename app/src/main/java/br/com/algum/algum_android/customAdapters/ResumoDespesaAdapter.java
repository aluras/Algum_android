package br.com.algum.algum_android.customAdapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.com.algum.algum_android.R;
import br.com.algum.algum_android.customViews.PercentView;
import br.com.algum.algum_android.entities.ResumoDespesa;

/**
 * Created by sn1007071 on 09/03/2016.
 */
public class ResumoDespesaAdapter extends ArrayAdapter {

    Context context;
    int layoutResourceId;
    ResumoDespesa data[] = null;

    public ResumoDespesaAdapter(Context context, int resource, ResumoDespesa[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ResumoDespesaHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent,false);

            holder = new ResumoDespesaHolder();
            //holder.txtNome = (TextView)row.findViewById(R.id.txtNome);
            //holder.txtValor = (TextView)row.findViewById(R.id.txtValor);
            //holder.txtCompara = (TextView)row.findViewById(R.id.txtCompara);
            holder.percent = (PercentView)row.findViewById(R.id.percent);

            row.setTag(holder);
        }
        else
        {
            holder = (ResumoDespesaHolder)row.getTag();
        }

        ResumoDespesa resumo = data[position];
        holder.txtNome.setText(resumo.getNome());
        holder.txtValor.setText("R$ "+ Float.toString(resumo.getValorRealizado()));

        Float calculo = resumo.getValorPlanejado() - resumo.getValorRealizado();
        if(calculo >=0 ){
            holder.txtCompara.setText("R$ "+ String.format("%.2f", calculo)+ " a menos que o planejado (R$ "+ String.format("%.2f", resumo.getValorPlanejado())+" )");
            holder.txtCompara.setTextColor(context.getResources().getColor(R.color.receita));
        }else{
            holder.txtCompara.setText("R$ "+ String.format("%.2f", Math.abs(calculo))+ " a mais que o planejado (R$ "+ String.format("%.2f", resumo.getValorPlanejado())+" )");
            holder.txtCompara.setTextColor(context.getResources().getColor(R.color.despesa));
        }

        holder.percent.setValue((resumo.getValorRealizado()/resumo.getValorPlanejado())*100);
        holder.percent.setStartColor(context.getResources().getColor(R.color.receita));
        holder.percent.setEndColor(context.getResources().getColor(R.color.despesa));

        return row;
    }

    static class ResumoDespesaHolder {
        TextView txtNome;
        TextView txtValor;
        TextView txtCompara;
        PercentView percent;
    }
}
