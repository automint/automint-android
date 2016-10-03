package in.automint.crn.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Manage temporary run time data throughout App
 * Created by ndkcha on 01/10/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class LocData {
    private SharedPreferences sharedPreferences;

    //  fields
    private static final String PREF_ADD_SERVICE = "temp_add_service";
    private static final String TREATMENTS = "treatments";
    private static final String PARTS = "parts";

    public void addServiceInstance(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_ADD_SERVICE, Context.MODE_PRIVATE);
    }

    public void storeTreatments(JSONArray treatments) {
        sharedPreferences.edit().putString(TREATMENTS, treatments.toString()).apply();
    }

    public JSONArray getTreatments() {
        String json = sharedPreferences.getString(TREATMENTS, "");
        try {
            return (json.isEmpty() ? null : (new JSONArray(json)));
        } catch (JSONException e) {
            return null;
        }
    }

    public void storeParts(JSONArray parts) {
        sharedPreferences.edit().putString(PARTS, parts.toString()).apply();
    }

    public JSONArray getParts() {
        String json = sharedPreferences.getString(PARTS, "");
        try {
            return (json.isEmpty() ? null : (new JSONArray(json)));
        } catch (JSONException e) {
            return null;
        }
    }

    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}
