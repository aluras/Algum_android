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

import br.com.algum.algum_android.customAdapters.ConfigContaAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class ContasActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>{

    private ConfigContaAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas);
        super.onCreateDrawer();

        ListView listContas = (ListView) findViewById(R.id.listContas);

        getSupportLoaderManager().initLoader(0, null, this);

        mAdapter = new ConfigContaAdapter(this,null,0);

        listContas.setAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        Uri contasUri = AlgumDBContract.ContasEntry.buildContaUsuarioUri(sharedPref.getInt(getString(R.string.idUsuario),0));

        String selection = "";

        return new CursorLoader(
                this,
                contasUri,
                null,
                selection,
                null,
                null
        );    }

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
