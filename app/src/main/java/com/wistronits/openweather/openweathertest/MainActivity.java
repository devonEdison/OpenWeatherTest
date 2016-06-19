package com.wistronits.openweather.openweathertest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    final static int MY_PERMISSIONS_REQUEST_LOCATION = 200;
    private static final String TAG = "MainActivity";
    TextView Location,temp;
    com.loopj.android.image.SmartImageView image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Location = (TextView)findViewById(R.id.Location);
        temp = (TextView)findViewById(R.id.temp);
        image = (com.loopj.android.image.SmartImageView)findViewById(R.id.imageView);
        Log.d(TAG,"0000000000000000000000000000000");

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    locationServiceInitial();
                    Log.d(TAG,"3333333333333333333333333");

                } else {
                    Toast.makeText(MainActivity.this,"拜託要給我權限",Toast.LENGTH_LONG).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.d(TAG,"44444444444444444444444444");

                }
                return;
            }
        }
    }
    private LocationManager lms;
    Double longitude,latitude;
    private void locationServiceInitial() {
        lms = (LocationManager) getSystemService(LOCATION_SERVICE);	//取得系統定位服務
        try {
            Location location = lms.getLastKnownLocation(LocationManager.GPS_PROVIDER);	//使用GPS定位座標
            getLocation(location);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private void getLocation(Location location) {	//將定位資訊顯示在畫面中
        if(location != null) {
            longitude = location.getLongitude();	//取得經度
            latitude = location.getLatitude();	//取得緯度
            Log.d(TAG,"longitude = "+longitude);
            Log.d(TAG,"latitude = "+latitude);
            try {
                Geocoder gcd = new Geocoder(MainActivity.this, Locale.getDefault());
                List<Address> addresses = gcd.getFromLocation(latitude, longitude, 1);
                if (addresses.size() > 0){
					Log.d(TAG,"addresses.get(0).getLocality() = "+addresses.get(0).getLocality());
                    Log.d(TAG,"addresses.getAdminArea = "+addresses.get(0).getAdminArea());
                    Location.setText(addresses.get(0).getLocality() +" , "+addresses.get(0).getAdminArea()+" , "+addresses.get(0).getCountryName());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            getweather();
        } else {
            Toast.makeText(this, "無法定位座標", Toast.LENGTH_LONG).show();
        }
    }

    private void getweather(){
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(10000);
        client.get(MainActivity.this, "http://api.openweathermap.org/data/2.5/weather?lat="+String.valueOf(latitude)+"&lon="+String.valueOf(longitude)+"&APPID=fafc81d50134b228e17c057bdcdaf701" +
                "&units=metric&lang=zh_tw", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                Log.d(TAG,"devon checking response = " + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    //city
//                    String city = obj.getString("name");
//                    JSONObject sys = obj.getJSONObject("sys");
//                    String country = sys.getString("country");

                    //temp
                    JSONObject main = obj.getJSONObject("main");
                    String tempString = main.getString("temp");
                    temp.setText(tempString + " °C");

                    //imageurl
                    JSONArray weather = obj.getJSONArray("weather");
                    Log.d(TAG,"devon weather = " + weather);
                    JSONObject first = weather.getJSONObject(0);
                    Log.d(TAG,"devon weather first = " + first);
                    String icon = first.getString("icon");
                    Log.d(TAG,"devon weather first = " + icon);
                    Log.d(TAG,"http://openweathermap.org/img/w/"+icon+".png");
                    image.setImageUrl("http://openweathermap.org/img/w/"+icon+".png");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG,"123412341234123412341234");

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION )) {
                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                Log.d(TAG,"111111111111111111111111111111");
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION },
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION },
                        MY_PERMISSIONS_REQUEST_LOCATION);
                Log.d(TAG,"222222222222222222222");

            }
        }else{
            locationServiceInitial();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
