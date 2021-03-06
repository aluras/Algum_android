package br.com.algum.algum_android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Menu;
import android.view.MenuItem;
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
        Uri contasUri = AlgumDBContract.ContasUsuarioEntry.buildContaUsuariosUri(id_usuario);

        String selection = AlgumDBContract.ContasUsuarioEntry.TABLE_NAME+"."+AlgumDBContract.ContasUsuarioEntry.COLUMN_EXCLUIDO + " = 0 ";

        String[] projection =
                {
                        AlgumDBContract.ContasEntry.TABLE_NAME+".*"
                };

        return new CursorLoader(
                this,
                contasUri,
                projection,
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

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (this.getClass().getSimpleName().equals("ContasActivity")){
            menu.findItem(R.id.action_add).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            Intent intent = new Intent();
            intent = new Intent(this, ContasEditActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LancamentoGruposActivity.class);
        startActivity(intent);
    }
}
