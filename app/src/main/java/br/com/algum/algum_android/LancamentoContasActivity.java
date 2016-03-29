package br.com.algum.algum_android;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.widget.GridView;
import android.widget.TextView;

import br.com.algum.algum_android.customAdapters.ContaAdapter;
import br.com.algum.algum_android.entities.Conta;

public class LancamentoContasActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String LOG_TAG = LancamentoContasActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_contas);
        super.onCreateDrawer();

        GridView gridContas = (GridView) findViewById(R.id.gridViewContas);
        Conta contas[] = new Conta[]{
                new Conta("CC Banco do Brasil",R.color.tile1),
                new Conta("Cartão CEF",R.color.tile2),
                new Conta("Carteira",R.color.tile3),
                new Conta("Vale Refeição",R.color.tile1)
        };

        ContaAdapter contasAdapter = new ContaAdapter(this,R.layout.tiles, contas);

        gridContas.setAdapter(contasAdapter);


        //Define cores para os botões de tipo de lançamento
        GradientDrawable gdDespesa = (GradientDrawable) findViewById(R.id.textDespesa).getBackground();
        gdDespesa.setColor(getResources().getColor(R.color.despesa));
        TextView txtDespesa = (TextView)findViewById(R.id.textDespesa);
        txtDespesa.setTextColor(getResources().getColor(R.color.texto_tipo));

        GradientDrawable gdReceita = (GradientDrawable) findViewById(R.id.textReceita).getBackground();
        gdReceita.setColor(getResources().getColor(R.color.receitaDisable));
        TextView txtReceita = (TextView)findViewById(R.id.textReceita);
        txtReceita.setTextColor(getResources().getColor(R.color.texto_tipo));

        GradientDrawable gdTransferencia = (GradientDrawable) findViewById(R.id.textTransferencia).getBackground();
        gdTransferencia.setColor(getResources().getColor(R.color.transferenciaDisable));
        TextView txtTransferencia = (TextView)findViewById(R.id.textTransferencia);
        txtTransferencia.setTextColor(getResources().getColor(R.color.texto_tipo));




    }
}
