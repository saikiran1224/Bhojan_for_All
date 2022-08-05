package com.kirandroid.bhojanforall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.kirandroid.bhojanforall.modals.Fooddetails;
import com.kirandroid.bhojanforall.modals.Volunteer;
import com.kirandroid.bhojanforall.utilities.MyAppPrefsManager;
import com.kirandroid.bhojanforall.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class VolunteerAdapter extends RecyclerView.Adapter<VolunteerAdapter.MyViewHolder> implements AdapterView.OnItemSelectedListener {

    private Context context;
    private List<Fooddetails> fooddetails;
    private DatabaseReference myRef, databaseReference;
    private MyAppPrefsManager myAppPrefsManager;

    public VolunteerAdapter(Context c, List<Fooddetails> f) {
        context = c;
        fooddetails = f;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.card, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        myAppPrefsManager = new MyAppPrefsManager(context);
        String email = myAppPrefsManager.getUserName();

        myRef = FirebaseDatabase.getInstance().getReference("Volunteers");
        myRef.keepSynced(true);

        databaseReference = FirebaseDatabase.getInstance().getReference("Food_Details");
        databaseReference.keepSynced(true);


        String donorname = fooddetails.get(position).getName();
        String donorphone = fooddetails.get(position).getPhone();
        String donoradd = fooddetails.get(position).getAddress();
        String donorstatus = fooddetails.get(position).getStatus();
        String place = fooddetails.get(position).getPlace();
        String tim = fooddetails.get(position).getTime();
        String noofpeople = fooddetails.get(position).getNo_of_people();
        String dondate = fooddetails.get(position).getDate();


        holder.name.setText(donorname);
        holder.phone.setText(donorphone);
        holder.address.setText(donoradd);
        holder.status1.setText(donorstatus);
        holder.type.setText(place);


        try {

            holder.address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String map = "http://maps.google.co.in/maps?q=" + donoradd;
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse(map));
                    context.startActivity(intent1);
                }
            });

            holder.phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", donorphone, null));
                    context.startActivity(intent);
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }


        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        try {
            Date date = inputFormat.parse(fooddetails.get(position).getDate());
            assert date != null;
            String niceDateStr = String.valueOf(DateUtils.getRelativeTimeSpanString(date.getTime(), Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS));
            holder.timesAgo.setText(niceDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        holder.book.setVisibility(View.VISIBLE);
        if (fooddetails.get(position).getStatus().equals("Booked")) {
            holder.book.setVisibility(View.GONE);
        }
        if (fooddetails.get(position).getStatus().isEmpty()) {
            holder.book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    AlertDialog.Builder adb = new AlertDialog.Builder(context);
                    adb.setTitle("Are you sure you want to Proceed ?");
                    adb.setMessage("By clicking OK, you need to pickup food from Donor's Doorstep directly.Please feel free to contact the Donor");
                    adb.setPositiveButton("OK", (dialog, which) -> {

                        final Query query = myRef.orderByChild("email").equalTo(email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot datas : dataSnapshot.getChildren()) {

                                    String key = datas.getKey();
                                    assert key != null;

                                    String volname = datas.getValue(Volunteer.class).getName();
                                    //volphone[0] = datas.getValue(Volunteer.class).getPhone();
                                    String key1 = myRef.push().getKey();
                                    assert key1 != null;
                                    myRef.child(key).child("bookedData").child(key1).setValue(

                                            new Fooddetails(donorname, donorphone, donoradd, place, "Went to Deliver", tim, dondate, noofpeople)
                                    );
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        final Query que = databaseReference.orderByChild("name").equalTo(holder.name.getText().toString());
                        //Toast.makeText(context, ""+name.getText().toString(), Toast.LENGTH_SHORT).show();
                        que.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot stat : dataSnapshot.getChildren()) {
                                    stat.getRef().child("status").setValue("Booked");
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(context, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


                        openDialog(position);



                       /* SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(donorphone, null, message, null, null);
*/


                    });
                    adb.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // dialog.cancel();
                            dialog.dismiss();
                        }
                    });
                    adb.show();

                    //notifyDataSetChanged();
                    //holder.book.setVisibility(View.GONE);


                }


            });


        }
    }


    @Override
    public int getItemCount() {

        return (null != fooddetails ? fooddetails.size() : 0);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }


    private void openDialog(int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Thank You for your Interest")
                .setMessage("Please find the below details for your reference " + "\n"
                        + "Name : " + fooddetails.get(position).getName() + "\n"
                        + "Mobile : " + fooddetails.get(position).getPhone() + "\n"
                        + "Location : " + fooddetails.get(position).getAddress())
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String donor_no = fooddetails.get(position).getPhone();
                        String message = "Food Robin is on his way to Pickup Food from your Doorstep.Please feel free to contact this Number";
                        Uri sendSmsTo = Uri.parse("smsto:" + donor_no);
                        Intent intent = new Intent(
                                Intent.ACTION_SENDTO, sendSmsTo);
                        intent.putExtra("sms_body", message);
                        context.startActivity(intent);
                        //context.startActivity(new Intent(context, MainActivity.class));


                    }
                });
        builder.show();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, phone, type, address, status1, timesAgo;
        Button book;

        public MyViewHolder(@NonNull final View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.voluncardname);
            phone = (TextView) itemView.findViewById(R.id.voluncardphone);
            type = (TextView) itemView.findViewById(R.id.voluncardtype);
            address = (TextView) itemView.findViewById(R.id.voluncardaddress);
            book = (Button) itemView.findViewById(R.id.voluncardbook);
            status1 = (TextView) itemView.findViewById(R.id.voluncardstatus);
            timesAgo = (TextView) itemView.findViewById(R.id.voluncardtimesAgo);

            //book.setEnabled(true);
        }
    }

    public void filterList(List<Fooddetails> filteredList) {
        fooddetails = filteredList;
        notifyDataSetChanged();
    }

}
