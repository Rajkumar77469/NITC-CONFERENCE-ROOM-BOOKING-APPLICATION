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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class BookingRequestConfirmedAdapter extends RecyclerView.Adapter<BookingRequestConfirmedAdapter.BookingRequestConfirmedHolder> {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    private Context context;
    private ArrayList<BookingRequest> requests;

    public BookingRequestConfirmedAdapter(Context context, ArrayList<BookingRequest> requests) {
        this.context = context;
        this.requests = requests;
    }

    @NonNull
    @Override
    public BookingRequestConfirmedAdapter.BookingRequestConfirmedHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.booking_request_recycler_view,parent,false);
        return new BookingRequestConfirmedAdapter.BookingRequestConfirmedHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull BookingRequestConfirmedAdapter.BookingRequestConfirmedHolder holder, int position) {

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
            EditText suggest=view1.findViewById(R.id.book_suggest);
            TextView suggest1=view1.findViewById(R.id.book_suggest_text);
            Button accept=view1.findViewById(R.id.book_room_accept);
            Button reject=view1.findViewById((R.id.book_room_reject));

            date.setText("Date : "+request.getDate());
            time.setText("Time : "+request.getTime());
            purpose.setText("Purpose : "+request.getPurpose());
            room.setText(("Room id : "+request.getRoomId()));
            client.setText("Client : "+request.getClient());


            if(request.getFaculty()!=null)
                faculty.setText(("Faculty : "+request.getFaculty()));
            else
                faculty.setText("Faculty : ");


            suggest1.setVisibility(View.GONE);
            suggest.setVisibility(View.GONE);
            accept.setVisibility(View.GONE);
            reject.setVisibility(View.GONE);
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }



    class BookingRequestConfirmedHolder extends RecyclerView.ViewHolder {

        private TextView roomId,date,req_no;
        private Button viewDetails;

        public BookingRequestConfirmedHolder(@NonNull View itemView) {
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
