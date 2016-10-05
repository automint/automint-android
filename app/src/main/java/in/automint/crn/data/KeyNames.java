package in.automint.crn.data;

/**
 * Holder to store key names for database
 * Created by ndkcha on 03/10/16.
 * @since 9.10.0
 * @version 0.10.0
 */

public class KeyNames {
    public static abstract class Customer {
        public static final String NAME = "name";
        public static final String MOBILE = "mobile";
        public static final String VEHICLES = "vehicles";
    }

    public static abstract class Vehicle {
        public static final String MANUFACTURER = "manuf";
        public static final String MODEL = "model";
        public static final String REG = "reg";
        public static final String TYPE = "type";
        public static final String SERVICES = "services";
    }

    public static abstract class Service {
        public static final String DATE = "date";
        public static final String COST = "cost";
        public static final String STATE = "state";
        public static final String STATUS = "status";
        public static final String ODO = "odo";
        public static final String INVOICE_NO = "invoiceno";
        public static final String PROBLEMS = "problems";
        public static final String INVENTORIES = "inventories";
    }
}
