package br.com.algum.algum_android;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.ListView;

import br.com.algum.algum_android.customAdapters.ExtratoAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class ExtratoActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ExtratoAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extrato);
        super.onCreateDrawer();

        ListView listLancamentos = (ListView) findViewById(R.id.listLancamentos);

        getSupportLoaderManager().initLoader(0, null, this);

        mAdapter = new ExtratoAdapter(this,null, 0);

        listLancamentos.setAdapter(mAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        Uri lancamentoUri = AlgumDBContract.LancamentoEntry.buildLancamentoUsuarioUri(sharedPref.getInt(getString(R.string.idUsuario), 0));

        String selection = "";

        String[] projection =
                {
                        AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_ID
                        , AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_DATA
                        , AlgumDBContract.GruposEntry.TABLE_NAME+"."+ AlgumDBContract.GruposEntry.COLUMN_NOME
                        , AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_VALOR
                        , AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID
                };

        return new CursorLoader(
                this,
                lancamentoUri,
                projection,
                selection,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
