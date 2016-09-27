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

        final String SQL_CREATE_TIPO_CONTA_TABLE = "CREATE TABLE " + AlgumDBContract.TipoContaEntry.TABLE_NAME + " (" +
                AlgumDBContract.TipoContaEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                AlgumDBContract.TipoContaEntry.COLUMN_DESCRICAO + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_TIPO_CONTA_TABLE);

        final String SQL_CREATE_TIPO_GRUPO_TABLE = "CREATE TABLE " + AlgumDBContract.TipoGrupoEntry.TABLE_NAME + " (" +
                AlgumDBContract.TipoGrupoEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                AlgumDBContract.TipoGrupoEntry.COLUMN_DESCRICAO + " TEXT NOT NULL)";

        db.execSQL(SQL_CREATE_TIPO_GRUPO_TABLE);

        final String SQL_CREATE_CONTA_TABLE = "CREATE TABLE " + AlgumDBContract.ContasEntry.TABLE_NAME + " (" +
                AlgumDBContract.ContasEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AlgumDBContract.ContasEntry.COLUMN_CONTA_ID + " INTEGER NULL," +
                AlgumDBContract.ContasEntry.COLUMN_TIPO_CONTA_ID + " INTEGER NOT NULL," +
                AlgumDBContract.ContasEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL, " +
                AlgumDBContract.ContasEntry.COLUMN_NOME + " TEXT NOT NULL, " +
                AlgumDBContract.ContasEntry.COLUMN_SALDO_INICIAL + " DECIMAL NOT NULL, " +
                AlgumDBContract.ContasEntry.COLUMN_SALDO + " DECIMAL NOT NULL," +
                AlgumDBContract.ContasEntry.COLUMN_EXCLUIDO + " INTEGER NOT NULL DEFAULT 0," +
                AlgumDBContract.ContasEntry.COLUMN_ALTERADO + " INT NOT NULL DEFAULT 0)";

        db.execSQL(SQL_CREATE_CONTA_TABLE);

        final String SQL_CREATE_USUARIO_TABLE = "CREATE TABLE " + AlgumDBContract.UsuariosEntry.TABLE_NAME + " (" +
                AlgumDBContract.UsuariosEntry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                AlgumDBContract.UsuariosEntry.COLUMN_EMAIL + " TEXT NOT NULL," +
                AlgumDBContract.UsuariosEntry.COLUMN_NOME + " TEXT NOT NULL," +
                AlgumDBContract.UsuariosEntry.COLUMN_DATA_SYNC + " INT)";

        db.execSQL(SQL_CREATE_USUARIO_TABLE);

        final String SQL_CREATE_GRUPO_TABLE = "CREATE TABLE " + AlgumDBContract.GruposEntry.TABLE_NAME + " (" +
                AlgumDBContract.GruposEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AlgumDBContract.GruposEntry.COLUMN_GRUPO_ID + " INTEGER NULL," +
                AlgumDBContract.GruposEntry.COLUMN_TIPO_ID + " INTEGER NOT NULL," +
                AlgumDBContract.GruposEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL," +
                AlgumDBContract.GruposEntry.COLUMN_NOME + " TEXT NOT NULL," +
                AlgumDBContract.GruposEntry.COLUMN_EXCLUIDO + " INTEGER NOT NULL DEFAULT 0," +
                AlgumDBContract.GruposEntry.COLUMN_ALTERADO + " INT NOT NULL DEFAULT 0)";

        db.execSQL(SQL_CREATE_GRUPO_TABLE);

        final String SQL_CREATE_GRUPO_USUARIO_TABLE = "CREATE TABLE " + AlgumDBContract.GrupoUsuariosEntry.TABLE_NAME + " (" +
                AlgumDBContract.GrupoUsuariosEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AlgumDBContract.GrupoUsuariosEntry.COLUMN_GRUPO_ID + " INTEGER NULL," +
                AlgumDBContract.GrupoUsuariosEntry.COLUMN_EMAIL + " TEXT NOT NULL," +
                AlgumDBContract.GrupoUsuariosEntry.COLUMN_EXCLUIDO + " INTEGER NOT NULL," +
                AlgumDBContract.GrupoUsuariosEntry.COLUMN_ALTERADO + " INT NOT NULL DEFAULT 0)";

        db.execSQL(SQL_CREATE_GRUPO_USUARIO_TABLE);

        final String SQL_CREATE_LANCAMENTO_TABLE = "CREATE TABLE " + AlgumDBContract.LancamentoEntry.TABLE_NAME + " (" +
                AlgumDBContract.LancamentoEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AlgumDBContract.LancamentoEntry.COLUMN_LANCAMENTO_ID + " INTEGER NULL," +
                AlgumDBContract.LancamentoEntry.COLUMN_DATA + " INT NOT NULL," +
                AlgumDBContract.LancamentoEntry.COLUMN_VALOR + " DECIMAL NOT NULL," +
                AlgumDBContract.LancamentoEntry.COLUMN_OBSERVACAO + " TEXT NULL," +
                AlgumDBContract.LancamentoEntry.COLUMN_GRUPO_ID + " INTEGER NOT NULL," +
                AlgumDBContract.LancamentoEntry.COLUMN_CONTA_ID + " INTEGER NOT NULL,"+
                AlgumDBContract.LancamentoEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL,"+
                AlgumDBContract.LancamentoEntry.COLUMN_EXCLUIDO + " INTEGER NOT NULL DEFAULT 0," +
                AlgumDBContract.LancamentoEntry.COLUMN_ALTERADO + " INT NOT NULL DEFAULT 0)";

        db.execSQL(SQL_CREATE_LANCAMENTO_TABLE);

        final String SQL_CREATE_LOG_TABLE = "CREATE TABLE " + AlgumDBContract.LogEntry.TABLE_NAME + " (" +
                AlgumDBContract.LogEntry.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                AlgumDBContract.LogEntry.COLUMN_DATA + " INT NOT NULL," +
                AlgumDBContract.LogEntry.COLUMN_MENSAGEM + " TEXT NULL," +
                AlgumDBContract.LogEntry.COLUMN_USUARIO_ID + " INTEGER NOT NULL)";

        db.execSQL(SQL_CREATE_LOG_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AlgumDBContract.ContasEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlgumDBContract.UsuariosEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlgumDBContract.GruposEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + AlgumDBContract.LancamentoEntry.TABLE_NAME);
        onCreate(db);

    }
}
