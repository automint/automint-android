package in.automint.crn.manage;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * To manipulate UI elements runtime
 * Created by ndkcha on 28/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class UiElements {
    private static final String TAG = "UiElements";

    //  keep track of calling activity
    private Activity activity;

    //  different date format objects for different purposes
    private SimpleDateFormat displayServiceDateFormat, dbFormat;

    /**
     * Default constructor to initialize class object
     * It identifies the calling activity.
     * @param activity as calling activity
     */
    public UiElements(Activity activity) {
        this.activity = activity;
        displayServiceDateFormat = new SimpleDateFormat("dd MMM yyyy", activity.getResources().getConfiguration().locale);
        dbFormat  = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", activity.getResources().getConfiguration().locale);
    }

    /**
     * Coverts current date in display format (dd MMM yyyy)
     * @return date as string
     */
    public String todayInDisplayFormat() {
        return displayServiceDateFormat.format(Calendar.getInstance().getTime());
    }

    /**
     * Coverts display formatted date to db format (dd MMM yyyy to moment.js format)
     * @param date as date in dd MMM yyyy
     * @return date as string
     */
    public String convertDisplayToDbFormat(String date) {
        try {
            return dbFormat.format(displayServiceDateFormat.parse(date)).replaceAll("(\\+\\d\\d)(\\d\\d)", "$1:$2");
        } catch (ParseException e) {
            Log.e(TAG, "Error in Parsing Date (covertToDbFormat): " + ((date == null) ? "no date" : date));
            return null;
        }
    }

    /**
     * Converts db formatted date ot display format (moment.js format to dd MMM yyyy format)
     * @param date as date in moment.js format
     * @return date as string
     */
    public String convertDbToDisplayFormat(String date) {
        try {
            return displayServiceDateFormat.format(dbFormat.parse(date));
        } catch (ParseException e) {
            Log.e(TAG, "Error in Parsing Date (convertDbToDisplay): " + ((date == null) ? "no date" : date));
            return null;
        }
    }

    /**
     * Initialize date picker dialog which will allow user to select date and display in particular box
     * @param result as edit box in which result will be displayed
     * @param maxDateEnabled true if user is not allowed select date grater than today
     */
    public void showDatePickerDialog(final AppCompatEditText result, final boolean maxDateEnabled) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(activity, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar date = Calendar.getInstance();
                date.set(year, monthOfYear, dayOfMonth);
                result.setText(displayServiceDateFormat.format(date.getTime()));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (maxDateEnabled)
            dialog.getDatePicker().setMaxDate(new Date().getTime());
        dialog.show();
    }

    /**
     * Displays SnackBar with particular message and duration
     * @param messageId as integer value of String resource
     * @param duration as duration
     */
    public void showSnackBar(int messageId, int duration) {
        View content = activity.findViewById(android.R.id.content);
        if (content != null)
            Snackbar.make(content, messageId, duration).show();
    }

    /**
     * Method to hide keyboard from a particular view
     * It works only for activities and those elements which can refer to calling activity
     * @param view as currently focused view
     */
    public void hideKeyboard(View view) {
        if (activity == null)
            return;
        InputMethodManager keyboardManager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keyboardManager != null)
            keyboardManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
