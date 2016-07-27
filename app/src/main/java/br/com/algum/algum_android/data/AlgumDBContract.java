package br.com.algum.algum_android.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sn1007071 on 28/03/2016.
 */
public class AlgumDBContract {

    public static final String CONTENT_AUTHORITY = "br.com.algum.algum_android.provider";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_CONTAS = "contas";
    public static final String PATH_USUARIOS = "usuarios";
    public static final String PATH_GRUPOS = "grupos";
    public static final String PATH_LANCAMENTOS = "lancamentos";

    public static final String DATE_FORMAT = "yyyyMMdd";

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date){
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }
    public static Date getDateFromDb(String dateText) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            return dbDateFormat.parse(dateText);
        } catch ( ParseException e ) {
            e.printStackTrace();
            return null;
        }
    }

    public static final class ContasEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONTAS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_CONTAS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_CONTAS;

        public static final String TABLE_NAME = "contas";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_CONTA_ID = "conta_id";
        public static final String COLUMN_TIPO_CONTA_ID = "tipo_conta_id";
        public static final String COLUMN_USUARIO_ID = "usuario_id";
        public static final String COLUMN_NOME = "nome";

        public static Uri buildContaUsuarioUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    public static final class UsuariosEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USUARIOS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_USUARIOS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_USUARIOS;

        public static final String TABLE_NAME = "usuarios";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_EMAIL = "email";

        public static Uri buildUsuarioUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class GruposEntry implements BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GRUPOS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_GRUPOS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_GRUPOS;

        public static final String TABLE_NAME = "grupos";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_GRUPO_ID = "grupo_id";
        public static final String COLUMN_NOME = "nome";
        public static final String COLUMN_TIPO_ID = "tipo_id";

        public static Uri buildGrupoUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    public static final class LancamentoEntry implements  BaseColumns{
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_LANCAMENTOS).build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + "/" + PATH_LANCAMENTOS;
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_AUTHORITY + "/" + PATH_LANCAMENTOS;

        public static final String TABLE_NAME = "lancamentos";

        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_LANCAMENTO_ID = "lancamento_id";
        public static final String COLUMN_DATA = "data";
        public static final String COLUMN_VALOR = "valor";
        public static final String COLUMN_OBSERVACAO = "observacao";
        public static final String COLUMN_TIPO_ID = "tipo_id";
        public static final String COLUMN_GRUPO_ID = "grupo_id";
        public static final String COLUMN_CONTA_ORIGEM_ID = "conta_origem_id";
        public static final String COLUMN_CONTA_DESTINO_ID = "conta_destino_id";

        public static Uri buildLancamentoUsuarioUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
