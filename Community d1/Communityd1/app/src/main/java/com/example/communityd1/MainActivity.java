package com.example.communityd1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_card_view);
        setTitle("USER PANEL");

        firebaseAuth= FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        CardView viewRooms=findViewById(R.id.view_room_card);
        CardView myBookings=findViewById(R.id.view_booking_card);
        CardView signout=findViewById(R.id.signout_card);
        CardView editProfile=findViewById(R.id.view_profile_card);

        TextView mainhead=findViewById(R.id.MainHead);
        mainhead.setText("WELCOME - "+firebaseAuth.getCurrentUser().getEmail());

//        setContentView(R.layout.activity_main);
//        Button viewRooms=findViewById(R.id.view_rooms);
//        Button myBookings=findViewById(R.id.view_my_bookings);
//        Button editProfile=findViewById(R.id.editProfile);
//        Button signout=findViewById(R.id.signout);

        viewRooms.setOnClickListener(view -> {
            Intent i=new Intent(MainActivity.this,ViewRoom.class);
            i.putExtra("userType","User");
            startActivity(i);
        });

        myBookings.setOnClickListener(view -> {
            Intent i=new Intent(MainActivity.this,View_user_booking.class);
            startActivity(i);
        });

        editProfile.setOnClickListener(view -> {
            Intent i=new Intent(MainActivity.this, Profile.class);
            i.putExtra("userType","User");
            startActivity(i);
        });

        signout.setOnClickListener(view -> {
            firebaseAuth.signOut();
            Intent i=new Intent(MainActivity.this,Login.class);
            startActivity(i);
            finish();
        });

    }
}