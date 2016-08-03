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
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.algum.algum_android.R;
import br.com.algum.algum_android.data.AlgumDBContract;

/**
 * Created by sn1007071 on 28/03/2016.
 */
public class AlgumSyncAdapter extends AbstractThreadedSyncAdapter {

    public final String LOG_TAG = AlgumSyncAdapter.class.getSimpleName();
    private String tok;

    public AlgumSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);

        SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.userInfo), Context.MODE_PRIVATE);
        tok = sharedPref.getString(getContext().getString(R.string.tokenUsuario), "");
    }


    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");



        SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.userInfo), Context.MODE_PRIVATE);
        int usuarioId = sharedPref.getInt(getContext().getString(R.string.idUsuario),0);


        // Will contain the raw JSON response as a string.
        String ContasJsonStr = null;
        String GruposJsonStr = null;
        String LancamentosJsonStr = null;

        String format = "json";

        try {
            Log.d(LOG_TAG, "Starting sync Contas");
            //Atualiza Contas
            final String CONTA_BASE_URL = getContext().getString(R.string.WSurl) + "contas";
            ContasJsonStr = callService(CONTA_BASE_URL);

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
                    contasValues.put(AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID, conta.getInt("tipo_conta_id"));
                    contasValues.put(AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID, contaUsuario.getInt("usuario_id"));

                    getContext().getContentResolver().insert(AlgumDBContract.ContasEntry.CONTENT_URI, contasValues);
                }
                cursor.close();
            }


            Log.d(LOG_TAG, "Starting sync Grupos");
            //Atualiza Grupos
            final String GRUPO_BASE_URL = getContext().getString(R.string.WSurl) + "grupos";
            GruposJsonStr = callService(GRUPO_BASE_URL);

            JSONArray gruposArray = new JSONArray(GruposJsonStr);

            for(int i = 0; i < gruposArray.length(); i++){

                JSONObject grupoJson = gruposArray.getJSONObject(i);
                JSONObject grupo = grupoJson.getJSONObject("Grupo");

                String mSelectionClause = AlgumDBContract.GruposEntry.COLUMN_GRUPO_ID + " = ? ";
                String[] mSelectionArgs = {grupo.getString("id")};
                Cursor cursor = getContext().getContentResolver().query(AlgumDBContract.GruposEntry.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);

                if(cursor.getCount() < 1){

                    ContentValues gruposValues = new ContentValues();
                    gruposValues.put(AlgumDBContract.GruposEntry.COLUMN_ID, grupo.getInt("id"));
                    gruposValues.put(AlgumDBContract.GruposEntry.COLUMN_NOME, grupo.getString("nome"));
                    gruposValues.put(AlgumDBContract.GruposEntry.COLUMN_GRUPO_ID, grupo.getInt("id"));
                    gruposValues.put(AlgumDBContract.GruposEntry.COLUMN_TIPO_ID, grupo.getInt("id_tipo_grupo"));

                    getContext().getContentResolver().insert(AlgumDBContract.GruposEntry.CONTENT_URI, gruposValues);
                }
                cursor.close();
            }

            Log.d(LOG_TAG, "Starting sync Lancamentos");
            //Atualiza Lançamentos
            final String LANCAMENTO_BASE_URL = getContext().getString(R.string.WSurl) + "lancamentos";

            // -- ENVIA OS NOVOS
            String projection[] = {AlgumDBContract.LancamentoEntry.TABLE_NAME+".*"};
            String selection = AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID + " IS NULL ";
            String sortOrder = AlgumDBContract.LancamentoEntry.COLUMN_DATA;
            Cursor lancamentos = getContext().getContentResolver().query(AlgumDBContract.LancamentoEntry.buildLancamentoUsuarioUri(usuarioId),projection,selection,null,sortOrder);

            lancamentos.moveToFirst();
            while(lancamentos.isAfterLast() == false){
                SimpleDateFormat dateFformat = new SimpleDateFormat("dd/MM/yyyy");
                String params = "date="+dateFformat.format(new Date(lancamentos.getLong(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_DATA))) );
                params = params + "&valor="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_VALOR));
                params = params + "&observacao="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO));
                params = params + "&grupo_id="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID));
                params = params + "&conta_id="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ORIGEM_ID));

                LancamentosJsonStr = callServiceGravaLancamento(LANCAMENTO_BASE_URL,params);

                JSONObject lancamento = new JSONObject(LancamentosJsonStr).getJSONObject("Lancamento");

                String mSelectionClause = AlgumDBContract.LancamentoEntry.COLUMN_ID + " = ? ";
                String[] mSelectionArgs = {lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_ID))};

                ContentValues values = new ContentValues();
                values.put(AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID, lancamento.getInt("id"));

                getContext().getContentResolver().update(AlgumDBContract.LancamentoEntry.CONTENT_URI,values,mSelectionClause,mSelectionArgs);

                lancamentos.moveToNext();
            }
            lancamentos.close();
            //-- RECEBE OS QUE NÃO ESTÃO LOCAL
            LancamentosJsonStr = callService(LANCAMENTO_BASE_URL);
            JSONArray lancamentoArray = new JSONArray(LancamentosJsonStr);

            for(int i = 0; i < lancamentoArray.length(); i++){

                JSONObject lancamentoJson = lancamentoArray.getJSONObject(i);
                JSONObject lancamento = lancamentoJson.getJSONObject("Lancamento");

                String mSelectionClause = AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID + " = ? ";
                String[] mSelectionArgs = {lancamento.getString("id")};
                Cursor cursor = getContext().getContentResolver().query(AlgumDBContract.LancamentoEntry.buildLancamentoUsuarioUri(usuarioId), null, mSelectionClause, mSelectionArgs, null);

                if(cursor.getCount() < 1){
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Long date = dateFormat.parse(lancamento.getString("data")).getTime();

                    ContentValues lancamentoValues = new ContentValues();
                    lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID, lancamento.getInt("id"));
                    lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ORIGEM_ID, lancamento.getInt("conta_id"));
                    lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID, lancamento.getInt("grupo_id"));
                    lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_DATA, date);
                    lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO, lancamento.getString("observacao"));
                    lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_VALOR, lancamento.getString("valor"));

                    getContext().getContentResolver().insert(AlgumDBContract.LancamentoEntry.CONTENT_URI, lancamentoValues);
                }
                cursor.close();
            }

            Log.d(LOG_TAG, "Finishing sync");
            //syncResult.delayUntil = 12 * 60 * 60;

        }catch (JSONException e) {
            Log.e(LOG_TAG, "Error ", e);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error ", e);
        }

    }

    private String callService(String pUrl){

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{
            Uri builtUri = Uri.parse(pUrl).buildUpon().build();

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
                return "";
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
                return "";
            }
            return buffer.toString();
        }catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return "";
        }finally {
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

    private String callServiceGravaLancamento(String pUrl, String params){

        SharedPreferences sharedPref = getContext().getSharedPreferences(getContext().getString(R.string.userInfo), Context.MODE_PRIVATE);
        String tok = sharedPref.getString(getContext().getString(R.string.tokenUsuario), "");

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{
            Uri builtUri = Uri.parse(pUrl).buildUpon().build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Application-Authorization", tok);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(params);
            writer.flush();
            writer.close();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return "";
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
                return "";
            }
            return buffer.toString();
        }catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            return "";
        }finally {
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
