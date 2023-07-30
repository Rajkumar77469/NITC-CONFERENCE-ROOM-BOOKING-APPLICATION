package com.example.communityd1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communityd1.DataClasses.Room;
import com.example.communityd1.DataClasses.RoomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewRoom extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    private RoomAdapter adapter;
    ArrayList<Room> names=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
        String userType=i.getStringExtra("userType");
        if(userType.equals("refresh"))
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        setContentView(R.layout.activity_view_room);
        setTitle("ROOMS");

        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        Button remove=findViewById(R.id.remove_room);
        if(userType.equals("User")) {
            remove.setVisibility(View.GONE);
        }
        remove.setOnClickListener(view -> {
            onRemove();
        });

        recyclerView=findViewById(R.id.rooms);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new RoomAdapter(this, names);
        progressDialog.show();
        createListData();

    }

    private void createListData() {
        firebaseFirestore.collection("Room")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            int i=0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Map<String, String> mp=(HashMap)document.getData();
                                Room r=new Room(mp.get("name"),mp.get("capacity"),mp.get("bookingId"));
                                names.add(r);
                            }
                            recyclerView.setAdapter(adapter);
                            recyclerView.addItemDecoration(new DividerItemDecoration(ViewRoom.this,
                                    LinearLayoutManager.HORIZONTAL));
                            progressDialog.cancel();
                        }
                        else {
                            Toast.makeText(ViewRoom.this,"Error getting data",Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void onRemove() {
        View v=getLayoutInflater().inflate(R.layout.add_room,null,false);

        TextView head=v.findViewById(R.id.add_room_head);
        EditText name=v.findViewById(R.id.roomName);
        EditText cap=v.findViewById(R.id.roomCap);
        TextView disclaimer=v.findViewById(R.id.remove_room_discalimer);
        Button cancel=v.findViewById(R.id.add_room_cancel);
        Button accept=v.findViewById(R.id.add_room_accept);

        cap.setVisibility(View.GONE);
        disclaimer.setVisibility(View.VISIBLE);
        head.setText("Remove a room");

        AlertDialog alertDialog=new AlertDialog.Builder(ViewRoom.this).setView(v).create();
        alertDialog.show();

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String rName=name.getText().toString();
                if(rName.length()>0) {
                    progressDialog.show();
                    Intent i=new Intent(ViewRoom.this, ViewRoom.class);
                    i.putExtra("userType","refresh");
                    firebaseFirestore.collection("Room")
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        int f = 0;
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Map<String, String> mp = (HashMap) document.getData();
                                            if (mp.get("name").equals(rName)) {
                                                String id=mp.get("roomId");
                                                firebaseFirestore.collection("Room")
                                                        .document(id)
                                                        .delete()
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                startActivity(i);
                                                                finish();
                                                                Toast.makeText(ViewRoom.this, rName+" deleted", Toast.LENGTH_SHORT).show();
                                                                progressDialog.cancel();
                                                                alertDialog.cancel();
                                                            }
                                                        });
                                                f = 1;
                                                break;
                                            }
                                        }
                                        if (f == 0) {
                                            Toast.makeText(ViewRoom.this, "no such room", Toast.LENGTH_SHORT).show();
                                            progressDialog.cancel();
                                        }
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(ViewRoom.this, "field can't be null", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

    }
}