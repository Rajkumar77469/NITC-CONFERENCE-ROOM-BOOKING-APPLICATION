package com.example.communityd1.DataClasses;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityd1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class BookingRequestAdapter extends RecyclerView.Adapter<BookingRequestAdapter.BookingRequestHolder> {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    private Context context;
    private ArrayList<BookingRequest> requests;

    public BookingRequestAdapter(Context context, ArrayList<BookingRequest> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public BookingRequestAdapter.BookingRequestHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.booking_request_recycler_view,parent,false);
        return new BookingRequestAdapter.BookingRequestHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BookingRequestAdapter.BookingRequestHolder holder, int position) {

        int x=position;
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(context);

        BookingRequest request=requests.get(position);
        holder.SetDetails(request,x);

        holder.viewDetails.setOnClickListener(view -> {
            View view1= LayoutInflater.from(context).inflate(R.layout.booking_details, null);
            AlertDialog alertDialog=new AlertDialog.Builder(context).setView(view1).create();
            alertDialog.show();

            TextView date=view1.findViewById(R.id.book_date);
            TextView time=view1.findViewById(R.id.book_time);
            TextView purpose=view1.findViewById(R.id.book_purpose);
            TextView room=view1.findViewById(R.id.book_roomId);
            TextView client=view1.findViewById(R.id.book_client);
            TextView faculty=view1.findViewById(R.id.book_faculty);
            TextView suggest=view1.findViewById(R.id.book_suggest_text);
            EditText suggest1=view1.findViewById(R.id.book_suggest);
            Button accept=view1.findViewById(R.id.book_room_accept);
            Button reject=view1.findViewById((R.id.book_room_reject));

            suggest.setVisibility(View.GONE);

            date.setText("Date : "+request.getDate());
            time.setText("Time : "+request.getTime());
            purpose.setText("Purpose : "+request.getPurpose());
            room.setText(("Room id : "+request.getRoomId()));
            client.setText("Client : "+request.getClient());

            if(request.getFaculty()!=null)
                faculty.setText(("Faculty : "+request.getFaculty()));
            else
                faculty.setText("Faculty : ");


            accept.setOnClickListener(v -> {
                String id=request.getRoomId()+request.getDate()+request.getTime();
                String delId=request.getBookingId();
                firebaseFirestore.collection("Booking confirmed")
                        .document(id)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot=task.getResult();
                                    if(documentSnapshot.exists()) {
                                        Toast.makeText(context, "A booking with same date and time exists.", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        progressDialog.show();
                                        firebaseFirestore.collection("Booking confirmed")
                                                .document(id)
                                                .set(new BookingRequest(request.getDate(),request.getTime(),request.getPurpose(),request.getClient(),request.getRoomId(),request.getBookingId(), request.getFaculty(), request.getReason()));

                                        firebaseFirestore.collection("Booking requests")
                                                .document(delId)
                                                .delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {
                                                    }
                                                });
                                        progressDialog.cancel();
                                        alertDialog.cancel();
                                        Toast.makeText(context, "accepted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                else {
                                    Toast.makeText(context, "error getting data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            });

            reject.setOnClickListener(v-> {
                String id=request.getBookingId();
                progressDialog.show();
                String reason=suggest1.getText().toString();
                    firebaseFirestore.collection("Booking rejected")
                            .document(id)
                            .set(new BookingRequest(request.getDate(),request.getTime(),request.getPurpose(),request.getClient(),request.getRoomId(),request.getBookingId(), request.getFaculty(), reason));

                    firebaseFirestore.collection("Booking requests")
                            .document(id)
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                }
                            });
//                notifyItemRemoved(position);
                    progressDialog.cancel();
                    alertDialog.cancel();
                    Toast.makeText(context, "rejected", Toast.LENGTH_SHORT).show();
                });

            });
        }


    @Override
    public int getItemCount() {
        return requests.size();
    }



    class BookingRequestHolder extends RecyclerView.ViewHolder {

        private TextView roomId,date,req_no;
        private Button viewDetails;

        public BookingRequestHolder(@NonNull View itemView) {
            super(itemView);

            req_no=itemView.findViewById(R.id.book_recycler_sno);
            roomId=itemView.findViewById(R.id.book_recycler_roomId);
            date=itemView.findViewById(R.id.book_recycler_date);
            viewDetails=itemView.findViewById(R.id.book_recycler_view);

        }

        void SetDetails(BookingRequest requests, int x) {

            roomId.setText(requests.getRoomId());
            date.setText(requests.getDate());
            String s=Integer.toString(x+1);
            req_no.setText(s);
        }
    }

}
