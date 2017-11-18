package com.kumailn.budgetapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.design.widget.FloatingActionButton;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    String[] convertResult = new String[1];
    public static String parseConvertResult;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    EditText cost;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        //startService(i);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "FAB is Pressed", Toast.LENGTH_LONG).show();
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
        final EditText cost = (EditText) view.findViewById(R.id.costID); //this is the cost
        final EditText item = (EditText) view.findViewById(R.id.itemDescriptionID);
        Button submit = (Button) view.findViewById(R.id.submitButton);


        alertDialogBuilder.setView(view); //Set the view to the Dialog View
        dialog = alertDialogBuilder.create();
        dialog.show();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!cost.getText().toString().isEmpty()) //checks if the user has entered anything
                {
                    try{
                        int result = Integer.parseInt(cost.getText().toString());
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                        SQLiteDatabase database = openOrCreateDatabase("data", MODE_PRIVATE, null);
                        database.execSQL("CREATE TABLE IF NOT EXISTS TotalBudget(PurchaseID INT NOT NULL, Cost INT, Item VARCHAR(30), Date VARCHAR(10), Total INT, PRIMARY KEY(PurchaseID))");

                        try{
                           Cursor c = database.rawQuery("SELECT Max(PurchaseID) AS ID FROM TotalBudget", null);

                           int purchaseID = c.getColumnIndex("ID");
                           c.moveToFirst();
                           Log.e("num is", Integer.toString(purchaseID));
                           Log.e("cursor is ", Integer.toString(c.getCount()));
                           int ID = c.getInt(purchaseID);
                           Log.e("purchaseID is ", Integer.toString(ID++));
                           ContentValues values = new ContentValues();
                           values.put("PurchaseID", ID);
                           values.put("Cost", result);
                           values.put("Item", item.getText().toString());
                           values.put("Date", formattedDate);
                           values.put("Total", 56);
                           database.insert("TotalBudget", null, values);
                       }
                       catch(Exception e)
                       {
                           Log.d("purchaseID is 0.", null);
                           database.execSQL("INSERT INTO TotalBudget(PurchaseID, Cost, Item, Date, Total) VALUES(0, 45,'hello', 'wow', 98)");
                       }

                        database.close();
                        dialog.dismiss();
                    }
                    catch (Exception e) {
                        Log.e("sqlerror",e.toString());
                        Toast.makeText(getApplicationContext(), "You did not enter an integer for cost.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        ShowInputDialog();
                    }
                }
            }
        });


    }


}
