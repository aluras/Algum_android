package br.com.algum.algum_android.customAdapters;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.TextView;

import br.com.algum.algum_android.LancamentoGrupoActivity;
import br.com.algum.algum_android.R;

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
    private boolean mDestino;

    public ContaAdapter(Context context, Cursor c, int flags, boolean destino) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
        mDestino = destino;
        //mContext = context;
    }

/*    public ContaAdapter(Context context, int resource, Conta[] data) {
        super(context, resource, data);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = data;
    }
    */
/*
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContaHolder holder = null;

        if (row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent,false);

            holder = new ContaHolder();
            holder.txtNome = (TextView)row.findViewById(R.id.textViewTile);

            row.setTag(holder);
        }
        else
        {
            holder = (ContaHolder)row.getTag();
        }

        Conta conta = data[position];
        holder.txtNome.setText(conta.getNome());
        GradientDrawable gd = (GradientDrawable) holder.txtNome.getBackground();
        gd.setColor(context.getResources().getColor(conta.getCor()));
        TextView txt = (TextView)holder.txtNome;
        txt.setTextColor(context.getResources().getColor(R.color.texto_tipo));

        holder.txtNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent lancamentoGruposIntent = new Intent(view.getContext(), LancamentoGrupoActivity.class);
                //context.startActivity(lancamentoGruposIntent);

            }
        });

        return row;
    }
*/
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.tiles, parent, false);
        final ContaHolder holder = new ContaHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        idTipoLancamento = ((LancamentoGrupoActivity) context).getTipoLancamento();
        nomeGrupo = ((LancamentoGrupoActivity) context).getNomeGrupo();
        idGrupo = ((LancamentoGrupoActivity) context).getIdGrupo();

        final ContaHolder holder = (ContaHolder) view.getTag();
        final String nomeConta = cursor.getString(4);
        final int idConta = cursor.getInt(1);
        final Context mContext = context;

        holder.txtNome.setText(nomeConta);

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
                    gdv.setColor(mContext.getResources().getColor(R.color.tile4));
                }

                GradientDrawable gdv = (GradientDrawable) view.getBackground();
                gdv.setColor(mContext.getResources().getColor(R.color.tile1));
                ((LancamentoGrupoActivity) mContext).recebeConta(mDestino, idConta, nomeConta, idGrupo, nomeGrupo, idTipoLancamento);
            }
        });

    }

    static class ContaHolder {
        TextView txtNome;

        public ContaHolder(View view) {
            this.txtNome = (TextView) view.findViewById(R.id.textViewTile);
        }
    }
}
