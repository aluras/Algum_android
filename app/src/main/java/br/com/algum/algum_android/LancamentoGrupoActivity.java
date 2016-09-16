package br.com.algum.algum_android;

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
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import br.com.algum.algum_android.customAdapters.ContaAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class LancamentoGrupoActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = LancamentoGrupoActivity.class.getSimpleName();

    private ContaAdapter mContasAdapter;
    private ContaAdapter mContasAdapterDestino;

    private int idTipoLancamento = 1;
    private String nomeGrupo = "";
    private int idGrupo = 0;
    private float valorGasto = 0;
    private int idContaOrigem = 0;
    private int idContaDestino = 0;
    private String nomeContaOrigem = "";
    private String nomeContaDestino = "";

    public int getTipoLancamento() {
        return idTipoLancamento;
    }

    public String getNomeGrupo() {
        return nomeGrupo;
    }

    public int getIdGrupo() {
        return idGrupo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_grupo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //super.onCreateDrawer();

        Intent intent = getIntent();

        idTipoLancamento = intent.getIntExtra("tipoLancamento", 1);
        nomeGrupo = intent.getStringExtra("nomeGrupo");
        idGrupo = intent.getIntExtra("idGrupo", 0);
        valorGasto = intent.getFloatExtra("valorGastoGrupo",0);



        TextView txtContaOrigem = (TextView) findViewById(R.id.txtContaOrigem);
        TextView txtContaDestino = (TextView) findViewById(R.id.txtContaDestino);
        GridView gridContas = (GridView) findViewById(R.id.gridViewGrupos);
        GridView gridViewContaDestino = (GridView) findViewById(R.id.gridViewContaDestino);

        txtContaOrigem.setText(R.string.contas);
        txtContaDestino.setVisibility(View.GONE);
        gridViewContaDestino.setVisibility(View.GONE);

        TextView txtTipoLancamento = (TextView) findViewById(R.id.txtTipoLancamento);
        TextView txtGasto = (TextView) findViewById(R.id.txtGastoGrupo);
        txtTipoLancamento.setText(nomeGrupo);
        txtGasto.setText("(R$ " + String.format("%.2f", valorGasto) + " no mês)");
        if(valorGasto>0){
            txtGasto.setTextColor(getResources().getColor(R.color.colorAccent));
        }else if(valorGasto<0){
            txtGasto.setTextColor(getResources().getColor(R.color.despesa));
        }
        LinearLayout desGrupo = (LinearLayout) findViewById(R.id.desGrupo);
        String tipoLancamento = "";
        switch (idTipoLancamento){
            case 1:
                tipoLancamento = getString(R.string.despesa);
                desGrupo.setBackgroundColor(getResources().getColor(R.color.despesaDisable));
                break;
            case 2:
                tipoLancamento = getString(R.string.receita);
                desGrupo.setBackgroundColor(getResources().getColor(R.color.receitaDisable));
                break;
            case 3:
                tipoLancamento = getString(R.string.transferencia);
                desGrupo.setBackgroundColor(getResources().getColor(R.color.transferenciaDisable));
                txtGasto.setVisibility(View.INVISIBLE);
                txtContaOrigem.setText(R.string.contaOrigem);
                txtContaDestino.setVisibility(View.VISIBLE);
                gridViewContaDestino.setVisibility(View.VISIBLE);
                break;
        }



        mContasAdapter = new ContaAdapter(this,null, 0, false);
        mContasAdapterDestino = new ContaAdapter(this,null, 0, true);

        getSupportLoaderManager().initLoader(0, null, this);

        gridContas.setAdapter(mContasAdapter);
        gridViewContaDestino.setAdapter(mContasAdapterDestino);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        Uri contasUri = AlgumDBContract.ContasEntry.buildContaUsuarioUri(sharedPref.getInt(getString(R.string.idUsuario),0));

        String selection = "";

        switch (idTipoLancamento){
            case 1:
                selection =  AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID + " IN (1,3,4,5) ";
                break;
            case 2:
                selection =  AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID + " IN (1,2,4,5) ";
                break;
        }

        return new CursorLoader(
                this,
                contasUri,
                null,
                selection,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mContasAdapter.swapCursor(data);
        mContasAdapter.notifyDataSetChanged();
        mContasAdapterDestino.swapCursor(data);
        mContasAdapterDestino.notifyDataSetChanged();


        setGridViewHeightBasedOnChildren((GridView) findViewById(R.id.gridViewGrupos));
        setGridViewHeightBasedOnChildren((GridView) findViewById(R.id.gridViewContaDestino));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mContasAdapter.swapCursor(null);
        mContasAdapterDestino.swapCursor(null);
    }

    public void recebeConta(boolean destino, int idConta, String nomeConta, int idGrupo, String nomeGrupo, int tipoLancamento, Float saldo){

        if (destino){
            idContaDestino = idConta;
            nomeContaDestino = nomeConta;
        }else{
            idContaOrigem = idConta;
            nomeContaOrigem = nomeConta;
        }

        if ((idContaDestino != 0 && idContaOrigem != 0) || tipoLancamento != 3){
            Intent lancamentoValorIntent = new Intent(this, LancamentoValorActivity.class);
            lancamentoValorIntent.putExtra("tipoLancamento",tipoLancamento);
            lancamentoValorIntent.putExtra("nomeGrupo",nomeGrupo);
            lancamentoValorIntent.putExtra("idGrupo",idGrupo);
            lancamentoValorIntent.putExtra("nomeContaOrigem",nomeContaOrigem);
            lancamentoValorIntent.putExtra("nomeContaDestino",nomeContaDestino);
            lancamentoValorIntent.putExtra("idContaOrigem",idContaOrigem);
            lancamentoValorIntent.putExtra("idContaDestino",idContaDestino);
            lancamentoValorIntent.putExtra("valorGastoGrupo",valorGasto);
            lancamentoValorIntent.putExtra("saldoConta",saldo);
            this.startActivity(lancamentoValorIntent);
        }
    }

    public void setGridViewHeightBasedOnChildren(GridView gridView) {
        ContaAdapter contaAdapter = (ContaAdapter) gridView.getAdapter();
        if (contaAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < contaAdapter.getCount(); i++) {
            View listItem = contaAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = (totalHeight + (gridView.getCount() - 1))*2;
        params.height = 824;
        gridView.setLayoutParams(params);
        gridView.requestLayout();
    }
}
