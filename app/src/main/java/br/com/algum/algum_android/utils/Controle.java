package br.com.algum.algum_android.utils;

import android.accounts.Account;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import br.com.algum.algum_android.BuildConfig;
import br.com.algum.algum_android.R;
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

    public static void showMessage(Activity activity, String message){

        Toast toast = new Toast(activity.getApplicationContext());
        View layout = activity.getLayoutInflater().inflate(R.layout.toast,(ViewGroup) activity.findViewById(R.id.toast_layout_root));
        TextView text = (TextView) layout.findViewById(R.id.toastText);
        text.setText(message);
        text.setTextColor(activity.getApplicationContext().getResources().getColor(R.color.colorBack));
        toast.setView(layout);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();

    }

    public static void syncData(Context context){

        final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";

        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.userInfo), Context.MODE_PRIVATE);
        String usuarioEmail = sharedPref.getString(context.getString(R.string.emailUsuario), "");


        Account newAccount = new Account(
                usuarioEmail, context.getString(R.string.sync_account_type));
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(newAccount, AUTHORITY, bundle);

    }

}
