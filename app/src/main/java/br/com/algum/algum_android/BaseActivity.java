package br.com.algum.algum_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

import br.com.algum.algum_android.sync.AlgumSyncTask;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "BaseActivity";

    protected String email;
    protected int id_usuario;
    protected GoogleApiClient mGoogleApiClient;

    // The authority for the sync adapter's content provider
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    // The account name
    public static String account;

    protected View v;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.algum_server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);

        if (sharedPref.contains(getString(R.string.emailUsuario))){
            email = sharedPref.getString(getString(R.string.emailUsuario), "");
            id_usuario = sharedPref.getInt(getString(R.string.idUsuario), 0);

            TextView txtEmail = (TextView) v.findViewById(R.id.txtEmail);
            txtEmail.setText(email);

        }else{
            Intent intent = new Intent();
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    protected void onCreateDrawer() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        v = navigationView.getHeaderView(0);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);

        //if(sharedPref.getString(getString(R.string.emailUsuario), "").equals("andrelrs80@gmail.com")){
            MenuItem menu = navigationView.getMenu().findItem(R.id.nav_log);
            menu.setVisible(true);
        //};


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.base, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_refresh){

            AlgumSyncTask task = new AlgumSyncTask(this);
            task.execute("");

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Intent intent = new Intent();

        if (id == R.id.nav_lancamento) {
            intent = new Intent(this, LancamentoContasActivity.class);
        }else if (id == R.id.nav_extrato){
            intent = new Intent(this, ExtratoActivity.class);
        }else if (id == R.id.nav_contas){
            intent = new Intent(this, ContasActivity.class);
        }else if (id == R.id.nav_log){
            intent = new Intent(this, ViewLogActivity.class);
        }else if (id == R.id.nav_exit) {
            account = email;

            /*
            Account newAccount = new Account(
                    account, getString(R.string.sync_account_type));

            Bundle bundle = new Bundle();
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
            bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);

            ContentResolver.removePeriodicSync(newAccount, AUTHORITY, bundle);
*/
            /*
            getContentResolver().delete(AlgumDBContract.LancamentoEntry.CONTENT_URI, null,null);
            getContentResolver().delete(AlgumDBContract.ContasEntry.CONTENT_URI, null, null);
            getContentResolver().delete(AlgumDBContract.GruposEntry.CONTENT_URI, null,null);
            getContentResolver().delete(AlgumDBContract.UsuariosEntry.CONTENT_URI, null,null);
            */
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(getString(R.string.emailUsuario));
            editor.remove(getString(R.string.emailUsuario));
            editor.commit();
            Auth.GoogleSignInApi.signOut(mGoogleApiClient);

            /*
            googleApiClient.connect();
            if(googleApiClient.isConnected()) {
                Log.d(TAG,"googleApiClient connected");
                googleApiClient.clearDefaultAccountAndReconnect();
                //Auth.GoogleSignInApi.signOut(googleApiClient);
            }
            */

            intent = new Intent(this, MainActivity.class);
//        }else if (id == R.id.nav_grupos) {
//            intent = new Intent(this, Grupos.class);
//        }else if (id == R.id.nav_planejamento) {
//            intent = new Intent(this, PlanejamentoActivity.class);
//        }else if (id == R.id.nav_extrato) {
//            intent = new Intent(this, ExtratoActivity.class);
//        }else if (id == R.id.nav_situacao) {
//            intent = new Intent(this, SituacaoActivity.class);
        }

        startActivity(intent);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

}
