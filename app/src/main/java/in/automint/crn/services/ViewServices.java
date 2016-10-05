package in.automint.crn.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.QueryOptions;
import com.couchbase.lite.util.Log;

import java.util.HashMap;
import java.util.Map;

import in.automint.crn.R;
import in.automint.crn.manage.CouchBaseLite;

/**
 * Activity to display all Services from Database
 * Created by ndkcha on 28/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class ViewServices extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ViewServices";

    //  backend elements
    private Database database;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_viewall);

        database = CouchBaseLite.getDatabaseInstance();
        mapViews();

        int count = database.getDocumentCount();
        QueryOptions options = new QueryOptions();
        options.setIncludeDocs(true);
        Map<String, Object> docs;
        try {
            docs = database.getAllDocs(options);
            Log.i(TAG, docs.toString());
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, e.getMessage());
        }
        Log.i(TAG, "docCount: " + count);
    }

    //  Map view variables to UI elements and assign listeners
    private void mapViews() {
        FloatingActionButton fabAdd = (FloatingActionButton) findViewById(R.id.fab_add);

        if (fabAdd != null)
            fabAdd.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fab_add:
                startActivity(new Intent(ViewServices.this, AddService.class));
                break;
        }
    }
}
