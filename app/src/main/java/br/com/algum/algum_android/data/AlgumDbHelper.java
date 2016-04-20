package br.com.algum.algum_android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sn1007071 on 28/03/2016.
 */
public class AlgumDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "algum.db";

    public AlgumDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_CONTA_TABLE = "CREATE TABLE " + AlgumDBContract.ContasEntry.TABLE_NAME + " (" +
                AlgumDBContract.ContasEntry.COLUMN_CONTA_ID + " INTEGER PRIMARY KEY," +
                AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL, " +
                AlgumDBContract.ContasEntry.COLUMN_NOME + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_CONTA_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlgumDBContract.ContasEntry.TABLE_NAME);
        onCreate(db);

    }
}
