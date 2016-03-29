package br.com.algum.algum_android.customAdapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import br.com.algum.algum_android.R;
import br.com.algum.algum_android.entities.Conta;

/**
 * Created by sn1007071 on 09/03/2016.
 */
public class ContaAdapter extends ArrayAdapter {

    Context context;
    int layoutResourceId;
    Conta data[] = null;

    public ContaAdapter(Context context, int resource, Conta[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContaHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent,false);

            holder = new ContaHolder();
            holder.txtNome = (TextView)row.findViewById(R.id.textViewTile);

            row.setTag(holder);
        }
        else
        {
            holder = (ContaHolder)row.getTag();
        }

        Conta conta = data[position];
        holder.txtNome.setText(conta.getNome());
        GradientDrawable gd = (GradientDrawable) holder.txtNome.getBackground();
        gd.setColor(context.getResources().getColor(conta.getCor()));
        TextView txt = (TextView)holder.txtNome;
        txt.setTextColor(context.getResources().getColor(R.color.texto_tipo));

        holder.txtNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent lancamentoGruposIntent = new Intent(view.getContext(), LancamentoGrupoActivity.class);
                //context.startActivity(lancamentoGruposIntent);

            }
        });

        return row;
    }

    static class ContaHolder {
        TextView txtNome;
    }
}
