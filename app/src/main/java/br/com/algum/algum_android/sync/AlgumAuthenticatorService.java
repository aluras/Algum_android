package br.com.algum.algum_android.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by sn1007071 on 20/04/2016.
 */
public class AlgumAuthenticatorService extends Service {
    private AlgumAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new AlgumAuthenticator(this);
    }

    /*
     * When the system binds to this Service to make the RPC call
     * return the authenticator's IBinder.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
