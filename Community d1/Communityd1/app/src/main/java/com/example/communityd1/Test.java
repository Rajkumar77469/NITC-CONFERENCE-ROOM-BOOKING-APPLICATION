package com.example.communityd1;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;

public class Test extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        ProgressDialog progressDialog=new ProgressDialog(Test.this);
        progressDialog.setMessage("loading...");
        progressDialog.show();

    }
}