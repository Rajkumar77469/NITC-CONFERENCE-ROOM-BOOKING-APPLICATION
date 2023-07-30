package com.example.communityd1.DataClasses;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityd1.R;
import com.example.communityd1.User_requests;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class SignupRequestsAdapter extends RecyclerView.Adapter<SignupRequestsAdapter.SignupRequestsHolder> {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    private Context context;
    private ArrayList<SignupRequests> requests;

    public SignupRequestsAdapter(Context context, ArrayList<SignupRequests> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public SignupRequestsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.signup_request_recycler_view,parent,false);
        return new SignupRequestsHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull SignupRequestsHolder holder, int position) {

        int x=position;
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(context);

        SignupRequests request=requests.get(position);
        holder.SetDetails(request,x);

        holder.accept.setOnClickListener(view -> {
            progressDialog.show();
            String mail=request.getMail();
            String name=request.getName();
            String contact=request.getContact();

            firebaseFirestore.collection("Admin")
                    .document(mail)
                    .set(new User(mail,name,contact)).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                firebaseFirestore.collection("Signup Requests")
                                        .document(mail)
                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(context, request.getMail()+" accepted", Toast.LENGTH_LONG).show();
                                                progressDialog.cancel();
                                                Intent i=new Intent(context, User_requests.class);
                                                i.putExtra("callType","refresh");
                                                context.startActivity(i);
                                                ((Activity) context).finish();
                                            }
                                        });
                            }
                            else {
                                progressDialog.cancel();
                                Toast.makeText(context, "error occurred", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });

        holder.reject.setOnClickListener(view -> {
            progressDialog.show();
            String s=request.getMail();
            firebaseFirestore.collection("Signup Requests")
                    .document(s)
                    .delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                progressDialog.cancel();
                                Toast.makeText(context, request.getMail()+" rejected", Toast.LENGTH_LONG).show();
                                Intent i=new Intent(context, User_requests.class);
                                i.putExtra("callType","refresh");
                                context.startActivity(i);
                                ((Activity) context).finish();
                            }
                            else {
                                progressDialog.cancel();
                                Toast.makeText(context, "error occurred", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }



    class SignupRequestsHolder extends RecyclerView.ViewHolder {

        private TextView email,req_no;
        private Button accept,reject;

        public SignupRequestsHolder(@NonNull View itemView) {
            super(itemView);

            req_no=itemView.findViewById(R.id.request_number);
            email=itemView.findViewById(R.id.remail);
            accept=itemView.findViewById(R.id.accept_request);
            reject=itemView.findViewById(R.id.reject_request);

        }

        void SetDetails(SignupRequests requests, int x) {

            email.setText(requests.getMail());
            String s=Integer.toString(x+1);
            req_no.setText(s);
        }
    }

}
