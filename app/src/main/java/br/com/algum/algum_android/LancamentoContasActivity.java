package br.com.algum.algum_android;

import android.accounts.Account;
import android.accounts.AccountManager;
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

import br.com.algum.algum_android.customAdapters.GrupoAdapter;
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
    private GrupoAdapter mGruposAdapter;
    private int tipoLancamento = 1;

    public int getTipoLancamento() {
        return tipoLancamento;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lancamento_contas);
        super.onCreateDrawer();


        /*
        Account account = AlgumAuthenticatorService.GetAccount();

        AccountManager accountManager = (AccountManager) context
                .getSystemService(Context.ACCOUNT_SERVICE);

        if (accountManager.addAccountExplicitly(account, null, null)) {

            ContentResolver.setIsSyncable(account, StubProvider.AUTHORITY, 1);

            ContentResolver.setSyncAutomatically(account,
                    StubProvider.AUTHORITY, true);

            ContentResolver.addPeriodicSync(account, StubProvider.AUTHORITY,
                    new Bundle(), SYNC_FREQUENCY);
        }
        */
/*
        mAccount = CreateSyncAccount(this);
        Bundle b = new Bundle();
        b.putBoolean(
                ContentResolver.SYNC_EXTRAS_MANUAL, true);
        b.putBoolean(
                ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        ContentResolver.addPeriodicSync(mAccount,ACCOUNT_TYPE,Bundle.EMPTY,60);
        //ContentResolver.requestSync(mAccount, this.getString(R.string.content_authority), b);
*/

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


    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        // Get an instance of the Android account manager

        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        context.ACCOUNT_SERVICE);

        //AccountManager accountManager = AccountManager.get(context);

        accountManager.addAccountExplicitly(newAccount, null, null);

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, null, null)) {
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
        Uri gruposUri = AlgumDBContract.GruposEntry.CONTENT_URI;
        String selection = AlgumDBContract.GruposEntry.COLUMN_TIPO_ID + " = ?";
        String[] selectionArgs = {String.valueOf(tipoLancamento)};
        return new CursorLoader(
                this,
                gruposUri,
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
