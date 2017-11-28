package com.quaeio.traily;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.quaeio.traily.SQLiteHandler;
import com.quaeio.traily.SessionManager;

import java.net.URL;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import me.pushy.sdk.Pushy;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String deviceToken;
    private String employeeno = "";
    private String employeetypeid = "";
    private String locationid = "";
    private SQLiteHandler db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Register first for notification
        //new RegisterForPushNotificationsAsync().execute();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        employeeno = user.get("employeeno");
        employeetypeid = user.get("employeetypeid");
        locationid = user.get("locationid");

        //set menu visibility
        if (employeetypeid.equals("1")) {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_approved_layout).setVisible(false);

            navigationView.setCheckedItem(R.id.nav_received_layout);
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
        } else {
            Menu nav_Menu = navigationView.getMenu();
            nav_Menu.findItem(R.id.nav_received_layout).setVisible(false);

            navigationView.setCheckedItem(R.id.nav_approved_layout);
            onNavigationItemSelected(navigationView.getMenu().getItem(1));
        }


        View hView = navigationView.getHeaderView(0);
        TextView nav_user = (TextView) hView.findViewById(R.id.txtEmployeeNo);
        nav_user.setText(employeeno);

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
    }

    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_received_layout) {
            Bundle bundle = new Bundle();
            bundle.putString("employeeno", employeeno);
            bundle.putString("employeetypeid", employeetypeid);
            bundle.putString("locationid", locationid);

            ReceivedDocumentFragment receivedDocumentFragment = new ReceivedDocumentFragment();
            receivedDocumentFragment.setArguments(bundle);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, receivedDocumentFragment);
            transaction.commit();
        } else if (id == R.id.nav_approved_layout) {
            Bundle bundle = new Bundle();
            bundle.putString("employeeno", employeeno);
            bundle.putString("employeetypeid", employeetypeid);
            bundle.putString("locationid", locationid);

            ApprovedDocumentFragment approvedDocumentFragment = new ApprovedDocumentFragment();
            approvedDocumentFragment.setArguments(bundle);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, approvedDocumentFragment);
            transaction.commit();
        } else if (id == R.id.nav_salaryloan_layout) {
            Bundle bundle = new Bundle();
            bundle.putString("employeeno", employeeno);
            bundle.putString("employeetypeid", employeetypeid);
            bundle.putString("locationid", locationid);

            SalaryLoanFragment salaryLoanFragment = new SalaryLoanFragment();
            salaryLoanFragment.setArguments(bundle);

            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.content_frame, salaryLoanFragment);
            transaction.commit();
        } else if (id == R.id.nav_logout) {
            logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Pushy object
    private class RegisterForPushNotificationsAsync extends AsyncTask<Void, Void, Exception> {
        protected Exception doInBackground(Void... params) {
            try {
                // Assign a unique token to this device
                deviceToken = Pushy.register(getApplicationContext());

                // Log it for debugging purposes
                Log.d("MyApp", "Pushy device token: " + deviceToken);

                // Send the token to your backend server via an HTTP GET request
                new URL("https://api.pushy.me/register/device?token=" + deviceToken).openConnection();

            } catch (Exception exc) {
                // Return exc to onPostExecute
                return exc;
            }

            // Success
            return null;
        }


        protected void onPostExecute(Exception exc) {
            // Failed?
            if (exc != null) {
                // Show error as toast message
                Toast.makeText(getApplicationContext(), "Register Error: " + exc.toString(), Toast.LENGTH_LONG).show();
                return;
            } else {
                Toast.makeText(getApplicationContext(), "Device Token: " + deviceToken, Toast.LENGTH_LONG).show();
            }
            // Succeeded, do something to alert the user
        }
    }
}
