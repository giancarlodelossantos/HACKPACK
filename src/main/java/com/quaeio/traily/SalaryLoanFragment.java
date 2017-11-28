package com.quaeio.traily;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by simeon.garcia on 11/26/2017.
 */

public class SalaryLoanFragment extends Fragment {

    View myView;
    private static final String TAG = SalaryLoanFragment.class.getSimpleName();
    private Button btnTransaction;
    private EditText txtDocumentId;
    private ProgressDialog pDialog;
    private String employeeno = "";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.salaryloan_layout, container, false);

        //init
        btnTransaction = (Button) myView.findViewById(R.id.btnTransaction);
        txtDocumentId = (EditText) myView.findViewById(R.id.txtDocumentID);

        // Progress dialog
        //pDialog = new ProgressDialog(getActivity().getApplicationContext());
        //pDialog.setCancelable(false);

        if(this.getArguments() != null){
            employeeno = this.getArguments().getString("employeeno");
            final String employeetypeid = this.getArguments().getString("employeetypeid");
            final String locationid = this.getArguments().getString("locationid");

            if(employeetypeid.equals("1")){
                //set text
                btnTransaction.setText("Recieve");
                btnTransaction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //call recieve function here
                        Recieve(txtDocumentId.getText().toString(), locationid);
                    }
                });
            }else{
                btnTransaction.setText("Approve");
                btnTransaction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //call approve function here
                        Approve(txtDocumentId.getText().toString(), locationid);
                    }
                });
            }
        }

        return myView;
    }

    //Private methods
    private void Recieve(final String documentId, final String locationId) {
        //business logic
        //check if already received the same documentid and department
        checkTransaction(documentId, new ServerCallback() {
                    @Override
                    public void onSuccess(JSONArray response) {
                        // do stuff here
                        try {

                            if (response.length() > 0) {

                                for (int i = 0; i < response.length(); i++) {

                                    JSONObject jObject = response.getJSONObject(i);
                                    String _locationid = jObject.getString("locationid");
                                    String _statusid = jObject.getString("statusid");
                                    String _documentqueueid = jObject.getString("documentqueueid");

                                    if (_statusid.equals("1")) {
                                        UpdateStatus(_documentqueueid,locationId,"2",employeeno);
                                        //Toast.makeText(getActivity().getApplicationContext(), documentId + " has been received!", Toast.LENGTH_LONG).show();
                                    } else if (_statusid.equals("2") && _locationid.equals(locationId)) {

                                        Toast.makeText(getActivity().getApplicationContext(), "Sorry this document has already received by this location", Toast.LENGTH_LONG).show();
                                    } else if (_statusid.equals("3") && _locationid.equals(locationId)) {

                                        Toast.makeText(getActivity().getApplicationContext(), "Sorry this document has already approved by this location", Toast.LENGTH_LONG).show();
                                    } else if (_statusid.equals("3") && !_locationid.equals(locationId)) {
                                        UpdateStatus(_documentqueueid,locationId,"2",employeeno);
                                        //Toast.makeText(getActivity().getApplicationContext(), documentId + " has been received!", Toast.LENGTH_LONG).show();
                                    }
                                }

                            } else {
                                Toast.makeText(getActivity().getApplicationContext(), "Not yet encoded", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
        //if not
        //recieved it
        //if yes
        //say that it is already recieved by this department
    }

    //update status
    private void UpdateStatus(String documentqueueId, String newlocationid, String newstatus, String employeeno){
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("employeeno", employeeno);
        params.put("documentqueueid", documentqueueId);
        params.put("locationid", newlocationid);
        params.put("statusid", newstatus);

        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, AppConfig.URL_UPDATESTATUS, new JSONObject(params), new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "Login Response: " + response.toString());

                        try {
                            JSONObject jObj = response;
                            String result = jObj.getString("result");
                            Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            // JSON error
                            e.printStackTrace();
                            Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "Login Error: " + error.getMessage());

                        Toast.makeText(getActivity().getApplicationContext(),
                                "Cannot update status", Toast.LENGTH_LONG).show();
                    }
                })
        {
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jsObjRequest);
    }

    private void Approve(final String documentId, final String locationId){
        //business logic
        //check if already approved the same documentid and department

        //if not
        //recieved it
        checkTransaction(documentId, new ServerCallback() {
            @Override
            public void onSuccess(JSONArray response) {
                // do stuff here
                try {

                    if (response.length() > 0) {

                        for (int i = 0; i < response.length(); i++) {

                            JSONObject jObject = response.getJSONObject(i);
                            String _locationid = jObject.getString("locationid");
                            String _statusid = jObject.getString("statusid");
                            String _documentqueueid = jObject.getString("documentqueueid");

                            if (_statusid.equals("1")) {

                                Toast.makeText(getActivity().getApplicationContext(), "Sorry this document has not yet received by this location", Toast.LENGTH_LONG).show();
                            } else if (_statusid.equals("3") && _locationid.equals(locationId)) {

                                Toast.makeText(getActivity().getApplicationContext(), "Sorry this document has already approved by this location", Toast.LENGTH_LONG).show();
                            } else if (_statusid.equals("2") && _locationid.equals(locationId)) {
                                UpdateStatus(_documentqueueid,locationId,"3",employeeno);
                                //Toast.makeText(getActivity().getApplicationContext(), documentId + " has been approved!", Toast.LENGTH_LONG).show();
                            } else if (_statusid.equals("2") && !_locationid.equals(locationId)) {

                                Toast.makeText(getActivity().getApplicationContext(), "Sorry this document has not yet received by this location", Toast.LENGTH_LONG).show();
                            }
                        }

                    } else {
                        Toast.makeText(getActivity().getApplicationContext(), "Not yet encoded", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getActivity().getApplicationContext(), "Json error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        //if yes
        //say that it is already approved by this department
    }


    private void checkTransaction(final String documentId, final ServerCallback callback){

        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                AppConfig.URL_CHECKTRANSACTION + documentId+ "&documenttypeid=1",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                       callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        Toast.makeText(getActivity().getApplicationContext(), "Not yet encoded", Toast.LENGTH_LONG).show();
                    }
                }
        );

        // Add JsonArrayRequest to the RequestQueue
        requestQueue.add(jsonArrayRequest);
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
