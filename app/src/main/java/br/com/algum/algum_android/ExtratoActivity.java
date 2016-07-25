package br.com.algum.algum_android;

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

        Uri lancamentoUri = AlgumDBContract.LancamentoEntry.CONTENT_URI;
        String selection = "";


        return new CursorLoader(
                this,
                lancamentoUri,
                null,
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
