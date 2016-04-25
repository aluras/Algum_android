package br.com.algum.algum_android.customAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.algum.algum_android.LancamentoGrupoActivity;
import br.com.algum.algum_android.R;
import br.com.algum.algum_android.entities.Conta;

/**
 * Created by sn1007071 on 09/03/2016.
 */
public class ContaAdapter extends CursorAdapter {

    Context mContext;
    int layoutResourceId;
    Conta data[] = null;

    public ContaAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

/*    public ContaAdapter(Context context, int resource, Conta[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }
    */
/*
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
*/
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.tiles, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ContaHolder holder = new ContaHolder(view);
        holder.txtNome.setText(cursor.getString(3));

        GradientDrawable gd = (GradientDrawable) holder.txtNome.getBackground();
        gd.setColor(context.getResources().getColor(R.color.tile1));
        TextView txt = (TextView)holder.txtNome;
        txt.setTextColor(context.getResources().getColor(R.color.texto_tipo));

        holder.txtNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lancamentoGruposIntent = new Intent(view.getContext(), LancamentoGrupoActivity.class);
                view.getContext().startActivity(lancamentoGruposIntent);

            }
        });

    }

    static class ContaHolder {
        TextView txtNome;

        public ContaHolder(View view) {
            this.txtNome = (TextView) view.findViewById(R.id.textViewTile);
        }
    }
}
