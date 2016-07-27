package br.com.algum.algum_android;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.algum.algum_android.data.AlgumDBContract;

public class LancamentoValorActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {

    private final String LOG_TAG = LancamentoValorActivity.class.getSimpleName();
    static final int DATE_DIALOG_ID = 0;

    private int idTipoLancamento = 1;
    private int idGrupo = 0;
    private int idContaOrigem = 0;
    private int idContaDestino = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_valor);
        super.onCreateDrawer();

        Intent intent = getIntent();

        idTipoLancamento = intent.getIntExtra("tipoLancamento",1);
        idGrupo = intent.getIntExtra("idGrupo",0);
        idContaOrigem = intent.getIntExtra("idContaOrigem",0);
        idContaDestino = intent.getIntExtra("idContaDestino",0);

        String tipoLancamento = "";
        switch (idTipoLancamento){
            case 1:
                tipoLancamento = getString(R.string.despesa);
                break;
            case 2:
                tipoLancamento = getString(R.string.receita);
                break;
            case 3:
                tipoLancamento = getString(R.string.transferencia);
                break;
        }

        TextView txtTipoLancamento = (TextView) findViewById(R.id.txtTipoLancamento);
        txtTipoLancamento.setText("Nova " + tipoLancamento + " - " + intent.getStringExtra("nomeGrupo"));

        TextView txtConta = (TextView) findViewById(R.id.txtConta);
        if(idTipoLancamento == 3){
            txtConta.setText("Conta: " + intent.getStringExtra("nomeContaOrigem") + " -> " + intent.getStringExtra("nomeContaDestino"));
        }else{
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

            float nValor = Float.parseFloat(valor.getText().toString().replace(',', '.'));
            if (idTipoLancamento == 1) {
                nValor = nValor * -1;
            }

            if (valor.getText().toString().trim().equals("")) {
                valor.setError("Digite o valor!");
            } else {
                ContentValues lancamentoValues = new ContentValues();
                //lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_ID, grupo.getInt("id"));
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_VALOR, nValor);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_DATA, data.getText().toString());
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO, observacao.getText().toString());
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_TIPO_ID, idTipoLancamento);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID, idGrupo);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ORIGEM_ID, idContaOrigem);
                lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_DESTINO_ID, idContaDestino);

                getContentResolver().insert(AlgumDBContract.LancamentoEntry.CONTENT_URI, lancamentoValues);

                Toast.makeText(this, "Lançamento registrado.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LancamentoContasActivity.class);
                startActivity(intent);

            }
        }catch (NumberFormatException exception) {
            Toast.makeText(this, "Erro: Valor inválido.", Toast.LENGTH_LONG).show();

        }catch (Exception exception) {
            Toast.makeText(this, "Erro: " + exception.getMessage(), Toast.LENGTH_LONG).show();

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
