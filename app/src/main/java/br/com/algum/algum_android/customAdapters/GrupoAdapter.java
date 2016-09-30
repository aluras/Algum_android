package br.com.algum.algum_android.customAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;

import br.com.algum.algum_android.LancamentoContaOrigemActivity;
import br.com.algum.algum_android.R;
import br.com.algum.algum_android.data.AlgumDBContract;

/**
 * Created by sn1007071 on 18/07/2016.
 */
public class GrupoAdapter extends CursorAdapter {

    //private Cursor mCursor;
    //private Context mContext;
    private LayoutInflater mInflater;
    long inicioMes;
    long finaloMes;

    public GrupoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        inicioMes = cal.getTime().getTime();
        cal.set(Calendar.DAY_OF_MONTH,cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        finaloMes = cal.getTime().getTime();

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.tiles, parent, false);
        final GrupoHolder holder = new GrupoHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int idTipoLancamento = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_TIPO_ID));
        int idGrupo = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_ID));
        String nomeGrupo = cursor.getString(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_NOME));
        Float valorGasto = 0f;

        Uri uriGasto = AlgumDBContract.LancamentoEntry.CONTENT_URI;
        String[] projection = {"SUM("+ AlgumDBContract.LancamentoEntry.COLUMN_VALOR+") AS " + AlgumDBContract.GruposEntry.COLUMN_GASTO};
        String selection = AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID + " = ? AND " +
                AlgumDBContract.LancamentoEntry.COLUMN_EXCLUIDO + " = ? " +
                " AND "+ AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+AlgumDBContract.LancamentoEntry.COLUMN_DATA +" >= ? "+
                " AND "+ AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+AlgumDBContract.LancamentoEntry.COLUMN_DATA +" <= ? ";

                String[] selectionArgs = {
                        Integer.toString(idGrupo),
                        "0",
                        Long.toString(inicioMes),
                        Long.toString(finaloMes)
                };

        Cursor cursorGasto = context.getContentResolver().query(uriGasto,projection,selection,selectionArgs,null);
        if (cursorGasto.getCount() > 0){
            cursorGasto.moveToFirst();
            valorGasto = cursorGasto.getFloat(cursorGasto.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_GASTO));
        }
        cursorGasto.close();

        final GrupoHolder holder = (GrupoHolder) view.getTag();
        holder.txtNome.setText(nomeGrupo);

        GradientDrawable gd = (GradientDrawable) holder.layout.getBackground();
        gd.setColor(context.getResources().getColor(R.color.tile4));
        TextView txt = (TextView)holder.txtNome;
        txt.setTextColor(context.getResources().getColor(R.color.texto_tipo));


        holder.layout.setOnClickListener(
                new GrupoOnClickListener(idTipoLancamento,nomeGrupo,idGrupo,valorGasto)
        );

    }

    public class GrupoOnClickListener implements View.OnClickListener
    {

        int idTipoLancamento;
        String nomeGrupo;
        int idGrupo;
        float valorGasto;

        public GrupoOnClickListener(int idTipoLancamento, String nomeGrupo, int idGrupo, float valorGasto) {
            this.idTipoLancamento = idTipoLancamento;
            this.nomeGrupo = nomeGrupo;
            this.idGrupo = idGrupo;
            this.valorGasto = valorGasto;
        }

        @Override
        public void onClick(View v)
        {
            Intent lancamentoContaIntent = new Intent(v.getContext(), LancamentoContaOrigemActivity.class);
            lancamentoContaIntent.putExtra("tipoLancamento",idTipoLancamento);
            lancamentoContaIntent.putExtra("nomeGrupo",nomeGrupo);
            lancamentoContaIntent.putExtra("idGrupo",idGrupo);
            lancamentoContaIntent.putExtra("valorGastoGrupo",valorGasto);
            v.getContext().startActivity(lancamentoContaIntent);
        }

    };

    static class GrupoHolder {
        LinearLayout layout;
        TextView txtNome;
        TextView txtSub;

        public GrupoHolder(View view) {
            this.txtNome = (TextView) view.findViewById(R.id.textViewTile);
            this.txtSub = (TextView) view.findViewById(R.id.txtSub);
            this.layout = (LinearLayout) view.findViewById(R.id.tilesLayout);
        }
    }



}
