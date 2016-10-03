package in.automint.crn.services.sections;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.view.View;
import android.view.Window;

import java.util.Locale;

import in.automint.crn.R;
import in.automint.crn.data.TreatmentHolder;
import in.automint.crn.manage.UiElements;

/**
 * Dialog to display and handle Treatment Details in Add Service module
 * Created by ndkcha on 30/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

class TreatmentDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private static final String TAG = "TreatmentDialog";

    //  ui elements
    private Dialog dialog;
    private Activity activity;
    private UiElements uiElements;
    private AppCompatEditText inputDetails, inputRate;

    //  backend elements
    private int method = Method.ADD;
    private int position;

    TreatmentDialog(Activity activity) {
        this.activity = activity;
        uiElements = new UiElements(activity);
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_service_add_treatment);
        dialog.setOnDismissListener(this);

        inputDetails = (AppCompatEditText) this.dialog.findViewById(R.id.input_treatment_details);
        inputRate = (AppCompatEditText) this.dialog.findViewById(R.id.input_rate);
        AppCompatButton buttonDone = (AppCompatButton) dialog.findViewById(R.id.button_done);
        if (buttonDone != null)
            buttonDone.setOnClickListener(this);
    }

    void add() {
        method = Method.ADD;
        inputDetails.setText("");
        inputRate.setText("");
        dialog.show();
        inputDetails.requestFocus();
    }

    void update(TreatmentHolder holder, int position) {
        this.position = position;
        method = Method.UPDATE;
        inputDetails.setText(holder.details);
        String cost = ((holder.rate % 1) == 0) ? String.valueOf(Math.round(holder.rate)) : String.format(Locale.ENGLISH, "%.2f", holder.rate);
        inputRate.setText(cost);
        dialog.show();
        inputRate.requestFocus();
        inputRate.setSelection(cost.length());
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
        TreatmentHolder holder;
        TreatmentsActivity callingActivity = (TreatmentsActivity) activity;
        if (callingActivity != null) {
            String details = inputDetails.getText().toString();
            String rate = inputRate.getText().toString();
            if (!details.isEmpty() && !rate.isEmpty()) {
                switch (method) {
                    case Method.ADD:
                        holder = new TreatmentHolder(Double.parseDouble(rate), details);
                        int total = callingActivity.adapter.getCount();
                        callingActivity.adapter.addItem(total, holder);
                        break;
                    case Method.UPDATE:
                        holder = (TreatmentHolder) callingActivity.adapter.getItem(position);
                        holder.details = details;
                        holder.rate = Double.parseDouble(rate);
                        callingActivity.adapter.notifyDataSetChanged();
                        break;
                }
                callingActivity.calculateCost();
            }
        } else
            uiElements.showSnackBar(R.string.error_treatment_add, Snackbar.LENGTH_SHORT);
    }

    private class Method {
        static final int ADD = 1;
        static final int UPDATE = 2;
    }
}
