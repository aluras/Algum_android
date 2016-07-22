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
import android.widget.TextView;

import br.com.algum.algum_android.LancamentoContasActivity;
import br.com.algum.algum_android.LancamentoGrupoActivity;
import br.com.algum.algum_android.R;

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
        idTipoLancamento = ((LancamentoContasActivity) context).getTipoLancamento();
        //nomeConta = ((LancamentoGrupoActivity) context).getNomeConta();
        //idConta = ((LancamentoGrupoActivity) context).getIdCOnta();
        final String nomeGrupo = cursor.getString(3);
        final int idGrupo = cursor.getInt(0);

        final GrupoHolder holder = (GrupoHolder) view.getTag();
        holder.txtNome.setText(nomeGrupo);

        GradientDrawable gd = (GradientDrawable) holder.txtNome.getBackground();
        gd.setColor(context.getResources().getColor(R.color.tile4));
        TextView txt = (TextView)holder.txtNome;
        txt.setTextColor(context.getResources().getColor(R.color.texto_tipo));


        holder.txtNome.setOnClickListener(new View.OnClickListener() {
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
                Intent lancamentoContaIntent = new Intent(view.getContext(), LancamentoGrupoActivity.class);
                lancamentoContaIntent.putExtra("tipoLancamento",idTipoLancamento);
                //lancamentoValorIntent.putExtra("nomeConta",nomeConta);
                //lancamentoValorIntent.putExtra("idConta",idConta);
                lancamentoContaIntent.putExtra("nomeGrupo",nomeGrupo);
                lancamentoContaIntent.putExtra("idGrupo",idGrupo);
                view.getContext().startActivity(lancamentoContaIntent);

            }
        });

    }

    static class GrupoHolder {
        TextView txtNome;

        public GrupoHolder(View view) {
            this.txtNome = (TextView) view.findViewById(R.id.textViewTile);
        }
    }

}
