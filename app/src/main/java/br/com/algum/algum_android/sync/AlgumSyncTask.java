package br.com.algum.algum_android.sync;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import br.com.algum.algum_android.BuildConfig;
import br.com.algum.algum_android.R;
import br.com.algum.algum_android.utils.Controle;

/**
 * Created by sn1007071 on 29/08/2016.
 */
public class AlgumSyncTask extends AsyncTask<String,Void,String> {

    public final String LOG_TAG = AlgumSyncAdapter.class.getSimpleName();

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    private boolean idrNoSilentSignIn = false;
    private boolean idrSyncActive = false;
    private Account newAccount;
    private Activity mActivity;

    public AlgumSyncTask(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    protected String doInBackground(String... strings) {

        SharedPreferences sharedPref = mActivity.getSharedPreferences(mActivity.getString(R.string.userInfo), Context.MODE_PRIVATE);

        newAccount = new Account(
                sharedPref.getString(mActivity.getString(R.string.emailUsuario), ""), mActivity.getString(R.string.sync_account_type));
        idrSyncActive = ContentResolver.isSyncActive(newAccount,AUTHORITY);

        if (!idrSyncActive){
            ContentResolver.removePeriodicSync(newAccount, AUTHORITY, new Bundle());
            if (!sharedPref.contains(mActivity.getString(R.string.idUsuario))){
                Log.d(LOG_TAG, "Cancel Sync - No user data");
                return "Erro - Sem dados de usuário";
            }

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(mActivity.getString(R.string.algum_server_client_id))
                    .requestEmail()
                    .build();

            GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mGoogleApiClient.connect();

            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

            if (opr.isDone()) {

                GoogleSignInResult result = opr.get();
                AlgumSyncOperation operations = new AlgumSyncOperation(mActivity,result);
                idrNoSilentSignIn = true;
                operations.performSync();

            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        idrNoSilentSignIn = false;
                    }
                });
            }

        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(idrSyncActive){
            Controle.showMessage(mActivity, "Sincronização em execução");
        }else{
            if(idrNoSilentSignIn){
                Controle.showMessage(mActivity, "Sincronização concluída");
                ContentResolver.addPeriodicSync(newAccount, AUTHORITY, new Bundle(), 60 * 60);
            }else{
                AlgumSyncTask task = new AlgumSyncTask(mActivity);
                task.execute("");
            }
        }
    }
}
