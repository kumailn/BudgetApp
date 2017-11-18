package com.kumailn.budgetapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String[] convertResult = new String[1];
    public static String parseConvertResult;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    EditText cost;

    LineChart chart;
    double[] dataObjects = {0.7960,0.7947,0.7946,0.7950,0.7958,0.7984,0.8019,0.8012,0.8012,0.7999,0.7980,0.7924,0.7978,0.8075,0.8066,0.8066,0.8059,0.8082,0.8188,0.8226,0.8241,0.8226,0.8223,0.8246,0.8221,0.8186,0.8196,0.8191,0.8199};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        startService(i);
        chart = (LineChart) findViewById(R.id.chart);

        //YourData[] dataObjects = ...;

        List<Entry> entries = new ArrayList<Entry>();

        for (double data : dataObjects) {
            // turn your data into Entry objects
            entries.add(new Entry((float)data, (int)data));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.GRAY); // styling, ...

        //LineData lineData = new LineData(dataSet);
        //chart.setData(lineData);
        chart.invalidate(); // refresh

        chart.setVisibility(View.VISIBLE);
        chart.setViewPortOffsets(0, 0, 0, 0);
        chart.setBackgroundColor(Color.rgb(104, 241, 175));
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(false);
        chart.setDrawGridBackground(false);

        XAxis x = chart.getXAxis();
        x.setEnabled(false);

        YAxis y = chart.getAxisLeft();
        y.setLabelCount(6, false);
        y.setTextColor(Color.WHITE);
        y.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
        y.setDrawGridLines(false);
        y.setAxisLineColor(Color.WHITE);

        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.animateXY(500, 500);
        chart.invalidate();

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



