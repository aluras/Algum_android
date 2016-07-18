package br.com.algum.algum_android.customAdapters;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import br.com.algum.algum_android.MainActivity;
import br.com.algum.algum_android.R;

/**
 * Created by sn1007071 on 18/07/2016.
 */
public class GrupoAdapter extends CursorAdapter {

    private Cursor mCursor;
    private Context mContext;
    private LayoutInflater mInflater;

    public GrupoAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mInflater = LayoutInflater.from(context);
        mContext = context;
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
        final GrupoHolder holder = (GrupoHolder) view.getTag();
        holder.txtNome.setText(cursor.getString(2));

        GradientDrawable gd = (GradientDrawable) holder.txtNome.getBackground();
        gd.setColor(context.getResources().getColor(R.color.tile1));
        TextView txt = (TextView)holder.txtNome;
        txt.setTextColor(context.getResources().getColor(R.color.texto_tipo));

        holder.txtNome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent lancamentoValorIntent = new Intent(view.getContext(), MainActivity.class);
                view.getContext().startActivity(lancamentoValorIntent);

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
