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
import android.widget.CursorTreeAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import br.com.algum.algum_android.ExtratoActivity;
import br.com.algum.algum_android.GruposActivity;
import br.com.algum.algum_android.GruposEditActivity;
import br.com.algum.algum_android.R;
import br.com.algum.algum_android.data.AlgumDBContract;
import br.com.algum.algum_android.utils.Controle;


/**
 * Created by sn1007071 on 19/09/2016.
 */
public class ConfigGrupoAdapter extends CursorTreeAdapter {
    private LayoutInflater mInflater;

    protected GruposActivity activity;

    public ConfigGrupoAdapter(Cursor c,Context context) {
        super(c, context);
        mInflater = LayoutInflater.from(context);
        activity = (GruposActivity) context;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor cursor) {


        int groupPos = cursor.getPosition();
        int groupId = cursor.getInt(cursor
                .getColumnIndex(AlgumDBContract.TipoGrupoEntry.COLUMN_ID));

        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.userInfo), Context.MODE_PRIVATE);
        Uri gruposUri = AlgumDBContract.GrupoUsuariosEntry.buildGrupoUsuariosUri(sharedPref.getInt(activity.getString(R.string.idUsuario), 0));

        String selection = AlgumDBContract.GruposEntry.COLUMN_TIPO_ID + " = " + groupId  + " AND " + AlgumDBContract.GrupoUsuariosEntry.TABLE_NAME+ "." + AlgumDBContract.GrupoUsuariosEntry.COLUMN_EXCLUIDO + " = 0 ";
        //String selection = "";

        String[] projection =
                {
                        AlgumDBContract.GruposEntry.TABLE_NAME+".*"
                };

        String order = AlgumDBContract.GruposEntry.TABLE_NAME+"."+AlgumDBContract.GruposEntry.COLUMN_NOME + " ASC";

        Cursor c1 = activity.getContentResolver().query(
                gruposUri,
                projection,
                selection,
                null,
                order
        );

        return c1;

    }

    @Override
    public View newGroupView(Context context, Cursor cursor,
                             boolean isExpanded, ViewGroup parent) {

        final View view = mInflater.inflate(R.layout.group_grupo, parent, false);
        return view;
    }

    @Override
    public void bindGroupView(View view, Context context, Cursor cursor,
                              boolean isExpanded) {

        TextView lblListHeader = (TextView) view
                .findViewById(R.id.txtGrupo);

        if (lblListHeader != null) {
            lblListHeader.setText(cursor.getString(cursor
                    .getColumnIndex(AlgumDBContract.TipoGrupoEntry.COLUMN_DESCRICAO)));
        }

    }

    @Override
    public View newChildView(Context context, Cursor cursor, boolean isLastChild, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.item_grupo, parent, false);
        final GrupoHolder holder = new GrupoHolder();
        holder.txtNome = (TextView)view.findViewById(R.id.txtNome);
        view.setTag(holder);
        return view;    }

    @Override
    public void bindChildView(View view, final Context context, Cursor cursor, boolean isLastChild) {

        View vwBtns = view.findViewById(R.id.vwBtns);
        vwBtns.setVisibility(View.GONE);

        final String txtNome = cursor.getString(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_NOME));

        final int idGrupo = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_ID));

        final GrupoHolder holder = (GrupoHolder) view.getTag();

        holder.txtNome.setText(txtNome);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View vwBtns = view.findViewById(R.id.vwBtns);
                if (vwBtns.getVisibility() == View.GONE) {
                    vwBtns.setVisibility(View.VISIBLE);
                } else {
                    vwBtns.setVisibility(View.GONE);
                }
            }
        });

        ImageButton btnEdit = (ImageButton) view.findViewById(R.id.btnEdit);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent = new Intent(context, GruposEditActivity.class);
                intent.putExtra("idGrupo", idGrupo);
                context.startActivity(intent);
            }
        });

        ImageButton btnDelete = (ImageButton) view.findViewById(R.id.btnDelete);

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Exclusão de Categoria");
                builder.setMessage("Confirma a exclusão da Categoria " + txtNome + "?");
                builder.setPositiveButton(R.string.excluir, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ContentValues GrupoValues = new ContentValues();
                        //GrupoValues.put(AlgumDBContract.GruposEntry.COLUMN_EXCLUIDO, 1);
                        //GrupoValues.put(AlgumDBContract.GruposEntry.COLUMN_ALTERADO, 1);
                        String selection = AlgumDBContract.GruposEntry.COLUMN_ID + " = ? ";
                        String[] selectionArgs = {Integer.toString(idGrupo)};
                        context.getContentResolver().update(AlgumDBContract.GruposEntry.CONTENT_URI, GrupoValues, selection, selectionArgs);

                        Controle.gravaLog(context, "Categoria "+txtNome+" removida", 0);

                    }
                })
                        .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

        ImageButton btnExtrato = (ImageButton) view.findViewById(R.id.btnExtrato);

        btnExtrato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent = new Intent(context, ExtratoActivity.class);
                context.startActivity(intent);
            }
        });




    }

    static class GrupoHolder {
        TextView txtNome;
    }
}
