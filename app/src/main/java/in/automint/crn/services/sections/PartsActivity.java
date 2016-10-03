package in.automint.crn.services.sections;

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
import in.automint.crn.data.PartHolder;

/**
 * Activity to add, edit or remove Parts from Service
 * Created by ndkcha on 01/10/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class PartsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "PartsActivity";

    //  ui elements
    private PartDialog partDialog;
    private AppCompatTextView textTotalCost;

    //  backend elements
    public Adapter adapter = new Adapter();
    private LocData locData = new LocData();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_parts);

        partDialog = new PartDialog(this);
        locData.addServiceInstance(getApplicationContext());

        mapViews();
        loadDefaultValues();
    }

    private void mapViews() {
        ListViewCompat listParts = (ListViewCompat) findViewById(R.id.list_parts);
        AppCompatButton buttonAddPart = (AppCompatButton) findViewById(R.id.button_add_part);
        AppCompatButton buttonDone = (AppCompatButton) findViewById(R.id.button_done);
        textTotalCost = (AppCompatTextView) findViewById(R.id.text_part_cost);

        if (buttonAddPart != null)
            buttonAddPart.setOnClickListener(this);
        if (buttonDone != null)
            buttonDone.setOnClickListener(this);
        if (listParts != null)
            listParts.setAdapter(adapter);
    }

    private void loadDefaultValues() {
        JSONArray parts = locData.getParts();
        if (parts != null) {
            List<PartHolder> partHolders = new ArrayList<>();
            for (int i = 0; i < parts.length(); i++) {
                JSONObject item = parts.optJSONObject(i);
                String name = item.optString(PartHolder.FIELD_NAME);
                String rate = item.optString(PartHolder.FIELD_RATE);
                String qty = item.optString(PartHolder.FIELD_QTY);
                String amount = item.optString(PartHolder.FIELD_AMOUNT);
                partHolders.add(new PartHolder(Double.valueOf(rate), Double.valueOf(qty), Double.valueOf(amount), name));
            }
            adapter.animateTo(partHolders);
        }
        calculateCost();
    }

    public void calculateCost() {
        double total = 0;
        for (PartHolder holder : adapter.getHolderList()) {
            total += holder.amount;
        }
        String cost = ((total % 1) == 0) ? String.valueOf(Math.round(total)) : String.format(Locale.ENGLISH, "%.2f", total);
        if (textTotalCost != null)
            textTotalCost.setText(cost);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_add_part:
                partDialog.add();
                break;
            case R.id.button_done:
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        JSONArray array = new JSONArray();
        for (PartHolder holder : adapter.getHolderList()) {
            JSONObject object = new JSONObject();
            try {
                object.put(PartHolder.FIELD_NAME, holder.name);
                object.put(PartHolder.FIELD_RATE, holder.rate);
                object.put(PartHolder.FIELD_QTY, holder.qty);
                object.put(PartHolder.FIELD_AMOUNT, holder.amount);
                array.put(object);
            } catch (JSONException e) {
                Log.e(TAG, "error in holder");
            }
        }
        locData.storeParts(array);
        super.onBackPressed();
    }

    public class Adapter extends BaseAdapter implements View.OnClickListener {
        private List<PartHolder> holderList = new ArrayList<>();

        List<PartHolder> getHolderList() {
            return holderList;
        }

        void animateTo(List<PartHolder> items) {
            applyAndAnimateRemovals(items);
            applyAndAnimateAdditions(items);
            applyAndAnimateMovedItems(items);
        }

        private void applyAndAnimateRemovals(List<PartHolder> newItems) {
            for (int i = holderList.size() - 1; i >= 0; i--) {
                final PartHolder model = holderList.get(i);
                if (!newItems.contains(model)) {
                    removeItem(i);
                }
            }
        }

        private void applyAndAnimateAdditions(List<PartHolder> newItems) {
            for (int i = 0, count = newItems.size(); i < count; i++) {
                final PartHolder model = newItems.get(i);
                if (!holderList.contains(model)) {
                    addItem(i, model);
                }
            }
        }

        private void applyAndAnimateMovedItems(List<PartHolder> newItems) {
            for (int toPosition = newItems.size() - 1; toPosition >= 0; toPosition--) {
                final PartHolder model = newItems.get(toPosition);
                final int fromPosition = holderList.indexOf(model);
                if (fromPosition >= 0 && fromPosition != toPosition) {
                    moveItem(fromPosition, toPosition);
                }
            }
        }

        PartHolder removeItem(int position) {
            final PartHolder model = holderList.remove(position);
            notifyDataSetChanged();
            return model;
        }

        void addItem(int position, PartHolder item) {
            holderList.add(position, item);
            notifyDataSetChanged();
        }

        void moveItem(int fromPosition, int toPosition) {
            final PartHolder model = holderList.remove(fromPosition);
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
            PartHolder holder = holderList.get(position);
            View view = (convertView == null) ? ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_service_add_parts, parent, false) : convertView;

            AppCompatTextView textPartName = (AppCompatTextView) view.findViewById(R.id.text_part_name);
            AppCompatTextView textRate  = (AppCompatTextView) view.findViewById(R.id.text_part_rate);
            AppCompatTextView textQty = (AppCompatTextView) view.findViewById(R.id.text_part_qty);
            AppCompatTextView textAmount = (AppCompatTextView) view.findViewById(R.id.text_part_amount);

            textPartName.setText(holder.name);
            String rate = ((holder.rate % 1) == 0) ? String.valueOf(Math.round(holder.rate)) : String.format(Locale.ENGLISH, "%.2f", holder.rate);
            String qty = ((holder.qty % 1) == 0) ? String.valueOf(Math.round(holder.qty)) : String.format(Locale.ENGLISH, "%.2f", holder.qty);
            String amount = ((holder.amount % 1) == 0) ? String.valueOf(Math.round(holder.amount)) : String.format(Locale.ENGLISH, "%.2f", holder.amount);
            textRate.setText(rate);
            textQty.setText(qty);
            textAmount.setText(amount);

            view.setTag(holder);
            view.setOnClickListener(this);

            return view;
        }

        @Override
        public void onClick(View v) {
            PartHolder holder = (PartHolder) v.getTag();
            int index = holderList.indexOf(holder);
            partDialog.update(holder, index);
            Log.i(TAG, index + " " + holder.name);
        }
    }
}
