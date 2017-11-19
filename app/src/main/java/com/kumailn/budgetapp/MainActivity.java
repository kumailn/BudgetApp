package com.kumailn.budgetapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.facebook.stetho.Stetho;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    String[] convertResult = new String[1];
    public static String parseConvertResult;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    EditText cost;

    private float[] yData = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f};
    private String[] xData = {"Mitch", "Jessica" , "Mohammad" , "Kelsey", "Sam", "Robert", "Ashley"};
    PieChart pieChart ;
    ArrayList<Entry> entries ;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        //Make sure permissions are granted
        startService(i);
        //d



        SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        Integer value1 = sharedPreferences.getInt("Num", 0);
        if(value1 == 0){
            sharedPreferences.edit().putInt("Num", value1++).apply();
            startActivity(new Intent(getApplicationContext(), Main3Activity.class));
        }
        sharedPreferences.edit().putInt("Num", value1++).apply();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "FAB is Pressed", Toast.LENGTH_LONG).show();
                ShowInputDialog();
            }
        });


        pieChart = (PieChart) findViewById(R.id.pieChart);

        entries = new ArrayList<>();

        PieEntryLabels = new ArrayList<String>();

        AddValuesToPIEENTRY();

        AddValuesToPieEntryLabels();

        pieDataSet = new PieDataSet(entries, "");

        pieData = new PieData(PieEntryLabels, pieDataSet);

        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        pieChart.setData(pieData);

        pieChart.animateY(3000);

    }

    public void AddValuesToPIEENTRY(){

        entries.add(new BarEntry(2f, 0));
        entries.add(new BarEntry(4f, 1));
        entries.add(new BarEntry(6f, 2));
        entries.add(new BarEntry(8f, 3));
        entries.add(new BarEntry(7f, 4));
        entries.add(new BarEntry(3f, 5));

    }

    public void AddValuesToPieEntryLabels(){

        PieEntryLabels.add("January");
        PieEntryLabels.add("February");
        PieEntryLabels.add("March");
        PieEntryLabels.add("April");
        PieEntryLabels.add("May");
        PieEntryLabels.add("June");

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
                        float result = Float.valueOf(cost.getText().toString());
                        result = result * 100;
                        int cost = (int)result;
                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                        SQLiteDatabase database = openOrCreateDatabase("data", MODE_PRIVATE, null);
                        database.execSQL("CREATE TABLE IF NOT EXISTS TotalBudget(PurchaseID INT NOT NULL, Cost INT, Item VARCHAR(30), Date VARCHAR(10), Total INT, PRIMARY KEY(PurchaseID))");

                        try{
                           Cursor c = database.rawQuery("SELECT Max(PurchaseID) AS ID FROM TotalBudget", null);
                           Cursor a = database.rawQuery("SELECT Sum(Cost) AS AA FROM TotalBudget", null);

                            int total = a.getColumnIndex("AA");
                           a.moveToFirst();

                           int purchaseID = c.getColumnIndex("ID");
                           c.moveToFirst();
                           Log.e("num is", Integer.toString(purchaseID));
                           Log.e("cursor is ", Integer.toString(c.getCount()));
                           int ID = c.getInt(purchaseID);
                           int totalN = a.getInt(total);
                            Toast.makeText(getApplicationContext(), totalN, Toast.LENGTH_SHORT).show();
                            Log.e("purchaseID is ", Integer.toString(ID++));
                           ContentValues values = new ContentValues();
                           values.put("PurchaseID", ID);
                           values.put("Cost", cost);
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
