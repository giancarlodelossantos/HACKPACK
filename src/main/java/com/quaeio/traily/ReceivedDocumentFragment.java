package com.quaeio.traily;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.app.ListActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by simeon.garcia on 11/28/2017.
 */

public class ReceivedDocumentFragment extends Fragment {

    View myView;
    private static final String TAG = ReceivedDocumentFragment.class.getSimpleName();
    private ProgressDialog pDialog;
    private String employeeno = "";

    ListView list;

    ArrayList<Document> itemname = new ArrayList<Document>();



    Integer[] imgid = {
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
            R.mipmap.ic_launcher_round,
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        myView = inflater.inflate(R.layout.receiveddocuments_layout, container, false);

        // Progress dialog
        pDialog = new ProgressDialog(getActivity());
        pDialog.setCancelable(false);

        if(this.getArguments() != null){
            employeeno = this.getArguments().getString("employeeno");
            GetData(employeeno);
        }


        return myView;
    }

    private void GetData(final String employeeNo){
        getReceivedDocuments(employeeNo, new ServerCallback() {
            @Override
            public void onSuccess(JSONArray result) {
                // do stuff here
                try {

                    if (result.length() > 0) {

                        for (int i = 0; i < result.length(); i++) {

                            JSONObject jObject = result.getJSONObject(i);
                            String _DocumentNo = jObject.getString("DocumentNo");
                            String _Date = jObject.getString("Date");
                            String _Time = jObject.getString("Time");

                            Document item = new Document();
                            item.setDocumentNo(_DocumentNo);
                            item.setDate("Date Received "+ _Date);
                            item.setTime(_Time);

                            itemname.add(item);
                        }

                        //then display
                        CustomListAdapter adapter = new CustomListAdapter(getActivity(), itemname, imgid);
                        list = (ListView) myView.findViewById(R.id.list);
                        list.setAdapter(adapter);

                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view,
                                                    int position, long id) {
                                // TODO Auto-generated method stub
                                String Slecteditem = itemname.get(+position).getDocumentNo();
                                Toast.makeText(getActivity().getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

                            }
                        });

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
    }


    //private functions
    private void getReceivedDocuments(final String employeeno, final ServerCallback callback){
         pDialog.setMessage("Loading...");
         showDialog();
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());

        // Initialize a new JsonArrayRequest instance
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                AppConfig.URL_GETRECIEVEDDOCS + employeeno,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        hideDialog();
                        callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        // Do something when error occurred
                        hideDialog();
                        Toast.makeText(getActivity().getApplicationContext(), "Error encountered", Toast.LENGTH_LONG).show();
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
