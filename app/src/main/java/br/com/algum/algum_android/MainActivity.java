package br.com.algum.algum_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener{

    private static final String TAG = "MainActivuty";
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_GET_TOKEN = 9002;

    private Context mContext;

    private String retorno;

    private GoogleApiClient mGoogleApiClient;

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

    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
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

            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(getString(R.string.emailUsuario), email);
            editor.putString(getString(R.string.tokenUsuario), id_token);
            editor.commit();

            ValidaUsuarioTask validaUsuarioTask = new ValidaUsuarioTask();
            validaUsuarioTask.execute(email, id_token);


        }else{
            Toast.makeText(MainActivity.this, result.toString(), Toast.LENGTH_LONG).show();
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


    public class ValidaUsuarioTask extends AsyncTask<String, Void, Integer> {
        private final String LOG_TAG = ValidaUsuarioTask.class.getSimpleName();
        private Exception e=null;

        @Override
        protected Integer doInBackground(String... params) {

            if (params.length == 0) {
                return 0;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String strRetorno;


            try{

                URL url = new URL(getString(R.string.WSurl)+"usuarios");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                //String postParams = getString(R.string.emailUsuario)+"="+params[0];
                urlConnection.setRequestProperty("Application-Authorization", params[1]);
                //urlConnection.setRequestProperty("Content-length", Integer.toString(postParams.length()));

                //urlConnection.setDoOutput(true);
                //urlConnection.setDoInput(true);
                //DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                //wr.write(postParams.getBytes());

                urlConnection.connect();
                StringBuffer buffer = new StringBuffer();

                if(urlConnection.getResponseCode() == 200){
                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    if (inputStream == null) {
                        // Nothing to do.
                        throw new Exception("Usuário não encontrado.");
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    strRetorno = buffer.toString();

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        throw new Exception("Usuário não encontrado.");
                    }

                }else{
                    InputStream inputStream = urlConnection.getErrorStream();
                    if (inputStream == null) {
                        // Nothing to do.
                        throw new Exception("Mensagem de erro em branco.");
                    }

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    strRetorno = buffer.toString();

                    throw new Exception(strRetorno.toString());

                    //return 0;

                }

                return 1;
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                this.e = e;
            } catch (Exception e){
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
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
            return 1;
        }

        @Override
        protected void onPostExecute(Integer s) {
            if(this.e != null){
                Toast.makeText(mContext, "Erro ao validar usuário! Tente novamente mais tarde.\n" + this.e.getMessage() , Toast.LENGTH_LONG).show();
                View btnLogin = (View) findViewById(R.id.sign_in_button);
                btnLogin.setVisibility(View.VISIBLE);
                View loading = (View) findViewById(R.id.loadingPanel);
                loading.setVisibility(View.GONE);
            }else{
                SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putInt(getString(R.string.idUsuario), s);
                editor.commit();

                Intent intent = new Intent(mContext,LancamentoContasActivity.class);
                intent.putExtra("teste","executado");
                startActivity(intent);
            }

        }
    }
}
