package in.automint.crn.manage;

import android.content.Context;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.util.Log;

import java.io.IOException;

import in.automint.crn.data.MintConst;

/**
 * To handle database operation on CBL
 * Created by ndkcha on 28/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class CouchBaseLite {
    private static final String TAG = "CouchBaseLite";

    private static Database database;
    private static Manager manager;

    public static Database getDatabaseInstance() {
        try {
            if ((database == null) && (manager != null))
                database = manager.getDatabase(MintConst.Db.MAIN);
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, e.toString());
        }
        return database;
    }

    public static Manager getManagerInstance(Context context) {
        try {
            if (manager == null)
                manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
        return manager;
    }

    public static void initManagerInstance(Context context) {
        try {
            if (manager == null)
                manager = new Manager(new AndroidContext(context), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }
    }
}
