package com.siteberry.uts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
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
//import butterknife.Bind;

public class SignupActivity extends AppCompatActivity {
//    @Bind(R.id.progressBar) ProgressBar progressBar;
    private TextView loginBack;
    private EditText inputFname,inputLname,inputEmail,inputPassword,inputReEnterPassword,inputMobile,inputAnswer;
    private ProgressDialog progressDialog;
    private AppCompatButton signupButton;
    private Spinner securityQuestion;
    private String fName,lName,eMail,password,reEnterPassword,mobileNumber,securityAnswer,qid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        String url = "http://utswebservice.azurewebsites.net/api/index.php/get/ques/all";
        signupButton = (AppCompatButton)findViewById(R.id.button_signup);
        securityQuestion = (Spinner)findViewById(R.id.securityQuestions);
        loginBack = (TextView)findViewById(R.id.link_login_back);
        inputEmail = (EditText)findViewById(R.id.input_email_signup);
        inputFname = (EditText)findViewById(R.id.input_fname);
        inputLname = (EditText)findViewById(R.id.input_lname);
        inputPassword = (EditText)findViewById(R.id.input_password_signup);
        inputReEnterPassword = (EditText)findViewById(R.id.input_password_signup_confirm);
        inputMobile = (EditText)findViewById(R.id.input_mobile);
        inputAnswer = (EditText)findViewById(R.id.input_answer);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fName = inputFname.getText().toString();
                lName = inputLname.getText().toString();
                eMail = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                reEnterPassword = inputReEnterPassword.getText().toString();
                mobileNumber = inputMobile.getText().toString();
                securityAnswer = inputAnswer.getText().toString();
                int q = (securityQuestion.getSelectedItemPosition()+1);
                qid = Integer.toString(q);
                if(password.equals(reEnterPassword)) {
                    new Validate().execute(fName, lName, eMail, password, mobileNumber,securityAnswer,qid);//CALLING ASYNCTASK TO VALIDATE EMAIL ADDRESS.
                }
                else {
                    Toast.makeText(SignupActivity.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                }

            }
        });
        loginBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginBack = new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(loginBack);
                finish();
            }
        });
        AsyncTask asyncTask = new MyAsyncTask();
        asyncTask.execute();//SYNCING SECURITY QUESTIONS FRONM DATABASE
    }

public class MyAsyncTask extends AsyncTask<Object,Object,String>{
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(SignupActivity.this);
        progressDialog.setProgressStyle(R.style.AppTheme);
        progressDialog.setMessage("Syncing...");
        progressDialog.show();
        progressDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    protected String doInBackground(Object...objects) {
        String urlParams = "http://utswebservice.azurewebsites.net/api/index.php/get/ques/all";
        String result ="";
        try{
            URL url = new URL(urlParams);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            InputStream stream = connection.getInputStream();
            int ch=-1;
            while ((ch=stream.read())!=-1){
                result += (char)ch;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            result="ERROR IO";
            Toast.makeText(SignupActivity.this, "IO ERROR OCCURED", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        List<String> list = new ArrayList<String>();
        try {
            JSONArray questions = new JSONArray(s);
            for (int i=0;i<questions.length();i++){
                JSONObject question = questions.getJSONObject(i);
                String q = question.getString("ques");
                list.add(q);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                    SignupActivity.this,
                    android.R.layout.simple_spinner_dropdown_item,
                    list
            );
            securityQuestion.setAdapter(adapter);
            progressDialog.dismiss();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    }
    public class Validate extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(SignupActivity.this);
            progressDialog.setProgressStyle(R.style.AppTheme);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            String fName,lName,eMail,password,mobileNumber,securityAnswer,qid;
            fName = strings[0];
            lName = strings[1];
            eMail = strings[2];
            password = strings[3];
            mobileNumber = strings[4];
            securityAnswer = strings[5];
            qid = strings[6];
            String url = "https://utswebservice.azurewebsites.net/api/index.php/get/check/"+eMail;
            try {
                URL urlThrow = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) urlThrow.openConnection();
                connection.setRequestMethod("GET");
                InputStream stream = connection.getInputStream();
                int ch=-1;
                while ((ch=stream.read())!=-1){
                    result += (char)ch;
                }
                Log.i("APPSTATEdata",""+result);
                connection.disconnect();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                JSONObject isTaken = new JSONObject(result);
                String check = isTaken.getString("isTaken");
                if(check.equals("true")){
                    Log.i("APPSTATE","email taken");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SignupActivity.this, "This email is already registered.", Toast.LENGTH_LONG).show();
                        }
                    });

                }
                else if (check.equals("false")){
                    url = "https://utswebservice.azurewebsites.net/api/index.php/post/newuser";
                    URL urlThrow = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) urlThrow.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("Accept", "application/json");
                    OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                    Log.i("APPSTATE","WORKING TILL HERE WRITER");
                    JSONObject signupData = new JSONObject();
                    signupData.put("email",eMail);
                    signupData.put("first_name",fName);
                    signupData.put("last_name",lName);
                    signupData.put("pass",password);
                    signupData.put("qid",qid);
                    signupData.put("ans",securityAnswer);
                    Log.i("APPSTATE JSON DATA",""+signupData.toString());
                    writer.write(signupData.toString());//SENDING JSON DATA TO SERVER.
                    writer.flush();
                    InputStream stream = connection.getInputStream();//GETTING RESPONSE FROM SERVER.
                    int ch=-1;
                    while ((ch=stream.read())!=-1){
                        result += (char)ch;
                    }
                    Log.i("APPSTATE JSON DATA",""+result);
                    writer.close();
                    Log.i("appstate result",""+result);

                }
                else {
                    Toast.makeText(SignupActivity.this, "Some error occured, Please try after sometime.", Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
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
            progressDialog.dismiss();
            Intent intent = new Intent(SignupActivity.this,SignupActivity.class);
            startActivity(intent);
            finish();
        }
    }

}

