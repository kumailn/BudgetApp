package com.kumailn.budgetapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mehdi.sakout.aboutpage.AboutPage;

public class Activity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View aboutPage = new AboutPage(this)
                .isRTL(false)
                .setImage(R.drawable.ic_wallet)
                .setDescription("A Budget App which helps you manage your budget.")
                .addGitHub("kumailn")
                .addGitHub("SkilloverluckST")
                .addGitHub("Saamirt")
                .create();
        setContentView(aboutPage);


    }
}
