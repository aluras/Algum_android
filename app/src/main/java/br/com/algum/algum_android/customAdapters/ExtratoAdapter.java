package br.com.algum.algum_android.customAdapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import br.com.algum.algum_android.R;

/**
 * Created by sn1007071 on 10/03/2016.
 */
public class ExtratoAdapter extends CursorAdapter {

    private LayoutInflater mInflater;
    private String strData = "";

    public ExtratoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_extrato, parent, false);
        final LancamentoHolder holder = new LancamentoHolder();
        holder.txtData = (TextView)view.findViewById(R.id.txtData);
        holder.txtValor = (TextView)view.findViewById(R.id.txtValor);
        holder.txtGrupo = (TextView)view.findViewById(R.id.txtGrupo);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        final String txtData = cursor.getString(2);
        final String txtGrupo = cursor.getString(12);
        final float txtValor = cursor.getFloat(3);

        final LancamentoHolder holder = (LancamentoHolder) view.getTag();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        holder.txtData.setText(txtData);
        holder.txtData.setVisibility(View.VISIBLE);
        strData = txtData;

        if (cursor.moveToPrevious()){
            if(txtData.equals(cursor.getString(2)) ){
                holder.txtData.setVisibility(View.GONE);
            }
        }


        holder.txtGrupo.setText(txtGrupo);
        holder.txtValor.setText("R$ " + String.format("%.2f", txtValor));
        if(txtValor>=0){
            holder.txtValor.setTextColor(context.getResources().getColor(R.color.receita));
        }else{
            holder.txtValor.setTextColor(context.getResources().getColor(R.color.despesa));
        }


    }


    static class LancamentoHolder {
        TextView txtData;
        TextView txtGrupo;
        TextView txtValor;
    }
}
