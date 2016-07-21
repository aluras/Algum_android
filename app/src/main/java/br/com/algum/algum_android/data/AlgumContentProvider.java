package br.com.algum.algum_android.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by sn1007071 on 29/03/2016.
 */
public class AlgumContentProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private AlgumDbHelper mDbHelper;

    private static final int CONTAS = 100;
    private static final int CONTAS_POR_USUARIO = 101;
    private static final int USUARIOS = 200;
    private static final int GRUPOS = 300;
    private static final int LANCAMENTOS = 400;

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

        matcher.addURI(authority, AlgumDBContract.PATH_USUARIOS, USUARIOS);

        matcher.addURI(authority, AlgumDBContract.PATH_GRUPOS, GRUPOS);

        matcher.addURI(authority, AlgumDBContract.PATH_LANCAMENTOS, LANCAMENTOS);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new AlgumDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case CONTAS_POR_USUARIO: {
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
            case LANCAMENTOS: {
                retCursor = mDbHelper.getReadableDatabase().query(
                        AlgumDBContract.LancamentoEntry.TABLE_NAME,
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
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
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
            case LANCAMENTOS:{
                long _id = mDbHelper.getWritableDatabase().insert(AlgumDBContract.LancamentoEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = AlgumDBContract.LancamentoEntry.buildGrupoUri(_id);
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
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
