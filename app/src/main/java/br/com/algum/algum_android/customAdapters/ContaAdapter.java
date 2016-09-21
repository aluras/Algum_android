package br.com.algum.algum_android.customAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.algum.algum_android.LancamentoContaDestinoActivity;
import br.com.algum.algum_android.LancamentoValorActivity;
import br.com.algum.algum_android.R;
import br.com.algum.algum_android.data.AlgumDBContract;

/**
 * Created by sn1007071 on 09/03/2016.
 */
public class ContaAdapter extends CursorAdapter {

    //private Cursor mCursor;
    //private Context mContext;
    private LayoutInflater mInflater;
    private int idTipoLancamento;
    private String nomeGrupo;
    private int idGrupo;
    private float valorGasto;
    private int idContaOrigem;
    private String nomeContaOrigem;

    public ContaAdapter(Context context, Cursor c, int flags, int tipoLancamento, String nmeGrupo, int idGrupo, float valorGasto, int idContaOrigem, String nomeContaOrigem) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
        idTipoLancamento = tipoLancamento;
        nomeGrupo = nmeGrupo;
        this.idGrupo = idGrupo;
        this.valorGasto = valorGasto;
        this.idContaOrigem = idContaOrigem;
        this.nomeContaOrigem = nomeContaOrigem;

    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.tiles, parent, false);
        final ContaHolder holder = new ContaHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        final ContaHolder holder = (ContaHolder) view.getTag();
        final String nomeConta = cursor.getString(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_NOME));
        final int idConta = cursor.getInt(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_ID));
        final Float saldo = cursor.getFloat(cursor.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_SALDO));
        final Context mContext = context;

        holder.txtNome.setText(nomeConta);
        holder.txtSub.setText(String.format("%.2f", saldo));

        GradientDrawable gd = (GradientDrawable) holder.layout.getBackground();
        gd.setColor(context.getResources().getColor(R.color.tile4));
        TextView txt = (TextView)holder.txtNome;
        txt.setTextColor(context.getResources().getColor(R.color.texto_tipo));

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewParent vwParent = view.getParent();

                for (int index = 0; index < ((ViewGroup) vwParent).getChildCount(); ++index) {
                    View nextChild = ((ViewGroup) vwParent).getChildAt(index);
                    GradientDrawable gdv = (GradientDrawable) nextChild.getBackground();
                    gdv.setColor(mContext.getResources().getColor(R.color.tile4));
                }

                GradientDrawable gdv = (GradientDrawable) view.getBackground();
                gdv.setColor(mContext.getResources().getColor(R.color.tile1));

                Intent intent;
                if(idTipoLancamento == 3 && idContaOrigem == 0){
                    intent = new Intent(context, LancamentoContaDestinoActivity.class);
                    intent.putExtra("nomeContaOrigem",nomeConta);
                    intent.putExtra("idContaOrigem",idConta);
                }else{
                    intent = new Intent(context, LancamentoValorActivity.class);
                    if(idTipoLancamento == 3){
                        intent.putExtra("nomeContaDestino",nomeConta);
                        intent.putExtra("idContaDestino",idConta);
                        intent.putExtra("nomeContaOrigem",nomeContaOrigem);
                        intent.putExtra("idContaOrigem",idContaOrigem);
                    }else{
                        intent.putExtra("nomeContaOrigem",nomeConta);
                        intent.putExtra("idContaOrigem",idConta);
                    }
                }

                intent.putExtra("tipoLancamento",idTipoLancamento);
                intent.putExtra("nomeGrupo",nomeGrupo);
                intent.putExtra("idGrupo",idGrupo);
                intent.putExtra("valorGastoGrupo",valorGasto);
                intent.putExtra("saldoConta",saldo);
                context.startActivity(intent);
            }
        });

    }

    static class ContaHolder {
        LinearLayout layout;
        TextView txtNome;
        TextView txtSub;

        public ContaHolder(View view) {
            this.txtNome = (TextView) view.findViewById(R.id.textViewTile);
            this.txtSub = (TextView) view.findViewById(R.id.txtSub);
            this.layout = (LinearLayout) view.findViewById(R.id.tilesLayout);
        }
    }
}
