package com.example.communityd1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Profile extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        setTitle("Edit profile");
        Intent i=getIntent();
        String userType=i.getStringExtra("userType");

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        progressDialog.show();

        TextView userMail=findViewById(R.id.mail);
        CheckBox editStatus=findViewById(R.id.checkBox);
        EditText name=findViewById(R.id.userName);
        EditText contact=findViewById(R.id.userContact);
        Button cancel=findViewById(R.id.cancel_profile);
        Button accept=findViewById(R.id.accept_profile);
        Button changePW=findViewById(R.id.changePW);

        changePW.setOnClickListener(view -> {
            onChangePW();
        });

        boolean x;
        if(userType.equals("User"))
            x=true;
        else
            x=false;

        name.setEnabled(false);
        contact.setEnabled(false);
        cancel.setEnabled(false);
        accept.setEnabled(false);

        editStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b==true) {
                    if(x)
                        name.setEnabled(true);
                    contact.setEnabled(true);
                    cancel.setEnabled(true);
                    accept.setEnabled(true);
                }
                else {
                    name.setEnabled(false);
                    contact.setEnabled(false);
                    cancel.setEnabled(false);
                    accept.setEnabled(false);
                }
            }
        });

        String mail=firebaseAuth.getCurrentUser().getEmail();
        userMail.setText(mail);
        final String[] nm={"name", "contact"};

        firebaseFirestore.collection(userType)
                .document(mail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot=task.getResult();
                            if(documentSnapshot.exists()) {
                                nm[0]=documentSnapshot.get("name").toString();
                                nm[1]=documentSnapshot.get("contact").toString();
                                name.setText(nm[0]);
                                contact.setText(nm[1]);
                                progressDialog.cancel();
                            }
                            else {
                                Toast.makeText(Profile.this, "no matching user", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        }
                        else {
                            Toast.makeText(Profile.this, "error getting data", Toast.LENGTH_SHORT).show();
                            progressDialog.cancel();
                        }
                    }
                });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.show();
                nm[0]=name.getText().toString();
                nm[1]=contact.getText().toString();
                if(nm[0].length()>0 && nm[1].length()>0) {
                    try {
                        Long x=Long.valueOf(0);
                        if(nm[1].length()!=10)
                            x/=0;
                        x=Long.parseLong(nm[1]);
                        Map<String, String> mp=new HashMap<>();
                        mp.put("name",nm[0]);
                        mp.put("contact",nm[1]);
                        firebaseFirestore.collection(userType)
                                .document(mail)
                                .update((HashMap)mp).addOnSuccessListener(new OnSuccessListener() {
                                    @Override
                                    public void onSuccess(Object o) {
                                        Toast.makeText(Profile.this, "updated", Toast.LENGTH_SHORT).show();
                                        progressDialog.cancel();
                                        finish();
                                    }
                                });
                    }
                    catch (Exception e) {
                        Toast.makeText(Profile.this, "please enter 10 digit numeric value", Toast.LENGTH_SHORT).show();
                        progressDialog.cancel();
                    }
                }
                else {
                    Toast.makeText(Profile.this, "Fields can't be null", Toast.LENGTH_SHORT).show();
                    progressDialog.cancel();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name.setText(nm[0]);
                contact.setText(nm[1]);
                Toast.makeText(Profile.this, "Values reset", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onChangePW() {
        View v=getLayoutInflater().inflate(R.layout.change_password, null, false);
        AlertDialog alertDialog=new AlertDialog.Builder(Profile.this).setView(v).create();
        alertDialog.show();

        EditText curr=v.findViewById(R.id.currentPass);
        EditText newp=v.findViewById(R.id.newPass);
        EditText newpc=v.findViewById(R.id.newPassConfirm);
        Button cancel=v.findViewById(R.id.cancel);
        Button accept=v.findViewById(R.id.accept);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cPass=curr.getText().toString();
                String nPass=newp.getText().toString();
                String nPassC=newpc.getText().toString();

                if(cPass.length()>0 && nPass.length()>0 && nPassC.length()>0) {
                    if(nPass.equals(nPassC)) {
                        if(!nPass.equals(cPass)) {
                            progressDialog.show();
                            FirebaseUser user=firebaseAuth.getCurrentUser();
                            String email=user.getEmail();
                            AuthCredential authCredential= EmailAuthProvider.getCredential(email,cPass);
                            user.reauthenticate(authCredential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                user.updatePassword(nPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if(task.isSuccessful()) {
                                                            Toast.makeText(Profile.this, "password updated successfully.", Toast.LENGTH_SHORT).show();
                                                            alertDialog.cancel();
                                                            progressDialog.cancel();
                                                        }
                                                        else {
                                                            Toast.makeText(Profile.this, "password should be atleast 6 characters", Toast.LENGTH_SHORT).show();
                                                            progressDialog.cancel();
                                                        }
                                                    }
                                                });
                                            }
                                            else {
                                                Toast.makeText(Profile.this, "enter current password correctly.", Toast.LENGTH_SHORT).show();
                                                progressDialog.cancel();
                                            }
                                        }
                                    });
                        }
                        else {
                            Toast.makeText(Profile.this, "current and new password are same.", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(Profile.this, "new passwords doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(Profile.this, "fields can't be null", Toast.LENGTH_SHORT).show();
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