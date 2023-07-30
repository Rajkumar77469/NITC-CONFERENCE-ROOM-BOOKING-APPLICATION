package com.example.communityd1;


import androidx.annotation.IntegerRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.communityd1.DataClasses.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class User_signup extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_signup);
        setTitle("SIGNUP");

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        EditText name=findViewById(R.id.name);
        EditText contact=findViewById(R.id.contact);
        EditText email=findViewById(R.id.email);
        EditText pass=findViewById(R.id.pass);
        EditText cpass=findViewById(R.id.cpass);

        Button signup=findViewById(R.id.signup);
        TextView login=findViewById(R.id.go_login);

        signup.setOnClickListener(view -> {
            String nm=name.getText().toString();
            String cont=contact.getText().toString();
            String em=email.getText().toString();
            String pas=pass.getText().toString();
            String cpas=cpass.getText().toString();



            if(nm.length()>0 && em.length()>0 && pas.length()>0 && cpas.length()>0) {
                if(em.length()>11 && em.substring(em.length()-11,em.length()).equals("@nitc.ac.in")) {
                    try {
                    int x=10;
                    if(cont.length()!=10)
                        x/=0;
                    Long y= Long.parseLong(cont);
                    if(pas.equals(cpas)) {
                        progressDialog.show();
                        firebaseAuth.createUserWithEmailAndPassword(em,pas)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        user=firebaseAuth.getCurrentUser();
                                        user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(User_signup.this, "User created. please check your mail", Toast.LENGTH_LONG).show();

                                                if(nm.equals("Admin")) {
                                                    User u=new User(em,nm,cont);
                                                    firebaseFirestore.collection("Signup Requests")
                                                            .document(em)
                                                            .set(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()) {
                                                                        FirebaseAuth.getInstance().signOut();
                                                                        goLogin();
                                                                    }
                                                                }
                                                            });
                                                }
                                                else {
                                                    User u = new User(em, nm, cont);
                                                    firebaseFirestore.collection("User")
                                                            .document(em)
                                                            .set(u).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()) {
                                                                        FirebaseAuth.getInstance().signOut();
                                                                        goLogin();
                                                                    }
                                                                }
                                                            });
                                                }
                                                progressDialog.cancel();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(User_signup.this, "email sending failed", Toast.LENGTH_SHORT).show();
                                                progressDialog.cancel();
                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(User_signup.this,e.getMessage(),Toast.LENGTH_LONG).show();
                                        progressDialog.cancel();
                                    }
                                });
                    }
                    else {
                        Toast.makeText(this, "Passwords doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
                catch (Exception e){
                    Toast.makeText(this, "please enter 10 digit contact no.", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "enter valid nitc id", Toast.LENGTH_SHORT).show();
                }
            }
            else {
                Toast.makeText(this, "Fields can't be null", Toast.LENGTH_SHORT).show();
            }
        });

        login.setOnClickListener(view -> {
            goLogin();
        });

    }

    private void goLogin() {
        Intent i=new Intent(User_signup.this, Login.class);
        startActivity(i);
        finish();
    }
}