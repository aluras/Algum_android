package br.com.algum.algum_android.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.algum.algum_android.R;
import br.com.algum.algum_android.data.AlgumDBContract;

/**
 * Created by sn1007071 on 28/03/2016.
 */
public class AlgumSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = AlgumSyncAdapter.class.getSimpleName();

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    public AlgumSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");

        SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.userInfo), Context.MODE_PRIVATE);
        String tok = sharedPref.getString(getContext().getString(R.string.tokenUsuario), "");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        // Will contain the raw JSON response as a string.
        String ContasJsonStr = null;

        String format = "json";

        try {
            final String CONTA_BASE_URL = getContext().getString(R.string.WSurl) + "contas";

            Uri builtUri = Uri.parse(CONTA_BASE_URL).buildUpon().build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Application-Authorization", tok);
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            ContasJsonStr = buffer.toString();

            JSONArray contasArray = new JSONArray(ContasJsonStr);

            for(int i = 0; i < contasArray.length(); i++){

                JSONObject contaJson = contasArray.getJSONObject(i);
                JSONObject conta = contaJson.getJSONObject("Conta");
                JSONArray contaUsuarioArray = contaJson.getJSONArray("ContaUsuario");
                JSONObject contaUsuario = contaUsuarioArray.getJSONObject(0);

                String mSelectionClause = AlgumDBContract.ContasEntry.COLUMN_CONTA_ID + " = ? ";
                String[] mSelectionArgs = {conta.getString("id")};
                Cursor cursor = getContext().getContentResolver().query(AlgumDBContract.ContasEntry.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);

                if(cursor.getCount() < 1){

                    ContentValues contasValues = new ContentValues();
                    contasValues.put(AlgumDBContract.ContasEntry.COLUMN_ID, contaUsuario.getInt("id"));
                    contasValues.put(AlgumDBContract.ContasEntry.COLUMN_NOME, conta.getString("nome"));
                    contasValues.put(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID, conta.getInt("id"));
                    contasValues.put(AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID, contaUsuario.getInt("usuario_id"));

                    getContext().getContentResolver().insert(AlgumDBContract.ContasEntry.CONTENT_URI, contasValues);
                }
            }

            getContext().getContentResolver().notifyChange(builtUri, null, false);




        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return;
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }



    }

    public static Account CreateSyncAccount(Context context) {
        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(
                        context.ACCOUNT_SERVICE);
        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            //onAccountCreated(newAccount, context);
        }
        return newAccount;
    }
}
