package com.kumailn.budgetapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Main3Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        Button buttonDone = (findViewById(R.id.buttonDone));
        final EditText editTextTotalMonthlyBudget = findViewById(R.id.edittextTotalMonthlyBudget);
        EditText editTextInterest = findViewById(R.id.edittextInterest);
        final EditText editTextCategory1 = findViewById(R.id.edittextCategory1);
        final EditText editTextCategory2 = findViewById(R.id.edittextCategory2);
        final EditText editTextCategory3 = findViewById(R.id.edittextCategory3);
        final EditText editTextCategory4 = findViewById(R.id.edittextCategory4);
        final EditText editTextCategory5 = findViewById(R.id.edittextCategory5);

        final EditText editTextPercent1 = findViewById(R.id.edittextCategoryPercent1);
        final EditText editTextPercent2 = findViewById(R.id.edittextCategoryPercent2);
        final EditText editTextPercent3 = findViewById(R.id.edittextCategoryPercent3);
        final EditText editTextPercent4 = findViewById(R.id.edittextCategoryPercent4);
        final EditText editTextPercent5 = findViewById(R.id.edittextCategoryPercent5);

        final SharedPreferences sharedPreferences = getSharedPreferences("myData", Context.MODE_PRIVATE);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferences.edit().putFloat("TotalBudget", Float.valueOf(String.valueOf(editTextTotalMonthlyBudget.getText()))).apply();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent backToMain = new Intent(getApplicationContext(), MainActivity.class)
                        .putExtra("TotalBudget", Float.valueOf(String.valueOf(editTextTotalMonthlyBudget.getText())))
                        .putExtra("Category1", editTextCategory1.getText().toString())
                        .putExtra("Percent1", editTextPercent1.getText().toString())
                        .putExtra("Percent2", editTextPercent2.getText().toString())
                        .putExtra("Category2", editTextCategory2.getText().toString())
                        .putExtra("Percent3", editTextPercent3.getText().toString())
                        .putExtra("Category3", editTextCategory3.getText().toString())
                        .putExtra("Percent4", editTextPercent4.getText().toString())
                        .putExtra("Category4", editTextCategory4.getText().toString())
                        .putExtra("Percent5", editTextPercent5.getText().toString())
                        .putExtra("Category5", editTextCategory5.getText().toString());

                startActivity(backToMain);
            }
        });
    }

}
