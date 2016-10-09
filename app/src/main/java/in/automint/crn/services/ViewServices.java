package in.automint.crn.services;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.QueryOptions;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import in.automint.crn.R;
import in.automint.crn.data.KeyNames;
import in.automint.crn.manage.CouchBaseLite;
import in.automint.crn.manage.UiElements;

/**
 * Activity to display all Services from Database
 * Created by ndkcha on 28/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class ViewServices extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ViewServices";

    //  ui elements
    private UiElements uiElements;

    //  backend elements
    private Adapter adapter = new Adapter();
    private Database database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_viewall);

        uiElements = new UiElements(this);
        database = CouchBaseLite.getDatabaseInstance();

        mapViews();
    }

    //  Map view variables to UI elements and assign listeners
    private void mapViews() {
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);
        ListViewCompat listServices = (ListViewCompat) findViewById(R.id.list_services);

        if (fabAdd != null)
            fabAdd.setOnClickListener(this);
        if (listServices != null)
            listServices.setAdapter(adapter);

        loadData();
    }

    private void loadData() {
        final List<Holder> holders = new ArrayList<>();
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                QueryOptions queryOptions = new QueryOptions();
                queryOptions.setIncludeDocs(true);
                queryOptions.setStartKey("usr");
                queryOptions.setEndKey("uss");
                try {
                    Map<String, Object> allDocs = database.getAllDocs(queryOptions);
                    if ((allDocs != null) && (!allDocs.isEmpty())) {
                        Log.i(TAG, allDocs.toString());
                        ArrayList list = (ArrayList) allDocs.get("rows");
                        for (Object object : list) {
                            QueryRow row = (QueryRow) object;
                            LinkedHashMap user = (LinkedHashMap) ((LinkedHashMap) row.asJSONDictionary().get("doc")).get("user");
                            LinkedHashMap vehicles = (LinkedHashMap) user.get("vehicles");
                            for (Object vId : vehicles.keySet()) {
                                LinkedHashMap vehicle = (LinkedHashMap) vehicles.get(vId);
                                LinkedHashMap services = (LinkedHashMap) vehicle.get("services");
                                for (Object sId : services.keySet()) {
                                    LinkedHashMap service = (LinkedHashMap) services.get(sId);
                                    String customerName = user.get(KeyNames.Customer.NAME).toString();
                                    if ((customerName == null) || (customerName.isEmpty()))
                                        customerName = user.get(KeyNames.Customer.MOBILE).toString();
                                    String manuf = vehicle.get(KeyNames.Vehicle.MANUFACTURER).toString();
                                    String model = vehicle.get(KeyNames.Vehicle.MODEL).toString();
                                    String vehicleName = ((manuf.isEmpty()) && (model.isEmpty())) ? vehicle.get(KeyNames.Vehicle.REG).toString() : (manuf + " " + model);
                                    String cost = service.get(KeyNames.Service.COST).toString();
                                    String date = service.get(KeyNames.Service.DATE).toString();
                                    String odo = service.get(KeyNames.Service.ODO).toString();
                                    Holder holder = new Holder(customerName, vehicleName, date, Double.valueOf(odo), Double.valueOf(cost));
                                    holders.add(holder);
                                }
                            }
                        }
                    }
                } catch (CouchbaseLiteException e) {
                    Log.e(TAG, "Error fetching data from database");
                }
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                adapter.animateTo(holders);
            }
        }.execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                startActivity(new Intent(ViewServices.this, AddService.class));
                break;
        }
    }

    private class Adapter extends BaseAdapter {
        private List<Holder> holderList = new ArrayList<>();

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
            View view = (convertView == null) ? ((LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE)).inflate(R.layout.item_services, parent, false) : convertView;

            AppCompatTextView textCustomerName = (AppCompatTextView) view.findViewById(R.id.text_customer_name);
            AppCompatTextView textVehicleName = (AppCompatTextView) view.findViewById(R.id.text_vehicle_name);
            AppCompatTextView textOdo = (AppCompatTextView) view.findViewById(R.id.text_odo);
            AppCompatTextView textDate = (AppCompatTextView) view.findViewById(R.id.text_date);
            AppCompatTextView textAmount = (AppCompatTextView) view.findViewById(R.id.text_amount);

            if (textCustomerName != null) {
                textCustomerName.setText(holder.customerName);
                textCustomerName.setVisibility((holder.customerName.equalsIgnoreCase("anonymous")) ? View.GONE : View.VISIBLE);
            }
            if (textVehicleName != null) {
                textVehicleName.setText(holder.vehicleName);
                textVehicleName.setVisibility((holder.vehicleName.equalsIgnoreCase("vehicle")) ? View.GONE : View.VISIBLE);
            }
            if (textOdo != null) {
                textOdo.setText(getString(R.string.text_odometer, String.valueOf(holder.odometer)));
                textOdo.setVisibility((holder.odometer > 0) ? View.VISIBLE : View.GONE);
            }
            if (textDate != null) {
                String date = uiElements.convertDbToDisplayFormat(holder.date);
                textDate.setText(date);
                textDate.setVisibility((date != null) ? View.VISIBLE : View.GONE);
            }
            if (textAmount != null)
                textAmount.setText(getString(R.string.text_amount, String.valueOf(holder.amount)));

            AppCompatImageView road = (AppCompatImageView) view.findViewById(R.id.icon_history_road);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) road.getLayoutParams();
            final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
            if (position == 0)
                layoutParams.setMargins(0, (int) (24f * scale + 0.5f), 0, 0);
            else if (position == holderList.size()-1) {
                layoutParams.setMargins(0, 0, 0, (int) (24f * scale + 0.5f));
                view.findViewById(R.id.view_separator).setVisibility(View.GONE);
            } else
                layoutParams.setMargins(0, 0, 0, 0);
            road.setVisibility((holderList.size() == 1) ? View.GONE : View.VISIBLE);
            road.setLayoutParams(layoutParams);

            return view;
        }
    }

    private class Holder {
        String customerName, vehicleName, date;
        double odometer, amount;

        public Holder(String customerName, String vehicleName, String date, double odometer, double amount) {
            this.customerName = customerName;
            this.vehicleName = vehicleName;
            this.date = date;
            this.odometer = odometer;
            this.amount = amount;
        }
    }
}
