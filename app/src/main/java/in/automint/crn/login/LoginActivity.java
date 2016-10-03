package in.automint.crn.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

import in.automint.crn.R;
import in.automint.crn.data.MintConst;
import in.automint.crn.manage.CouchBaseLite;
import in.automint.crn.manage.UiElements;
import in.automint.crn.services.AddService;
import in.automint.crn.services.ViewServices;

/**
 * Entry level activity for Automint App
 * Created by ndkcha on 26/09/16.
 * @since 0.10.0
 * @version 0.10.0
 */

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "LoginActivity";

    //  define UI elements
    private AppCompatEditText inputUsername, inputPassword;
    private CardView cardLoginBox, cardLoginWait;
    private UiElements uiElements;

    //  define network and backend elements
    private RequestQueue requestQueue;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        CouchBaseLite.initManagerInstance(getApplicationContext());
        uiElements = new UiElements(this);
        mapViews();
        requestQueue = Volley.newRequestQueue(this);
    }

    //  Map view variables to UI and assign listeners
    private void mapViews() {
        inputUsername = (AppCompatEditText) findViewById(R.id.input_username);
        inputPassword = (AppCompatEditText) findViewById(R.id.input_password);

        cardLoginBox = (CardView) findViewById(R.id.card_login_box);
        cardLoginWait = (CardView) findViewById(R.id.card_login_wait);

        AppCompatButton buttonSubmit = ((AppCompatButton) findViewById(R.id.button_submit));


        if (buttonSubmit != null)
            buttonSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //  when submit button is clicked, initiate Login Process
            case R.id.button_submit:
//                if (inputUsername.getText().toString().isEmpty()) {
//                    uiElements.showSnackBar(R.string.message_username_null, Snackbar.LENGTH_SHORT);
//                    return;
//                }
//                if (inputPassword.getText().toString().isEmpty()) {
//                    uiElements.showSnackBar(R.string.message_password_null, Snackbar.LENGTH_SHORT);
//                    return;
//                }
                if ((cardLoginBox != null) && (cardLoginWait != null)) {
                    cardLoginBox.setVisibility(View.GONE);
                    cardLoginWait.setVisibility(View.VISIBLE);
                }
                uiElements.hideKeyboard(v);
                backdoor();
                break;
        }
    }

    private void backdoor() {
        String code = inputUsername.getText().toString();
        code = code.toUpperCase();
        StringRequest bdRequest = new StringRequest(Request.Method.GET, MintConst.Url.ACTIVATION_URL(code), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);
                startActivity(new Intent(LoginActivity.this, AddService.class));
                LoginActivity.this.finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null)
                    Log.e(TAG, error.toString());
                startActivity(new Intent(LoginActivity.this, AddService.class));
                LoginActivity.this.finish();

            }
        });
        requestQueue.add(bdRequest);
    }

    private void iteractWithServer() {
        String username = inputUsername.getText().toString();
        String password = inputPassword.getText().toString();
        final HashMap<String, String> bodyParams = new HashMap<>();
        bodyParams.put("name", username);
        bodyParams.put("password", password);
        StringRequest loginRequest = new StringRequest(Request.Method.POST, MintConst.Url.AUTH_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error != null)
                    Log.e(TAG, error.toString());
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                return bodyParams;
            }
        };
        requestQueue.add(loginRequest);
    }
}
