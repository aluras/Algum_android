package br.com.algum.algum_android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import br.com.algum.algum_android.customAdapters.ConfigGrupoAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class GruposActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor> {

    private ConfigGrupoAdapter mAdapter;
    private ExpandableListView listGrupos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos);
        super.onCreateDrawer();
        listGrupos = (ExpandableListView) findViewById(R.id.listGrupos);

        mAdapter = new ConfigGrupoAdapter(null,this);

        getSupportLoaderManager().initLoader(123456, null, this);

        listGrupos.setAdapter(mAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        CursorLoader c1;

        if(id <123456) {
            Uri gruposUri = AlgumDBContract.GrupoUsuariosEntry.buildGrupoUsuariosUri(id_usuario);

            String selection = "";

            String[] projection =
                    {
                            AlgumDBContract.GruposEntry.TABLE_NAME+".*"
                    };

            String order = AlgumDBContract.GruposEntry.COLUMN_NOME + " ASC";

            c1 = new CursorLoader(
                    this,
                    gruposUri,
                    projection,
                    selection,
                    null,
                    order
            );
        }else{

            Uri tipoGrupoUri = AlgumDBContract.TipoGrupoEntry.CONTENT_URI;
            c1 = new CursorLoader(
                    this,
                    tipoGrupoUri,
                    null,
                    null,
                    null,
                    null
            );
        }

        return c1;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        int id = loader.getId();
        if(id < 123456){
            if(!data.isClosed()){
                if(mAdapter != null){
                    try{
                        mAdapter.setChildrenCursor(id,data);
                    }catch (NullPointerException e){
                        Log.w("DEBUG",
                                "Adapter expired, try again on the next query: "
                                        + e.getMessage());
                    }

                }

            }
        }else{

            mAdapter.setGroupCursor(data);
            int count = mAdapter.getGroupCount();
            for ( int i = 0; i < count; i++ )
                listGrupos.expandGroup(i);


        }

        //mAdapter.swapCursor(data);
        //mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        int id = loader.getId();
        if(id < 123456){
            mAdapter.setChildrenCursor(id,null);
            //mAdapter.setChildrenCursor(1,data);
        }else{
            mAdapter.setGroupCursor(null);
        }
               //mAdapter.swapCursor(null);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_add).setVisible(true);
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
            intent = new Intent(this, GruposEditActivity.class);
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
