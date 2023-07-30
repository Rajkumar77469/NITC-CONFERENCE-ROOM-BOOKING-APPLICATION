package com.example.communityd1.DataClasses;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.communityd1.AdminMain;
import com.example.communityd1.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.RoomHolder> {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    private Context context;
    private ArrayList<Room> rooms;

    String editDate="", editTime="";

    public RoomAdapter(Context context, ArrayList<Room> room) {
        this.context = context;
        this.rooms = room;
    }

    @NonNull
    @Override
    public RoomHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.room_recycler_view,parent,false);
        return new RoomHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.RoomHolder holder, int position) {

        int x=position;
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(context);

        Room room=rooms.get(position);
        holder.SetDetails(room,x);

        holder.book.setOnClickListener(view -> {
            View view1= LayoutInflater.from(context).inflate(R.layout.book_room, null);
            AlertDialog alertDialog=new AlertDialog.Builder(context).setView(view1).create();
            alertDialog.show();

//            EditText date=view1.findViewById(R.id.book_date);
//            EditText time=view1.findViewById(R.id.book_time);
            TextView limit=view1.findViewById(R.id.book_limit);
            EditText purpose=view1.findViewById(R.id.book_purpose);
            EditText faculty=view1.findViewById(R.id.booking_faculty);
            Button cancel=view1.findViewById(R.id.book_room_cancel);
            Button accept=view1.findViewById(R.id.book_room_accept);

            //copied start

            CardView dt = view1.findViewById(R.id.calendar_card);
            TextView show = view1.findViewById(R.id.date_view);

            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            LinearLayout ti=view1.findViewById(R.id.time_select);

            limit.setVisibility(View.GONE);
            purpose.setVisibility(View.GONE);
            faculty.setVisibility(View.GONE);
            cancel.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            ti.setVisibility(View.GONE);

            dt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DatePickerDialog dialog = new DatePickerDialog(view1.getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int day, int month, int year) {
                            calendar.set(Calendar.YEAR, day);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, year);
                            @SuppressLint("SimpleDateFormat") SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");

                            editDate = format.format(calendar.getTime());
                            show.setText("Date: "+ editDate);

                            limit.setVisibility(View.GONE);
                            purpose.setVisibility(View.VISIBLE);
                            faculty.setVisibility(View.VISIBLE);
                            cancel.setVisibility(View.VISIBLE);
                            accept.setVisibility(View.VISIBLE);
                            ti.setVisibility(View.VISIBLE);

                            firebaseFirestore=FirebaseFirestore.getInstance();

                            ArrayList<String> timings=new ArrayList<>();

                            timings.add("9:00 AM - 10:00 AM");
                            timings.add("10:00 AM - 11:00 AM");
                            timings.add("11:00 AM - 12:00 PM");
                            timings.add("12:00 PM - 1:00 PM");
                            timings.add("1:00 PM - 2:00 PM");
                            timings.add("2:00 PM - 3:00 PM");
                            timings.add("3:00 PM - 4:00 PM");
                            timings.add("4:00 PM - 5:00 PM");
                            timings.add("5:00 PM - 6:00 PM");
                            timings.add("6:00 PM - 7:00 PM");
                            timings.add("7:00 PM - 8:00 PM");

                            String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                            if(editDate.equals(date)){
                                String currentTime = new SimpleDateFormat("HH", Locale.getDefault()).format(new Date());
                                int t=Integer.parseInt(currentTime);
                                int x;
                                if(t>=0 && t<=8) x=0;
                                else if(t>=9 && t<=12) x=t-8;
                                else if(t>=13 && t<=19) x=t-8;
                                else x=11;

                                for(int i=0;i<x;i++) {
                                    if(!timings.isEmpty()) timings.remove(0);
                                }
                                if(timings.isEmpty()){
                                    limit.setVisibility(View.VISIBLE);
                                    purpose.setVisibility(View.GONE);
                                    faculty.setVisibility(View.GONE);
                                    cancel.setVisibility(View.GONE);
                                    accept.setVisibility(View.GONE);
                                    ti.setVisibility(View.GONE);
                                }
                            }
                            for(String s : timings) {
                                firebaseFirestore.collection("Booking confirmed")
                                        .document(room.getName()+editDate+s)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot=task.getResult();
                                                    if(documentSnapshot.exists()) {
                                                        timings.remove(s);
                                                    }
                                                }
                                            }
                                        });
                            }

                            AutoCompleteTextView autoCompleteSlots = view1.findViewById(R.id.auto_complete_time);
                            ArrayAdapter<String> adapterSlots = new ArrayAdapter<String>(view1.getContext(), R.layout.list_item, timings);

                            autoCompleteSlots.setAdapter(adapterSlots);

                            autoCompleteSlots.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    editTime = adapterView.getItemAtPosition(i).toString();
                                }
                            });
                        }
                    }, year, month, day);
                    dialog.getDatePicker().setMinDate(System.currentTimeMillis() +1000);
                    dialog.show();
                }
            });


            //copied end

            accept.setOnClickListener(v -> {
                String roomId=room.getName();
                String bDate=editDate;
                String bTime=editTime;
                String bPurpose=purpose.getText().toString();
                String currUser = firebaseAuth.getCurrentUser().getEmail();
                String fac = faculty.getText().toString();
                String reason = "";

                if(bDate.length()>0 && bTime.length()>0 && bPurpose.length()>0) {
                    DocumentReference documentReference=firebaseFirestore.collection("Booking requests").document();
                    String bookingId=documentReference.getId();
                    String id=roomId+bDate+bTime;
                    firebaseFirestore.collection("Booking confirmed")
                            .document(id)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if(task.isSuccessful()) {
                                        DocumentSnapshot documentSnapshot=task.getResult();
                                        if(documentSnapshot.exists()) {
                                            Toast.makeText(context, "A booking with same date and time already exist.", Toast.LENGTH_LONG).show();
                                            Toast.makeText(context, "Try booking another room.", Toast.LENGTH_SHORT).show();
                                        }
                                        else {
                                            BookingRequest b=new BookingRequest(bDate,bTime,bPurpose,currUser,roomId,bookingId,fac,reason);
                                            documentReference.set(b);
                                            Toast.makeText(context, "booking requested", Toast.LENGTH_LONG).show();
                                            alertDialog.cancel();
                                        }
                                    }
                                    else {
                                        Toast.makeText(context, "error getting data", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                else {
                    Toast.makeText(context, "Fields can't be null", Toast.LENGTH_SHORT).show();
                }
            });
            cancel.setOnClickListener(view2 -> {
                alertDialog.cancel();
            });
        });

    }

    @Override
    public int getItemCount() {
        return rooms.size();
    }

    class RoomHolder extends RecyclerView.ViewHolder {

        private TextView sno,name,capacity;
        private Button book;

        public RoomHolder(@NonNull View itemView) {
            super(itemView);
            sno=itemView.findViewById(R.id.room_recycler_sno);
            name=itemView.findViewById(R.id.room_recycler_rname);
            capacity=itemView.findViewById(R.id.room_recycler_rcap);
            book=itemView.findViewById(R.id.room_recycler_book);
        }

        void SetDetails(Room room,int x) {
            name.setText(room.getName());
            capacity.setText(room.getCapacity());
            String s=Integer.toString(x+1);
            sno.setText(s);
        }
    }

}
