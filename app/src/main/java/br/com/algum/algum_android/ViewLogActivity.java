package br.com.algum.algum_android;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import br.com.algum.algum_android.data.AlgumDBContract;

public class ViewLogActivity extends Activity {

    private SimpleCursorAdapter dataAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_log);

        ListView listViewLog = (ListView) findViewById(R.id.listViewLog);

        String[] columns = new String[] {
                AlgumDBContract.LogEntry.COLUMN_DATA,
                AlgumDBContract.LogEntry.COLUMN_MENSAGEM
        };

        int[] to = new int[]{
                R.id.txtLogData,
                R.id.txtLogMensagem
        };

        Cursor cursor = getContentResolver().query(
                AlgumDBContract.LogEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        dataAdapter = new SimpleCursorAdapter(
                this,
                R.layout.item_log,
                cursor,
                columns,
                to,
                0
        );

        listViewLog.setAdapter(dataAdapter);

    }
}
