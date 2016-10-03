package in.automint.crn.data;

/**
 * To hold static data throughout the App
 * Created by ndkcha on 27/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class MintConst {
    //  domain names for different functionality
    //  TODO: change this domain urls while switching to debug/release mode
    private static final String LICENSE_DOMAIN = "cbs.automint.in:8443";
    private static final String API_DOMAIN = "cbs.automint.in:8443";
    private static final String DB_DOMAIN = "cbs.automint.in:4984";

    //  Url for different API Calls and Sync
    public static abstract class Url {
        public static final String AUTH_URL = "https://" + LICENSE_DOMAIN + "/licensing/0.1/auth";
        public static String ACTIVATION_URL(String code) {
            return ("https://" + LICENSE_DOMAIN + "/licensing/0.1/activate/code/" + code);
        }
        public static final String CHANGE_PASSWORD_URL = "https://" + API_DOMAIN + "/api/0.1/password";
    }

    //  Database names
    public static abstract class Db {
        public static final String MAIN = "cbl-main";
        public static final String LOCAL = "cbl-local";
    }
}
