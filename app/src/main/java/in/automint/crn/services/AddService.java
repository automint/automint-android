package in.automint.crn.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import in.automint.crn.R;
import in.automint.crn.data.LocData;
import in.automint.crn.data.PartHolder;
import in.automint.crn.data.TreatmentHolder;
import in.automint.crn.manage.UiElements;
import in.automint.crn.services.sections.CustomerDetailsDialog;
import in.automint.crn.services.sections.PartsActivity;
import in.automint.crn.services.sections.TreatmentsActivity;
import in.automint.crn.services.sections.VehicleDetailsDialog;

/**
 * Activity to display and manage Add Service Form
 * Created by ndkcha on 29/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class AddService extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AddService";

    //  define UI elements
    private AppCompatCheckBox checkboxPayment;
    private AppCompatTextView textCustomerDetails, textVehicleName, textServiceCost;
    private UiElements uiElements;
    private CustomerDetailsDialog customerDetailsDialog;
    private VehicleDetailsDialog vehicleDetailsDialog;

    //  backend elements
    private Map<String, String> customerDetails = new HashMap<>();
    private Map<String, String> vehicleDetails = new HashMap<>();
    private Adapter treatmentAdapter = new Adapter();
    private Adapter partsAdapter = new Adapter();
    private LocData tmpDataTreatments = new LocData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_add);

        uiElements = new UiElements(this);
        tmpDataTreatments.addServiceInstance(getApplicationContext());
        customerDetailsDialog = new CustomerDetailsDialog(this);
        vehicleDetailsDialog = new VehicleDetailsDialog(this);

        mapViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        JSONArray treatments = tmpDataTreatments.getTreatments();
        JSONArray parts = tmpDataTreatments.getParts();
        if (treatments != null) {
            List<Holder> treatmentHolders = new ArrayList<>();
            for (int i = 0; i < treatments.length(); i++) {
                JSONObject item = treatments.optJSONObject(i);
                String details = item.optString(TreatmentHolder.FIELD_DETAILS);
                String rate = item.optString(TreatmentHolder.FIELD_RATE);
                treatmentHolders.add(new Holder(Double.valueOf(rate), 0, Double.valueOf(rate), details, Holder.TYPE_TREATMENT));
            }
            treatmentAdapter.animateTo(treatmentHolders);
        }
        if (parts != null) {
            List<Holder> partHolders = new ArrayList<>();
            for (int i = 0; i < parts.length(); i++) {
                JSONObject item = parts.optJSONObject(i);
                String name = item.optString(PartHolder.FIELD_NAME);
                String rate = item.optString(PartHolder.FIELD_RATE);
                String qty = item.optString(PartHolder.FIELD_QTY);
                String amount = item.optString(PartHolder.FIELD_AMOUNT);
                partHolders.add(new Holder(Double.valueOf(rate), Double.valueOf(qty), Double.valueOf(amount), name, Holder.TYPE_PART));
            }
            partsAdapter.animateTo(partHolders);
        }
        calculateCost();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tmpDataTreatments.clear();
    }

    //  Map view variables to UI and assign listeners
    private void mapViews() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    uiElements.showSnackBar(R.string.app_name, Snackbar.LENGTH_SHORT);
                }
            });
        }

        checkboxPayment = (AppCompatCheckBox) findViewById(R.id.checkbox_payment);
        textCustomerDetails = (AppCompatTextView) findViewById(R.id.text_customer_details);
        textVehicleName = (AppCompatTextView) findViewById(R.id.text_vehicle_name);
        textServiceCost = (AppCompatTextView) findViewById(R.id.text_service_cost);

        ListViewCompat listTreatments = (ListViewCompat) findViewById(R.id.list_treatments);
        ListViewCompat listParts = (ListViewCompat) findViewById(R.id.list_parts);
        CardView cardCustomerBox = (CardView) findViewById(R.id.card_customer_box);
        CardView cardVehicleBox = (CardView) findViewById(R.id.card_vehicle_box);
        CardView cardTreatmentsBox = (CardView) findViewById(R.id.card_treatments_box);
        CardView cardPartsBox = (CardView) findViewById(R.id.card_parts_box);
        AppCompatButton buttonSave = (AppCompatButton) findViewById(R.id.button_save);

        if (cardCustomerBox != null)
            cardCustomerBox.setOnClickListener(this);
        if (cardVehicleBox != null)
            cardVehicleBox.setOnClickListener(this);
        if (cardTreatmentsBox != null)
            cardTreatmentsBox.setOnClickListener(this);
        if (cardPartsBox != null)
            cardPartsBox.setOnClickListener(this);
        if (buttonSave != null)
            buttonSave.setOnClickListener(this);
        if (listTreatments != null)
            listTreatments.setAdapter(treatmentAdapter);
        if (listParts != null)
            listParts.setAdapter(partsAdapter);
    }

    public void calculateCost() {
        double total = 0;
        for (Holder treatmentHolder : treatmentAdapter.getHolderList()) {
            total += treatmentHolder.amount;
        }
        for (Holder partHolder : partsAdapter.getHolderList()) {
            total += partHolder.amount;
        }
        String cost = ((total % 1) == 0) ? String.valueOf(Math.round(total)) : String.format(Locale.ENGLISH, "%.2f", total);
        textServiceCost.setText(cost);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_customer_box:
                customerDetailsDialog.show(textCustomerDetails, customerDetails);
                break;
            case R.id.card_vehicle_box:
                vehicleDetailsDialog.show(textVehicleName, vehicleDetails);
                break;
            case R.id.card_treatments_box:
                startActivity(new Intent(AddService.this, TreatmentsActivity.class));
                break;
            case R.id.card_parts_box:
                startActivity(new Intent(AddService.this, PartsActivity.class));
                break;
        }
    }

    private class Adapter extends BaseAdapter implements View.OnClickListener {
        private List<Holder> holderList = new ArrayList<>();

        List<Holder> getHolderList() {
            return holderList;
        }

        void animateTo(List<Holder> items) {
            applyAndAnimateRemovals(items);
            applyAndAnimateAdditions(items);
            applyAndAnimateMovedItems(items);
        }

        private void applyAndAnimateRemovals(List<Holder> newItems) {
            for (int i = holderList.size() - 1; i >= 0; i--) {
                final Holder model = holderList.get(i);
                if (!newItems.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<Holder> newItems) {
            for (int i = 0, count = newItems.size(); i < count; i++) {
                final Holder model = newItems.get(i);
                if (!holderList.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<Holder> newItems) {
            for (int toPosition = newItems.size() - 1; toPosition >= 0; toPosition--) {
                final Holder model = newItems.get(toPosition);
                final int fromPosition = holderList.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        Holder removeItem(int position) {
            final Holder model = holderList.remove(position);
            notifyDataSetChanged();
            return model;
        }

        void addItem(int position, Holder item) {
            holderList.add(position, item);
            notifyDataSetChanged();
        }

        void moveItem(int fromPosition, int toPosition) {
            final Holder model = holderList.remove(fromPosition);
            holderList.add(toPosition, model);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return holderList.size();
        }

        @Override
        public Object getItem(int position) {
            return holderList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = holderList.get(position);
            View view = (convertView == null) ? ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_service_treatments, parent, false) : convertView;

            AppCompatTextView textDetails = (AppCompatTextView) view.findViewById(R.id.text_details);
            AppCompatTextView textRate = (AppCompatTextView) view.findViewById(R.id.text_rate);
            AppCompatTextView textIndex = (AppCompatTextView) view.findViewById(R.id.text_index);
            AppCompatTextView textQty = (AppCompatTextView) view.findViewById(R.id.text_qty);
            AppCompatTextView textAmount = (AppCompatTextView) view.findViewById(R.id.text_amount);
            AppCompatTextView textCurrencyRate = (AppCompatTextView) view.findViewById(R.id.text_currency_rate);
            AppCompatTextView textX = (AppCompatTextView) view.findViewById(R.id.text_x);
            AppCompatTextView textEqual = (AppCompatTextView) view.findViewById(R.id.text_equal);

            String index = (position + 1) + ". ";
            textIndex.setText(index);
            textDetails.setText(holder.details);
            String rate = ((holder.rate % 1) == 0) ? String.valueOf(Math.round(holder.rate)) : String.format(Locale.ENGLISH, "%.2f", holder.rate);
            String qty = ((holder.qty % 1) == 0) ? String.valueOf(Math.round(holder.qty)) : String.format(Locale.ENGLISH, "%.2f", holder.qty);
            String amount = ((holder.amount % 1) == 0) ? String.valueOf(Math.round(holder.amount)) : String.format(Locale.ENGLISH, "%.2f", holder.amount);
            textRate.setText(rate);
            textQty.setText(qty);
            textAmount.setText(amount);

            if (holder.type.equals(Holder.TYPE_TREATMENT)) {
                textRate.setVisibility(View.GONE);
                textCurrencyRate.setVisibility(View.GONE);
                textX.setVisibility(View.GONE);
                textEqual.setVisibility(View.GONE);
                textQty.setVisibility(View.GONE);
            }

            view.setTag(holder);
            view.setOnClickListener(this);
            return view;
        }

        @Override
        public void onClick(View v) {
            Holder holder = (Holder) v.getTag();
            startActivity(new Intent(AddService.this, ((holder.type.equals(Holder.TYPE_TREATMENT)) ? TreatmentsActivity.class : PartsActivity.class)));
        }
    }

    private class Holder {
        static final String TYPE_TREATMENT = "treatment";
        static final String TYPE_PART = "part";
        double rate, qty, amount;
        String details, type;

        Holder(double rate, double qty, double amount, String details, String type) {
            this.type = type;
            this.qty = qty;
            this.amount = amount;
            this.rate = rate;
            this.details = details;
        }
    }
}
