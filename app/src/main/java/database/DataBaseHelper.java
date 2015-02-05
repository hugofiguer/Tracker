package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import database.models.Note;
import util.LogUtil;

import static util.LogUtil.makeLogTag;


public class DataBaseHelper extends SQLiteOpenHelper {

    private static final String TAG = makeLogTag(DataBaseHelper.class);
    private static final String DATABASE_NAME = "tracker.db";

    // NOTE: carefully update onUpgrade() when bumping database versions to make
    // sure user data is saved.

    private static final int VER_1            = 1;  // 1.1
    private static final int DATABASE_VERSION = VER_1;
    public static final int NOT_UPDATE        = -1;
    private final Context mContext;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        LogUtil.addCheckpoint("DatabaseHelper : onCreate");

        db.execSQL("CREATE TABLE "+ Note.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"description text NOT NULL,"
                +"user_id integer NOT NULL,"
                +"timestamp varchar(250) NOT NULL,"
                +"FOREIGN KEY (user_id) REFERENCES user (id))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // NOTE: This switch statement is designed to handle cascading database
        // updates, starting at the current version and falling through to all
        // future upgrade cases. Only use "break;" when you want to drop and
        // recreate the entire database.
        switch (oldVersion) {
            case VER_1:
                Log.i(TAG, "Database Ver " + oldVersion);
        }

        Log.d(TAG, "after upgrade logic, at version " + oldVersion);
        if (oldVersion != DATABASE_VERSION) {
            Log.w(TAG, "Destroying old data during upgrade");
            db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE);
            onCreate(db);
        }
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
