package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import database.DataBaseAdapter;


public class Note extends Table {

    public static final String TABLE = "note";

    public static final String ID = "id";
    public static final String DESCRIPTION = "description";
    public static final String USER_ID = "user_id";
    public static final String TIMESTAMP = "timestamp";

    public static long insert(Context context, String description,int user_id,  String timestamp ) {
        ContentValues cv = new ContentValues();
        cv.put(DESCRIPTION, description);
        cv.put(USER_ID, user_id);
        cv.put(TIMESTAMP, timestamp);
        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static int delete(Context context, int note_id) {

        return DataBaseAdapter.getDB(context).delete(TABLE, ID+"="+note_id, null);
    }

    public static Cursor getAll(Context context) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, USER_ID+"="+Session.getSessionActive(context).getId(), null, null, null, TIMESTAMP + " DESC");
        if (cursor.getCount() > 0)
            cursor.moveToFirst();

        return cursor;

    }

    public static Cursor getNote(Context context, String id) {

        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, "("+ID+"="+id +") and ("+USER_ID+"="+Session.getSessionActive(context).getId()+")", null, null, null, null);
        if (cursor.getCount() > 0)
            cursor.moveToFirst();

        return cursor;
    }
}
