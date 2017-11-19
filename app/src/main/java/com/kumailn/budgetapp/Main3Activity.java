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
                startActivity(new Intent(getApplicationContext(), MainActivity.class).putExtra("TotalBudget", Float.valueOf(String.valueOf(editTextTotalMonthlyBudget.getText()))));
            }
        });
        //csajsk
    }

}
