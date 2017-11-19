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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.dataprovider.LineDataProvider;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    String[] convertResult = new String[1];
    public static String parseConvertResult;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    EditText cost;

    private float[] yData = {25.3f, 10.6f, 66.76f, 44.32f, 46.01f, 16.89f, 23.9f};
    private String[] xData = {"Mitch", "Jessica" , "Mohammad" , "Kelsey", "Sam", "Robert", "Ashley"};
    double[] cadToUSD = {0.7960,0.7947,0.7946,0.7950,0.7958,0.7984,0.8019,0.8012,0.8012,0.7999,0.7980,0.7924,0.7978,0.8075,0.8066,0.8066,0.8059,0.8082,0.8188,0.8226,0.8241,0.8226,0.8223,0.8246,0.8221,0.8186,0.8196,0.8191,0.8199};

    PieChart pieChart ;
    ArrayList<Entry> entries ;
    ArrayList<Entry> entriesTwo ;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;
    SharedPreferences sharedPreferences;
    Float currentBudgetLeft;
    Float totalMonthlyBudget;
    TextView currentBudgetView;
    LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);
        sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        Intent i = new Intent(getApplicationContext(), LocationService.class);
        //Make sure permissions are granted
        startService(i);
        //d

        sharedPreferences.edit().putFloat("Budget", 0).apply();
        sharedPreferences.edit().putFloat("TotalBudget", 1000).apply();


        currentBudgetLeft = sharedPreferences.getFloat("Budget", 0);
        totalMonthlyBudget = sharedPreferences.getFloat("TotalBudget", 0);


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


        //line chart
        LineChart mChart = findViewById(R.id.lineChart);
        Integer[] dataObjects = new Integer[10];
        for(int ii = 0; ii < 9; ii++){
            dataObjects[ii] = ii;
        }

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
                        currentBudgetLeft = currentBudgetLeft - result;
                        sharedPreferences.edit().putFloat("Budget", currentBudgetLeft).apply();
                        currentBudgetView = findViewById(R.id.text_spent);
                        currentBudgetView.setText(String.valueOf(currentBudgetLeft));


                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                        SQLiteDatabase database = openOrCreateDatabase("dataV4", MODE_PRIVATE, null);
                        database.execSQL("CREATE TABLE IF NOT EXISTS TotalBudget(PurchaseID INT NOT NULL, Cost FLOAT, Item VARCHAR(30), Date VARCHAR(10), Total FLOAT, PRIMARY KEY(PurchaseID))");
                        try{
                           Cursor c = database.rawQuery("SELECT Max(PurchaseID) AS ID FROM TotalBudget", null);

                           Cursor b = database.rawQuery("SELECT Min(Total) AS budget FROM TotalBudget", null);
                           b.moveToFirst();
                           float minBudget = b.getFloat(0);
                           minBudget = minBudget - result;



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
                           values.put("Total", minBudget);
                           database.insert("TotalBudget", null, values);
                           Cursor a = database.rawQuery("SELECT Sum(Cost) AS AA FROM TotalBudget", null);
                           int total = a.getColumnIndex("AA");
                           a.moveToFirst();
                           float totalN = a.getFloat(total);
                           Log.e("total is: ", Float.toString(totalN)); //WHERE THE TOTAL IS

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
