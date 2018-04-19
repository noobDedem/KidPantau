package tugas.besar.pbp.kidpantau.main;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, OnNavigationItemSelectedListener {
    private static final int MY_PERMISSION_REQUEST_LOCATION = 99;
    private String ip, email, key, nama;
    private int response_code1, response_code2, delay = 60000, level;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private ArrayList<User> user = new ArrayList<>();
    private HttpURLConnection connInsert, connSelect;
    private URL url = null;
    private Handler handler = new Handler();
    private GoogleApiClient googleApiClient;
    private DrawerLayout drawer;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sp = getSharedPreferences("Login", MODE_PRIVATE);
        email = sp.getString("EMAIL", "");
        key = sp.getString("KEY", "");
        level = sp.getInt("LEVEL", -1);
        nama = sp.getString("USERNAME", "");

        if (level == 0) {
            setContentView(R.layout.activity_drawer);
            drawer  = (DrawerLayout) findViewById(R.id.drawer_layout);
        } else {
            setContentView(R.layout.activity_drawer_anak);
            drawer  = (DrawerLayout) findViewById(R.id.drawer_layout_anak);
        }

        ip = getString(R.string.ip);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View nView = navigationView.getHeaderView(0);
        TextView namaTv = nView.findViewById(R.id.nama);
        TextView familykeyTv = nView.findViewById(R.id.familykey);
        namaTv.setText(nama);
        familykeyTv.setText("Invitation Key: " + key);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkLocationPermission();
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        googleApiClient.connect();
        loopUpdate();
    }

    private void getCurrentLocation() {
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
                    connSelect = (HttpURLConnection) url.openConnection();
                    connSelect.setReadTimeout(15000);
                    connSelect.setConnectTimeout(70000);
                    connSelect.setRequestMethod("GET");
                    connSelect.setDoOutput(true);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    response_code1 = connSelect.getResponseCode();

                    if (response_code1 == HttpURLConnection.HTTP_OK) {
                        InputStream input = connSelect.getInputStream();
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
                    connSelect.disconnect();
                }

                if (!user.isEmpty()) {
                    user.clear();
                }

                try {
                    if (response_code1 == HttpURLConnection.HTTP_OK) {
                        JSONArray jsonArray = new JSONArray(json);

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String id = jsonObject.getString("id");
                            String nama = jsonObject.getString("username");
                            double latitude = jsonObject.getDouble("location_lat");
                            double longitude = jsonObject.getDouble("location_long");
                            String time = jsonObject.getString("time");
                            int level = jsonObject.getInt("level");
                            user.add(new User(nama, latitude, longitude, time, setColor(level)));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (user.size() != 0) {
                            mMap.clear();
                            for (int i = 0; i < user.size(); i++) {
                                double k = (double) i;
                                Marker marker1 = mMap.addMarker(new MarkerOptions().position(new LatLng(user.get(i).getLattitude(),
                                        user.get(i).getLongitude())).title(user.get(i).getNama()).
                                        icon(BitmapDescriptorFactory.defaultMarker(user.get(i).getColor())));
                            }
                        }
                    }
                });
            }
        }).start();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
            }
        }
    }

    private void requestLocation() {
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        String provider = locationManager.getBestProvider(criteria, true);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            checkLocationPermission();
        } else {
            locationManager.requestLocationUpdates(provider, 10000, 10, this);
        }
    }

    private void updateHistory(final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String updateLocationUrl = ip + "KidPantau/update_location.php?email=" + email + "&latitude=" + location.getLatitude() + "&longitude=" + location.getLongitude();

                try {
                    url = new URL(updateLocationUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    connInsert = (HttpURLConnection) url.openConnection();
                    connInsert.setReadTimeout(15000);
                    connInsert.setConnectTimeout(70000);
                    connInsert.setRequestMethod("GET");
                    connInsert.setDoOutput(true);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                try {
                    response_code2 = connInsert.getResponseCode();

                    if (response_code2 != HttpURLConnection.HTTP_OK) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Gagal mengupdate", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getApplicationContext(), "Berhasil mengupdate", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    connInsert.disconnect();
                }
            }
        }).start();
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

    public void loopUpdate() {
        handler.postDelayed(new Runnable() {
            public void run() {
                checkLocationPermission();
                Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                if (location != null) {
                    updateHistory(location);
                } else {
                    Toast.makeText(getApplicationContext(), "Location NULL", Toast.LENGTH_LONG).show();
                }
                getCurrentLocation();
                handler.postDelayed(this, delay);
            }
        }, delay);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        } else {
            mMap.setMyLocationEnabled(true);
        }

        ImageButton buttonLocation = findViewById(R.id.btnLocation);
        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCurrentLocation();
            }
        });

    }

    @Override
    public void onLocationChanged(final Location location) {
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
        mMap.animateCamera(cameraUpdate);
        updateHistory(location);
        getCurrentLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_LOCATION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                } else {
                    Toast.makeText(this, "permission denied", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (level == 0) {
            if (id == R.id.history) {
                Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
                intent.putExtra("KEY", key);
                startActivity(intent);
            } else if (id == R.id.logout) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                this.finish();
                System.exit(0);
            }
        } else {
            if (id == R.id.logout) {
                SharedPreferences.Editor editor = sp.edit();
                editor.clear();
                editor.commit();
                this.finish();
                System.exit(0);
            }
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
