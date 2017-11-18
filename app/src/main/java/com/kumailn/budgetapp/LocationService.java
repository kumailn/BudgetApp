package com.kumailn.budgetapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationService extends Service {
    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("Service", "Started");

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                parseJSON();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        return START_STICKY;
    }

    com.android.volley.RequestQueue requestQueue;
    public String parseJSON(){
        requestQueue = Volley.newRequestQueue(this);
        //String jsonURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.97529585290097,-81.32619448150638&radius=500&type=store&keyword=store&key=AIzaSyCWR1MBKg3N4DDdrpsQ4hhQkcUPVBBNMCE";
        String jsonURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.97529585290097,-81.32619448150638&radius=500&type=store&keyword=store&key=AIzaSyCWR1MBKg3N4DDdrpsQ4hhQkcUPVBBNMCE";
        final String[] aaa = {""};
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonURL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            aaa[0] = response.getJSONArray("results").getJSONObject(0).getString("name");
                            //JSONArray jsonArray = response.getJSONArray("name");
                            //Toast.makeText(MainActivity.this, "JSON WORKS", Toast.LENGTH_SHORT).show();
                            Log.e("JSONVOLLEY", aaa[0]);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("No results found", "");
                        }
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("VOLLEY", "ERROR");
                    }
                }
        );
        requestQueue.add(jsonObjectRequest);
        return aaa[0];
    }
}
