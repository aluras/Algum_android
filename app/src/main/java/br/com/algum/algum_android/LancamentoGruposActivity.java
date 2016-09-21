package br.com.algum.algum_android;

import android.accounts.Account;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import java.util.Calendar;

import br.com.algum.algum_android.customAdapters.GrupoAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class LancamentoGruposActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderCallbacks<Cursor> {

    private final String LOG_TAG = LancamentoGruposActivity.class.getSimpleName();


    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "br.com.algum";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;
    private GrupoAdapter mGruposAdapter;
    private int tipoLancamento = 1;

    public int getTipoLancamento() {
        return tipoLancamento;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_contas);
        super.onCreateDrawer();

        TextView txtDataSync = (TextView) findViewById(R.id.txtDataSync);
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        //txtDataSync.setText("Sincronizado em: " + sharedPref.getString(getString(R.string.dataSync), ""));


        GridView gridContas = (GridView) findViewById(R.id.gridViewContas);

        //Define cores para os botões de tipo de lançamento
        GradientDrawable gdDespesa = (GradientDrawable) findViewById(R.id.textDespesa).getBackground();
        gdDespesa.setColor(getResources().getColor(R.color.despesa));
        TextView txtDespesa = (TextView)findViewById(R.id.textDespesa);
        txtDespesa.setTextColor(getResources().getColor(R.color.texto_tipo));
        txtDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaTipo(v);
            }
        });

        GradientDrawable gdReceita = (GradientDrawable) findViewById(R.id.textReceita).getBackground();
        gdReceita.setColor(getResources().getColor(R.color.receitaDisable));
        TextView txtReceita = (TextView)findViewById(R.id.textReceita);
        txtReceita.setTextColor(getResources().getColor(R.color.texto_tipo));
        txtReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaTipo(v);
            }
        });

        GradientDrawable gdTransferencia = (GradientDrawable) findViewById(R.id.textTransferencia).getBackground();
        gdTransferencia.setColor(getResources().getColor(R.color.transferenciaDisable));
        TextView txtTransferencia = (TextView)findViewById(R.id.textTransferencia);
        txtTransferencia.setTextColor(getResources().getColor(R.color.texto_tipo));
        txtTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaTipo(v);
            }
        });

        mGruposAdapter = new GrupoAdapter(this,null, 0);

        getSupportLoaderManager().initLoader(0, null, this);

        gridContas.setAdapter(mGruposAdapter);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void mudaTipo(View v) {

        GradientDrawable gdDespesa = (GradientDrawable) findViewById(R.id.textDespesa).getBackground();
        gdDespesa.setColor(getResources().getColor(R.color.despesaDisable));

        GradientDrawable gdReceita = (GradientDrawable) findViewById(R.id.textReceita).getBackground();
        gdReceita.setColor(getResources().getColor(R.color.receitaDisable));

        GradientDrawable gdTransferencia = (GradientDrawable) findViewById(R.id.textTransferencia).getBackground();
        gdTransferencia.setColor(getResources().getColor(R.color.transferenciaDisable));

        switch (v.getId()){
            case R.id.textDespesa:
                gdDespesa.setColor(getResources().getColor(R.color.despesa));
                tipoLancamento = 1;
                break;
            case R.id.textReceita:
                gdReceita.setColor(getResources().getColor(R.color.receita));
                tipoLancamento = 2;
                break;
            case R.id.textTransferencia:
                gdTransferencia.setColor(getResources().getColor(R.color.transferencia));
                tipoLancamento = 3;
                break;

        }
        getSupportLoaderManager().restartLoader(0, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH,1);
        long inicioMes = c.getTime().getTime();
        c.set(Calendar.DAY_OF_MONTH,c.getActualMaximum(Calendar.DAY_OF_MONTH));
        long finaloMes = c.getTime().getTime();

        //Uri gruposUri = AlgumDBContract.GruposEntry.buildGrupoUri(id_usuario);
        Uri gruposUri = AlgumDBContract.GruposEntry.CONTENT_URI;
        String selection = AlgumDBContract.GruposEntry.COLUMN_TIPO_ID + " = ?";
        String[] selectionArgs = {String.valueOf(tipoLancamento)};
        String[] projection = {
                AlgumDBContract.GruposEntry.TABLE_NAME+"."+AlgumDBContract.GruposEntry.COLUMN_ID
                , AlgumDBContract.GruposEntry.TABLE_NAME+"."+AlgumDBContract.GruposEntry.COLUMN_NOME
                , AlgumDBContract.GruposEntry.TABLE_NAME+"."+AlgumDBContract.GruposEntry.COLUMN_GRUPO_ID
                , AlgumDBContract.GruposEntry.TABLE_NAME+"."+AlgumDBContract.GruposEntry.COLUMN_TIPO_ID
                , "(SELECT SUM("+ AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_VALOR+") "+
                    "FROM "+ AlgumDBContract.LancamentoEntry.TABLE_NAME+
                    " WHERE "+ AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID+"="+ AlgumDBContract.GruposEntry.TABLE_NAME+"."+ AlgumDBContract.GruposEntry.COLUMN_ID+
                    " AND "+ AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+AlgumDBContract.LancamentoEntry.COLUMN_DATA +" >= "+Long.toString(inicioMes)+
                    " AND "+ AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+AlgumDBContract.LancamentoEntry.COLUMN_DATA +" <= "+Long.toString(finaloMes)+
                    ") AS "+ AlgumDBContract.GruposEntry.COLUMN_GASTO
        };

        String order = AlgumDBContract.GruposEntry.COLUMN_TIPO_ID +" ASC,"+ AlgumDBContract.GruposEntry.COLUMN_NOME + " ASC";

        return new CursorLoader(
                this,
                gruposUri,
                projection,
                selection,
                selectionArgs,
                order
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGruposAdapter.swapCursor(data);
        mGruposAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGruposAdapter.swapCursor(null);
    }
}
