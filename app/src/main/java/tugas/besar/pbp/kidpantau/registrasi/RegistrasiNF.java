package tugas.besar.pbp.kidpantau.registrasi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import tugas.besar.pbp.kidpantau.Login;
import tugas.besar.pbp.kidpantau.R;
import tugas.besar.pbp.kidpantau.main.MapsActivity;

public class RegistrasiNF extends AppCompatActivity {
    EditText mEmail, mUname, mPass;
    Button signup;
    String email,uname,pass,key;
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    int level;
    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi_nf);
        mEmail = (EditText)findViewById(R.id.email);
        mUname = (EditText)findViewById(R.id.username);
        mPass = (EditText)findViewById(R.id.password);
        signup = (Button)findViewById(R.id.signup);
        key = randomAlphaNumeric(5);
        mEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mEmail.getText().toString().length()<=0){
                    mEmail.setError("Email is Required");
                }else{
                    mEmail.setError(null);
                }
            }
        });

        mPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mPass.getText().toString().length()<=0){
                    mPass.setError("Password is Required");
                }else{
                    mPass.setError(null);
                }
            }
        });

        mUname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(mUname.getText().toString().length()<=0){
                    mUname.setError("User Name is Required");
                }else{
                    mUname.setError(null);
                }
            }
        });



        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEmail.getText().toString();
                uname = mUname.getText().toString();
                pass = mPass.getText().toString();
                level = 0;
                System.out.print(level);

                new AsyncFetch().execute();
            }
        });

    }

    public static String randomAlphaNumeric(int count) {

        StringBuilder builder = new StringBuilder();

        while (count-- != 0) {

            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());

            builder.append(ALPHA_NUMERIC_STRING.charAt(character));

        }

        return builder.toString();

    }

    private class AsyncFetch extends AsyncTask<Integer, Void, String> {
        HttpURLConnection conn;
        URL url = null;
        String addTugas = getString(R.string.ip) + "KidPantau/registernf.php?email="+email+"&pass="+pass+"&uname="+uname+"&key="+key+"&level="+level;
        String addTugasUrl = addTugas.replaceAll(" ", "%20");

        @Override
        protected String doInBackground(Integer... integers) {
            try {
                url = new URL(addTugasUrl);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                conn  = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(10000);
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                int response_code = conn.getResponseCode();

                if (response_code == HttpURLConnection.HTTP_OK) {
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    return (result.toString());
                } else {
                    return ("unsuccesfull");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            String cekEmail= "",cekUname= "",status="";
            String emailFlag, unameFlag, statusFlag;
            try {

                JSONObject mainObject = new JSONObject(s);
                status = mainObject.getString("status");
                cekEmail = mainObject.getString("email");
                cekUname = mainObject.getString("uname");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            switch (cekEmail) {
                case "true" :
                    emailFlag = "true";
                    break;
                default:
                    emailFlag = "false";
                    break;
            }

            switch (cekUname) {
                case "true" :
                    unameFlag = "true";
                    break;
                default:
                    unameFlag = "false";
                    break;
            }

            if(emailFlag.equals("true")){
                Toast.makeText(RegistrasiNF.this, "Email is already used", Toast.LENGTH_SHORT).show();
            }else{
                if(unameFlag.equals("true")){
                    Toast.makeText(RegistrasiNF.this, "User name is already used", Toast.LENGTH_SHORT).show();
                }
            }

            switch (status) {
                case "true" :
                    statusFlag = "true";
                    break;
                default:
                    statusFlag = "false";
                    break;
            }

            if(statusFlag=="true"){
                SharedPreferences myPrefs = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("EMAIL", email);
                editor.putString("USERNAME", uname);
                editor.putString("PASSWORD",pass);
                editor.putString("KEY",key);
                editor.putInt("LEVEL",level);
                editor.commit();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                Option.option.finish();
                Login.login.finish();
                finish();
            }
        }
    }
}
