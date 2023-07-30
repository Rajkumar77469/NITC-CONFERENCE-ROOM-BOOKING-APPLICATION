package com.example.communityd1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.example.communityd1.DataClasses.BookingRequest;
import com.example.communityd1.DataClasses.BookingRequestAdapter;
import com.example.communityd1.DataClasses.BookingRequestConfirmedAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class View_Booking_Confirmed extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private BookingRequestConfirmedAdapter adapter;
    ArrayList<BookingRequest> names=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_booking_confirmed);
        setTitle("ALL CONFIRMED BOOKINGS");

        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        recyclerView=findViewById(R.id.recycler_all_booking);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new BookingRequestConfirmedAdapter(this, names);

        progressDialog.show();
        createListData();

    }

    void createListData() {

        firebaseFirestore.collection("Booking confirmed")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, String> mp=(HashMap)document.getData();
                                BookingRequest r=new BookingRequest(mp.get("date"),mp.get("time"), mp.get("purpose"), mp.get("client"), mp.get("roomId"), mp.get("bookingId"),mp.get("faculty"),mp.get("reason"));
                                names.add(r);
                            }
                            recyclerView.setAdapter(adapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(View_Booking_Confirmed.this,
                                    LinearLayoutManager.HORIZONTAL));
                            progressDialog.cancel();
                        }
                        else {
                            Toast.makeText(View_Booking_Confirmed.this,"Error getting data",Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

}