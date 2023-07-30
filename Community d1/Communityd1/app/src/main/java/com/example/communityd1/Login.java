package com.example.communityd1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setTitle("LOGIN");


        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);

        EditText email=findViewById(R.id.lmail);
        EditText pass=findViewById(R.id.lpass);

        Button login=findViewById(R.id.login);
        TextView go_signup=findViewById(R.id.go_signup);
        Button forgot=findViewById(R.id.forgot_password);

        login.setOnClickListener(view -> {
            String em=email.getText().toString();
            String ps=pass.getText().toString();
            final int[] f = {0};
            if(em.length()>0 && ps.length()>0) {
                progressDialog.show();
                firebaseAuth.signInWithEmailAndPassword(em,ps)
                        .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                            @Override
                            public void onSuccess(AuthResult authResult) {
                                firebaseUser=firebaseAuth.getCurrentUser();
                                if(firebaseUser.isEmailVerified()) {
                                    firebaseFirestore.collection("Admin")
                                            .document(em)
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot documentSnapshot = task.getResult();
                                                        if (documentSnapshot.exists()) {
                                                            Intent i = new Intent(Login.this, AdminMain.class);
                                                            startActivity(i);
                                                            progressDialog.cancel();
                                                            finish();
                                                        }
                                                        else {
                                                            firebaseFirestore.collection("Signup Requests")
                                                                    .document(em)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                            DocumentSnapshot dSnapshot = task.getResult();
                                                                            if (dSnapshot.exists()) {
                                                                                Toast.makeText(Login.this, "please wait for your profile approval", Toast.LENGTH_SHORT).show();
                                                                                firebaseAuth.signOut();
                                                                                progressDialog.cancel();
                                                                            }
                                                                            else {
                                                                                Intent i = new Intent(Login.this, MainActivity.class);
                                                                                startActivity(i);
                                                                                progressDialog.cancel();
                                                                                finish();
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                    else {
                                                        Toast.makeText(Login.this, "Error getting data", Toast.LENGTH_LONG).show();
                                                        progressDialog.cancel();
                                                    }
                                                }
                                            });
                                }
                                else {
                                    Toast.makeText(Login.this, "please verify your email first", Toast.LENGTH_SHORT).show();
                                    firebaseAuth.signOut();
                                    progressDialog.cancel();
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
            }
            else {
                Toast.makeText(this, "Fields can't be null", Toast.LENGTH_SHORT).show();
            }
        });

        go_signup.setOnClickListener(view -> {
            Intent i=new Intent(Login.this, User_signup.class);
            startActivity(i);
        });

        forgot.setOnClickListener(view -> {
            String em=email.getText().toString();

            if(em.length()>0) {
                progressDialog.show();

                firebaseAuth.sendPasswordResetEmail(em)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.cancel();
                                Toast.makeText(Login.this,"Password reset mail sent",Toast.LENGTH_LONG).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressDialog.cancel();
                                Toast.makeText(Login.this,e.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });
            }
            else
                Toast.makeText(this, "please enter email field", Toast.LENGTH_SHORT).show();

        });

    }
}