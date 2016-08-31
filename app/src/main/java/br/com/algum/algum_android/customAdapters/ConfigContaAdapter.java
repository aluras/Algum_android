package br.com.algum.algum_android.customAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import br.com.algum.algum_android.ContasEditActivity;
import br.com.algum.algum_android.ExtratoActivity;
import br.com.algum.algum_android.R;
import br.com.algum.algum_android.data.AlgumDBContract;

/**
 * Created by sn1007071 on 19/08/2016.
 */
public class ConfigContaAdapter extends CursorAdapter {
    private LayoutInflater mInflater;

    public ConfigContaAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_conta, parent, false);
        final ContaHolder holder = new ContaHolder();
        holder.txtNome = (TextView)view.findViewById(R.id.txtNome);
        holder.txtTipo = (TextView)view.findViewById(R.id.txtTipo);
        holder.txtSaldo = (TextView)view.findViewById(R.id.txtSaldo);
        view.setTag(holder);
        return view;    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        View vwBtns = view.findViewById(R.id.vwBtns);
        vwBtns.setVisibility(View.GONE);

        final String txtNome = cursor.getString(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_NOME));
        final String txtTipo = cursor.getString(cursor.getColumnIndex(AlgumDBContract.TipoContaEntry.COLUMN_DESCRICAO));
        final Float txtSaldo = cursor.getFloat(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_SALDO));
        final int idConta = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_ID));
        final int idConta2 = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID));

        final ContaHolder holder = (ContaHolder) view.getTag();

        holder.txtNome.setText(txtNome);
        holder.txtTipo.setText(txtTipo);
        holder.txtSaldo.setText("R$ " + String.format("%.2f", txtSaldo));

        if(txtSaldo>=0){
            holder.txtSaldo.setTextColor(context.getResources().getColor(R.color.receita));
        }else{
            holder.txtSaldo.setTextColor(context.getResources().getColor(R.color.despesa));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View vwBtns = view.findViewById(R.id.vwBtns);
                if(vwBtns.getVisibility() == View.GONE){
                    vwBtns.setVisibility(View.VISIBLE);
                }else{
                    vwBtns.setVisibility(View.GONE);
                }
            }
        });

        ImageButton btnEdit = (ImageButton) view.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent = new Intent(context, ContasEditActivity.class);
                intent.putExtra("idConta", idConta);
                context.startActivity(intent);
            }
        });


        ImageButton btnExtrato = (ImageButton) view.findViewById(R.id.btnExtrato);

        btnExtrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent = new Intent(context, ExtratoActivity.class);
                intent.putExtra("idConta",idConta2);
                context.startActivity(intent);
            }
        });




    }

    static class ContaHolder {
        TextView txtNome;
        TextView txtTipo;
        TextView txtSaldo;
    }
}
