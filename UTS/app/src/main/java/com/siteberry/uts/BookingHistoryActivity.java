package com.siteberry.uts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.siteberry.UTSDto;
import com.siteberry.dto.RegisteredUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.siteberry.uts.R.id.booking_history_view;

public class BookingHistoryActivity extends AppCompatActivity {
    private ListView bookingHistoryView;
    private String ticketId[];
    private String JSONData = "";
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);
        bookingHistoryView = (ListView)findViewById(booking_history_view);
//        Intent intent = getIntent();
        Log.i("appstatesessionid",""+ UTSDto.sessionId);
        String sessionId = UTSDto.sessionId;
        View header = (View)getLayoutInflater().inflate(R.layout.booking_history_header,null);
        bookingHistoryView.addHeaderView(header);
        new GetHistory().execute(sessionId);
    }
    public class GetHistory extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            String urlParams = BookingHistoryActivity.this.getString(R.string.get_booking_history)+strings[0];
            String result = "";
            try {
                URL url = new URL(urlParams);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                InputStream stream = connection.getInputStream();//GETTING RESPONSE FROM SERVER.
                int ch=-1;
                while ((ch=stream.read())!=-1){
                    result += (char)ch;
                }
                Log.i("appstateresult",""+result);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
                JSONData = s;
                Log.i("appstatejsn",""+JSONData);
                new GetTicketQr().execute(JSONData);
            }
        }

    public class GetTicketQr extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(BookingHistoryActivity.this);
            progressDialog.setProgressStyle(R.style.AppTheme);
            progressDialog.setMessage("Loading..");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String data = strings[0];
            try {
                JSONArray ticketData = new JSONArray(data);
                HttpURLConnection connection;
                InputStream stream;
                Bitmap bm;
                final BookingHistory tickets[] = new BookingHistory[ticketData.length()];
                Log.i("appstatebm","creating bitmap qr");
                ticketId = new String[ticketData.length()];
                for (int i=0;i<ticketData.length();i++){
                    JSONObject ticketDetails = ticketData.getJSONObject(i);
                    String sourceStation = ticketDetails.getString("source");
                    String destinationStation= ticketDetails.getString("destination");
                    Log.i("appstateTID",""+ticketId[i]);
                    URL url = new URL(BookingHistoryActivity.this.getString(R.string.get_qr)+"&chl="+ticketId[i]+ "&chs=200x200&chld=L|0");
                    connection = (HttpURLConnection)url.openConnection();
                    connection.setRequestMethod("GET");
                    stream = connection.getInputStream();
                    BufferedInputStream bis = new BufferedInputStream(stream);
                    bm = BitmapFactory.decodeStream(bis);
                    bis.close();
                    Log.i("appstatebmp",""+bm.toString());
                    tickets[i] = new BookingHistory(bm,sourceStation,destinationStation);
                    Log.i("appstate","in loop");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.i("appstatetickets",""+tickets[1].sourceStation);
                        BookingHistoryAdapter adapter = new BookingHistoryAdapter(BookingHistoryActivity.this,R.layout.booking_history_row,tickets);
                        bookingHistoryView.setSelector(R.drawable.booking_selector);
                        bookingHistoryView.setAdapter(adapter);
                        progressDialog.dismiss();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
