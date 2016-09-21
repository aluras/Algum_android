package br.com.algum.algum_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import br.com.algum.algum_android.customAdapters.ContaAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class LancamentoContaOrigemActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = LancamentoContaOrigemActivity.class.getSimpleName();

    private ContaAdapter mContasAdapter;

    private int idTipoLancamento = 1;
    private String nomeGrupo = "";
    private int idGrupo = 0;
    private float valorGasto = 0;
    private int idContaOrigem = 0;
    private String nomeContaOrigem = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_grupo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //super.onCreateDrawer();

        Intent intent = getIntent();

        idTipoLancamento = intent.getIntExtra("tipoLancamento", 1);
        nomeGrupo = intent.getStringExtra("nomeGrupo");
        idGrupo = intent.getIntExtra("idGrupo", 0);
        valorGasto = intent.getFloatExtra("valorGastoGrupo",0);



        TextView txtContaOrigem = (TextView) findViewById(R.id.txtContaOrigem);
        GridView gridContas = (GridView) findViewById(R.id.gridViewGrupos);

        txtContaOrigem.setText(R.string.contas);

        TextView txtTipoLancamento = (TextView) findViewById(R.id.txtTipoLancamento);
        TextView txtGasto = (TextView) findViewById(R.id.txtGastoGrupo);
        txtTipoLancamento.setText(nomeGrupo);
        txtGasto.setText("(R$ " + String.format("%.2f", valorGasto) + " no mês)");
        if(valorGasto>0){
            txtGasto.setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(valorGasto<0){
            txtGasto.setTextColor(getResources().getColor(R.color.despesa));
        }
        LinearLayout desGrupo = (LinearLayout) findViewById(R.id.desGrupo);
        RelativeLayout botoesValores = (RelativeLayout) findViewById(R.id.botoesValores);
        String tipoLancamento = "";
        switch (idTipoLancamento){
            case 1:
                tipoLancamento = getString(R.string.despesa);
                botoesValores.setBackgroundColor(getResources().getColor(R.color.despesaDisable));
                break;
            case 2:
                tipoLancamento = getString(R.string.receita);
                botoesValores.setBackgroundColor(getResources().getColor(R.color.receitaDisable));
                break;
            case 3:
                tipoLancamento = getString(R.string.transferencia);
                botoesValores.setBackgroundColor(getResources().getColor(R.color.transferenciaDisable));
                txtContaOrigem.setText(R.string.contaOrigem);
                txtGasto.setVisibility(View.INVISIBLE);
                break;
        }



        mContasAdapter = new ContaAdapter(this,null, 0, idTipoLancamento, nomeGrupo, idGrupo, valorGasto, 0, null);

        getSupportLoaderManager().initLoader(0, null, this);

        gridContas.setAdapter(mContasAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        Uri contasUri = AlgumDBContract.ContasEntry.buildContaUsuarioUri(sharedPref.getInt(getString(R.string.idUsuario), 0));

        String[] projection = {AlgumDBContract.ContasEntry.TABLE_NAME+".*"};

        String selection = "";

        switch (idTipoLancamento){
            case 1:
                selection =  AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID + " IN (1,3,4,5) ";
                break;
            case 2:
                selection =  AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID + " IN (1,2,4,5) ";
                break;
        }

        return new CursorLoader(
                this,
                contasUri,
                projection,
                selection,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mContasAdapter.swapCursor(data);
        mContasAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContasAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(this, LancamentoGruposActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}
