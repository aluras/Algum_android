package br.com.algum.algum_android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.GridView;
import android.widget.TextView;

import br.com.algum.algum_android.customAdapters.GrupoAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class LancamentoGrupoActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = LancamentoContasActivity.class.getSimpleName();

    private GrupoAdapter mGruposAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_grupo);
        super.onCreateDrawer();

        Intent intent = getIntent();

        String tipoLancamento = "";
        switch (intent.getIntExtra("tipoLancamento",1)){
            case 1:
                tipoLancamento = getString(R.string.despesa);
                break;
            case 2:
                tipoLancamento = getString(R.string.receita);
                break;
            case 3:
                tipoLancamento = getString(R.string.transferencia);
                break;
        }

        TextView txtTipoLancamento = (TextView) findViewById(R.id.txtTipoLancamento);
        txtTipoLancamento.setText("Nova " + tipoLancamento);

        TextView txtConta = (TextView) findViewById(R.id.txtConta);
        txtConta.setText("Conta: "+intent.getStringExtra("nomeConta"));

        GridView gridContas = (GridView) findViewById(R.id.gridViewGrupos);

        mGruposAdapter = new GrupoAdapter(this,null, 0);

        getSupportLoaderManager().initLoader(0, null, this);

        gridContas.setAdapter(mGruposAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri gruposUri = AlgumDBContract.GruposEntry.CONTENT_URI;
        return new CursorLoader(
                this,
                gruposUri,
                null,
                null,
                null,
                null
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
