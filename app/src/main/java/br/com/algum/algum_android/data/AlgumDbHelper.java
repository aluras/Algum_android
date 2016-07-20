package br.com.algum.algum_android.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by sn1007071 on 28/03/2016.
 */
public class AlgumDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 6;

    public static final String DATABASE_NAME = "algum.db";

    public AlgumDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_CONTA_TABLE = "CREATE TABLE " + AlgumDBContract.ContasEntry.TABLE_NAME + " (" +
                AlgumDBContract.ContasEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                AlgumDBContract.ContasEntry.COLUMN_CONTA_ID + " INTEGER NOT NULL," +
                AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID + " INTEGER NOT NULL," +
                AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL, " +
                AlgumDBContract.ContasEntry.COLUMN_NOME + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_CONTA_TABLE);
/*
        //TODO: Remove after the sync adapter is ready
        String SQL_INSERT_CONTA = "INSERT INTO contas VALUES(1,1,1,2,'Conta Corrente')";
        db.execSQL(SQL_INSERT_CONTA);
        SQL_INSERT_CONTA = "INSERT INTO contas VALUES(2,2,2,2,'Investimento')";
        db.execSQL(SQL_INSERT_CONTA);
        SQL_INSERT_CONTA = "INSERT INTO contas VALUES(3,3,3,2,'Cartão de Crédito')";
        db.execSQL(SQL_INSERT_CONTA);
        SQL_INSERT_CONTA = "INSERT INTO contas VALUES(4,4,4,2,'Espécie')";
        db.execSQL(SQL_INSERT_CONTA);
        SQL_INSERT_CONTA = "INSERT INTO contas VALUES(5,5,5,2,'VR')";
        db.execSQL(SQL_INSERT_CONTA);
*/
        final String SQL_CREATE_USUARIO_TABLE = "CREATE TABLE " + AlgumDBContract.UsuariosEntry.TABLE_NAME + " (" +
                AlgumDBContract.UsuariosEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                AlgumDBContract.UsuariosEntry.COLUMN_EMAIL + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_USUARIO_TABLE);

        final String SQL_CREATE_GRUPO_TABLE = "CREATE TABLE " + AlgumDBContract.GruposEntry.TABLE_NAME + " (" +
                AlgumDBContract.GruposEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                AlgumDBContract.GruposEntry.COLUMN_GRUPO_ID + " INTEGER NOT NULL," +
                AlgumDBContract.GruposEntry.COLUMN_TIPO_ID + " INTEGER NOT NULL," +
                AlgumDBContract.GruposEntry.COLUMN_NOME + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_GRUPO_TABLE);
/*
        //TODO: Remove after the sync adapter is ready
        String SQL_INSERT_GRUPO = "INSERT INTO grupos VALUES(1,1,'Alimentação')";
        db.execSQL(SQL_INSERT_GRUPO);
        SQL_INSERT_GRUPO = "INSERT INTO grupos VALUES(2,2,'Educação')";
        db.execSQL(SQL_INSERT_GRUPO);
*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlgumDBContract.ContasEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlgumDBContract.UsuariosEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlgumDBContract.GruposEntry.TABLE_NAME);
        onCreate(db);

    }
}
