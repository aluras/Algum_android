package br.com.algum.algum_android.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by sn1007071 on 28/03/2016.
 */
public class AlgumSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static AlgumSyncAdapter sAlgumSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("AlgumSyncService", "onCreate - AlgumSyncService");
        synchronized (sSyncAdapterLock) {
            if (sAlgumSyncAdapter == null) {
                sAlgumSyncAdapter = new AlgumSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sAlgumSyncAdapter.getSyncAdapterBinder();
    }
}
