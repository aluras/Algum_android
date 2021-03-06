package br.com.algum.algum_android;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;

import br.com.algum.algum_android.customAdapters.GrupoAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class LancamentoGruposActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderCallbacks<Cursor> {

    private final String LOG_TAG = LancamentoGruposActivity.class.getSimpleName();

    private GrupoAdapter mGruposAdapter;
    private int tipoLancamento = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_contas);
        super.onCreateDrawer();

        GridView gridContas = (GridView) findViewById(R.id.gridViewContas);

        //Define cores para os botões de tipo de lançamento
        GradientDrawable gdDespesa = (GradientDrawable) findViewById(R.id.textDespesa).getBackground();
        gdDespesa.setColor(getResources().getColor(R.color.despesa));
        TextView txtDespesa = (TextView)findViewById(R.id.textDespesa);
        txtDespesa.setTextColor(getResources().getColor(R.color.texto_tipo));
        txtDespesa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaTipo(v);
            }
        });

        GradientDrawable gdReceita = (GradientDrawable) findViewById(R.id.textReceita).getBackground();
        gdReceita.setColor(getResources().getColor(R.color.receitaDisable));
        TextView txtReceita = (TextView)findViewById(R.id.textReceita);
        txtReceita.setTextColor(getResources().getColor(R.color.texto_tipo));
        txtReceita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaTipo(v);
            }
        });

        GradientDrawable gdTransferencia = (GradientDrawable) findViewById(R.id.textTransferencia).getBackground();
        gdTransferencia.setColor(getResources().getColor(R.color.transferenciaDisable));
        TextView txtTransferencia = (TextView)findViewById(R.id.textTransferencia);
        txtTransferencia.setTextColor(getResources().getColor(R.color.texto_tipo));
        txtTransferencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mudaTipo(v);
            }
        });

        mGruposAdapter = new GrupoAdapter(this,null, 0);

        getSupportLoaderManager().initLoader(0, null, this);

        gridContas.setAdapter(mGruposAdapter);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void mudaTipo(View v) {

        GradientDrawable gdDespesa = (GradientDrawable) findViewById(R.id.textDespesa).getBackground();
        gdDespesa.setColor(getResources().getColor(R.color.despesaDisable));

        GradientDrawable gdReceita = (GradientDrawable) findViewById(R.id.textReceita).getBackground();
        gdReceita.setColor(getResources().getColor(R.color.receitaDisable));

        GradientDrawable gdTransferencia = (GradientDrawable) findViewById(R.id.textTransferencia).getBackground();
        gdTransferencia.setColor(getResources().getColor(R.color.transferenciaDisable));

        switch (v.getId()){
            case R.id.textDespesa:
                gdDespesa.setColor(getResources().getColor(R.color.despesa));
                tipoLancamento = 1;
                break;
            case R.id.textReceita:
                gdReceita.setColor(getResources().getColor(R.color.receita));
                tipoLancamento = 2;
                break;
            case R.id.textTransferencia:
                gdTransferencia.setColor(getResources().getColor(R.color.transferencia));
                tipoLancamento = 3;
                break;

        }
        getSupportLoaderManager().restartLoader(0, null, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri grupoUsuariosUri = AlgumDBContract.GrupoUsuariosEntry.buildGrupoUsuariosUri(id_usuario);

        String selection = AlgumDBContract.GrupoUsuariosEntry.TABLE_NAME+ "."+ AlgumDBContract.GrupoUsuariosEntry.COLUMN_EXCLUIDO + " = ? AND "+
                AlgumDBContract.GruposEntry.TABLE_NAME+"."+ AlgumDBContract.GruposEntry.COLUMN_TIPO_ID + " = ? ";
        String[] selectionArgs = {"0",String.valueOf(tipoLancamento)};

        return new CursorLoader(
                this,
                grupoUsuariosUri,
                null,
                selection,
                selectionArgs,
                null
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mGruposAdapter.swapCursor(data);
        mGruposAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mGruposAdapter.swapCursor(null);
    }
}
