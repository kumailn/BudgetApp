package com.kumailn.budgetapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LocationService extends Service {
    String ammountLeft = "0";
    String myCurrentLocation = "lol";
    String APIKEY = "AIzaSyCJeTSV92YjuUI0zGN20Wr2UoHxJCyQn8U";
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

        //Change to sharedPref load
        ammountLeft = "$52.53";

        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                parseJSON(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));

                //Testvalues: 42.97529585290097, -81.32619448150638

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
    public String parseJSON(String lat, String lon){
        requestQueue = Volley.newRequestQueue(this);
        //String jsonURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.97529585290097,-81.32619448150638&radius=500&type=store&keyword=store&key=AIzaSyCWR1MBKg3N4DDdrpsQ4hhQkcUPVBBNMCE";
        //String jsonURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.97529585290097,-81.32619448150638&radius=500&type=store&keyword=store&key=AIzaSyCWR1MBKg3N4DDdrpsQ4hhQkcUPVBBNMCE";
        String jsonURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" + lat + "," + lon + "&radius=200&type=store&keyword=store&key=" + APIKEY;
        //String jsonURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=" +lat+ ","+lon+"&rankby=distance&type=store&keyword=store&key=AIzaSyCWR1MBKg3N4DDdrpsQ4hhQkcUPVBBNMCE";
        Log.e("jsonURL", jsonURL);
        final String[] aaa = {""};
        final String[] currentLocation = {"lol"};
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonURL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            aaa[0] = response.getJSONArray("results").getJSONObject(1).getString("name");
                            if(!myCurrentLocation.equals(aaa[0])){
                                myCurrentLocation = aaa[0];
                                //JSONArray jsonArray = response.getJSONArray("name");
                                //Toast.makeText(MainActivity.this, "JSON WORKS", Toast.LENGTH_SHORT).show();
                                Log.e("JSONVOLLEY", aaa[0]);

                                PowerManager pm = (PowerManager)getApplicationContext().getSystemService(Context.POWER_SERVICE);
                                boolean isScreenOn = pm.isScreenOn();
                                Log.e("screen on..........", ""+isScreenOn);
                                if(isScreenOn==false)
                                {
                                    PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK |PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE,"MyLock");
                                    wl.acquire(10000);
                                    PowerManager.WakeLock wl_cpu = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,"MyCpuLock");

                                    wl_cpu.acquire(10000);
                                }

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                Intent intent_main = new Intent(getApplicationContext(), Main2Activity.class);

                                PendingIntent pendingIntentMain = PendingIntent.getActivity(getApplicationContext(), 0, intent_main, PendingIntent.FLAG_CANCEL_CURRENT);
                                Notification notificationPopup = new Notification.Builder(getApplicationContext()).setContentTitle("You're near, " + aaa[0] + ", you have " + ammountLeft + " left to spend").setContentText("Click here")
                                        .setContentIntent(pendingIntentMain).setAutoCancel(true).setSmallIcon(R.drawable.ic_launcher_background)
                                        //Option to show timestamp in notification, set show timestamp to true
                                        .setWhen(System.currentTimeMillis()).setShowWhen(true).setPriority(Notification.PRIORITY_MAX)
                                        .setDefaults(Notification.DEFAULT_ALL).build();
                                notificationManager.notify(0, notificationPopup);
                            }


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
