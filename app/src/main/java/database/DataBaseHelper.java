package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import database.models.Note;
import database.models.People;
import database.models.Permission;
import database.models.Profile;
import database.models.Session;
import database.models.User;
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

        db.execSQL("CREATE TABLE "+ User.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"username varchar(250) NOT NULL,"
                +"password varchar(250) NOT NULL,"
                +"profile_id integer NOT NULL,"
                +"people_id integer NOT NULL,"
                +"FOREIGN KEY (profile_id) REFERENCES profile (id),"
                +"FOREIGN KEY (people_id) REFERENCES profile (id))");

        db.execSQL("CREATE TABLE "+ Permission.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY AUTOINCREMENT,"
                +"profile_id integer NOT NULL,"
                +"module_id integer NOT NULL,"
                +"FOREIGN KEY (profile_id) REFERENCES profile (id),"
                +"FOREIGN KEY (module_id) REFERENCES module (id))");

        db.execSQL("CREATE TABLE "+ Profile.TABLE+" ("
                +"id integer NOT NULL PRIMARY KEY,"
                +"name varchar(250) NOT NULL)");

        db.execSQL("CREATE TABLE "+ People.TABLE +" (" +
                "    id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "    first_name varchar(250) NOT NULL," +
                "    last_name varchar(250) NOT NULL," +
                "    email varchar(250) NOT NULL," +
                "    rfc varchar(250)," +
                "    phone_number varchar(250)," +
                "    address_1 varchar(250)," +
                "    address_2 varchar(250)," +
                "    city varchar(250)," +
                "    state varchar(250)," +
                "    country varchar(250)," +
                "    comments varchar(250)," +
                "    zip_code integer," +
                "    photo blob" +
                ")");

        db.execSQL("CREATE TABLE "+ Session.TABLE+" (" +
                "    id integer NOT NULL PRIMARY KEY AUTOINCREMENT," +
                "    status integer NOT NULL," +
                "    timestamp datetime NOT NULL DEFAULT (datetime('now','localtime'))," +
                "    token varchar(250) NOT NULL," +
                "    device varchar(250) NOT NULL," +
                "    user_id integer NOT NULL," +
                "    FOREIGN KEY (user_id) REFERENCES user (id)" +
                ")");

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
            db.execSQL("DROP TABLE IF EXISTS " + User.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Permission.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Profile.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + People.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Session.TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + Note.TABLE);
            onCreate(db);
        }
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }
}
