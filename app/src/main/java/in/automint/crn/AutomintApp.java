package in.automint.crn;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

/**
 * Application class to define certain parameters at global level
 * Created by ndkcha on 26/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class AutomintApp extends MultiDexApplication {

    //  empty default constructor
    public AutomintApp() { }

    /**
     * Installs Multi Dex support if needed for current Android VM
     * @param base context of current application
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
