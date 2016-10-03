package in.automint.crn.data;

/**
 * Data holder for Treatment Details
 * Created by ndkcha on 01/10/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class TreatmentHolder {
    public static final String FIELD_DETAILS = "details";
    public static final String FIELD_RATE = "rate";
    public double rate;
    public String details;

    public TreatmentHolder(double rate, String details) {
        this.rate = rate;
        this.details = details;
    }
}
