package in.automint.crn.services.sections;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import in.automint.crn.R;
import in.automint.crn.data.LocData;
import in.automint.crn.data.TreatmentHolder;

/**
 * Activity to add, edit or remove Treatments from Service
 * Created by ndkcha on 30/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class TreatmentsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "TreatmentsActivity";

    //  ui elements
    private TreatmentDialog treatmentDialog;
    private AppCompatTextView textTotalCost;

    //  backend elements
    public Adapter adapter = new Adapter();
    private LocData locData = new LocData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_treatments);

        treatmentDialog = new TreatmentDialog(this);
        locData.addServiceInstance(getApplicationContext());

        mapViews();
        loadDefaultValues();
    }

    private void mapViews() {
        ListViewCompat listTreatments = (ListViewCompat) findViewById(R.id.list_treatments);
        AppCompatButton buttonAddTreatment = (AppCompatButton) findViewById(R.id.button_add_treatment);
        AppCompatButton buttonDone = (AppCompatButton) findViewById(R.id.button_done);
        textTotalCost = (AppCompatTextView) findViewById(R.id.text_treatment_cost);

        if (buttonAddTreatment != null)
            buttonAddTreatment.setOnClickListener(this);
        if (buttonDone != null)
            buttonDone.setOnClickListener(this);
        if (listTreatments != null)
            listTreatments.setAdapter(adapter);
    }

    private void loadDefaultValues() {
        JSONArray treatments = locData.getTreatments();
        if (treatments != null) {
            List<TreatmentHolder> treatmentHolders = new ArrayList<>();
            for (int i = 0; i < treatments.length(); i++) {
                JSONObject item = treatments.optJSONObject(i);
                String details = item.optString(TreatmentHolder.FIELD_DETAILS);
                String rate = item.optString(TreatmentHolder.FIELD_RATE);
                treatmentHolders.add(new TreatmentHolder(Double.valueOf(rate), details));
            }
            adapter.animateTo(treatmentHolders);
        }
        calculateCost();
    }

    public void calculateCost() {
        double total = 0;
        for (TreatmentHolder holder : adapter.getHolderList()) {
            total += holder.rate;
        }
        String cost = ((total % 1) == 0) ? String.valueOf(Math.round(total)) : String.format(Locale.ENGLISH, "%.2f", total);
        if (textTotalCost != null)
            textTotalCost.setText(cost);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_treatment:
                treatmentDialog.add();
                break;
            case R.id.button_done:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        JSONArray array = new JSONArray();
        for (TreatmentHolder holder : adapter.getHolderList()) {
            JSONObject object = new JSONObject();
            try {
                object.put(TreatmentHolder.FIELD_DETAILS, holder.details);
                object.put(TreatmentHolder.FIELD_RATE, holder.rate);
                array.put(object);
            } catch (JSONException e) {
                Log.e(TAG, "error in holder");
            }
        }
        locData.storeTreatments(array);
        super.onBackPressed();
    }

    public class Adapter extends BaseAdapter implements View.OnClickListener {
        private List<TreatmentHolder> holderList = new ArrayList<>();

        List<TreatmentHolder> getHolderList() {
            return holderList;
        }

        void animateTo(List<TreatmentHolder> items) {
            applyAndAnimateRemovals(items);
            applyAndAnimateAdditions(items);
            applyAndAnimateMovedItems(items);
        }

        private void applyAndAnimateRemovals(List<TreatmentHolder> newItems) {
            for (int i = holderList.size() - 1; i >= 0; i--) {
                final TreatmentHolder model = holderList.get(i);
                if (!newItems.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<TreatmentHolder> newItems) {
            for (int i = 0, count = newItems.size(); i < count; i++) {
                final TreatmentHolder model = newItems.get(i);
                if (!holderList.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<TreatmentHolder> newItems) {
            for (int toPosition = newItems.size() - 1; toPosition >= 0; toPosition--) {
                final TreatmentHolder model = newItems.get(toPosition);
                final int fromPosition = holderList.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        TreatmentHolder removeItem(int position) {
            final TreatmentHolder model = holderList.remove(position);
            notifyDataSetChanged();
            return model;
        }

        void addItem(int position, TreatmentHolder item) {
            holderList.add(position, item);
            notifyDataSetChanged();
        }

        void moveItem(int fromPosition, int toPosition) {
            final TreatmentHolder model = holderList.remove(fromPosition);
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
            TreatmentHolder holder = holderList.get(position);
            View view = (convertView == null) ? ((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_service_add_treatments, parent, false) : convertView;

            AppCompatTextView textDetails = (AppCompatTextView) view.findViewById(R.id.text_treatment_details);
            AppCompatTextView textCost = (AppCompatTextView) view.findViewById(R.id.text_treatment_cost);

            textDetails.setText(holder.details);
            String cost = ((holder.rate % 1) == 0) ? String.valueOf(Math.round(holder.rate)) : String.format(Locale.ENGLISH, "%.2f", holder.rate);
            textCost.setText(cost);

            view.setTag(holder);
            view.setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View v) {
            TreatmentHolder holder = (TreatmentHolder) v.getTag();
            int index = holderList.indexOf(holder);
            treatmentDialog.update(holder, index);
            Log.i(TAG, holder.details);
        }
    }
}
