package com.quaeio.traily;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.quaeio.traily.AppConfig;
import com.quaeio.traily.AppController;
import com.quaeio.traily.SQLiteHandler;
import com.quaeio.traily.SessionManager;

public class LoginActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private Button btnLogin;
    private EditText inputEmployeeNo;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmployeeNo = (EditText) findViewById(R.id.employeeno);
        inputPassword = (EditText) findViewById(R.id.password);
        btnLogin = (Button) findViewById(R.id.btnLogin);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String employeeno = inputEmployeeNo.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                // Check for empty data in the form
                if (!employeeno.isEmpty() && !password.isEmpty()) {
                    // login user
                    checkLogin(employeeno, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }

        });

        // Link to Register Screen


    }

    /**
     * function to verify login details in mysql db
     * */
    private void checkLogin(final String employeeno, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage("Logging in ...");
        showDialog();

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("employeeno", employeeno);
        params.put("password", password);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Method.POST, AppConfig.URL_LOGIN, new JSONObject(params), new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Login Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = response;

                        // user successfully logged in
                        // Create login session
                        session.setLogin(true);

                        // Now store the user in SQLite
                        String employeeauthid = jObj.getString("employeeauthid");
                        String employeeno = jObj.getString("employeeno");
                        String locationid = jObj.getString("locationid");
                        String employeetypeid = jObj.getString("employeetypeid");
                        String createddate = jObj.getString("createddate");

                        //pass employeetypeid to MainActivity
                         //Bundle bundle = new Bundle();
                         //bundle.putString("employeetypeid", employeetypeid);

                        // Inserting row in users table
                        db.addUser(employeeno, locationid, employeetypeid, employeeauthid, createddate);

                        // Launch main activity
                        Intent intent = new Intent(LoginActivity.this,
                                MainActivity.class);
                        startActivity(intent);
                        finish();

                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());

                Toast.makeText(getApplicationContext(),
                        "Invalid Employee No. or Password", Toast.LENGTH_LONG).show();
                hideDialog();
            }
        })

        {
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
