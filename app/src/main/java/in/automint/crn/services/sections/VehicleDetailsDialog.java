package in.automint.crn.services.sections;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import in.automint.crn.R;

/**
 * Dialog to display and handle Vehicle Details in Add Service module
 * Created by ndkcha on 30/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class VehicleDetailsDialog implements View.OnClickListener, DialogInterface.OnDismissListener {
    private static final String TAG = "VehicleDetailsDialog";

    private Dialog dialog;
    private AppCompatTextView target;
    private AppCompatSpinner spinnerVehicleTypes;
    private Map<String, String> vehicleDetails;

    public VehicleDetailsDialog(Activity activity) {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_service_vehicledetails);
        dialog.setOnDismissListener(this);

        spinnerVehicleTypes = (AppCompatSpinner) dialog.findViewById(R.id.spinner_vehicle_type);
        AppCompatButton buttonDone = (AppCompatButton) dialog.findViewById(R.id.button_done);
        if (buttonDone != null)
            buttonDone.setOnClickListener(this);

        List<String> vehicleTypes = new ArrayList<>();
        vehicleTypes.clear();
        vehicleTypes.add("Bike");
        vehicleTypes.add("Car");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, R.layout.spinner_item, R.id.text_spinner_item, vehicleTypes);
        spinnerVehicleTypes.setAdapter(adapter);
    }

    public void show(AppCompatTextView target, Map<String, String> vehicleDetails) {
        this.target = target;
        this.vehicleDetails = vehicleDetails;
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
        AppCompatEditText inputVehicleManuf = (AppCompatEditText) this.dialog.findViewById(R.id.input_vehicle_manuf);
        AppCompatEditText inputVehicleModel = (AppCompatEditText) this.dialog.findViewById(R.id.input_vehicle_model);
        AppCompatEditText inputVehicleReg = (AppCompatEditText) this.dialog.findViewById(R.id.input_vehicle_reg);
        String vehicleName;
        String manuf = inputVehicleManuf.getText().toString();
        String model = inputVehicleModel.getText().toString();
        String reg = inputVehicleReg.getText().toString();
        if (!manuf.isEmpty() || !model.isEmpty()) {
            vehicleName = (!manuf.isEmpty()) ? manuf : "";
            vehicleName = vehicleName.concat((!model.isEmpty()) ? (" " + model) : "");
            vehicleName = vehicleName.concat((!reg.isEmpty()) ? " | " + reg : "");
        } else
            vehicleName = reg;
        if (vehicleName.isEmpty()) {
            target.setTypeface(Typeface.SANS_SERIF, Typeface.ITALIC);
            target.setText(R.string.message_click_to_add);
        } else {
            target.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            target.setText(vehicleName);
        }
        this.vehicleDetails.put("manuf", manuf);
        this.vehicleDetails.put("model", model);
        this.vehicleDetails.put("reg", reg);
        this.vehicleDetails.put("type", spinnerVehicleTypes.getSelectedItem().toString());
    }
}
