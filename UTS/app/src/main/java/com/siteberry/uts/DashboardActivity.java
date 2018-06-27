package com.siteberry.uts;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
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
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.siteberry.UTSDto;
import com.siteberry.dao.User;
import com.siteberry.dto.RegisteredUser;
import com.siteberry.helper.MyOpenSQLiteHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TextView nameView,eMailView;
    private NavigationView navigationView;
    private MyOpenSQLiteHelper mosh;
    private User userDao;
    private ProgressDialog progressDialog;
    private Spinner sourceStations,destinationStations;
    private AppCompatButton proceed_and_review_button;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        navigationView = (NavigationView)findViewById(R.id.nav_view);
        View view = navigationView.getHeaderView(0);
        nameView = (TextView) view.findViewById(R.id.nameView);
        eMailView = (TextView) view.findViewById(R.id.eMailView);
        sourceStations = (Spinner)findViewById(R.id.sourceStation);
        destinationStations = (Spinner)findViewById(R.id.destinationStation);
        proceed_and_review_button = (AppCompatButton)findViewById(R.id.proceed_and_review_button);
        Log.i("appstate data",""+ UTSDto.userName);
//        Intent intent = getIntent();
        String sessionId = UTSDto.sessionId;
        Log.i("Appstate receiving",""+sessionId);
//        reg.setSessionId(sessionId); SET SESSION ID HERE, AS IN STATIC IT IS ALREADY SETTED
        Log.i("appstatesession",""+UTSDto.sessionId);
        String url[] = new String[2];
        url[0] = DashboardActivity.this.getString(R.string.get_stations);
        url[1] = DashboardActivity.this.getString(R.string.get_profile_data)+sessionId;
        Log.i("appstateurl",""+url[0]);
        new GetStations().execute(url[0]);
        new GetUserData().execute(url[1]);
        setSupportActionBar(toolbar);
        proceed_and_review_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sourceStations.getSelectedItemPosition() == destinationStations.getSelectedItemPosition()){
                    Toast.makeText(DashboardActivity.this, "Source and destination stations can't be same", Toast.LENGTH_SHORT).show();
                }else {
                    //PROCEED TO TICKET BOOKING
                }
            }
        });
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nameView.setText(UTSDto.userName);
        eMailView.setText(UTSDto.password);
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
        getMenuInflater().inflate(R.menu.dashboard, menu);
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
        final Intent[] intent = new Intent[1];

        if (id == R.id.nav_edit_profile) {
            intent[0] = new Intent(getApplicationContext(),EditActivity.class);
            startActivity(intent[0]);
        } else if (id == R.id.nav_booking_history) {
            intent[0] = new Intent(getApplicationContext(),BookingHistoryActivity.class);
            startActivity(intent[0]);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            intent[0] = new Intent(DashboardActivity.this,ShareActivity.class);
            startActivity(intent[0]);

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder alert = new AlertDialog.Builder(DashboardActivity.this);
            alert.setTitle("Alert!").setMessage("Are you sure you want to logout?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    intent[0] = new Intent(DashboardActivity.this,LoginActivity.class);
                    new GetUserData().execute("");
                    startActivity(intent[0]);
                    finish();
                }
            }).setNegativeButton("No",null).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;

    }
    public class GetStations extends AsyncTask<String,Void,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            progressDialog = new ProgressDialog(DashboardActivity.this);
            progressDialog.setProgressStyle(R.style.AppTheme);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String urlParams = null;
            InputStream stream;
            urlParams = strings[0];
            URL urL;
            HttpURLConnection connection;
            if (urlParams.equals(DashboardActivity.this.getString(R.string.destroy_session))) {

            } else{
                Log.i("appstate", "" + DashboardActivity.this.getString(R.string.get_stations));
            try {
                urL = new URL(urlParams);
                connection = (HttpURLConnection) urL.openConnection();
                connection.setRequestMethod("GET");
                stream = connection.getInputStream();
                int ch = -1;
                while ((ch = stream.read()) != -1) {
                    result += (char) ch;
                }
                Log.i("APPSTATEdata", "" + result);
                connection.disconnect();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                result = "IO ERROR OCCURED";
                e.printStackTrace();
            }
        }
            urlParams = null;
            return result;
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            List<String> stations = new ArrayList<String>();
            try {
                JSONArray data = new JSONArray(s);
                for (int i=0;i<data.length();i++){
                    JSONObject station = data.getJSONObject(i);
                    String temp = station.getString("sname");
                    stations.add(temp);
                }
                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(DashboardActivity.this,android.R.layout.simple_spinner_dropdown_item,stations);
                sourceStations.setAdapter(arrayAdapter);
                destinationStations.setAdapter(arrayAdapter);
                progressDialog.dismiss();
                Log.i("appstateresult[1]",""+s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public class GetUserData extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String urlParams = strings[0];
            URL url;
            HttpURLConnection connection;
            InputStream stream;
            Log.i("appstateurl", "" + urlParams);
            if (strings[0].equals("")) {
                urlParams = DashboardActivity.this.getString(R.string.destroy_session) + UTSDto.sessionId;
                try {
                    url = new URL(urlParams);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    Log.i("appstate","destroying session");
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    Log.i("APPSTATE", "WORKING TILL HERE WRITER");
                    writer.write("[]");//SENDING JSON DATA TO SERVER.
                    writer.flush();
                    stream = connection.getInputStream();//GETTING RESPONSE FROM SERVER.
                    int ch = -1;
                    while ((ch = stream.read()) != -1) {
                        result += (char) ch;
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

            try {
                url = new URL(urlParams);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                stream = connection.getInputStream();
                int ch = -1;
                while ((ch = stream.read()) != -1) {
                    result += (char) ch;
                }
                Log.i("APPSTATEdata", "" + result);
                connection.disconnect();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
            Log.i("appstatedes",""+result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONArray profile = new JSONArray(s);
                JSONObject profile_data = profile.getJSONObject(0);
                String email = profile_data.getString("email");
                String name = profile_data.getString("first_name")+" "+profile_data.getString("last_name");
                eMailView.setText(email);
                nameView.setText(name);
                UTSDto.fName = profile_data.getString("first_name");
                UTSDto.lName = profile_data.getString("last_name");
                UTSDto.mobileNumber = profile_data.getString("phone");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
