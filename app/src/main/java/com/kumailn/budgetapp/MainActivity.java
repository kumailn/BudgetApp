package com.kumailn.budgetapp;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    String[] convertResult = new String[1];
    public static String parseConvertResult;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button button = findViewById(R.id.button);
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        startService(i);

        Button button2 = (Button) findViewById(R.id.button);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShowInputDialog();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings){

            Toast.makeText(getApplicationContext(), "Click Works", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    com.android.volley.RequestQueue requestQueue;
    public String parseJSON(){
        requestQueue = Volley.newRequestQueue(this);
        //String jsonURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.97529585290097,-81.32619448150638&radius=500&type=store&keyword=store&key=AIzaSyCWR1MBKg3N4DDdrpsQ4hhQkcUPVBBNMCE";
        String jsonURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=42.97529585290097,-82.32619448150638&radius=500&type=store&keyword=store&key=AIzaSyCWR1MBKg3N4DDdrpsQ4hhQkcUPVBBNMCE";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, jsonURL,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            String aaa = response.getJSONArray("results").getJSONObject(0).getString("name");
                            //JSONArray jsonArray = response.getJSONArray("name");
                            //Toast.makeText(MainActivity.this, "JSON WORKS", Toast.LENGTH_SHORT).show();
                            Log.e("JSONVOLLEY", aaa);
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
        return parseConvertResult;
    }
    public void ShowInputDialog(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        Button submit = (Button) view.findViewById(R.id.submitButton);


        alertDialogBuilder.setView(view); //Set the view to the Dialog View
        dialog = alertDialogBuilder.create();
        dialog.show();
        final EditText cost = (EditText) findViewById(R.id.costID);
        EditText item = (EditText) findViewById(R.id.itemDescriptionID);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if(cost.getText() != null)
//                {
//                    SQLiteDatabase database = openOrCreateDatabase("data", MODE_PRIVATE, null);
//                    database.execSQL("CREATE IF NOT EXISTS Budget ");
//                }
                dialog.dismiss();
                //hello
                //try again
            }
        });


    }


}
