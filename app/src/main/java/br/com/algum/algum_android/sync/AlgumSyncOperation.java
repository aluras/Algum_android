package br.com.algum.algum_android.sync;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

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
import br.com.algum.algum_android.utils.Controle;

/**
 * Created by sn1007071 on 29/08/2016.
 */
public class AlgumSyncOperation {

    public final String LOG_TAG = AlgumSyncAdapter.class.getSimpleName();

    private Context mContext;

    private String tok;

    private int usuarioId;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


    public AlgumSyncOperation(Context mContext,GoogleSignInResult googleSignInResult) {
        this.mContext = mContext;

        SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.userInfo), Context.MODE_PRIVATE);
        usuarioId = sharedPref.getInt(mContext.getString(R.string.idUsuario), 0);

        if (googleSignInResult.isSuccess()) {
            GoogleSignInAccount acct = googleSignInResult.getSignInAccount();
            tok = acct.getIdToken();
        } else {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.remove(mContext.getString(R.string.emailUsuario));
            editor.commit();
            return;
        }
    }

    public void syncTipoConta() throws Exception{
        String TipoContasJsonStr = null;

        try {

            Log.d(LOG_TAG, "Starting sync Tipo Contas");
            //Controle.gravaLog(mContext, "SyncOperations - Atualizar tipo conta", usuarioId);

            //Atualiza Contas
            final String TIPO_CONTA_BASE_URL = mContext.getString(R.string.WSurl) + "tipo_contas";
            TipoContasJsonStr = callService(TIPO_CONTA_BASE_URL);

            JSONArray tipoContasArray = new JSONArray(TipoContasJsonStr);

            for(int i = 0; i < tipoContasArray.length(); i++){

                JSONObject tipoContaJson = tipoContasArray.getJSONObject(i);
                JSONObject tipoConta = tipoContaJson.getJSONObject("TipoConta");

                String mSelectionClause = AlgumDBContract.TipoContaEntry.COLUMN_ID + " = ? ";
                String[] mSelectionArgs = {tipoConta.getString("id")};
                Cursor cursor = mContext.getContentResolver().query(AlgumDBContract.TipoContaEntry.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);

                ContentValues tipoContasValues = new ContentValues();
                tipoContasValues.put(AlgumDBContract.TipoContaEntry.COLUMN_ID, tipoConta.getInt("id"));
                tipoContasValues.put(AlgumDBContract.TipoContaEntry.COLUMN_DESCRICAO, tipoConta.getString("descricao"));

                if(cursor.getCount() < 1){
                    mContext.getContentResolver().insert(AlgumDBContract.TipoContaEntry.CONTENT_URI, tipoContasValues);
                }else{
                    mContext.getContentResolver().update(AlgumDBContract.TipoContaEntry.CONTENT_URI, tipoContasValues, mSelectionClause,mSelectionArgs);
                }
                cursor.close();
            }
        }catch (JSONException e) {
            //Controle.gravaLog(mContext,dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(),usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
        }

    }

    public void syncGrupos()throws Exception{
        String GruposJsonStr = null;

        try{
            Log.d(LOG_TAG, "Starting sync Grupos");
            //Controle.gravaLog(mContext, "SyncOperations - Atualizar grupos", usuarioId);

            //Atualiza Grupos
            final String GRUPO_BASE_URL = mContext.getString(R.string.WSurl) + "grupos";
            GruposJsonStr = callService(GRUPO_BASE_URL);

            JSONArray gruposArray = new JSONArray(GruposJsonStr);

            for(int i = 0; i < gruposArray.length(); i++){

                JSONObject grupoJson = gruposArray.getJSONObject(i);
                JSONObject grupo = grupoJson.getJSONObject("Grupo");

                String mSelectionClause = AlgumDBContract.GruposEntry.COLUMN_GRUPO_ID + " = ? ";
                String[] mSelectionArgs = {grupo.getString("id")};
                Cursor cursor = mContext.getContentResolver().query(AlgumDBContract.GruposEntry.CONTENT_URI, null, mSelectionClause, mSelectionArgs, null);

                ContentValues gruposValues = new ContentValues();
                gruposValues.put(AlgumDBContract.GruposEntry.COLUMN_ID, grupo.getInt("id"));
                gruposValues.put(AlgumDBContract.GruposEntry.COLUMN_NOME, grupo.getString("nome"));
                gruposValues.put(AlgumDBContract.GruposEntry.COLUMN_GRUPO_ID, grupo.getInt("id"));
                gruposValues.put(AlgumDBContract.GruposEntry.COLUMN_TIPO_ID, grupo.getInt("id_tipo_grupo"));

                if(cursor.getCount() < 1){
                    mContext.getContentResolver().insert(AlgumDBContract.GruposEntry.CONTENT_URI, gruposValues);
                }else{
                    mContext.getContentResolver().update(AlgumDBContract.GruposEntry.CONTENT_URI, gruposValues, mSelectionClause, mSelectionArgs);
                }
                cursor.close();
            }
        }catch (JSONException e) {
            //Controle.gravaLog(mContext,dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(),usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
        }
    }

    public void addLancamentos()throws Exception{

        String LancamentosJsonStr = null;
        //Controle.gravaLog(mContext, "SyncOperations - Insere Lancamentos", usuarioId);

        final String LANCAMENTO_BASE_URL = mContext.getString(R.string.WSurl) + "lancamentos";

        try{
            // -- ENVIA OS NOVOS
            String projection[] = {AlgumDBContract.LancamentoEntry.TABLE_NAME+".*"};
            String selection = AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID + " IS NULL ";
            String sortOrder = AlgumDBContract.LancamentoEntry.COLUMN_DATA;
            Cursor lancamentos = mContext.getContentResolver().query(AlgumDBContract.LancamentoEntry.buildLancamentoUsuarioUri(usuarioId),projection,selection,null,sortOrder);

            lancamentos.moveToFirst();
            while(lancamentos.isAfterLast() == false){
                SimpleDateFormat dateFformat = new SimpleDateFormat("dd/MM/yyyy");
                String params = "date="+dateFformat.format(new Date(lancamentos.getLong(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_DATA))) );
                params = params + "&valor="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_VALOR));
                params = params + "&observacao="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO));
                params = params + "&grupo_id="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID));
                params = params + "&conta_id="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID));
                params = params + "&usuario_id="+lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_USUARIO_ID));

                LancamentosJsonStr = callServiceGrava(LANCAMENTO_BASE_URL,params);

                JSONObject lancamento = new JSONObject(LancamentosJsonStr).getJSONObject("Lancamento");

                String mSelectionClause = AlgumDBContract.LancamentoEntry.COLUMN_ID + " = ? ";
                String[] mSelectionArgs = {lancamentos.getString(lancamentos.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_ID))};

                ContentValues values = new ContentValues();
                values.put(AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID, lancamento.getInt("id"));

                mContext.getContentResolver().update(AlgumDBContract.LancamentoEntry.CONTENT_URI,values,mSelectionClause,mSelectionArgs);

                lancamentos.moveToNext();
            }
            lancamentos.close();

        }catch (JSONException e) {
            //Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(), usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
        }
    }

    public void deleteLancamentos()throws Exception{

        String LancamentosJsonStr = null;
        //Controle.gravaLog(mContext, "SyncOperations - Exclui Lancamentos", usuarioId);

        final String LANCAMENTO_BASE_URL = mContext.getString(R.string.WSurl) + "lancamentos";


        // EXCLUI LANCAMENTOS
        String[] projectionDelete = {AlgumDBContract.LancamentoEntry.TABLE_NAME+".*"};
        String selectionDelete = AlgumDBContract.LancamentoEntry.COLUMN_EXCLUIDO + " = 1 ";
        Cursor lancamentosDelete = mContext.getContentResolver().query(AlgumDBContract.LancamentoEntry.CONTENT_URI,projectionDelete,selectionDelete,null,null);

        lancamentosDelete.moveToFirst();
        while(lancamentosDelete.isAfterLast() == false){

            if(lancamentosDelete.getInt(lancamentosDelete.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID))>0){
                callServiceExclui(LANCAMENTO_BASE_URL+"/"+lancamentosDelete.getString(lancamentosDelete.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID)));
            }

            String selection = AlgumDBContract.LancamentoEntry.COLUMN_ID + " = ? ";
            String[] selectionArgs = {lancamentosDelete.getString(lancamentosDelete.getColumnIndex(AlgumDBContract.LancamentoEntry.COLUMN_ID))};
            mContext.getContentResolver().delete(AlgumDBContract.LancamentoEntry.CONTENT_URI, selection, selectionArgs);

            lancamentosDelete.moveToNext();
        }
        lancamentosDelete.close();

    }

    public void syncLancamentos()throws Exception{

        String LancamentosJsonStr = null;

        try{
            Log.d(LOG_TAG, "Starting sync Lancamentos");
            //Controle.gravaLog(mContext, "SyncOperations - Atualizar Lancamentos", usuarioId);

            //Atualiza Lançamentos
            final String LANCAMENTO_BASE_URL = mContext.getString(R.string.WSurl) + "lancamentos";

            //-- RECEBE OS QUE NÃO ESTÃO LOCAL
            LancamentosJsonStr = callService(LANCAMENTO_BASE_URL);
            JSONArray lancamentoArray = new JSONArray(LancamentosJsonStr);

            for(int i = 0; i < lancamentoArray.length(); i++){

                JSONObject lancamentoJson = lancamentoArray.getJSONObject(i);
                JSONObject lancamento = lancamentoJson.getJSONObject("Lancamento");

                String mSelectionClause = AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID + " = ? ";
                String[] mSelectionArgs = {lancamento.getString("id")};
                Cursor cursor = mContext.getContentResolver().query(AlgumDBContract.LancamentoEntry.buildLancamentoUsuarioUri(usuarioId), null, mSelectionClause, mSelectionArgs, null);

                if(cursor.getCount() < 1){
                    if(lancamento.getInt("excluido")==0){
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        Long date = dateFormat.parse(lancamento.getString("data")).getTime();

                        ContentValues lancamentoValues = new ContentValues();
                        lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID, lancamento.getInt("id"));
                        lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID, lancamento.getInt("conta_id"));
                        lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID, lancamento.getInt("grupo_id"));
                        lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_DATA, date);
                        lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO, lancamento.getString("observacao"));
                        lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_VALOR, lancamento.getString("valor"));
                        lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_USUARIO_ID, lancamento.getString("usuario_id"));
                        lancamentoValues.put(AlgumDBContract.LancamentoEntry.COLUMN_EXCLUIDO, 0);

                        mContext.getContentResolver().insert(AlgumDBContract.LancamentoEntry.CONTENT_URI, lancamentoValues);
                    }
                }else{
                    if(lancamento.getInt("excluido")==1){
                        String selection = AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID + " = ? ";
                        String[] selectionArgs = {lancamento.getString("id")};
                        mContext.getContentResolver().delete(AlgumDBContract.LancamentoEntry.CONTENT_URI, selection, selectionArgs);
                    }
                }
                cursor.close();
            }
        }catch (JSONException e) {
            Controle.gravaLog(mContext,dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(),usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
        }catch (ParseException e) {
            //Controle.gravaLog(mContext,dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(),usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
        }
    }

    public void addContas()throws Exception{
        String ContasJsonStr = null;
        //Controle.gravaLog(mContext, "SyncOperations - Insere Contas", usuarioId);

        final String CONTA_BASE_URL = mContext.getString(R.string.WSurl) + "contas";
        try {
            // -- ENVIA OS NOVOS
            String[] projectionContasAlteradas = {AlgumDBContract.ContasEntry.TABLE_NAME+".*"};
            String selectionContasAlteradas = AlgumDBContract.ContasEntry.COLUMN_ALTERADO + " = 1 ";
            Cursor contas = mContext.getContentResolver().query(AlgumDBContract.ContasEntry.buildContaUsuarioUri(usuarioId),projectionContasAlteradas,selectionContasAlteradas,null,null);

            contas.moveToFirst();
            while(contas.isAfterLast() == false){
                String params = "nome="+contas.getString(contas.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_NOME));
                params = params + "&saldo="+contas.getString(contas.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_SALDO));
                params = params + "&tipo_conta_id="+contas.getString(contas.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID));
                params = params + "&usuario_id="+contas.getString(contas.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID));

                if(contas.getInt(contas.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID))> 0){
                    params = params + "&id="+contas.getString(contas.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID));
                    ContasJsonStr = callServiceGrava(CONTA_BASE_URL + "/" + contas.getString(contas.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID)), params);
                }else{
                    ContasJsonStr = callServiceGrava(CONTA_BASE_URL, params);
                }


                JSONObject conta = new JSONObject(ContasJsonStr).getJSONObject("Conta");

                String mSelectionClause = AlgumDBContract.ContasEntry.COLUMN_ID + " = ? ";
                String[] mSelectionArgs = {contas.getString(contas.getColumnIndex(AlgumDBContract.ContasEntry.COLUMN_ID))};

                ContentValues values = new ContentValues();
                values.put(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID, conta.getInt("id"));
                values.put(AlgumDBContract.ContasEntry.COLUMN_ALTERADO, 0);

                mContext.getContentResolver().update(AlgumDBContract.ContasEntry.CONTENT_URI,values,mSelectionClause,mSelectionArgs);

                contas.moveToNext();
            }
            contas.close();

        }catch (JSONException e) {
            //Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(), usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
        }
    }

    public void syncContas()throws Exception{
        String ContasJsonStr = null;

        try{
            Log.d(LOG_TAG, "Starting sync Contas");
            //Controle.gravaLog(mContext, "SyncOperations - Atualizar Contas", usuarioId);

            //Atualiza Contas
            final String CONTA_BASE_URL = mContext.getString(R.string.WSurl) + "contas";
            //RECEBE AS DO SERVIDOR
            ContasJsonStr = callService(CONTA_BASE_URL);

            JSONArray contasArray = new JSONArray(ContasJsonStr);

            for(int i = 0; i < contasArray.length(); i++){

                JSONObject contaJson = contasArray.getJSONObject(i);
                JSONObject conta = contaJson.getJSONObject("Conta");
                JSONObject contaUsuario = contaJson.getJSONObject("ContaUsuario");

                String mSelectionClause = AlgumDBContract.ContasEntry.COLUMN_CONTA_ID + " = ? ";
                String[] mSelectionArgs = {conta.getString("id")};
                Cursor cursor = mContext.getContentResolver().query(AlgumDBContract.ContasEntry.buildContaUsuarioUri(usuarioId), null, mSelectionClause, mSelectionArgs, null);

                ContentValues contasValues = new ContentValues();
                contasValues.put(AlgumDBContract.ContasEntry.COLUMN_NOME, conta.getString("nome"));
                contasValues.put(AlgumDBContract.ContasEntry.COLUMN_CONTA_ID, conta.getInt("id"));
                contasValues.put(AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID, conta.getInt("tipo_conta_id"));
                contasValues.put(AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID, contaUsuario.getInt("usuario_id"));
                contasValues.put(AlgumDBContract.ContasEntry.COLUMN_SALDO_INICIAL, conta.getString("saldo_inicial"));
                contasValues.put(AlgumDBContract.ContasEntry.COLUMN_SALDO, conta.getString("saldo"));

                if(cursor.getCount() < 1){

                    mContext.getContentResolver().insert(AlgumDBContract.ContasEntry.CONTENT_URI, contasValues);
                }else{
                    mContext.getContentResolver().update(AlgumDBContract.ContasEntry.CONTENT_URI, contasValues, mSelectionClause,mSelectionArgs);
                }
                cursor.close();
            }

        }catch (JSONException e) {
            //Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(), usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
        }
    }

    public void atualizaUsuario()throws Exception{
        Log.d(LOG_TAG, "Starting update User");
        //Controle.gravaLog(mContext, "SyncOperations - Atualizar usuario (Data Sync)", usuarioId);

        final String CONTA_BASE_URL = mContext.getString(R.string.WSurl) + "usuarios";

        String projection[] = {AlgumDBContract.UsuariosEntry.TABLE_NAME+".*"};
        String selection = AlgumDBContract.UsuariosEntry.COLUMN_ID + " = " + usuarioId;
        Cursor usuarios = mContext.getContentResolver().query(AlgumDBContract.UsuariosEntry.CONTENT_URI,projection,selection,null,null);
        usuarios.moveToFirst();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        String params = "id="+usuarioId;
        params = params + "&sincronizado="+format.format(new Date(usuarios.getLong(usuarios.getColumnIndex(AlgumDBContract.UsuariosEntry.COLUMN_DATA_SYNC))));

        callServiceGrava(CONTA_BASE_URL + "/" + usuarioId, params);

    }

    public void atualizaDataSync(){
        //Controle.gravaLog(mContext, "SyncOperations - Atualizar usuario local (Data Sync)", usuarioId);

        Date data = new Date();

        ContentValues usuarioValues = new ContentValues();
        usuarioValues.put(AlgumDBContract.UsuariosEntry.COLUMN_ID, usuarioId);
        usuarioValues.put(AlgumDBContract.UsuariosEntry.COLUMN_DATA_SYNC, data.getTime());

        String mSelectionClause = AlgumDBContract.UsuariosEntry.COLUMN_ID + " = ? ";
        String[] mSelectionArgs = {Integer.toString(usuarioId)};

        mContext.getContentResolver().update(AlgumDBContract.UsuariosEntry.CONTENT_URI, usuarioValues, mSelectionClause, mSelectionArgs);

    }

    public boolean performSync() {

        try {
            Log.d(LOG_TAG, "Starting sync");

            atualizaUsuario();
            syncTipoConta();
            syncGrupos();
            syncLancamentos();
            syncContas();
            atualizaDataSync();

            Log.d(LOG_TAG, "Finishing sync");

            SharedPreferences sharedPref = mContext.getSharedPreferences(mContext.getString(R.string.userInfo), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(mContext.getString(R.string.dataSync), dateFormat.format(new Date()));
            editor.commit();

            //Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " Sincronização concluída.", usuarioId);

        }catch (Exception e){
            //Controle.gravaLog(mContext,e.getMessage(),usuarioId);
            Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(), usuarioId);
            return false;
        }

        return true;
    }

    public void performEdit(){
        try{
            addContas();
            deleteLancamentos();
            addLancamentos();

        }catch (Exception e){
            Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(), usuarioId);
        }

    }

    public boolean performSendEdit(){
        try{
            performEdit();

            Controle.syncData(mContext);

        }catch (Exception e){
            Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(), usuarioId);
            return false;
        }

        return true;
    }

    private String callService(String pUrl)throws Exception{

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
            //Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(), usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            throw new Exception(e);
        }
        finally {
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

    private String callServiceGrava(String pUrl, String params)throws Exception{

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
            //Controle.gravaLog(mContext,dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(),usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
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

    private void callServiceExclui(String pUrl) throws Exception{

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try{
            Uri builtUri = Uri.parse(pUrl).buildUpon().build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            urlConnection.setRequestMethod("DELETE");
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("Application-Authorization", tok);
            urlConnection.setUseCaches(false);

            urlConnection.getResponseCode();


        }catch (IOException e) {
            //Controle.gravaLog(mContext, dateTimeFormat.format(new Date()) + " - " + e.toString() + e.getMessage(), usuarioId);
            Log.e(LOG_TAG, "Error ", e);
            throw new Exception(e);
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
}