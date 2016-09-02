package br.com.algum.algum_android.sync;

import android.accounts.Account;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import br.com.algum.algum_android.LancamentoContasActivity;
import br.com.algum.algum_android.MainActivity;
import br.com.algum.algum_android.R;

/**
 * Created by sn1007071 on 29/08/2016.
 */
public class AlgumSyncTask extends AsyncTask<String,Void,String> {

    public final String LOG_TAG = AlgumSyncAdapter.class.getSimpleName();

    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

    private boolean idrNoSilentSignIn = false;
    private boolean idrSyncActive = false;
    private Account newAccount;
    private Context mContext;

    public AlgumSyncTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {

        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.userInfo), Context.MODE_PRIVATE);

        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

        //ContentResolver.removePeriodicSync(newAccount, AUTHORITY, bundle);
        if (!sharedPref.contains(mContext.getString(R.string.idUsuario))){
            Log.d(LOG_TAG, "Cancel Sync - No user data");
            return "Erro - Sem dados de usuário";
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(mContext.getString(R.string.algum_server_client_id))
                .requestEmail()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mGoogleApiClient.connect();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

        if (opr.isDone()) {

            GoogleSignInResult result = opr.get();
            AlgumSyncOperation operations = new AlgumSyncOperation(mContext,result);
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

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(idrNoSilentSignIn){
            //ContentResolver.addPeriodicSync(newAccount, AUTHORITY, new Bundle(), 60 * 60);
            if(mContext.getClass() == MainActivity.class){
                Intent intent = new Intent(mContext,LancamentoContasActivity.class);
                mContext.startActivity(intent);
            }
        }else{
            AlgumSyncTask task = new AlgumSyncTask(mContext);
            task.execute("");
        }
    }
}
