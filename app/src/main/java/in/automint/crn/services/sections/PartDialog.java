package in.automint.crn.services.sections;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;

import java.util.Locale;

import in.automint.crn.R;
import in.automint.crn.data.PartHolder;
import in.automint.crn.manage.UiElements;

/**
 * Dialog to display and handle Part Details in Add Service module
 * Created by ndkcha on 03/10/16.
 * @since 0.10.0
 * @version 0.10.0
 */

class PartDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private static final String TAG = "PartDialog";

    //  ui elements
    private Dialog dialog;
    private Activity activity;
    private UiElements uiElements;
    private AppCompatEditText inputName, inputRate, inputQty, inputAmount;

    //  backend elements
    private int method = Method.ADD;
    private int position;
    private boolean watchingText = false;

    PartDialog(Activity activity) {
        this.activity = activity;
        uiElements = new UiElements(activity);
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_service_add_part);
        dialog.setOnDismissListener(this);

        inputName = (AppCompatEditText) this.dialog.findViewById(R.id.input_part_name);
        inputRate = (AppCompatEditText) this.dialog.findViewById(R.id.input_rate);
        inputQty = (AppCompatEditText) this.dialog.findViewById(R.id.input_qty);
        inputAmount = (AppCompatEditText) this.dialog.findViewById(R.id.input_amount);
        AppCompatButton buttonDone = (AppCompatButton) this.dialog.findViewById(R.id.button_done);
        if (buttonDone != null)
            buttonDone.setOnClickListener(this);
    }

    void add() {
        method = Method.ADD;
        inputName.setText("");
        inputRate.setText("");
        inputQty.setText("1");
        inputAmount.setText("");
        dialog.show();
        inputName.requestFocus();
        inputRate.addTextChangedListener(rateWatcher);
        inputQty.addTextChangedListener(qtyWatcher);
        inputAmount.addTextChangedListener(amountWatcher);
    }

    void update(PartHolder holder, int position) {
        this.position = position;
        method = Method.UPDATE;
        inputName.setText(holder.name);
        String rate = ((holder.rate % 1) == 0) ? String.valueOf(Math.round(holder.rate)) : String.format(Locale.ENGLISH, "%.2f", holder.rate);
        String qty = ((holder.qty % 1) == 0) ? String.valueOf(Math.round(holder.qty)) : String.format(Locale.ENGLISH, "%.2f", holder.qty);
        String amount = ((holder.amount % 1) == 0) ? String.valueOf(Math.round(holder.amount)) : String.format(Locale.ENGLISH, "%.2f", holder.amount);
        inputRate.setText(rate);
        inputQty.setText(qty);
        inputAmount.setText(amount);
        dialog.show();
        inputRate.requestFocus();
        inputRate.setSelection(rate.length());
        inputRate.addTextChangedListener(rateWatcher);
        inputQty.addTextChangedListener(qtyWatcher);
        inputAmount.addTextChangedListener(amountWatcher);
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
        inputRate.removeTextChangedListener(rateWatcher);
        inputQty.removeTextChangedListener(qtyWatcher);
        inputAmount.removeTextChangedListener(amountWatcher);
        PartHolder holder;
        PartsActivity callingActivity = (PartsActivity) activity;
        if (callingActivity != null) {
            String name = inputName.getText().toString();
            String rate = inputRate.getText().toString();
            String qty = inputQty.getText().toString();
            String amount = inputAmount.getText().toString();
            if (qty.isEmpty())
                qty = "1";
            double r = Double.valueOf(rate);
            double q = Double.valueOf(qty);
            double a = Double.valueOf(amount);
            if (amount.isEmpty()) {
                a = r * q;
            }
            if (!name.isEmpty() && !rate.isEmpty()) {
                switch (method) {
                    case Method.ADD:
                        holder = new PartHolder(r, q, a, name);
                        int total = callingActivity.adapter.getCount();
                        callingActivity.adapter.addItem(total, holder);
                        break;
                    case Method.UPDATE:
                        holder = (PartHolder) callingActivity.adapter.getItem(position);
                        holder.name = name;
                        holder.rate = r;
                        holder.qty = q;
                        holder.amount = a;
                        callingActivity.adapter.notifyDataSetChanged();
                        break;
                }
                callingActivity.calculateCost();
            }
        } else
            uiElements.showSnackBar(R.string.error_part_add, Snackbar.LENGTH_SHORT);
    }

    private TextWatcher rateWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (watchingText)
                return;
            watchingText = true;
            String qty = inputQty.getText().toString();
            String rate = inputRate.getText().toString();
            if (qty.isEmpty())
                qty = "1";
            if (!rate.isEmpty()) {
                try {
                    double q = Double.parseDouble(qty);
                    double r = Double.parseDouble(rate);
                    double amount = r * q;
                    String target = ((amount % 1) == 0) ? String.valueOf(Math.round(amount)) : String.format(Locale.ENGLISH, "%.2f", amount);
                    inputAmount.setText(target);
                } catch (NumberFormatException e) { inputAmount.setText(""); }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            watchingText = false;
        }
    };

    private TextWatcher qtyWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (watchingText)
                return;
            watchingText = true;
            String rate = inputRate.getText().toString();
            String qty = inputQty.getText().toString();
            if (qty.isEmpty())
                qty = "1";
            if (!rate.isEmpty()) {
                try {
                    double q = Double.parseDouble(qty);
                    double r = Double.parseDouble(rate);
                    double amount = r * q;
                    String target = ((amount % 1) == 0) ? String.valueOf(Math.round(amount)) : String.format(Locale.ENGLISH, "%.2f", amount);
                    inputAmount.setText(target);
                } catch (NumberFormatException e) { inputAmount.setText(""); }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            watchingText = false;
        }
    };

    private TextWatcher amountWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (watchingText)
                return;
            watchingText = true;
            String amount = inputAmount.getText().toString();
            String qty = inputQty.getText().toString();
            if (qty.isEmpty())
                qty = "1";
            if (!amount.isEmpty()) {
                try {
                    double a = Double.parseDouble(amount);
                    double q = Double.parseDouble(qty);
                    double rate = a / q;
                    String target = ((rate % 1) == 0) ? String.valueOf(Math.round(rate)) : String.format(Locale.ENGLISH, "%.2f", rate);
                    inputRate.setText(target);
                } catch (NumberFormatException e) { inputRate.setText(""); }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            watchingText = false;
        }
    };

    private class Method {
        static final int ADD = 1;
        static final int UPDATE = 2;
    }
}
