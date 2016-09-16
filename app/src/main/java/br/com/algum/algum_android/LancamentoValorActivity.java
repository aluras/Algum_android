package br.com.algum.algum_android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.algum.algum_android.data.AlgumDBContract;
import br.com.algum.algum_android.utils.Controle;

public class LancamentoValorActivity extends AppCompatActivity
        implements View.OnClickListener {

    private final String LOG_TAG = LancamentoValorActivity.class.getSimpleName();
    static final int DATE_DIALOG_ID = 0;

    private int idTipoLancamento = 1;
    private int idGrupo = 0;
    private float valorGasto = 0;
    private float saldo = 0;
    private int idContaOrigem = 0;
    private int idContaDestino = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_valor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //super.onCreateDrawer();

        Intent intent = getIntent();

        idTipoLancamento = intent.getIntExtra("tipoLancamento",1);
        idGrupo = intent.getIntExtra("idGrupo",0);
        idContaOrigem = intent.getIntExtra("idContaOrigem",0);
        idContaDestino = intent.getIntExtra("idContaDestino",0);
        valorGasto = intent.getFloatExtra("valorGastoGrupo",0);
        saldo = intent.getFloatExtra("saldoConta",0);

        TextView txtTipoLancamento = (TextView) findViewById(R.id.txtTipoLancamento);
        TextView txtGasto = (TextView) findViewById(R.id.txtGastoGrupo);
        txtTipoLancamento.setText(intent.getStringExtra("nomeGrupo"));
        txtGasto.setText("(R$ " + String.format("%.2f", valorGasto) + " no mês)");
        if(valorGasto>0){
            txtGasto.setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(valorGasto<0){
            txtGasto.setTextColor(getResources().getColor(R.color.despesa));
        }
        RelativeLayout botoesValores = (RelativeLayout) findViewById(R.id.botoesValores);
        String tipoLancamento = "";
        switch (idTipoLancamento){
            case 1:
                tipoLancamento = getString(R.string.despesa);
                botoesValores.setBackgroundColor(getResources().getColor(R.color.despesaDisable));
                break;
            case 2:
                tipoLancamento = getString(R.string.receita);
                botoesValores.setBackgroundColor(getResources().getColor(R.color.receitaDisable));
                break;
            case 3:
                txtGasto.setVisibility(View.INVISIBLE);
                tipoLancamento = getString(R.string.transferencia);
                botoesValores.setBackgroundColor(getResources().getColor(R.color.transferenciaDisable));
                break;
        }

        TextView txtConta = (TextView) findViewById(R.id.txtConta);
        TextView txtSaldo = (TextView) findViewById(R.id.txtSaldoConta);
        if(idTipoLancamento == 3){
            txtConta.setText("Conta: " + intent.getStringExtra("nomeContaOrigem") + " -> " + intent.getStringExtra("nomeContaDestino"));
        }else{
            txtSaldo.setText("(saldo: R$ "+String.format("%.2f", saldo)+")");
            if(saldo>0){
                txtSaldo.setTextColor(getResources().getColor(R.color.colorAccent));
            }else if(valorGasto<0){
                txtSaldo.setTextColor(getResources().getColor(R.color.despesa));
            }
            txtConta.setText("Conta: " + intent.getStringExtra("nomeContaOrigem"));
        }


        TextView txtData = (TextView) findViewById(R.id.txtData);
        TextView txtValor = (TextView) findViewById(R.id.txtValor);
        TextView txtDetalhe = (TextView) findViewById(R.id.txtDetalhe);
        final Button button = (Button) findViewById(R.id.button);

        Date data = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        txtData.setText(format.format(data));
        txtData.setOnClickListener(this);

        if(txtValor.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gravar();
            }
        });

        txtValor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    gravar();
                }
                return false;
            }
        });

        txtDetalhe.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId== EditorInfo.IME_ACTION_DONE){
                    gravar();
                }
                return false;
            }
        });

    }

    protected void gravar() {
        try {
            TextView valor = (TextView) findViewById(R.id.txtValor);
            TextView data = (TextView) findViewById(R.id.txtData);
            TextView observacao = (TextView) findViewById(R.id.txtDetalhe);

            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            Date date = format.parse(data.getText().toString());

            if (valor.getText().toString().trim().equals("")) {
                valor.setError("Digite o valor!");
            } else {

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
                int usuarioId = sharedPref.getInt(getString(R.string.idUsuario),0);

                float nValor = Float.parseFloat(valor.getText().toString().replace(',', '.'));
                ContentValues lancamentoValues = new ContentValues();

                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_VALOR, nValor);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_DATA, date.getTime());
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO, observacao.getText().toString());
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID, idGrupo);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID, idContaDestino);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_USUARIO_ID, usuarioId);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_EXCLUIDO, 0);

                ContentValues saldoValue = new ContentValues();
                saldoValue.put(AlgumDBContract.LancamentoEntry.COLUMN_VALOR,nValor);
                saldoValue.put(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID,idContaDestino);

                if (idTipoLancamento == 3){
                    getContentResolver().insert(AlgumDBContract.LancamentoEntry.CONTENT_URI, lancamentoValues);
                    getContentResolver().update(AlgumDBContract.ContasEntry.CONTENT_SALDO_URI, saldoValue, null, null);

                }

                if (idTipoLancamento == 1 || idTipoLancamento == 3) {
                    nValor = nValor * -1;
                }

                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_VALOR, nValor);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID, idContaOrigem);

                getContentResolver().insert(AlgumDBContract.LancamentoEntry.CONTENT_URI, lancamentoValues);

                saldoValue.put(AlgumDBContract.LancamentoEntry.COLUMN_VALOR, nValor);
                saldoValue.put(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID, idContaOrigem);

                getContentResolver().update(AlgumDBContract.ContasEntry.CONTENT_SALDO_URI, saldoValue, null, null);

                Controle.showMessage(this, "Lançamento registrado.");
                Controle.syncData(this);
                Intent intent = new Intent(this, LancamentoContasActivity.class);
                startActivity(intent);

            }
        }catch (NumberFormatException exception) {
            Controle.showMessage(this,"Erro: Valor inválido.");
            TextView txtValor = (TextView) findViewById(R.id.txtValor);
            if(txtValor.requestFocus()) {
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }

        }catch (Exception exception) {
            Controle.showMessage(this,"Erro: " + exception.getMessage());
            Log.e(LOG_TAG, "Error ", exception);

        }



    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Calendar calendario = Calendar.getInstance();

        int ano = calendario.get(Calendar.YEAR);
        int mes = calendario.get(Calendar.MONTH);
        int dia = calendario.get(Calendar.DAY_OF_MONTH);

        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, ano, mes, dia);
        }

        return null;
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener()
    {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
        {
            Calendar cal = Calendar.getInstance();
            cal.set(year,monthOfYear,dayOfMonth);
            Date data = cal.getTime();
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            TextView txtData = (TextView) findViewById(R.id.txtData);
            txtData.setText(format.format(data));
        }
    };

    @Override
    public void onClick(View v) {
        if (v == (TextView) findViewById(R.id.txtData)){
            showDialog(DATE_DIALOG_ID);
        }
    }
}
