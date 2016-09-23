package br.com.algum.algum_android;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import br.com.algum.algum_android.data.AlgumDBContract;
import br.com.algum.algum_android.utils.Controle;

public class GruposEditActivity extends AppCompatActivity
        implements  LoaderManager.LoaderCallbacks<Cursor>  {

    private SimpleCursorAdapter sAdapter;
    private int idGrupo = 0;
    private int idTipoGrupo = 0;
    private int idUsuario = 0;
    private Spinner spinner;
    private EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupos_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //super.onCreateDrawer();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        idUsuario = sharedPref.getInt(getString(R.string.idUsuario), 0);

        spinner = (Spinner) findViewById(R.id.spinner2);
        final Button button = (Button) findViewById(R.id.button);
        editText2 = (EditText) findViewById(R.id.editText2);

        getSupportLoaderManager().initLoader(0, null, this);

        sAdapter = new SimpleCursorAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                null,
                new String[] {AlgumDBContract.TipoGrupoEntry.COLUMN_DESCRICAO},
                new int[] {android.R.id.text1},
                0);

        sAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(sAdapter);

        Intent intent = getIntent();
        idGrupo = intent.getIntExtra("idGrupo",0);

        String selection = AlgumDBContract.GruposEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = {Integer.toString(idGrupo)};
        Cursor cGrupo = getContentResolver().query(AlgumDBContract.GruposEntry.CONTENT_URI,null,selection,selectionArgs,null);

        if(cGrupo.getCount() > 0) {
            cGrupo.moveToFirst();
            editText2.setText(cGrupo.getString(cGrupo.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_NOME)));
            idTipoGrupo = cGrupo.getInt(cGrupo.getColumnIndex(AlgumDBContract.GruposEntry.COLUMN_TIPO_ID));

        }else{
            if(editText2.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravar();
            }
        });

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = AlgumDBContract.TipoGrupoEntry.CONTENT_URI;

        return new CursorLoader(
                this,
                uri,
                null,
                null,
                null,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        sAdapter.swapCursor(data);
        sAdapter.notifyDataSetChanged();
        if (idTipoGrupo > 0){
            data.moveToFirst();
            while(data.isAfterLast()==false){
                if (data.getInt(data.getColumnIndex(AlgumDBContract.TipoGrupoEntry.COLUMN_ID)) == idTipoGrupo){
                    spinner.setSelection(data.getPosition());
                }
                data.moveToNext();
            }

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        sAdapter.swapCursor(null);
    }

    protected void gravar(){

        String nome = editText2.getText().toString();
        Cursor obj = (Cursor) spinner.getSelectedItem();
        int idTipoGrupo = obj.getInt(obj.getColumnIndex(AlgumDBContract.TipoGrupoEntry.COLUMN_ID));

        if(nome.equals("")){
            editText2.setError("Digite o nome");
            return;
        }

        ContentValues values = new ContentValues();
        values.put(AlgumDBContract.GruposEntry.COLUMN_NOME, nome);
        values.put(AlgumDBContract.GruposEntry.COLUMN_TIPO_ID,idTipoGrupo);
        values.put(AlgumDBContract.GruposEntry.COLUMN_ALTERADO, 1);

        if(idGrupo > 0){
            String mSelectionClause = AlgumDBContract.GruposEntry.COLUMN_ID + " = ? ";
            String[] mSelectionArgs = {Integer.toString(idGrupo)};
            getContentResolver().update(AlgumDBContract.GruposEntry.CONTENT_URI, values, mSelectionClause,mSelectionArgs);
        }else{
            values.put(AlgumDBContract.GruposEntry.COLUMN_USUARIO_ID,idUsuario);
            getContentResolver().insert(AlgumDBContract.GruposEntry.CONTENT_URI, values);
        }

        Controle.showMessage(this, "Categoria registrada.");
        Controle.syncData(this);

        Intent intent = new Intent(this, GruposActivity .class);
        startActivity(intent);
    }
}
