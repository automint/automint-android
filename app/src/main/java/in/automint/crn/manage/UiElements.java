package in.automint.crn.manage;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

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
    private Context context;

    /**
     * Default constructor to initialize class object
     * It identifies the calling activity.
     * @param activity as calling activity
     */
    public UiElements(Activity activity) {
        this.activity = activity;
    }

    /**
     * Default constructor to initialize class object
     * It identifies the calling context
     * @param context as calling context
     */
    public UiElements(Context context) {
        this.context = context;
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
