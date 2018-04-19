package tugas.besar.pbp.kidpantau.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import lab.praktikum.pbp.kidpantau.R;
import tugas.besar.pbp.kidpantau.model.User;

public class HistoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<User> arrayList;
    private String ip, key;
    private int response_code;
    private URL url = null;
    private HttpURLConnection conn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        Bundle extras = getIntent().getExtras();
        ip = getString(R.string.ip);
        key = extras.getString("KEY");

        arrayList = new ArrayList<>();
        getKeluarga();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        mRecyclerView = findViewById(R.id.recyclerview);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new HistoryAdapter(getApplicationContext(), arrayList);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getKeluarga() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String currentLocationUrl = ip + "KidPantau/check_history.php?familykey=" + key;
                String json = "";

                try {
                    url = new URL(currentLocationUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(15000);
                    conn.setConnectTimeout(70000);
                    conn.setRequestMethod("GET");
                    conn.setDoOutput(true);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    response_code = conn.getResponseCode();

                    if (response_code == HttpURLConnection.HTTP_OK) {
                        InputStream input = conn.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                        StringBuilder result = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                            result.append(line);
                        }
                        input.close();
                        reader.close();
                        json = result.toString();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Gagal mengupdate", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    conn.disconnect();
                }

                if (!arrayList.isEmpty()) {
                    arrayList.clear();
                }

                try {
                    if (response_code == HttpURLConnection.HTTP_OK) {
                        JSONArray jsonArray = new JSONArray(json);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String nama = jsonObject.getString("username");
                            String email = jsonObject.getString("User_email");
                            double latitude = jsonObject.getDouble("location_lat");
                            double longitude = jsonObject.getDouble("location_long");
                            String time = jsonObject.getString("time");
                            int level = jsonObject.getInt("level");
                            arrayList.add(new User(nama, email, latitude, longitude, time, setColor(level)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                        System.out.println(arrayList);
                    }
                });
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public float setColor(int level) {
        switch (level) {
            case 0:
                return BitmapDescriptorFactory.HUE_AZURE; //orang tua
            case 1:
                return BitmapDescriptorFactory.HUE_ROSE; //anak
        }
        return 0;
    }
}
