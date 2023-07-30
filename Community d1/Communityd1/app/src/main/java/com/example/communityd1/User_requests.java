package com.example.communityd1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communityd1.DataClasses.SignupRequests;
import com.example.communityd1.DataClasses.SignupRequestsAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User_requests extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;

    private SignupRequestsAdapter adapter;
    ArrayList<SignupRequests> names=new ArrayList<>();
    RecyclerView recyclerView;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        String callType=i.getStringExtra("callType");
        if(callType.equals("refresh"))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        setContentView(R.layout.activity_user_requests);
        setTitle("SIGNUP REQUESTS");

        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        TextView req_list=findViewById(R.id.request_list);
        recyclerView=findViewById(R.id.requests);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new SignupRequestsAdapter(this, names);
        progressDialog.show();
        createListData();

    }

    private void createListData() {

        firebaseFirestore.collection("Signup Requests")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                this returns a field named "email" in the document//
//                                Map<String, String>mp=(HashMap)document.get("email");

                                Map<String, String>mp=(HashMap)document.getData();
                                SignupRequests r=new SignupRequests(mp.get("name"),mp.get("email"),mp.get("contact"));
                                names.add(r);
                            }

                            recyclerView.setAdapter(adapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(User_requests.this,
                                    LinearLayoutManager.HORIZONTAL));
                            progressDialog.cancel();
                        }
                        else {
                            Toast.makeText(User_requests.this,"Error getting data",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }
}