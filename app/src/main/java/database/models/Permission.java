package database.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sellcom.tracker_interno.NavigationDrawerFragment;

import java.util.ArrayList;
import java.util.List;

import database.DataBaseAdapter;


public class Permission extends Table {

    public static final String TABLE = "permission";

    public static final String ID = "id";
    public static final String PROFILE_ID = "profile_id";
    public static final String MODULE_ID = "module_id";

    private int id;
    private String profile;
    private String module;

    public static long insert(Context context, int profile_id, int module_id) {
        ContentValues cv = new ContentValues();
        cv.put(PROFILE_ID, profile_id);
        cv.put(MODULE_ID, module_id);

        return DataBaseAdapter.getDB(context).insert(TABLE, null, cv);
    }

    public static List<Integer> getModules(Context context, int profileId) {
        List<Integer> modules = new ArrayList<Integer>();
        Cursor cursor = DataBaseAdapter.getDB(context).query(TABLE, null, PROFILE_ID + "=" + profileId, null, null, null, null);
        if (cursor != null) {

            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

                int moduleId = cursor.getInt(cursor.getColumnIndexOrThrow(MODULE_ID));
                modules.add(moduleId);
            }
            cursor.close();
        }
        return modules;
    }

    public static void setBasicPermission(Context context, String profileName) {

        Profile.addPermission(context, profileName, NavigationDrawerFragment.HOME);
        Profile.addPermission(context, profileName, NavigationDrawerFragment.WORK_PLAN);
        Profile.addPermission(context, profileName, NavigationDrawerFragment.SETTINGS);
        Profile.addPermission(context, profileName, NavigationDrawerFragment.LOG_OUT);
    }

    public static void setFullPermission(Context context, String profileName) {

        Profile.addPermission(context, profileName, NavigationDrawerFragment.HOME);
    }
}
