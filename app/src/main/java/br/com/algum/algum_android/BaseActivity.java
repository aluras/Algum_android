package br.com.algum.algum_android;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "BaseActivity";

    protected String id_token;
    protected String email;
    protected int id_usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE);

        if (sharedPref.contains(getString(R.string.tokenUsuario))){
            id_token = sharedPref.getString(getString(R.string.tokenUsuario),"");
            email = sharedPref.getString(getString(R.string.emailUsuario),"");
            id_usuario = sharedPref.getInt(getString(R.string.idUsuario), 0);


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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
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
        }else if (id == R.id.nav_exit) {
            getSharedPreferences(getString(R.string.userInfo), Context.MODE_PRIVATE).edit().clear().commit();
/*
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.algum_server_client_id))
                    .requestEmail()
                    .build();

            GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();

            Auth.GoogleSignInApi.signOut(googleApiClient);
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
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
}
