package com.example.communityd1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.communityd1.DataClasses.BookingRequest;
import com.example.communityd1.DataClasses.BookingRequestAdapter;
import com.example.communityd1.DataClasses.Room;
import com.example.communityd1.DataClasses.RoomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class View_Booking_request extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private BookingRequestAdapter adapter;
    ArrayList<BookingRequest> names=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        String callType=i.getStringExtra("callType");
        if(callType.equals("refresh"))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        setContentView(R.layout.activity_view_booking_request);
        setTitle("BOOKING REQUESTS");

        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        recyclerView=findViewById(R.id.recycler_booking_request);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new BookingRequestAdapter(this, names);

        progressDialog.show();
        createListData();
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(View_Booking_request.this,
                LinearLayoutManager.HORIZONTAL));
    }

    void createListData() {

        firebaseFirestore.collection("Booking requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        for(DocumentChange dc: queryDocumentSnapshots.getDocumentChanges()) {
                            if(dc.getType()==DocumentChange.Type.ADDED) {
                                Map<String, String> mp = (HashMap) dc.getDocument().getData();
                                BookingRequest r = new BookingRequest(mp.get("date"), mp.get("time"), mp.get("purpose"), mp.get("client"), mp.get("roomId"), mp.get("bookingId"),mp.get("faculty"),mp.get("reason"));
                                names.add(r);
                            }
                        }
                        progressDialog.cancel();
                        adapter.notifyDataSetChanged();
                    }
                });
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        names.clear();
//                        if(task.isSuccessful()) {
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Map<String, String> mp=(HashMap)document.getData();
//                                System.out.println(mp.get("client")+" "+mp.get("purpose"));
//                                BookingRequest r=new BookingRequest(mp.get("date"),mp.get("time"), mp.get("purpose"), mp.get("client"), mp.get("roomId"), mp.get("bookingId"));
//                                names.add(r);
//                            }
//                            adapter.notifyDataSetChanged();
//                            progressDialog.cancel();
//                        }
//                        else {
//                            Toast.makeText(View_Booking_request.this,"Error getting data",Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });

    }

}