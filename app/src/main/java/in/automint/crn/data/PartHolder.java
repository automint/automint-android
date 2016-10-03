package in.automint.crn.data;

/**
 * Data holder for Part Details
 * Created by ndkcha on 02/10/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class PartHolder {
    public static final String FIELD_NAME = "name";
    public static final String FIELD_RATE = "rate";
    public static final String FIELD_QTY = "qty";
    public static final String FIELD_AMOUNT = "amount";

    public double rate, qty, amount;
    public String name;

    public PartHolder(double rate, double qty, double amount, String name) {
        this.rate = rate;
        this.qty = qty;
        this.amount = amount;
        this.name = name;
    }
}
