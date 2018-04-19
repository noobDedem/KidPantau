package tugas.besar.pbp.kidpantau.main;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

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
import java.util.List;
import java.util.Locale;

import lab.praktikum.pbp.kidpantau.R;
import tugas.besar.pbp.kidpantau.model.Location;

public class ListHistoryActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private ArrayList<Location> arrayList;
    private String ip, email;
    private int response_code;
    private URL url = null;
    private HttpURLConnection conn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_history);

        Bundle extras = getIntent().getExtras();
        email = extras.getString("email");

        ip = getString(R.string.ip);

        arrayList = new ArrayList<>();
        getLocation();

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
        mAdapter = new ListHistoryAdapter(getApplicationContext(), arrayList);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void getLocation() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String currentLocationUrl = ip + "KidPantau/list_history.php?email=" + email;
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
                            double latitude = jsonObject.getDouble("location_lat");
                            double longitude = jsonObject.getDouble("location_long");
                            String time = jsonObject.getString("time");
                            arrayList.add(new Location(getAddress(latitude, longitude), time));
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

    public String getAddress(double lat, double lang) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses= geocoder.getFromLocation(lat, lang, 1);
            Address address = addresses.get(0);
            String alamat = address.getAddressLine(0);
            alamat = alamat + "\n" + address.getCountryName();
            alamat = alamat + "\n" + address.getAdminArea();
            alamat = alamat + "\n" + address.getSubAdminArea();
            alamat = alamat + "\n" + address.getLocality();

            return alamat;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
