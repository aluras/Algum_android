package br.com.algum.algum_android.customAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.algum.algum_android.LancamentoGruposActivity;
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
    private int idTipoLancamento;
    //private String nomeConta;
    //private int idConta;

    public GrupoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
        //mContext = context;
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
        idTipoLancamento = ((LancamentoGruposActivity) context).getTipoLancamento();
        //nomeConta = ((LancamentoContaOrigemActivity) context).getNomeConta();
        //idConta = ((LancamentoContaOrigemActivity) context).getIdCOnta();
        final String nomeGrupo = cursor.getString(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_NOME));
        final int idGrupo = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_ID));
        final float valorGasto = cursor.getFloat(cursor.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_GASTO));

        final GrupoHolder holder = (GrupoHolder) view.getTag();
        holder.txtNome.setText(nomeGrupo);

        GradientDrawable gd = (GradientDrawable) holder.layout.getBackground();
        gd.setColor(context.getResources().getColor(R.color.tile4));
        TextView txt = (TextView)holder.txtNome;
        txt.setTextColor(context.getResources().getColor(R.color.texto_tipo));


        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewParent vwParent = view.getParent();

                for(int index=0; index<((ViewGroup)vwParent).getChildCount(); ++index) {
                    View nextChild = ((ViewGroup)vwParent).getChildAt(index);
                    GradientDrawable gdv = (GradientDrawable) nextChild.getBackground();
                    gdv.setColor((view.getContext().getResources().getColor(R.color.tile4)));
                }

                GradientDrawable gdv = (GradientDrawable) view.getBackground();
                gdv.setColor(view.getContext().getResources().getColor(R.color.tile1));
                Intent lancamentoContaIntent = new Intent(view.getContext(), LancamentoContaOrigemActivity.class);
                lancamentoContaIntent.putExtra("tipoLancamento",idTipoLancamento);
                //lancamentoValorIntent.putExtra("nomeConta",nomeConta);
                //lancamentoValorIntent.putExtra("idConta",idConta);
                lancamentoContaIntent.putExtra("nomeGrupo",nomeGrupo);
                lancamentoContaIntent.putExtra("idGrupo",idGrupo);
                lancamentoContaIntent.putExtra("valorGastoGrupo",valorGasto);
                view.getContext().startActivity(lancamentoContaIntent);

            }
        });

    }

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
