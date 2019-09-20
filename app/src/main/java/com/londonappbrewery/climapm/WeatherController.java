package com.londonappbrewery.climapm;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.logging.LogManager;

import cz.msebera.android.httpclient.Header;


public class WeatherController extends AppCompatActivity {

    // Constants:
    final int REQUEST_CODE = 123;
    final String WEATHER_URL = "http://api.openweathermap.org/data/2.5/weather";
    // App ID to use OpenWeather data
    final String APP_ID = "34d4c38a9beabb79aa1b3e547b95e0d2";
    // Time between location updates (5000 milliseconds or 5 seconds)
    final long MIN_TIME = 5000;
    // Distance between location updates (1000m or 1km)
    final float MIN_DISTANCE = 1000;

    // TODO: Set LOCATION_PROVIDER here:
    final String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;


    // Member Variables:
    TextView mCityLabel;
    ImageView mWeatherImage;
    TextView mTemperatureLabel;

    // TODO: Declare a LocationManager and a LocationListener here:
    //start or stop location updates requestion
    LocationManager mLocationManager;

    //notify if the location changed
    LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_controller_layout);
        Log.d("Weather", "onCreate method is called!");

        // Linking the elements in the layout to Java code
        mCityLabel = (TextView) findViewById(R.id.locationTV);
        mWeatherImage = (ImageView) findViewById(R.id.weatherSymbolIV);
        mTemperatureLabel = (TextView) findViewById(R.id.tempTV);
        ImageButton changeCityButton = (ImageButton) findViewById(R.id.changeCityButton);


        // TODO: Add an OnClickListener to the changeCityButton here:
        changeCityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(WeatherController.this, ChangeCityActivity.class);
                startActivity(myIntent);
            }
        });

    }


    // TODO: Add onResume() here:
    // Called after onCreate() method and before a user starts interact with app
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("Weather", "onResume method is called!");
        Log.d("Weather", "Getting weather for current location");

        Intent intent = getIntent();
        String city = intent.getStringExtra("City");
        if(city != null){
             getWeatherForNewCity(city);
        }
        else {
            getWeatherForCurrentLocation();
        }
    }


    // TODO: Add getWeatherForNewCity(String city) here:
private void getWeatherForNewCity(String city){

        RequestParams params = new RequestParams();
        params.put("q", city);
        params.put("appid", APP_ID);
        letsDoSomeNetworking(params);
}

    // TODO: Add getWeatherForCurrentLocation() here:

    private void getWeatherForCurrentLocation() {

        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Log.d("Weather", "onLocationChanged() callback requested!");
                String longitude = String.valueOf(location.getLongitude());
                String  latitude = String.valueOf(location.getLatitude());
                Log.d("Weather", "longitude is: " + longitude);
                Log.d("Weather", "latitude is: " + latitude);

                RequestParams params = new RequestParams();

                params.put("lat", latitude);
                params.put("lon", longitude);
                params.put("appid", APP_ID);
                letsDoSomeNetworking( params);



            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

                Log.d("Weather", "onProviderDisabled() callback requested!");
            }
        };

      //automate part of code, request permission

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions,


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;

            //  and then overriding
            // public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        mLocationManager.requestLocationUpdates(LOCATION_PROVIDER, MIN_TIME, MIN_DISTANCE, mLocationListener);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE){
            if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.d("Weather", "Permission guaranted!");
                getWeatherForCurrentLocation();
            }
            else
                Log.d("Weather", "Permission denied!");
        }
    }

    // TODO: Add letsDoSomeNetworking(RequestParams params) here:

    private void letsDoSomeNetworking(RequestParams params){

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(WEATHER_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Log.d("Weather", "JSON response:" + response);
              WeatherDataModel weatherDataModel =  WeatherDataModel.fromJSON(response);
              updateUI(weatherDataModel);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("Weather", "Error: "+ statusCode);
                Log.d("Weather", "Response status:" + statusCode);
                Toast.makeText(WeatherController.this, "Request Failed: "+statusCode, Toast.LENGTH_SHORT);
            }
        });

    }



    // TODO: Add updateUI() here:
    private void updateUI(WeatherDataModel weatherDataModel){
        mTemperatureLabel.setText(weatherDataModel.getTemperature());
        mCityLabel.setText(weatherDataModel.getCity());
        int resourceID = getResources().getIdentifier(weatherDataModel.getIconeName(), "drawable", getPackageName()) ;
        mWeatherImage.setImageResource(resourceID);
    }



    // TODO: Add onPause() here:


    @Override
    protected void onPause() {
        super.onPause();
       if (mLocationManager!= null){
           mLocationManager.removeUpdates(mLocationListener);
       }
    }
}
