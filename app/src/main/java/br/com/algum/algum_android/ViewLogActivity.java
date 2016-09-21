package br.com.algum.algum_android;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.algum.algum_android.data.AlgumDBContract;

public class ViewLogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log);

        ListView listViewLog = (ListView) findViewById(R.id.listViewLog);

        String[] columns = new String[] {
                AlgumDBContract.LogEntry.COLUMN_MENSAGEM
        };

        int[] to = new int[]{
                R.id.txtLogMensagem
        };

        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.item_log,
                null,
                columns,
                to,
                0
        );
        getSupportLoaderManager().initLoader(0, null, this);
        listViewLog.setAdapter(dataAdapter);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri gruposUri = AlgumDBContract.LogEntry.CONTENT_URI;
        return new CursorLoader(
                this,
                gruposUri,
                null,
                null,
                null,
                null
        );    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        dataAdapter.swapCursor(data);
        dataAdapter.notifyDataSetChanged();    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        dataAdapter.swapCursor(null);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LancamentoGruposActivity.class);
        startActivity(intent);
    }
}
