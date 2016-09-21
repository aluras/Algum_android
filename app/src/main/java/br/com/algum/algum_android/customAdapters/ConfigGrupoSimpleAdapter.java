package br.com.algum.algum_android.customAdapters;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorTreeAdapter;

import br.com.algum.algum_android.data.AlgumDBContract;

/**
 * Created by sn1007071 on 20/09/2016.
 */
public class ConfigGrupoSimpleAdapter extends SimpleCursorTreeAdapter {

    private Activity mActivity;

    public ConfigGrupoSimpleAdapter(Context context, Cursor cursor, int groupLayout, String[] groupFrom, int[] groupTo, int childLayout, String[] childFrom, int[] childTo) {
        super(context, cursor, groupLayout, groupFrom, groupTo, childLayout, childFrom, childTo);
        mActivity = (Activity) context;
    }

    @Override
    protected Cursor getChildrenCursor(Cursor cursor) {
        int groupId = cursor.getInt(cursor
                .getColumnIndex(AlgumDBContract.TipoGrupoEntry.COLUMN_ID));
/*
        android.content.Loader<Object> loader = mActivity.getLoaderManager().getLoader(groupId);
        if ( loader != null && loader.isReset() ) {
            mActivity.getLoaderManager().restartLoader(groupId, null, mFragment);
        } else {
            mActivity.getLoaderManager().initLoader(groupId, null, mFragment);
        }

    }
*/
    return null;
    }
}
