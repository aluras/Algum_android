package br.com.algum.algum_android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.algum.algum_android.customAdapters.ExtratoAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class ExtratoActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderManager.LoaderCallbacks<Cursor>,View.OnClickListener {

    private ExtratoAdapter mAdapter;
    private int mIdConta;
    static final int MONTH_DIALOG_ID = 0;
    private Date dataExtrato;
    private ListView listLancamentos;
    private DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extrato);
        super.onCreateDrawer();

        dataExtrato = new Date();

        Intent intent = getIntent();

        mIdConta = intent.getIntExtra("idConta", 0);

        listLancamentos = (ListView) findViewById(R.id.listLancamentos);

        getSupportLoaderManager().initLoader(0, null, this);

        mAdapter = new ExtratoAdapter(this,null, 0);

        listLancamentos.setAdapter(mAdapter);

        TextView txtMes = (TextView) findViewById(R.id.textView3);
        SimpleDateFormat format = new SimpleDateFormat("MMMM/yyyy");
        txtMes.setText(format.format(dataExtrato));
        txtMes.setOnClickListener(this);

        ImageButton btAnterior = (ImageButton)findViewById(R.id.mesAnterior);
        btAnterior.setOnClickListener(this);

        ImageButton btPosterior = (ImageButton)findViewById(R.id.mesPosterior);
        btPosterior.setOnClickListener(this);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        Uri lancamentoUri = AlgumDBContract.LancamentoEntry.buildLancamentoUsuarioUri(sharedPref.getInt(getString(R.string.idUsuario), 0));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dataExtrato);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.DAY_OF_MONTH,1);
        Date dataInicio = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date dataTermino = calendar.getTime();

        String selection = "";

        selection = AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_DATA+" >= "+Long.toString(dataInicio.getTime())
                    + " AND "+AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_DATA+" <= "+Long.toString(dataTermino.getTime());

        if(mIdConta == 0){
            selection = selection + "";
        }else{
            selection = selection + " AND "+AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID+" = "+Integer.toString(mIdConta);
        }

        String[] projection =
                {
                        AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_ID
                        , AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_DATA
                        , AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID
                        , AlgumDBContract.GruposEntry.TABLE_NAME+"."+ AlgumDBContract.GruposEntry.COLUMN_NOME
                        , AlgumDBContract.ContasEntry.TABLE_NAME+"."+ AlgumDBContract.ContasEntry.COLUMN_NOME+" AS conta_nome"
                        , AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_VALOR
                        , AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID
                        , AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO
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

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case MONTH_DIALOG_ID:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Escolha o mês");
                LayoutInflater li = getLayoutInflater();
                View v = li.inflate(R.layout.month_picker, null);
                datePicker = (DatePicker) v.findViewById(R.id.monthPicker);
                datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android")).setVisibility(View.GONE);
                dialog.setView(v);
                dialog.setNegativeButton("Cancelar", null);
                dialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Calendar ca = Calendar.getInstance();
                        ca.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                        dataExtrato = ca.getTime();
                        getSupportLoaderManager().restartLoader(0,null,ExtratoActivity.this);
                        TextView txtMes = (TextView) findViewById(R.id.textView3);
                        SimpleDateFormat format = new SimpleDateFormat("MMMM/yyyy");
                        txtMes.setText(format.format(dataExtrato));
                    }
                });

                return dialog.show();
        }

        return null;
    }

    @Override
    public void onClick(View v) {
        if (v == (TextView) findViewById(R.id.textView3)){
            showDialog(MONTH_DIALOG_ID);
            Calendar ca = Calendar.getInstance();
            ca.setTime(dataExtrato);
            datePicker.updateDate(ca.get(Calendar.YEAR), ca.get(Calendar.MONTH), ca.get(Calendar.DAY_OF_MONTH));
        }
        if (v == (ImageButton) findViewById(R.id.mesAnterior)){
            Calendar ca = Calendar.getInstance();
            ca.setTime(dataExtrato);
            ca.add(Calendar.MONTH, -1);
            dataExtrato = ca.getTime();
            getSupportLoaderManager().restartLoader(0,null,ExtratoActivity.this);
            TextView txtMes = (TextView) findViewById(R.id.textView3);
            SimpleDateFormat format = new SimpleDateFormat("MMMM/yyyy");
            txtMes.setText(format.format(dataExtrato));
        }
        if (v == (ImageButton) findViewById(R.id.mesPosterior)){
            Calendar ca = Calendar.getInstance();
            ca.setTime(dataExtrato);
            ca.add(Calendar.MONTH, 1);
            dataExtrato = ca.getTime();
            getSupportLoaderManager().restartLoader(0,null,ExtratoActivity.this);
            TextView txtMes = (TextView) findViewById(R.id.textView3);
            SimpleDateFormat format = new SimpleDateFormat("MMMM/yyyy");
            txtMes.setText(format.format(dataExtrato));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        if(mIdConta == 0){
            intent = new Intent(this, LancamentoGruposActivity.class);
        }else{
            intent = new Intent(this, ContasActivity.class);
        }
        startActivity(intent);
    }
}
