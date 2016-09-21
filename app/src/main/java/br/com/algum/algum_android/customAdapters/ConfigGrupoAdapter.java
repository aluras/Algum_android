package br.com.algum.algum_android.customAdapters;

import android.content.Context;
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

        //activity.getSupportLoaderManager().initLoader(groupId, null, activity);
        SharedPreferences sharedPref = activity.getSharedPreferences(activity.getString(R.string.userInfo), Context.MODE_PRIVATE);
        Uri gruposUri = AlgumDBContract.GruposEntry.buildGrupoUri(sharedPref.getInt(activity.getString(R.string.idUsuario), 0));
        //Uri gruposUri = AlgumDBContract.GruposEntry.CONTENT_URI;

        String selection = AlgumDBContract.GruposEntry.COLUMN_TIPO_ID + " = " + groupId;

        String[] projection =
                {
                        AlgumDBContract.GruposEntry.TABLE_NAME+"."+ AlgumDBContract.GruposEntry.COLUMN_ID
                        ,AlgumDBContract.GruposEntry.TABLE_NAME+"."+ AlgumDBContract.GruposEntry.COLUMN_GRUPO_ID
                        , AlgumDBContract.GruposEntry.TABLE_NAME+"."+ AlgumDBContract.GruposEntry.COLUMN_NOME
                        , AlgumDBContract.TipoGrupoEntry.TABLE_NAME+"."+ AlgumDBContract.TipoGrupoEntry.COLUMN_DESCRICAO
                };

        String order = AlgumDBContract.GruposEntry.COLUMN_TIPO_ID +" ASC,"+ AlgumDBContract.GruposEntry.COLUMN_NOME + " ASC";

        Cursor c1 = activity.getContentResolver().query(
                gruposUri,
                projection,
                selection,
                null,
                order
        );

        return c1;

/*
        Loader loader = activity.getSupportLoaderManager().getLoader(groupId);

        if (loader != null && !loader.isReset()) {
            activity.getSupportLoaderManager().restartLoader(groupId, null,
                    activity);
        } else {
            activity.getSupportLoaderManager().initLoader(groupId, null,
                    activity);
        }

        return null;
*/
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
                intent = new Intent(context, GruposEditActivity.class);
                intent.putExtra("idGrupo", idGrupo);
                context.startActivity(intent);
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
