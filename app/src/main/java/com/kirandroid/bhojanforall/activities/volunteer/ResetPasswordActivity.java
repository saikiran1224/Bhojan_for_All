package com.kirandroid.bhojanforall.activities.volunteer;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Volunteer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ResetPasswordActivity extends AppCompatActivity {

    Button send;
    EditText reset;
    TextView af;
    FirebaseAuth firebaseAuth;
    boolean connected = false;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        reset = (EditText) findViewById(R.id.respwdemail);
        send = (Button) findViewById(R.id.respwdsend);
        af = (TextView) findViewById(R.id.respwdafter);

        firebaseAuth = FirebaseAuth.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Volunteer Reset Password");
        }
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
        }

        if (!connected) {
            Toast.makeText(ResetPasswordActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
        }

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = reset.getText().toString().trim();
                if (email.equals("")) {
                    Toast.makeText(ResetPasswordActivity.this, "Please Enter Email", Toast.LENGTH_SHORT).show();
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        databaseReference = FirebaseDatabase.getInstance().getReference("Volunteers");
                        Query query = databaseReference.orderByChild("email").equalTo(email);
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()) {
                                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                        final String category = dataSnapshot1.getValue(Volunteer.class).getCategory();
                                        //   Toast.makeText(RecipientLoginActivity.this, "" + category, Toast.LENGTH_SHORT).show();
                                        if (category.equals("Volunteer")) {
                                            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        //Toast.makeText(ResetPassword.this,"Reset Link send to Email",Toast.LENGTH_SHORT).show();
                                                        //startActivity(new Intent(ResetPassword.this,LoginActivity.class));
                                                        af.setText("An Reset Password link is sent to \nYour Registered Email Address");
                                                    } else {
                                                        String error = task.getException().getMessage();
                                                        Toast.makeText(ResetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        } else {
                                            Toast.makeText(ResetPasswordActivity.this, "Invalid User !!!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } else {
                                    Toast.makeText(ResetPasswordActivity.this, "Invalid User !!!", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(ResetPasswordActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    } else {
                        Toast.makeText(ResetPasswordActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                    }

                }
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
