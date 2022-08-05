package com.kirandroid.bhojanforall.recyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kirandroid.bhojanforall.adapter.GalleryAdapter;
import com.kirandroid.bhojanforall.modals.Image;
import com.kirandroid.bhojanforall.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecyclerViewGallery extends AppCompatActivity {

    DatabaseReference databaseReference;
    String doc, deg, photo, number;
    androidx.recyclerview.widget.RecyclerView recyclerView;
    ListView listView;
    ArrayList<Image> list;
    GalleryAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recycler_view__gallery);


        final ProgressDialog progressDialog
                = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        recyclerView = (androidx.recyclerview.widget.RecyclerView) findViewById(R.id.myrecycler);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Gallery");
        }

        list = new ArrayList<Image>();

        databaseReference = FirebaseDatabase.getInstance().getReference("Image_Gallery");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                        Image d = dataSnapshot1.getValue(Image.class);
                        list.add(d);
                    }
                    progressDialog.dismiss();
                    myAdapter = new GalleryAdapter(RecyclerViewGallery.this, list);
                    recyclerView.setAdapter(myAdapter);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(RecyclerViewGallery.this, "No Photos to Show", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
