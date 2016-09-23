package br.com.algum.algum_android.sync;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.algum.algum_android.R;
import br.com.algum.algum_android.utils.Controle;

/**
 * Created by sn1007071 on 28/03/2016.
 */
public class AlgumSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = AlgumSyncAdapter.class.getSimpleName();
    private String tok;

    private int usuarioId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    private GoogleApiClient mGoogleApiClient;


    public AlgumSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

    }


    @Override
    public void onPerformSync(final Account account, final Bundle extras, final String authority, ContentProviderClient provider, final SyncResult syncResult) {

        SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.userInfo), Context.MODE_PRIVATE);

        if (!sharedPref.contains(getContext().getString(R.string.idUsuario))){
            Log.d(LOG_TAG, "Cancel Sync - No user data");
            return;
        }
        usuarioId = sharedPref.getInt("idUsuario",0);

        Controle.gravaLog(getContext(), dateTimeFormat.format(new Date()) + " SyncAdapter iniciado", usuarioId);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getContext().getString(R.string.algum_server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        try {
            ConnectionResult result = mGoogleApiClient.blockingConnect();
            if (result.isSuccess()) {
                Controle.gravaLog(getContext(), dateTimeFormat.format(new Date()) + " Sync - conectado Google signin", usuarioId);

                GoogleSignInResult googleSignInResult =
                        Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient).await();


                AlgumSyncOperation operations = new AlgumSyncOperation(getContext(),googleSignInResult);

                if(!operations.performSync()) {
                    syncResult.stats.numAuthExceptions++;
                    syncResult.fullSyncRequested = true;
                }

                SharedPreferences s = PreferenceManager.getDefaultSharedPreferences(getContext());
                syncResult.delayUntil = Integer.parseInt(s.getString(getContext().getString(R.string.sync_freq),getContext().getString(R.string.sync_freq_default))) *60;

                Controle.gravaLog(getContext(), dateTimeFormat.format(new Date()) + " Sync - Concluida", usuarioId);
            }else{
                Controle.gravaLog(getContext(), dateTimeFormat.format(new Date()) + " Sync - não conectado Google signin: "+result.getErrorMessage(), usuarioId);
            }
        } finally {
            mGoogleApiClient.disconnect();
        }

    }


}
