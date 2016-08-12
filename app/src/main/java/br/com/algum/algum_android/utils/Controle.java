package br.com.algum.algum_android.utils;

import android.content.ContentValues;
import android.content.Context;

import java.util.Date;

import br.com.algum.algum_android.data.AlgumDBContract;

/**
 * Created by sn1007071 on 08/08/2016.
 */
public class Controle {

    public static void gravaLog(Context context, String message, int usuarioId){

        ContentValues values = new ContentValues();

        Date data = new Date();
        values.put(AlgumDBContract.LogEntry.COLUMN_DATA, data.getTime());
        values.put(AlgumDBContract.LogEntry.COLUMN_MENSAGEM,message);
        values.put(AlgumDBContract.LogEntry.COLUMN_USUARIO_ID,usuarioId);

        context.getContentResolver().insert(AlgumDBContract.LogEntry.CONTENT_URI,values);

    }
}
