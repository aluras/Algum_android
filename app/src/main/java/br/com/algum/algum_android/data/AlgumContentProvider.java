package br.com.algum.algum_android.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AlgumContentProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AlgumDbHelper mDbHelper;

    private static final int CONTAS = 100;
    private static final int CONTAS_POR_USUARIO = 101;
    private static final int TIPOS_CONTAS = 102;
    private static final int SALDO_CONTA = 103;

    private static final int USUARIOS = 200;
    private static final int USUARIOS_POR_ID = 201;

    private static final int GRUPOS = 300;
    private static final int GRUPOS_POR_USUARIO = 301;
    private static final int TIPOS_GRUPOS = 302;
    private static final int USUARIOS_POR_GRUPO = 303;

    private static final int LANCAMENTOS = 400;
    private static final int LANCAMENTOS_POR_USUARIO = 401;

    private static final int LOG = 900;

    private static UriMatcher buildUriMatcher() {
        // I know what you're thinking.  Why create a UriMatcher when you can use regular
        // expressions instead?  Because you're not crazy, that's why.

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = AlgumDBContract.CONTENT_AUTHORITY;

        // For each type of URI you want to add, create a corresponding code.
        matcher.addURI(authority, AlgumDBContract.PATH_CONTAS, CONTAS);
        matcher.addURI(authority, AlgumDBContract.PATH_CONTAS + "/*", CONTAS_POR_USUARIO);
        matcher.addURI(authority, AlgumDBContract.PATH_TIPO_CONTA, TIPOS_CONTAS);
        matcher.addURI(authority, AlgumDBContract.PATH_SALDO_CONTA, SALDO_CONTA);

        matcher.addURI(authority, AlgumDBContract.PATH_USUARIOS, USUARIOS);
        matcher.addURI(authority, AlgumDBContract.PATH_USUARIOS + "/*", USUARIOS_POR_ID);

        matcher.addURI(authority, AlgumDBContract.PATH_GRUPOS, GRUPOS);
        matcher.addURI(authority, AlgumDBContract.PATH_GRUPOS + "/*", GRUPOS_POR_USUARIO);
        matcher.addURI(authority, AlgumDBContract.PATH_TIPO_GRUPO, TIPOS_GRUPOS);
        matcher.addURI(authority, AlgumDBContract.PATH_GRUPO_USUARIOS, USUARIOS_POR_GRUPO);

        matcher.addURI(authority, AlgumDBContract.PATH_LANCAMENTOS, LANCAMENTOS);
        matcher.addURI(authority, AlgumDBContract.PATH_LANCAMENTOS + "/*", LANCAMENTOS_POR_USUARIO);

        matcher.addURI(authority, AlgumDBContract.PATH_LOG, LOG);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new AlgumDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CONTAS_POR_USUARIO: {
                SQLiteQueryBuilder _QB = new SQLiteQueryBuilder();

                if (selection != null && !selection.trim().isEmpty()){
                    selection = selection + " AND ";
                }
                selection = selection + AlgumDBContract.ContasEntry.TABLE_NAME + "." + AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID + " = " + uri.getLastPathSegment();

                _QB.setTables(AlgumDBContract.ContasEntry.TABLE_NAME +
                        " INNER JOIN " + AlgumDBContract.TipoContaEntry.TABLE_NAME + " ON " +
                        AlgumDBContract.TipoContaEntry.TABLE_NAME + "." + AlgumDBContract.TipoContaEntry.COLUMN_ID + " = " +
                        AlgumDBContract.ContasEntry.TABLE_NAME + "." + AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID);

                String _OrderBy = AlgumDBContract.ContasEntry.TABLE_NAME + "." + AlgumDBContract.ContasEntry.COLUMN_NOME + " ASC ";

                retCursor = _QB.query(mDbHelper.getReadableDatabase(),projection,selection,selectionArgs,null,null,_OrderBy);

                break;

/*
                if (selection != null && !selection.trim().isEmpty()){
                    selection = selection + " AND ";
                }
                selection = selection + AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID + " = " + uri.getLastPathSegment();

                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.ContasEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );
                break;
*/
            }
            case CONTAS: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.ContasEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );
                break;
            }
            case TIPOS_CONTAS: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.TipoContaEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );
                break;
            }
            case USUARIOS: {

                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.UsuariosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );
                break;
            }
            case USUARIOS_POR_ID: {
                if (selection != null && !selection.trim().isEmpty()){
                    selection = selection + " AND ";
                }
                selection = selection + AlgumDBContract.UsuariosEntry.COLUMN_ID + " = " + uri.getLastPathSegment();

                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.UsuariosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );
                break;
            }
            case GRUPOS: {

                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.GruposEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );

                break;
            }
            case TIPOS_GRUPOS: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.TipoGrupoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );
                break;
            }
            case GRUPOS_POR_USUARIO: {
                SQLiteQueryBuilder _QB = new SQLiteQueryBuilder();

                if (selection != null && !selection.trim().isEmpty()) {
                    selection = selection + " AND ";
                }
                selection = selection + AlgumDBContract.GruposEntry.TABLE_NAME + "." + AlgumDBContract.GruposEntry.COLUMN_USUARIO_ID + " = " + uri.getLastPathSegment();

                _QB.setTables(AlgumDBContract.GruposEntry.TABLE_NAME +
                        " INNER JOIN " + AlgumDBContract.TipoGrupoEntry.TABLE_NAME + " ON " +
                        AlgumDBContract.TipoGrupoEntry.TABLE_NAME + "." + AlgumDBContract.TipoGrupoEntry.COLUMN_ID + " = " +
                        AlgumDBContract.GruposEntry.TABLE_NAME + "." + AlgumDBContract.GruposEntry.COLUMN_TIPO_ID);

                retCursor = _QB.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);

                break;
            }
            case USUARIOS_POR_GRUPO: {

                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.GrupoUsuariosEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );

                break;
            }
            case LANCAMENTOS: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.LancamentoEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null, sortOrder
                );

                break;
            }
            case LANCAMENTOS_POR_USUARIO: {
                SQLiteQueryBuilder _QB = new SQLiteQueryBuilder();

                if (selection != null && !selection.trim().isEmpty()){
                    selection = selection + " AND ";
                }
                selection = selection + AlgumDBContract.ContasEntry.TABLE_NAME + "." + AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID + " = " + uri.getLastPathSegment();
                selection = selection + " AND " + AlgumDBContract.LancamentoEntry.TABLE_NAME + "." + AlgumDBContract.LancamentoEntry.COLUMN_EXCLUIDO + " = 0 ";

                _QB.setTables(AlgumDBContract.LancamentoEntry.TABLE_NAME +
                        " INNER JOIN " + AlgumDBContract.GruposEntry.TABLE_NAME + " ON " +
                        AlgumDBContract.GruposEntry.TABLE_NAME + "." + AlgumDBContract.GruposEntry.COLUMN_ID + " = " +
                        AlgumDBContract.LancamentoEntry.TABLE_NAME + "." + AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID +
                        " INNER JOIN " + AlgumDBContract.ContasEntry.TABLE_NAME + " ON " +
                        AlgumDBContract.ContasEntry.TABLE_NAME + "." + AlgumDBContract.ContasEntry.COLUMN_ID + " = " +
                        AlgumDBContract.LancamentoEntry.TABLE_NAME + "." + AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID);

                String _OrderBy = AlgumDBContract.LancamentoEntry.TABLE_NAME + "." + AlgumDBContract.LancamentoEntry.COLUMN_DATA + " DESC, " +
                        AlgumDBContract.LancamentoEntry.TABLE_NAME+"."+ AlgumDBContract.LancamentoEntry.COLUMN_ID + " DESC ";

                retCursor = _QB.query(mDbHelper.getReadableDatabase(),projection,selection,selectionArgs,null,null,_OrderBy);

                break;
            }
            case LOG: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.LogEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,sortOrder
                );
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Uri returnUri;

        switch (sUriMatcher.match(uri)){
            case CONTAS:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.ContasEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AlgumDBContract.ContasEntry.buildContaUsuarioUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TIPOS_CONTAS:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.TipoContaEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = null;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USUARIOS:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.UsuariosEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AlgumDBContract.ContasEntry.buildContaUsuarioUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case GRUPOS:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.GruposEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AlgumDBContract.GruposEntry.buildGrupoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case TIPOS_GRUPOS:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.TipoGrupoEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = null;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case USUARIOS_POR_GRUPO:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.GrupoUsuariosEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = null;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LANCAMENTOS:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.LancamentoEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = null;//AlgumDBContract.LancamentoEntry.buildLancamentoUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            case LOG:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.LogEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = null;
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        int _id;
        switch (sUriMatcher.match(uri)) {
            case LANCAMENTOS: {
                _id = mDbHelper.getWritableDatabase().delete(AlgumDBContract.LancamentoEntry.TABLE_NAME, selection, selectionArgs);
                 break;
            }
            case CONTAS: {
                _id = mDbHelper.getWritableDatabase().delete(AlgumDBContract.ContasEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TIPOS_CONTAS: {
                _id = mDbHelper.getWritableDatabase().delete(AlgumDBContract.TipoContaEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case GRUPOS: {
                _id = mDbHelper.getWritableDatabase().delete(AlgumDBContract.GruposEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case USUARIOS_POR_GRUPO: {
                _id = mDbHelper.getWritableDatabase().delete(AlgumDBContract.GrupoUsuariosEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case USUARIOS: {
                _id = mDbHelper.getWritableDatabase().delete(AlgumDBContract.UsuariosEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case LOG: {
                _id = mDbHelper.getWritableDatabase().delete(AlgumDBContract.LogEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return _id;    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int _id = 0;
        switch (sUriMatcher.match(uri)) {
            case LANCAMENTOS: {
                _id = mDbHelper.getWritableDatabase().update(AlgumDBContract.LancamentoEntry.TABLE_NAME, values, selection, selectionArgs);
                //if (_id == 0)
                //    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            }
            case CONTAS: {
                _id = mDbHelper.getWritableDatabase().update(AlgumDBContract.ContasEntry.TABLE_NAME, values, selection, selectionArgs);
                //if (_id == 0)
                //    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            }
            case SALDO_CONTA: {

                String strUpdate = "update "+ AlgumDBContract.ContasEntry.TABLE_NAME
                        +" set "+ AlgumDBContract.ContasEntry.COLUMN_SALDO+" = "+ AlgumDBContract.ContasEntry.COLUMN_SALDO+"+("+values.getAsString(AlgumDBContract.LancamentoEntry.COLUMN_VALOR)+")"
                        +" where "+ AlgumDBContract.ContasEntry.COLUMN_ID+" = "+ values.getAsString(AlgumDBContract.ContasEntry.COLUMN_ID);
                mDbHelper.getWritableDatabase().execSQL(strUpdate);
                break;
            }
            case TIPOS_CONTAS: {
                _id = mDbHelper.getWritableDatabase().update(AlgumDBContract.TipoContaEntry.TABLE_NAME, values, selection, selectionArgs);
                //if (_id == 0)
                //    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            }
            case GRUPOS: {
                _id = mDbHelper.getWritableDatabase().update(AlgumDBContract.GruposEntry.TABLE_NAME, values, selection, selectionArgs);
                //if (_id == 0)
                //    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            }
            case TIPOS_GRUPOS: {
                _id = mDbHelper.getWritableDatabase().update(AlgumDBContract.TipoGrupoEntry.TABLE_NAME, values, selection, selectionArgs);
                //if (_id == 0)
                //    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            }
            case USUARIOS: {
                _id = mDbHelper.getWritableDatabase().update(AlgumDBContract.UsuariosEntry.TABLE_NAME, values, selection, selectionArgs);
                //if (_id == 0)
                //    throw new android.database.SQLException("Failed to update row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri,null);
        return _id;
    }

}
