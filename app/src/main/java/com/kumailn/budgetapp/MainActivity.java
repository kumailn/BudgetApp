package com.kumailn.budgetapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
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
import java.util.Arrays;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    String[] convertResult = new String[1];
    public static String parseConvertResult;

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog dialog;
    EditText cost;
    
    PieChart pieChart ;
    ArrayList<Entry> entries ;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;
    SharedPreferences sharedPreferences;
    Float currentBudgetLeft;
    Float totalMonthlyBudget;
    TextView currentBudgetView;
    LineChart chart;

    //Category/Percent vars
    String category1 = ".";
    String category2 = ".";
    String category3 = ".";
    String category4 = ".";
    String category5 = ".";
    String[] categories;
    int percent1 = 1;
    int percent2 = 1;
    int percent3 = 1;
    int percent4 = 1;
    int percent5 = 1;
    String[] percents;
    float category1sum = 0;
    float category2sum = 0;
    float category3sum = 0;
    float category4sum = 0;
    float category5sum = 0;
    float[] categorysum = new float[5];
    //int[] percents = new int[5];

    String currentItemCategory = "null";

    ArrayList<String> allItemNames;
    ArrayList<String> allItemPrices;
    ArrayList<String> allItemCategories;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Stetho.initializeWithDefaults(this);
        sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        //push
        }
        else{
            Intent i = new Intent(getApplicationContext(), LocationService.class);
            //Make sure permissions are granted
            startService(i);

        }


        //sharedPreferences.edit().putFloat("Budget", 0).apply();
        //sharedPreferences.edit().putFloat("TotalBudget", 1000).apply();

        Intent myIntent = getIntent();
        currentBudgetLeft = sharedPreferences.getFloat("Budget", 0);
        totalMonthlyBudget = sharedPreferences.getFloat("TotalBudget", 0);

        //Category and Percent values

        TextView textView = findViewById(R.id.text_spent);
        textView.setText(String.valueOf("$" + String.format("%.2f",currentBudgetLeft)));

        Intent intent = new Intent(getApplicationContext(), BudgetWidget.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), BudgetWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
        sendBroadcast(intent);

        if(!String.valueOf(myIntent.getFloatExtra("TotalBudget", (float)0.0)).equals("0.0")){
            textView.setText(String.valueOf(String.valueOf(myIntent.getFloatExtra("TotalBudget", (float)0.0))));
        }

        Integer value1 = sharedPreferences.getInt("Num", 0);
        if(value1 == 0){
            sharedPreferences.edit().putInt("Num", value1 + 1).apply();
            startActivity(new Intent(getApplicationContext(), Main3Activity.class));
        }
        else{
            sharedPreferences.edit().putInt("Num", value1 + 1).apply();
        }
        Log.e("The value is: ", String.valueOf(value1));
        if(value1 == 1){
            category1 = myIntent.getStringExtra("Category1");
            category2 = myIntent.getStringExtra("Category2");
            category3 = myIntent.getStringExtra("Category3");
            category4 = myIntent.getStringExtra("Category4");
            category5 = myIntent.getStringExtra("Category5");
            try{
                percent1 = Integer.valueOf(myIntent.getStringExtra("Percent1"));
                percent2 = Integer.valueOf(myIntent.getStringExtra("Percent2"));
                percent3 = Integer.valueOf(myIntent.getStringExtra("Percent3"));
                percent4 = Integer.valueOf(myIntent.getStringExtra("Percent4"));
                percent5 = Integer.valueOf(myIntent.getStringExtra("Percent5"));
            }catch(Exception e){}


            sharedPreferences.edit().putString("Category1", category1).apply();
            sharedPreferences.edit().putString("Category2", category2).apply();
            sharedPreferences.edit().putString("Category3", category3).apply();
            sharedPreferences.edit().putString("Category4", category4).apply();
            sharedPreferences.edit().putString("Category5", category5).apply();
            try{
                sharedPreferences.edit().putInt("Percent1", percent1).apply();
                sharedPreferences.edit().putInt("Percent2", percent2).apply();
                sharedPreferences.edit().putInt("Percent3", percent3).apply();
                sharedPreferences.edit().putInt("Percent4", percent4).apply();
                sharedPreferences.edit().putInt("Percent5", percent5).apply();
            }catch(Exception e){}


        }
        else{
            category1 = sharedPreferences.getString("Category1", "null");
            category2 = sharedPreferences.getString("Category2", "null");
            category3 = sharedPreferences.getString("Category3", "null");
            category4 = sharedPreferences.getString("Category4", "null");
            category5 = sharedPreferences.getString("Category5", "null");

            percent1 = sharedPreferences.getInt("Percent1", 0);
            percent2 = sharedPreferences.getInt("Percent2", 0);
            percent3 = sharedPreferences.getInt("Percent3", 0);
            percent4 = sharedPreferences.getInt("Percent4", 0);
            percent5 = sharedPreferences.getInt("Percent5", 0);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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

        pieDataSet.setColors(ColorTemplate.createColors(new int[]{
                getResources().getColor(R.color.color1),
                getResources().getColor(R.color.color2),
                getResources().getColor(R.color.color3),
                getResources().getColor(R.color.color4),
                getResources().getColor(R.color.color5)}));
        pieChart.setData(pieData);
        pieChart.setDescription(null);
        pieChart.animateY(3000);
        pieChart.setCenterTextSize(20);


        //line chart
        Integer[] dataObjects = new Integer[10];
        for(int ii = 0; ii < 9; ii++){
            dataObjects[ii] = ii;
        }

        pieChart.setCenterText("Something ");

    }

    public void AddValuesToPIEENTRY(){
        entries.add(new BarEntry(percent1, 0));
        entries.add(new BarEntry(percent2, 1));
        entries.add(new BarEntry(percent3, 2));
        entries.add(new BarEntry(percent4, 3));
        entries.add(new BarEntry(percent5, 4));

    }

    public void AddValuesToPieEntryLabels(){
            PieEntryLabels.add(category1);
            PieEntryLabels.add(category2);
            PieEntryLabels.add(category3);
            PieEntryLabels.add(category4);
            PieEntryLabels.add(category5);


    pieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
        @Override
        public void onValueSelected(Entry entry, int i, Highlight highlight) {
            int percentVal = 0;

            switch(entry.getXIndex()){
                case 0:
                    percentVal = percent1;
                case 1:
                    percentVal = percent2;
                case 2:
                    percentVal = percent3;
                case 3:
                    percentVal = percent4;
                case 4:
                    percentVal = percent5;


            }
            Log.e("i selected: ", String.valueOf(i) + " " + entry.getXIndex());
            pieChart.setCenterText("Spent " + String.valueOf(categorysum[entry.getXIndex()]) + " Out of \n" + ((percentVal/100) * totalMonthlyBudget) );
        }

        @Override
        public void onNothingSelected() {

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
        else if(item.getItemId() == R.id.about){
            startActivity(new Intent(getApplicationContext(), Activity4.class));
        }
        return super.onOptionsItemSelected(item);
    }

    public void ShowInputDialog(){
        alertDialogBuilder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_view, null);
        final EditText cost = (EditText) view.findViewById(R.id.costID); //this is the cost
        final EditText item = (EditText) view.findViewById(R.id.itemDescriptionID);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);
        Button submit = (Button) view.findViewById(R.id.submitButton);
        final Spinner mySpin = view.findViewById(R.id.spinnerCat);
        Log.e("Categories: ",  category1 + category2 + category3);
        final ArrayList<String> ss = new ArrayList<>(Arrays.asList(category1, category2, category3, category4, category5));
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, ss);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //mySpin.setSelection(0);

        mySpin.setAdapter(myAdapter);
        alertDialogBuilder.setView(view); //Set the view to the Dialog View
        dialog = alertDialogBuilder.create();
        dialog.show();


        mySpin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentItemCategory = ss.get(position);
                mySpin.setSelection(position);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                if(!cost.getText().toString().isEmpty()) //checks if the user has entered anything
                {
                    try{
                        Integer value1 = sharedPreferences.getInt("Num", 0);
                        totalMonthlyBudget = sharedPreferences.getFloat("TotalBudget", 0);
                        float result = Float.valueOf(cost.getText().toString());
                        if(value1 == 1 || value1 == 2){
                            currentBudgetLeft = totalMonthlyBudget - result;
                            sharedPreferences.edit().putInt("Num", value1 + 1).apply();
                        }else{
                            currentBudgetLeft = currentBudgetLeft - result;
                        }
                        sharedPreferences.edit().putFloat("Budget", currentBudgetLeft).apply();
                        currentBudgetView = findViewById(R.id.text_spent);
                        //currentBudgetView.setText(String.valueOf(currentBudgetLeft));
                        currentBudgetView.setText(String.valueOf("$" + String.format("%.2f",currentBudgetLeft)));
                        TextView t = findViewById(R.id.text_spent);

                        if(currentBudgetLeft < 0){
                            t.setTextColor(Color.RED);
                        }
                        else{
                            t.setTextColor(R.color.colorPrimary);
                        }


                        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                        SQLiteDatabase database = openOrCreateDatabase("dataV5", MODE_PRIVATE, null);
                        database.execSQL("CREATE TABLE IF NOT EXISTS TotalBudget(PurchaseID INT NOT NULL, Cost FLOAT, Category VARCHAR(30), Item VARCHAR(30), Date VARCHAR(10), Credit BIT, PRIMARY KEY(PurchaseID))");
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
                           values.put("Category", currentItemCategory);
                           values.put("Item", item.getText().toString()); //new item
                           values.put("Date", formattedDate);
                           if(checkBox.isChecked())
                               values.put("Credit", 1);
                           else
                               values.put("Credit",0);

                           database.insert("TotalBudget", null, values);
                           Cursor a = database.rawQuery("SELECT Sum(Cost) AS AA FROM TotalBudget", null);
                           int total = a.getColumnIndex("AA");
                           a.moveToFirst();
                           float totalN = a.getFloat(total);
                           Log.e("total is: ", Float.toString(totalN)); //WHERE THE TOTAL IS

                            Cursor u = database.rawQuery("SELECT * FROM TotalBudget", null);
                            int itemID = u.getColumnIndex("Item");
                            int categoryID = u.getColumnIndex("Category");
                            int costID = u.getColumnIndex("Cost");
                            try {
                                while (u.moveToNext()) {
                                    allItemNames.add(u.getString(itemID));
                                    allItemCategories.add(u.getString(categoryID));
                                    allItemPrices.add(Float.toString(u.getFloat(costID)));
                                }
                            } finally {
                                u.close();
                            }
                            if(category1 != null)
                            {
                                String[] columns = { "Cost" };
                                String[] whereArguments = {category1};
                                Cursor cursor = database.query("TotalBudget", columns, "Category = ?", whereArguments, null, null, null);
                                float cat1 = 0;
                                try {
                                    while (cursor.moveToNext()) {
                                        cat1 = cat1 + cursor.getFloat(0);
                                    }
                                } finally {
                                    cursor.close();
                                }
                                Log.e("Total cost is: ", Float.toString(cat1));
                                categorysum[0] = cat1;
                            }
                            if(category2 != null)
                            {
                                String[] columns = { "Cost" };
                                String[] whereArguments = {category2};
                                Cursor cursor = database.query("TotalBudget", columns, "Category = ?", whereArguments, null, null, null);
                                float cat2 = 0;
                                try {
                                    while (cursor.moveToNext()) {
                                        cat2 = cat2 + cursor.getFloat(0);
                                    }
                                } finally {
                                    cursor.close();
                                }
                                Log.e("Total cost is: ", Float.toString(cat2));
                                categorysum[1] = cat2;
                            }
                            if(category3 != null)
                            {
                                String[] columns = { "Cost" };
                                String[] whereArguments = {category3};
                                Cursor cursor = database.query("TotalBudget", columns, "Category = ?", whereArguments, null, null, null);
                                float cat3 = 0;
                                try {
                                    while (cursor.moveToNext()) {
                                        cat3 = cat3 + cursor.getFloat(0);
                                    }
                                } finally {
                                    cursor.close();
                                }
                                Log.e("Total cost is: ", Float.toString(cat3));
                                categorysum[2] = cat3;
                            }
                            if(category4 != null)
                            {
                                String[] columns = { "Cost" };
                                String[] whereArguments = {category4};
                                Cursor cursor = database.query("TotalBudget", columns, "Category = ?", whereArguments, null, null, null);
                                float cat4 = 0;
                                try {
                                    while (cursor.moveToNext()) {
                                        cat4 = cat4 + cursor.getFloat(0);
                                    }
                                } finally {
                                    cursor.close();
                                }
                                Log.e("Total cost is: ", Float.toString(cat4));
                                categorysum[3] = cat4;
                            }
                            if(category5 != null)
                            {
                                String[] columns = { "Cost" };
                                String[] whereArguments = {category5};
                                Cursor cursor = database.query("TotalBudget", columns, "Category = ?", whereArguments, null, null, null);
                                float cat5 = 0;
                                try {
                                    while (cursor.moveToNext()) {
                                        cat5 = cat5 + cursor.getFloat(0);
                                    }
                                } finally {
                                    cursor.close();
                                }
                                Log.e("Total cost is: ", Float.toString(cat5));
                                categorysum[4] = cat5;
                            }

                        }
                       catch(Exception e)
                       {
                           Log.d("purchaseID is 0.", null);
                       }

                        database.close();
                        dialog.dismiss();
                    }
                    catch (Exception e) {
                        Log.e("sqlerror",e.toString());
                        Toast.makeText(getApplicationContext(), "You did not enter an integer for cost.", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        ShowInputDialog(); //hello
                    }
                }
            }
        });


    }


    /*@Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);
        TextView text = findViewById(R.id.text_spent);

        //sharedPreferences.edit().putFloat("Budget", 0).apply();
        //sharedPreferences.edit().putFloat("TotalBudget", 1000).apply();


        currentBudgetLeft = sharedPreferences.getFloat("Budget", 0);
        totalMonthlyBudget = sharedPreferences.getFloat("TotalBudget", 0);
        Log.e(String.valueOf(currentBudgetLeft), "current money");
        text.setText(String.valueOf(currentBudgetLeft));


        Intent i = new Intent(getApplicationContext(), LocationService.class);
        //Make sure permissions are granted
        startService(i);

    }*/


}
