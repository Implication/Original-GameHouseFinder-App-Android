package trajon.felton.gamehousefinder;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;

import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks,
        OnConnectionFailedListener {
    public ArrayList<Store> arrayOfStores = new ArrayList<>();

    private GoogleApiClient mGoogleApiClient;

    public class UserLoc {
        private double userLat = 33.8829f;
        private double userLon = -117.8869f;

        public double getLat() {
            return userLat;
        }

        public double getLon() {
            return userLon;
        }

        public void setLat(double f) {
            userLat = f;
        }

        public void setLon(double f) {
            userLon = f;
        }
    }

    public UserLoc userLoc = new UserLoc();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("info", "In onCreate");
        //Google API Client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        //Check if we have permissions to location
        int PERMS_FINE_LOCATION = 0;
        int PERMS_INTERNET = 0;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET},
                    PERMS_INTERNET);
        }
        Log.i("info", "In onCreate2");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMS_FINE_LOCATION);
        }
        Log.i("info", "In onCreate3");

        checkLocationActive();

        //Adds the stores to the ArrayList
        addStores.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        Log.i("info", "In onCreate4");
        //Sorts the stores by ascending distance


        //whereAmI();
        Log.i("info", "In onCreate5");

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        /*if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            //Get user's last location
            LocationManager locationManager = (LocationManager) getSystemService(this.LOCATION_SERVICE);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(final Location location) {

                }
            };
            //locationManager.requestLocationUpdates(mGoogleApiClient,(long)1000,(double)0,locationListener);
            Location userLastLoc = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            Toast.makeText(this, "User Lat: " + userLastLoc.getLatitude(), Toast.LENGTH_SHORT).show();
        }*/
    }

    protected void checkLocationActive() {
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        Log.i("info", "In LocationActive");
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // notify user
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("Location must be activated for this app to function. Activate it now?");
            dialog.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent openLocationSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(openLocationSettings);
                }
            });
            dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    finish();
                }
            });
            dialog.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i("info", "In onRequestPermissionsResult");
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            checkLocationActive();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("GPS access is necessary for this app to function properly. Without it," +
                    " the app does nothing.")
                    .setTitle("GPS Required")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    }).show();
        }
    }

    @Override
    protected void onRestart() {
        Log.i("info", "In onRestart");
        super.onRestart();
        checkLocationActive();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i("info", "In onConnected");
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("info", "In onConnectionSuspended");
        // We are not connected anymore!
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("info", "In onConnectionFailed");
        // We tried to connect but failed!
    }

    protected void onStart() {
        Log.i("info", "In onStart");
        super.onStart();
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        Log.i("info", "In onStop");
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    //protected void whereAmI() {
    //   //Permission check for Fine Location
    //    int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
    //    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
    //        //Get user's last location
    //        Location userLastLoc = LocationServices.FusedLocationApi.getLastLocation(
    //                mGoogleApiClient);
    //        if (userLastLoc != null) {
    //            userLoc.setLat((double) userLastLoc.getLatitude());
    //            userLoc.setLon((double) userLastLoc.getLatitude());
    //        }
    //        /*AlertDialog.Builder builder = new AlertDialog.Builder(this);
    //        builder.setMessage("UserLat: " + userLoc.getLat() +
    //                "\nUserLon: " + userLoc.getLon())
    //                .setTitle("User Stats").show();*/
    //    }
    //}

    protected void showStores() {
        Log.i("info", "In showStores");
        Toast.makeText(this, "In showStores", Toast.LENGTH_SHORT);
        setContentView(R.layout.activity_main);
        //Creating the store adapter for the current context and the array
        StoreAdapter adapter = new StoreAdapter(this, arrayOfStores);

        //Designating the ListView in activity_main.xml and settings its adapter to "adapter"
        final ListView myListView = (ListView) findViewById(R.id.listv);
        myListView.setAdapter(adapter);


        //Operation to run on tapping a list item
        myListView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getBaseContext(), StoreInfo.class);
                        Bundle bundle = new Bundle();
                        TextView tv = (TextView) view.findViewById(R.id.tvName);
                        String text = tv.getText().toString();

                        for (Store s : arrayOfStores) {
                            Log.i("StoreIDs",String.valueOf(s.storeID));
                            if (s.storeName.equals(text)) {
                                intent.putExtra("storeID", s.storeID);
                                break;
                            }
                        }

                        intent.putExtra("text", text);
                        intent.putExtra("arrayList", arrayOfStores);
                        startActivity(intent);
                    }
                });
    }

    AsyncTask<Void, Void, Void> addStores = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... params) {
            URL url;
            InputStream phpReturn = null;
            BufferedReader buffreader;
            JSONArray obj = null;
            try {
                url = new URL("http://107.184.164.127:8040/query362.php");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                phpReturn = urlConnection.getInputStream();
                buffreader = new BufferedReader(new InputStreamReader(phpReturn));
                obj = new JSONArray(buffreader.readLine());
            } catch (MalformedURLException e) {
                setTitle("BadURL");
            } catch (IOException e) {
                setTitle("BadIO");
            } catch (JSONException e) {
                setTitle("BadJSON");
            } catch (NullPointerException e) {
                Log.e("Bad", "No pointer");
            }


            int length = obj.length();
            List<Integer> storeIDs = new ArrayList<>();
            List<String> storeNames = new ArrayList<>();
            List<String> storeAddr1s = new ArrayList<>();
            List<String> storeAddr2s = new ArrayList<>();
            List<Double> storeLons = new ArrayList<>();
            List<Double> storeLats = new ArrayList<>();

            try {
                for (int i = 0; i < length; i++) {
                    int temp = Integer.parseInt(obj.getJSONArray(i).getString(0));
                    storeIDs.add(temp);
                }
                for (int i = 0; i < length; i++) {
                    storeNames.add(obj.getJSONArray(i).getString(1));
                }
                for (int i = 0; i < length; i++) {
                    storeAddr1s.add(obj.getJSONArray(i).getString(2));
                }
                for (int i = 0; i < length; i++) {
                    storeAddr2s.add(obj.getJSONArray(i).getString(3));
                }
                for (int i = 0; i < length; i++) {
                    double temp = Double.parseDouble(obj.getJSONArray(i).getString(4)) / 10000;
                    storeLons.add(temp);
                }
                for (int i = 0; i < length; i++) {
                    double temp = Double.parseDouble(obj.getJSONArray(i).getString(5)) / 10000;
                    storeLats.add(temp);
                }
            } catch (JSONException e) {
                Log.e("listapp", "exception", e);
            }

            for (int i = 0; i < length; i++) {
                Store temp = new Store(storeIDs.get(i), storeNames.get(i), storeAddr1s.get(i), storeAddr2s.get(i), storeLons.get(i), storeLats.get(i), distanceCalc(userLoc.getLon(), userLoc.getLat(), storeLons.get(i), storeLats.get(i)));
                arrayOfStores.add(temp);
            }

            try {
                phpReturn.close();
            } catch (Exception e) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Collections.sort(arrayOfStores, new Comparator<Store>() {
                        public int compare(Store p1, Store p2) {
                            return (int) (p1.distance * 10000) - (int) (p2.distance * 10000);
                        }
                    }
            );
            showStores();
        }
    };


    protected double distanceCalc(double lon1, double lat1, double lon2, double lat2) {
        final double R = 6372.8; // In kilometers
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.pow(Math.sin(dLat / 2), 2) + Math.pow(Math.sin(dLon / 2), 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double raw = R * c / 1.60934;
        double raw100 = Math.round(raw * 100);
        int rawToInt = (int) raw100;
        return (double) rawToInt / 100;
    }


}