package br.com.algum.algum_android.customAdapters;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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
import br.com.algum.algum_android.utils.Controle;

/**
 * Created by sn1007071 on 19/08/2016.
 */
public class ConfigContaAdapter extends CursorAdapter {
    private LayoutInflater mInflater;
    private String txtTipo = "";

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
        holder.txtTitular = (TextView)view.findViewById(R.id.txtTitular);
        holder.btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);
        holder.btnExtrato = (ImageButton) view.findViewById(R.id.btnExtrato);
        holder.btnShare = (ImageButton) view.findViewById(R.id.btnShare);
        holder.btnEdit = (ImageButton) view.findViewById(R.id.btnEdit);
        view.setTag(holder);
        return view;    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.userInfo), Context.MODE_PRIVATE);
        final int idUsuario = sharedPref.getInt(context.getString(R.string.idUsuario), 0);

        View vwBtns = view.findViewById(R.id.vwBtns);
        vwBtns.setVisibility(View.GONE);

        Uri uriTipo = AlgumDBContract.TipoContaEntry.CONTENT_URI;
        String selection = AlgumDBContract.TipoContaEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = {cursor.getString(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID))};
        Cursor cursorTipoConta = context.getContentResolver().query(uriTipo,null,selection,selectionArgs,null);
        if(cursorTipoConta.getCount() > 0){
            cursorTipoConta.moveToFirst();
            txtTipo = cursorTipoConta.getString(cursorTipoConta.getColumnIndex(AlgumDBContract.TipoContaEntry.COLUMN_DESCRICAO));
        }

        final String txtNome = cursor.getString(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_NOME));
        final Float txtSaldo = cursor.getFloat(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_SALDO));
        final int idConta = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_ID));
        //final int idContaUsuario = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.ContasUsuarioEntry.TABLE_NAME+ AlgumDBContract.ContasUsuarioEntry.COLUMN_ID));
        final int idUsuarioDono = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID));
        final String nomeUsuarioDono = cursor.getString(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_USUARIO_NOME));

        final ContaHolder holder = (ContaHolder) view.getTag();

        holder.txtNome.setText(txtNome);
        holder.txtTipo.setText(txtTipo);
        holder.txtSaldo.setText("R$ " + String.format("%.2f", txtSaldo));

        if(txtSaldo>=0){
            holder.txtSaldo.setTextColor(context.getResources().getColor(R.color.colorAccent));
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

        if(idUsuario == idUsuarioDono){
            holder.btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent = new Intent(context, ContasEditActivity.class);
                    intent.putExtra("idConta", idConta);
                    context.startActivity(intent);
                }
            });
            holder.btnEdit.setVisibility(View.VISIBLE);
        }else{
            holder.btnEdit.setVisibility(View.GONE);
        }

        holder.btnExtrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent = new Intent(context, ExtratoActivity.class);
                intent.putExtra("idConta",idConta);
                context.startActivity(intent);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Exclusão de Conta");
                builder.setMessage("Confirma a exclusão da Conta " + txtNome + "?");
                builder.setPositiveButton(R.string.excluir, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues ContaValues = new ContentValues();

                        ContaValues.put(AlgumDBContract.ContasUsuarioEntry.COLUMN_EXCLUIDO, 1);
                        ContaValues.put(AlgumDBContract.ContasUsuarioEntry.COLUMN_ALTERADO, 1);
                        String selection = AlgumDBContract.ContasUsuarioEntry.COLUMN_ID + " = ? ";
                        //String[] selectionArgs = {Integer.toString(idContaUsuario)};
                        //context.getContentResolver().update(AlgumDBContract.ContasUsuarioEntry.CONTENT_URI, ContaValues, selection, selectionArgs);

                        Controle.gravaLog(context, "Conta " + txtNome + " removida", 0);

                    }
                })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        if(idUsuario == idUsuarioDono){
            holder.btnShare.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent = new Intent(context, ContasEditActivity.class);
                    intent.putExtra("idConta", idConta);
                    context.startActivity(intent);
                }
            });
            holder.btnShare.setVisibility(View.VISIBLE);
        }else{
            holder.btnShare.setVisibility(View.GONE);
        }

        if(idUsuario == idUsuarioDono){
            holder.txtTitular.setText("");
        }else{
            holder.txtTitular.setText("Titular: "+nomeUsuarioDono);
        }


    }

    static class ContaHolder {
        TextView txtNome;
        TextView txtTipo;
        TextView txtSaldo;
        TextView txtTitular;
        ImageButton btnEdit;
        ImageButton btnShare;
        ImageButton btnExtrato;
        ImageButton btnDelete;
    }
}
