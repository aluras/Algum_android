package br.com.algum.algum_android;

import android.content.ContentValues;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import br.com.algum.algum_android.data.AlgumDBContract;
import br.com.algum.algum_android.utils.Controle;

public class ContasEditActivity extends AppCompatActivity
        implements  LoaderManager.LoaderCallbacks<Cursor> {

    private SimpleCursorAdapter sAdapter;
    private int idConta = 0;
    private int idTipoConta = 0;
    private int idUsuario = 0;
    private Spinner spinner;
    private EditText editText2;
    private EditText editText3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contas_edit);
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
        editText3 = (EditText) findViewById(R.id.editText3);

        getSupportLoaderManager().initLoader(0, null, this);

        sAdapter = new SimpleCursorAdapter(
                this,
                R.layout.support_simple_spinner_dropdown_item,
                null,
                new String[] {AlgumDBContract.TipoContaEntry.COLUMN_DESCRICAO},
                new int[] {android.R.id.text1},
                0);

        sAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(sAdapter);

        Intent intent = getIntent();
        idConta = intent.getIntExtra("idConta",0);

        String selection = AlgumDBContract.ContasEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = {Integer.toString(idConta)};
        Cursor cConta = getContentResolver().query(AlgumDBContract.ContasEntry.CONTENT_URI,null,selection,selectionArgs,null);

        editText3.setText(String.format("%.2f", 0F));

        if(cConta.getCount() > 0) {
            cConta.moveToFirst();
            editText2.setText(cConta.getString(cConta.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_NOME)));
            editText3.setText(String.format("%.2f", cConta.getFloat(cConta.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_SALDO))));
            idTipoConta = cConta.getInt(cConta.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID));

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

        Uri uri = AlgumDBContract.TipoContaEntry.CONTENT_URI;

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
        if (idTipoConta > 0){
            data.moveToFirst();
            while(data.isAfterLast()==false){
                if (data.getInt(data.getColumnIndex(AlgumDBContract.TipoContaEntry.COLUMN_ID)) == idTipoConta){
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



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void gravar(){

        String nome = editText2.getText().toString();
        Float saldoAtual = Float.parseFloat(editText3.getText().toString().replace(',', '.'));
        Cursor obj = (Cursor) spinner.getSelectedItem();
        int idTipoConta = obj.getInt(obj.getColumnIndex(AlgumDBContract.TipoContaEntry.COLUMN_ID));

        if(nome.equals("")){
            editText2.setError("Digite o nome");
            return;
        }


        ContentValues values = new ContentValues();
        values.put(AlgumDBContract.ContasEntry.COLUMN_NOME,nome);
        values.put(AlgumDBContract.ContasEntry.COLUMN_SALDO,saldoAtual);
        values.put(AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID,idTipoConta);
        values.put(AlgumDBContract.ContasEntry.COLUMN_ALTERADO, 1);

        if(idConta > 0){
            String mSelectionClause = AlgumDBContract.ContasEntry.COLUMN_ID + " = ? ";
            String[] mSelectionArgs = {Integer.toString(idConta)};
            getContentResolver().update(AlgumDBContract.ContasEntry.CONTENT_URI, values, mSelectionClause,mSelectionArgs);
        }else{
            values.put(AlgumDBContract.ContasEntry.COLUMN_SALDO_INICIAL,saldoAtual);
            values.put(AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID,idUsuario);
            getContentResolver().insert(AlgumDBContract.ContasEntry.CONTENT_URI, values);
        }

        Controle.showMessage(this, "Conta registrada.");
        Controle.syncData(this);

        Intent intent = new Intent(this, ContasActivity .class);
        startActivity(intent);
    }
}
