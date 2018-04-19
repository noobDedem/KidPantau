package tugas.besar.pbp.kidpantau;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import lab.praktikum.pbp.kidpantau.R;
import tugas.besar.pbp.kidpantau.main.MapsActivity;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splashscreen);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                SharedPreferences sp = getSharedPreferences("Login", MODE_PRIVATE);
                String nama = sp.getString("USERNAME", "");
                String email = sp.getString("EMAIL", "");
                int level = sp.getInt("LEVEL", -1);
                String key = sp.getString("KEY", "");

                if (nama.equals("") || nama.equals("") || level == -1 || email.equals("") || key.equals("")) {
                    Intent login = new Intent(getApplicationContext(), Login.class);
                    startActivity(login);
                    finish();
                } else {
                    Intent maps = new Intent(getApplicationContext(), MapsActivity.class);
                    startActivity(maps);
                    finish();
                }
            }
        }).start();
    }
}
