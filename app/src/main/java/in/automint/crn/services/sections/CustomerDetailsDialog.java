package in.automint.crn.services.sections;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.InputType;
import android.view.View;
import android.view.Window;

import java.util.Map;

import in.automint.crn.R;
import in.automint.crn.data.KeyNames;

/**
 * Dialog to display and handle Customer Details in Add Service module
 * Created by ndkcha on 29/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class CustomerDetailsDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private static final String TAG = "CustomerDetailsDialog";

    private Dialog dialog;
    private AppCompatTextView target;
    private Map<String, String> customerDetails;

    public CustomerDetailsDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_service_customerdetails);
        dialog.setOnDismissListener(this);

        AppCompatButton buttonDone = (AppCompatButton) dialog.findViewById(R.id.button_done);
        if (buttonDone != null)
            buttonDone.setOnClickListener(this);
    }

    public void show(AppCompatTextView target, Map<String, String> customerDetails) {
        this.target = target;
        this.customerDetails = customerDetails;
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_done:
                dialog.dismiss();
                break;
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        AppCompatEditText inputCustomerName = (AppCompatEditText) this.dialog.findViewById(R.id.input_customer_name);
        AppCompatEditText inputCustomerMobile = (AppCompatEditText) this.dialog.findViewById(R.id.input_customer_mobile);
        String name = inputCustomerName.getText().toString();
        String mobile = inputCustomerMobile.getText().toString();
        this.customerDetails.put(KeyNames.Customer.NAME, name);
        this.customerDetails.put(KeyNames.Customer.MOBILE, mobile);
        if (!mobile.isEmpty())
            name = (name.isEmpty() ? mobile : name.concat(" | " + mobile));
        if (name.isEmpty() || mobile.isEmpty()) {
            target.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
            target.setText(R.string.message_click_to_add);
        } else {
            target.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            target.setText(name);
        }
    }
}
