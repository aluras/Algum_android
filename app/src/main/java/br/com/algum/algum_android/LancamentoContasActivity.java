package br.com.algum.algum_android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
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

import br.com.algum.algum_android.customAdapters.ContaAdapter;
import br.com.algum.algum_android.data.AlgumDBContract;

public class LancamentoContasActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, LoaderCallbacks<Cursor> {

    private final String LOG_TAG = LancamentoContasActivity.class.getSimpleName();


    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "br.com.algum";
    // The account name
    public static final String ACCOUNT = "dummyaccount";
    // Instance fields
    Account mAccount;
    private ContaAdapter mCcontasAdapter;

    public int getTipoLancamento() {
        return tipoLancamento;
    }

    private int tipoLancamento = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_contas);
        super.onCreateDrawer();

        mAccount = CreateSyncAccount(this);
        Bundle b = new Bundle();
        b.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        //ContentResolver.requestSync(mAccount, this.getString(R.string.content_authority), b);

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

        //Cursor cursor = getContentResolver().query(AlgumDBContract.ContasEntry.CONTENT_URI, null, null, null, null);
        //cursor.setNotificationUri(getContentResolver(),AlgumDBContract.ContasEntry.CONTENT_URI);

        mCcontasAdapter = new ContaAdapter(this,null, 0);

        getSupportLoaderManager().initLoader(0, null, this);

        gridContas.setAdapter(mCcontasAdapter);

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
        //mCcontasAdapter.notifyDataSetChanged();

    }


    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
               context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        // Get an instance of the Android account manager
        /*
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        context.ACCOUNT_SERVICE);
*/
        AccountManager accountManager = AccountManager.get(context);

        accountManager.addAccountExplicitly(newAccount, null, null);

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            //onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contasUri = AlgumDBContract.ContasEntry.CONTENT_URI;
        String selection = "";

        switch (tipoLancamento){
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
        mCcontasAdapter.swapCursor(data);
        mCcontasAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCcontasAdapter.swapCursor(null);
    }
}
