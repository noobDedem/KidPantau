package tugas.besar.pbp.kidpantau;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import lab.praktikum.pbp.kidpantau.R;
import tugas.besar.pbp.kidpantau.main.MapsActivity;
import tugas.besar.pbp.kidpantau.registrasi.Option;

public class Login extends AppCompatActivity {
    EditText username, password;
    Button button;
    TextView register;
    String usernameLogin, passwordLogin;
    public static Activity login;

    public Login() {}

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = this;

        username = (EditText)findViewById(R.id.login_username);
        password = (EditText)findViewById(R.id.login_password);
        button = (Button)findViewById(R.id.login_button);
        register = (TextView) findViewById(R.id.signup);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usernameLogin = username.getText().toString();
                passwordLogin = password.getText().toString();
                try {
                    new AsyncFetch(getApplicationContext()).execute();
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentregis = new Intent(Login.this, Option.class);
                startActivity(intentregis);
            }
    });
    }

    private class AsyncFetch extends AsyncTask<Integer, Void, String> {
        Context context;
        HttpURLConnection conn;
        URL url = null;
        String loginUrl = getString(R.string.ip) + "KidPantau/login.php?username="+usernameLogin+"&password="+passwordLogin;

        public AsyncFetch(Context context) {
            this.context = context.getApplicationContext();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            try {
                url = new URL(loginUrl);
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
            String status = "", email = "", username = "", familykey = "", password = "", key="";
            String statusFlag;
            Integer level = null;
            try {

                JSONObject mainObject = new JSONObject(s);
                JSONObject data = mainObject.getJSONObject("data");
                status = mainObject.getString("status");
                username = data.getString("username");
                password = data.getString("password");
                email = data.getString("email");
                key = data.getString("familykey");
                level = data.getInt("level");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            switch (status) {
                case "true" :
                    statusFlag = "true";
                    break;
                default:
                    statusFlag = "false";
                    break;
            }
            if (statusFlag == "true") {
                SharedPreferences myPrefs = getSharedPreferences("Login", MODE_PRIVATE);
                SharedPreferences.Editor editor = myPrefs.edit();
                editor.putString("USERNAME", username);
                editor.putString("EMAIL",email);
                editor.putString("PASSWORD",password);
                editor.putString("KEY",key);
                editor.putInt("LEVEL",level);
                editor.commit();

                Intent intentortu = new Intent(context, MapsActivity.class);
                startActivity(intentortu);
                finish();
            } else {
                Toast.makeText(context, "Username atau password salah", Toast.LENGTH_LONG).show();
            }
        }
    }
}