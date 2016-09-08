package br.com.algum.algum_android.sync;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;

import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.algum.algum_android.R;
import br.com.algum.algum_android.utils.Controle;

/**
 * Created by sn1007071 on 05/09/2016.
 */
public class AlgumEditTask extends AsyncTask<String,Void,String>  {
    public final String LOG_TAG = AlgumEditTask.class.getSimpleName();

    private boolean idrSucesso = false;
    private Context mContext;
    private int usuarioId;
    private String usuarioEmail = "";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

    public AlgumEditTask(Context context) {
        this.mContext = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.userInfo), Context.MODE_PRIVATE);
        usuarioId = sharedPref.getInt(mContext.getString(R.string.idUsuario), 0);
        usuarioEmail = sharedPref.getString(mContext.getString(R.string.emailUsuario), "");

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
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                AlgumSyncOperation operations = new AlgumSyncOperation(mContext,result);
                idrSucesso = true;
                operations.performSendEdit();

            } else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.remove(mContext.getString(R.string.emailUsuario));
                editor.commit();
            }


        } else {
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    idrSucesso = false;
                }
            });
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(idrSucesso){
            Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " Edições sincronizadas.", usuarioId);
        }else{
            AlgumEditTask task = new AlgumEditTask(mContext);
            task.execute("");
        }
    }
}
