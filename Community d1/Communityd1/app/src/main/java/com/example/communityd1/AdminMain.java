package com.example.communityd1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communityd1.DataClasses.Room;
import com.example.communityd1.DataClasses.SignupRequests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AdminMain extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main_card_view);
        setTitle("ADMIN PANEL");

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        CardView view_booking_req=findViewById(R.id.view_req_admin_card);
        CardView view_booking_all=findViewById(R.id.view_all_bookings_admin_card);
        CardView view_my_bookings=findViewById(R.id.view_own_bookings_admin_card);
        CardView addRoom=findViewById(R.id.add_room_admin_card);
        CardView viewRoom=findViewById(R.id.view_rooms_admin_card);
        CardView signout=findViewById(R.id.signout_admin_card);
        CardView view_signup_req=findViewById(R.id.view_signup_req_admin_card);
        CardView editProfile=findViewById(R.id.view_profile_admin_card);

        TextView mainhead=findViewById(R.id.MainHead);
        mainhead.setText("WELCOME - "+firebaseAuth.getCurrentUser().getEmail());

//        setContentView(R.layout.activity_admin_main);
//        Button view_signup_req=findViewById(R.id.view_requests);
//        Button view_booking_req=findViewById(R.id.view_booking_requests);
//        Button view_booking_all=findViewById(R.id.view_all_bookings);
//        Button view_my_bookings=findViewById(R.id.view_my_bookings);
//        Button addRoom=findViewById(R.id.addRoom);
//        Button viewRoom=findViewById(R.id.view_rooms);
//        Button editProfile=findViewById(R.id.admin_edit);
//        Button signout=findViewById(R.id.admin_signout);

        view_signup_req.setOnClickListener(view -> {
            Intent i=new Intent(AdminMain.this, User_requests.class);
            i.putExtra("callType","fresh");
            startActivity(i);
//            Intent i=new Intent(AdminMain.this, Test.class);
//            startActivity(i);
        });

        view_booking_req.setOnClickListener(view -> {
            Intent i=new Intent(AdminMain.this, View_Booking_request.class);
            i.putExtra("callType","fresh");
            startActivity(i);
        });

        view_booking_all.setOnClickListener(view -> {
            Intent i=new Intent(AdminMain.this, View_Booking_Confirmed.class);
            startActivity(i);
        });

        view_my_bookings.setOnClickListener(view -> {
            Intent i=new Intent(AdminMain.this, View_user_booking.class);
            startActivity(i);
        });

        addRoom.setOnClickListener(view -> {
            onAdd();
        });

        viewRoom.setOnClickListener(view -> {
            Intent i=new Intent(AdminMain.this, ViewRoom.class);
            i.putExtra("userType","Admin");
            startActivity(i);
        });

        editProfile.setOnClickListener(view -> {
            Intent i=new Intent(AdminMain.this, Profile.class);
            i.putExtra("userType","Admin");
            startActivity(i);
        });

        signout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            Intent i=new Intent(AdminMain.this, Login.class);
            startActivity(i);
            finish();
        });

    }

    void onAdd () {
        View view=getLayoutInflater().inflate(R.layout.add_room, null, false);
        firebaseFirestore=FirebaseFirestore.getInstance();

        EditText rname=view.findViewById(R.id.roomName);
        EditText rcap=view.findViewById(R.id.roomCap);
        Button cancel=view.findViewById(R.id.add_room_cancel);
        Button accept=view.findViewById(R.id.add_room_accept);

        AlertDialog alertDialog=new AlertDialog.Builder(AdminMain.this).setView(view).create();
        alertDialog.show();

        accept.setOnClickListener(v -> {
            progressDialog=new ProgressDialog(this);
            progressDialog.show();
            String rName=rname.getText().toString();
            String rCap=rcap.getText().toString();

//            DocumentReference documentReference= firebaseFirestore.collection("Room").document();
//            String id= documentReference.getId();
//            String t="3";
//            Room r=new Room(id,t);
//            documentReference.set(r);
//            progressDialog.cancel();

            if(rName.length()>0 && rCap.length()>0) {
                firebaseFirestore.collection("Room")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    int f=0;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        Map<String, String> mp=(HashMap)document.getData();
                                        if(mp.get("name").toLowerCase().equals(rName.toLowerCase())) {
                                            f=1;
                                            break;
                                        }
                                    }
                                    if (f==1) {
                                        Toast.makeText(AdminMain.this, "room with similar name already exists", Toast.LENGTH_SHORT).show();
                                        progressDialog.cancel();
                                    } else {
                                        DocumentReference documentReference=firebaseFirestore.collection("Room").document();
                                        String roomId=documentReference.getId();

                                        Map<String, String> mp=new HashMap<>();
                                        mp.put("name",rName);
                                        mp.put("capacity",rCap);
                                        mp.put("roomId",roomId);
//                                            Room r=new Room(rName,rCap,roomId);
                                        documentReference.set(mp);
                                        Toast.makeText(AdminMain.this, "Room added", Toast.LENGTH_LONG).show();
                                        alertDialog.cancel();
                                        progressDialog.cancel();
                                    }
                                } else {
                                    progressDialog.cancel();
                                    Toast.makeText(AdminMain.this, "error getting data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else {
                progressDialog.cancel();
                Toast.makeText(AdminMain.this, "Fields can't be null", Toast.LENGTH_SHORT).show();
            }

        });

        cancel.setOnClickListener(v -> alertDialog.cancel());
    }
}