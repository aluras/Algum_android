package br.com.algum.algum_android;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

import br.com.algum.algum_android.data.AlgumDBContract;
import br.com.algum.algum_android.utils.Controle;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.settings, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener,
            GoogleApiClient.OnConnectionFailedListener {

        protected Context mContext;
        private GoogleApiClient mGoogleApiClient;
        public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.algum_server_client_id))
                    .requestEmail()
                    .build();

            mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            mContext = this.getActivity();

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            Preference myPref = findPreference(getString(R.string.user_remove));
            myPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("Exclusão de dados");
                    builder.setMessage("Confirma a exclusão dos dados do usuario?");
                    builder.setPositiveButton(R.string.excluir, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                                    new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(Status status) {
                                            getActivity().getContentResolver().delete(AlgumDBContract.LancamentoEntry.CONTENT_URI, null, null);
                                            getActivity().getContentResolver().delete(AlgumDBContract.ContasEntry.CONTENT_URI, null, null);
                                            getActivity().getContentResolver().delete(AlgumDBContract.GruposEntry.CONTENT_URI, null, null);
                                            getActivity().getContentResolver().delete(AlgumDBContract.UsuariosEntry.CONTENT_URI, null, null);
                                            getActivity().getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE).edit().clear().commit();
                                            startActivity(new Intent(mContext, MainActivity.class));
                                        }
                                    });
                            Controle.gravaLog(mContext, "Remoção de dados do usuario", 0);

                        }
                    })
                            .setNegativeButton(R.string.cancelar, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).show();

                    return true;
                }
            });

            Preference myPrefLog = findPreference(getString(R.string.log_remove));
            myPrefLog.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                public boolean onPreferenceClick(Preference pref) {
                     getActivity().getContentResolver().delete(AlgumDBContract.LogEntry.CONTENT_URI, null, null);
                     Controle.showMessage(getActivity(), "Log excluido.");
                     return true;
                }
            });
        }

        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences()
                    .registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences()
                    .unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if (s.equals(getString(R.string.sync_freq))) {
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
                Account newAccount = new Account(
                        sharedPref.getString(mContext.getString(R.string.emailUsuario), "")
                        , getActivity().getString(R.string.sync_account_type));
                // Get an instance of the Android account manager
                AccountManager accountManager =
                        (AccountManager) getActivity().getSystemService(
                                Context.ACCOUNT_SERVICE);

                ContentResolver.addPeriodicSync(newAccount, AUTHORITY, new Bundle(),
                        Integer.parseInt(sharedPreferences.getString(s,mContext.getString(R.string.sync_freq_default))) * 60);
                Controle.gravaLog(this.getActivity(),"Alteração na frequencia de sincronização para "+sharedPreferences.getString(s,"")+ " minutos",0);
            }
            if (s.equals(getString(R.string.sync_ativ))){
                SharedPreferences sharedPref = getActivity().getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
                Account newAccount = new Account(
                        sharedPref.getString(mContext.getString(R.string.emailUsuario), "")
                        , getActivity().getString(R.string.sync_account_type));
                if(sharedPreferences.getBoolean(s,false)){
                    ContentResolver.addPeriodicSync(newAccount, AUTHORITY, new Bundle(),
                            Integer.parseInt(sharedPreferences.getString(mContext.getString(R.string.sync_freq),mContext.getString(R.string.sync_freq_default))) * 60);
                }else{
                    ContentResolver.removePeriodicSync(newAccount, AUTHORITY, new Bundle());
                }
                Controle.gravaLog(this.getActivity(),"Ativação de sincronização para "+Boolean.toString(sharedPreferences.getBoolean(s, false)),0);
            }

        }

        @Override
        public void onStart() {
            super.onStart();
            mGoogleApiClient.connect();
        }

        @Override
        public void onStop() {
            super.onStop();
            mGoogleApiClient.disconnect();
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            startActivity(new Intent(mContext, MainActivity.class));
        }
    }
}
