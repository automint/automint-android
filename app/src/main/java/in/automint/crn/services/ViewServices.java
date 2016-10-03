package in.automint.crn.services;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import in.automint.crn.R;

/**
 * Activity to display all Services from Database
 * Created by ndkcha on 28/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class ViewServices extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ViewServices";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_viewall);

        mapViews();
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
