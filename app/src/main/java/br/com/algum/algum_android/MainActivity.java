package br.com.algum.algum_android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.algum.algum_android.data.AlgumDBContract;
import br.com.algum.algum_android.sync.AlgumSyncTask;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private static final String TAG = "MainActivuty";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 9002;

    private Context mContext;
    private Activity mActivity;

    private String retorno;

    private GoogleApiClient mGoogleApiClient;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "algum.com.br";
    // The account name
    public static String account;

    public Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button listeners
        findViewById(R.id.sign_in_button).setOnClickListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.algum_server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());

        mContext = this;
        mActivity = this;

        //mAccount = CreateSyncAccount(mContext);

    }

    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
        if(sharedPref.contains(getString(R.string.emailUsuario))){

            Intent intent = new Intent(mContext,LancamentoGruposActivity.class);
            startActivity(intent);
            /*
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);

            if (opr.isDone()) {
                // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
                // and the GoogleSignInResult will be available instantly.
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            }else{
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
            */
        }else{

            View btnLogin = (View) findViewById(R.id.sign_in_button);
            btnLogin.setVisibility(View.VISIBLE);
            View loading = (View) findViewById(R.id.loadingPanel);
            loading.setVisibility(View.GONE);

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            String id_token = acct.getIdToken();
            String email = acct.getEmail();
            //String refreshToken = acct.

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.emailUsuario), email);
            editor.commit();

            ValidaUsuarioTask validaUsuarioTask = new ValidaUsuarioTask();
            validaUsuarioTask.execute(email, id_token);


        }else{
            Toast toast = new Toast(getApplicationContext());
            View layout = getLayoutInflater().inflate(R.layout.toast,(ViewGroup) findViewById(R.id.toast_layout_root));
            TextView text = (TextView) layout.findViewById(R.id.toastText);
            text.setText(result.toString());
            text.setTextColor(getResources().getColor(R.color.colorBack));
            toast.setView(layout);
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();

            Log.e(TAG, result.getStatus().toString());
            View btnLogin = (View) findViewById(R.id.sign_in_button);
            btnLogin.setVisibility(View.VISIBLE);
            View loading = (View) findViewById(R.id.loadingPanel);
            loading.setVisibility(View.GONE);

        }
    }


    private void signIn() {
        View btnLogin = (View) findViewById(R.id.sign_in_button);
        btnLogin.setVisibility(View.GONE);
        View loading = (View) findViewById(R.id.loadingPanel);
        loading.setVisibility(View.VISIBLE);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    /**
     * Create a new dummy account for the sync adapter
     *
     * @param context The application context
     */
    public static Account CreateSyncAccount(Context context) {
        Log.d(TAG, "Creating Sync Account");

        // Create the account type and default account
        Account newAccount = new Account(
                account, context.getString(R.string.sync_account_type));
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        ACCOUNT_SERVICE);
        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
        if (accountManager.addAccountExplicitly(newAccount, null, null)) {
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call context.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            Log.d(TAG, "Starting Sync Services");
            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);

            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);

            ContentResolver.addPeriodicSync(newAccount, AUTHORITY, new Bundle(), 20 * 60);

            return newAccount;
        }

        return null;
    }

    public class ValidaUsuarioTask extends AsyncTask<String, Void, Integer> {
        private final String LOG_TAG = ValidaUsuarioTask.class.getSimpleName();
        private Exception e=null;

        @Override
        protected Integer doInBackground(String... params) {

            int usuarioId = 0;

            if (params.length == 0) {
                return 0;
            }

            String mSelectionClause = AlgumDBContract.UsuariosEntry.COLUMN_EMAIL + " = ? ";
            String[] mSelectionArgs = {params[0]};
            Cursor cursor = getContentResolver().query(AlgumDBContract.UsuariosEntry.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);

            if(cursor.getCount()==0){

                HttpURLConnection urlConnection = null;
                BufferedReader reader = null;
                String strRetorno;

                try{

                    URL url = new URL(getString(R.string.WSurl)+"usuarios");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    urlConnection.setRequestProperty("Application-Authorization", params[1]);

                    urlConnection.connect();
                    StringBuffer buffer = new StringBuffer();

                    if(urlConnection.getResponseCode() == 200){
                        InputStream inputStream = urlConnection.getInputStream();
                        if (inputStream == null) {
                            throw new Exception("Usuário não encontrado.");
                        }

                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line + "\n");
                        }

                        strRetorno = buffer.toString();

                        if (buffer.length() == 0) {
                            throw new Exception("Usuário não encontrado.");
                        }

                        JSONObject usuarioJson = new JSONObject(strRetorno);

                        ContentValues usuarioValues = new ContentValues();
                        usuarioValues.put(AlgumDBContract.UsuariosEntry.COLUMN_ID, usuarioJson.getJSONObject("Usuario").getInt("id"));
                        usuarioValues.put(AlgumDBContract.UsuariosEntry.COLUMN_EMAIL, usuarioJson.getJSONObject("Usuario").getString("email"));
                        usuarioValues.put(AlgumDBContract.UsuariosEntry.COLUMN_DATA_SYNC, 0L);

                        getContentResolver().insert(AlgumDBContract.UsuariosEntry.CONTENT_URI, usuarioValues);

                        PreferenceManager.setDefaultValues(mContext, R.xml.preferences, false);

                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putInt(getString(R.string.idUsuario), usuarioJson.getJSONObject("Usuario").getInt("id"));
                        editor.putString(getString(R.string.emailUsuario), usuarioJson.getJSONObject("Usuario").getString("email"));
                        editor.commit();

                        usuarioId = usuarioJson.getJSONObject("Usuario").getInt("id");

                        Log.d(TAG, "Usuario novo");



                    }else{
                        InputStream inputStream = urlConnection.getErrorStream();
                        if (inputStream == null) {
                            throw new Exception("Mensagem de erro em branco.");
                        }

                        reader = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            buffer.append(line + "\n");
                        }

                        strRetorno = buffer.toString();

                        throw new Exception(strRetorno.toString());

                    }

                    return usuarioId;
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    this.e = e;
                } catch (Exception e){
                    Log.e(LOG_TAG, "Error ", e);
                    this.e = e;
                }finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (final IOException e) {
                            Log.e(LOG_TAG, "Error closing stream", e);
                            this.e = e;
                        }
                    }
                }
            }else{
                cursor.moveToFirst();
                usuarioId = cursor.getInt(cursor.getColumnIndex("_id"));

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.idUsuario), usuarioId);
                editor.putString(getString(R.string.emailUsuario), cursor.getString(cursor.getColumnIndex("email")));
                editor.commit();
                Log.d(TAG, "Usuario existente");
            }
            cursor.close();

            return usuarioId;
        }

        @Override
        protected void onPostExecute(Integer s) {
            if(this.e != null){
                try{
                    AlertDialog.Builder builder1  = new AlertDialog.Builder(MainActivity.this);
                    builder1.setTitle("Ops.");
                    builder1.setMessage("Erro ao validar usuário! Tente novamente mais tarde.\n" + this.e.getMessage());
                    builder1.setCancelable(true);
                    builder1.setNeutralButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }catch (Exception e){
                    Log.e(LOG_TAG, "Error closing stream", e);
                }


                View btnLogin = (View) findViewById(R.id.sign_in_button);
                btnLogin.setVisibility(View.VISIBLE);
                View loading = (View) findViewById(R.id.loadingPanel);
                loading.setVisibility(View.GONE);
            }else{

                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
                account = sharedPref.getString(getString(R.string.emailUsuario), "");

                //mAccount = CreateSyncAccount(mContext);
                /*
                Bundle bundle = new Bundle();
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
                bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

                ContentResolver.requestSync(mAccount, AUTHORITY, bundle);
                */
                AlgumSyncTask task = new AlgumSyncTask(mActivity);
                task.execute("");

            }

        }
    }
}
