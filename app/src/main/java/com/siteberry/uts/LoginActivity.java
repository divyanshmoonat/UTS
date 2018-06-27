package com.siteberry.uts;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.siteberry.dao.User;
import com.siteberry.dto.RegisteredUser;
import com.siteberry.helper.MyOpenSQLiteHelper;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    private EditText inputEmail,inputPassword;
    private CheckBox remember;
    private Button login;
    private TextView signup_link;
    private String userName,password,sessionId;
    private SharedPreferences sp;
    private ProgressDialog loading;
    private RegisteredUser reg = new RegisteredUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inputEmail = (EditText)findViewById(R.id.input_email);
        inputPassword = (EditText)findViewById(R.id.input_password);
        remember = (CheckBox)findViewById(R.id.remember);
        login = (Button)findViewById(R.id.btn_login);
        signup_link = (TextView)findViewById(R.id.link_signup);
        remember.setChecked(true);
        sp = getSharedPreferences("profile",MODE_PRIVATE);
        sessionId = sp.getString("sessionId","");
        userName = sp.getString("userName","");
        password = sp.getString("password","");
        if (userName.equals("")&&password.equals("")){
            Toast.makeText(this, "No Stored data found", Toast.LENGTH_SHORT).show();
        }
        else {
            inputEmail.setText(userName);
            inputPassword.setText(password);
            reg.setUserName(userName);
            reg.setPassword(password);
        }
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userName = inputEmail.getText().toString();
                password = inputPassword.getText().toString();
                reg.setUserName(userName);
                reg.setPassword(password);
                new ValidateLogin().execute();
            }
        });
        signup_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("APPSTATE","ON DESTROY CALLED");
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("sessionId","");
        editor.commit();
    }

    public class ValidateLogin extends AsyncTask<Void,String,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = new ProgressDialog(LoginActivity.this);
            loading.setProgressStyle(R.style.AppTheme);
            loading.setMessage("Loggin In...");
            loading.show();
            loading.setCanceledOnTouchOutside(false);
        }

        @Override
        protected String doInBackground(Void...voids) {
            Log.i("APPSTATE","ENTERED IN DOINBG");
            String result="";
            String urlString = LoginActivity.this.getString(R.string.login_url);
//            String urlString = strings[0];
            JSONObject loginData = new JSONObject();
            try {
                loginData.put("email",reg.getUserName());//LOADING DATA TO JSON FILE.
                loginData.put("pass",reg.getPassword());
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("APPSTATE","ERROR GENERATING JSON");
            }
            Log.i("APPDATA",""+loginData.toString());
            try {
                Log.i("APPSTATE","ENTERED TRY");
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                Log.i("APPSTATE","CONNECTINO MEHTOD SET TO POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");
                Log.i("APPSTATE","CONNECTINO ESTABLIESHED");
                //set headers and method
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                Log.i("APPSTATE","WORKING TILL HERE WRITER");
                writer.write(loginData.toString());//SENDING JSON DATA TO SERVER.
                writer.flush();
                InputStream stream = connection.getInputStream();//GETTING RESPONSE FROM SERVER.
                int ch=-1;
                while ((ch=stream.read())!=-1){
                    result += (char)ch;
                }
                Log.i("APPSTATE JSON DATA",""+result);
                writer.close();
            }
            catch (Exception e){
                e.printStackTrace();
                Log.i("APPSTATE","ERROR OCCURED");
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loading.dismiss();
            String sessionId="";
            try {
                JSONObject session = new JSONObject(s);
                sessionId = session.getString("session");
            } catch (JSONException e) {
                e.printStackTrace();
                Log.i("APPSTATE","ERROR PARSING SESSION");
            }
            if (sessionId.equals("false")){
                Toast.makeText(LoginActivity.this, "Invalid Email or password", Toast.LENGTH_SHORT).show();
            }
            else{
                reg.setSessionId(sessionId);
                SharedPreferences.Editor editor = sp.edit();
                if (remember.isChecked()){
                    editor.putString("userName",reg.getUserName());//AT SOME PLACES USERNAME AND EMAIL ARE SAME.
                    editor.putString("password",reg.getPassword());
                    editor.putString("sessionId",reg.getSessionId());
                    editor.commit();
                }
                else if(!remember.isChecked()){
                    editor.putString("userName","");
                    editor.putString("password","");
                    editor.putString("sessionId","");
                    editor.commit();
                }
                Intent intent = new Intent(LoginActivity.this,DashboardActivity.class);
                intent.putExtra("eMail",reg.getUserName());
                intent.putExtra("sessionId",""+reg.getSessionId());
                Log.i("APPSTATE DATA",""+reg.getUserName());
                Log.i("APPSTATE SENDING",""+reg.getSessionId());
                MyOpenSQLiteHelper mosh = new MyOpenSQLiteHelper(LoginActivity.this,"uts",null,1);
                User user = new User(mosh);
               // long i = user.insert(reg);
                long i =1;
                if (i>0){
                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
                startActivity(intent);
                finish();
//                Toast.makeText(LoginActivity.this, ""+sessionId, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
