package br.com.algum.algum_android.customAdapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.algum.algum_android.R;
import br.com.algum.algum_android.data.AlgumDBContract;
import br.com.algum.algum_android.utils.Controle;

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
    public void bindView(View view, final Context context, Cursor cursor) {

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        final int idLancamento = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_ID));
        final int idConta = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID));
        final String txtData = format.format(new Date(cursor.getLong(cursor.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_DATA))));
        final String txtGrupo = cursor.getString(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_NOME));
        final String txtConta = cursor.getString(cursor.getColumnIndex("conta_nome"));
        final float txtValor = cursor.getFloat(cursor.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_VALOR));
        final String txtObservacao = cursor.getString(cursor.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO));

        final LancamentoHolder holder = (LancamentoHolder) view.getTag();

        holder.txtData.setText(txtData);
        holder.txtData.setVisibility(View.VISIBLE);
        strData = txtData;

        if (cursor.moveToPrevious()){
            if(txtData.equals(format.format(new Date(cursor.getLong(cursor.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_DATA)))) )){
                holder.txtData.setVisibility(View.GONE);
            }
        }


        holder.txtGrupo.setText(txtGrupo);
        holder.txtValor.setText("R$ " + String.format("%.2f", txtValor));
        if(txtValor>=0){
            holder.txtValor.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }else{
            holder.txtValor.setTextColor(context.getResources().getColor(R.color.despesa));
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                View detailView = mInflater.inflate(R.layout.detail_lancamento, null);
                TextView lancamentoData = (TextView) detailView.findViewById(R.id.lancamento_data);
                lancamentoData.setText("Data: " + txtData);
                TextView lancamentoGrupo = (TextView) detailView.findViewById(R.id.lancamento_grupo);
                lancamentoGrupo.setText("Grupo: " + txtGrupo);
                TextView lancamentoConta = (TextView) detailView.findViewById(R.id.lancamento_conta);
                lancamentoConta.setText("Conta: " + txtConta);
                TextView lancamentoValor = (TextView) detailView.findViewById(R.id.lancamento_valor);
                lancamentoValor.setText("Valor: R$ " + String.format("%.2f", txtValor));
                if(txtValor>=0){
                    lancamentoValor.setTextColor(context.getResources().getColor(R.color.colorAccent));
                }else{
                    lancamentoValor.setTextColor(context.getResources().getColor(R.color.despesa));
                }
                TextView lancamentoObservacao = (TextView) detailView.findViewById(R.id.lancamento_observacao);
                lancamentoObservacao.setText("Observação: " + txtObservacao);

                builder.setView(detailView)
                        .setTitle(context.getString(R.string.title_activity_main))
                        .setPositiveButton(R.string.excluir, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ContentValues lancamentoValues = new ContentValues();
                                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_EXCLUIDO, 1);
                                String selection = AlgumDBContract.LancamentoEntry.COLUMN_ID + " = ? ";
                                String[] selectionArgs = {Integer.toString(idLancamento)};
                                context.getContentResolver().update(AlgumDBContract.LancamentoEntry.CONTENT_URI, lancamentoValues, selection, selectionArgs);

                                ContentValues saldoValue = new ContentValues();
                                saldoValue.put(AlgumDBContract.LancamentoEntry.COLUMN_VALOR,-txtValor);
                                saldoValue.put(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID, idConta);

                                context.getContentResolver().update(AlgumDBContract.ContasEntry.CONTENT_SALDO_URI, saldoValue, null, null);
                                Controle.syncData(context);

                            }
                        })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });



                Dialog dialog = builder.create();

                dialog.show();
            }
        });

    }


    static class LancamentoHolder {
        TextView txtData;
        TextView txtGrupo;
        TextView txtValor;
    }
}
