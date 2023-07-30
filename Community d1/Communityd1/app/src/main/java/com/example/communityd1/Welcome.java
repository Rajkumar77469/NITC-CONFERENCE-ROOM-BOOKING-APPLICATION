package com.example.communityd1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class Welcome extends AppCompatActivity {

    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    private static int TIME_OUT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();

        ProgressBar progressBar=findViewById(R.id.HomeProgressBar);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                check_login_status();
            }
        }, TIME_OUT);
    }

    void check_login_status() {
        final int[] f = {0};
        if(firebaseUser!=null) {
            firebaseFirestore.collection("Admin")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            String em=firebaseUser.getEmail();
                            for(QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if(documentSnapshot.get("email").equals(em)) {
                                    Intent i=new Intent(Welcome.this, AdminMain.class);
                                    startActivity(i);
                                    f[0] =1;
                                    finish();
                                }
                            }
                            if(f[0]==0) {
                                Intent i=new Intent(Welcome.this, MainActivity.class);
                                startActivity(i);
                                f[0] =1;
                                finish();
                            }
                        }
                    });
        }
        else {
            Intent i=new Intent(Welcome.this, Login.class);
            startActivity(i);
            finish();
        }
    }

}