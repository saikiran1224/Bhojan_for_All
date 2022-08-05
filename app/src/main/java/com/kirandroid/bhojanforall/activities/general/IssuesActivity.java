package com.kirandroid.bhojanforall.activities.general;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.modals.Report;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class IssuesActivity extends AppCompatActivity {
    public Button b;
    EditText email, report;
    private FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    Report rep;
    public String s1, s2;
    public String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    boolean connected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);

        email = (EditText) findViewById(R.id.issuesemail);
        report = (EditText) findViewById(R.id.issuesproblem);
        b = (Button) findViewById(R.id.issuessubmit);

        firebaseAuth = FirebaseAuth.getInstance();
        rep = new Report();
        ref = FirebaseDatabase.getInstance().getReference().child("Application_Issues");
        s1 = email.getText().toString().trim();
        s2 = report.getText().toString().trim();

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
            Toast.makeText(IssuesActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
        }

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                s1 = email.getText().toString().trim();
                s2 = report.getText().toString().trim();
                if (s1.isEmpty()) {
                    Toast.makeText(IssuesActivity.this, "Please enter Email", Toast.LENGTH_LONG).show();
                } else if (!s1.matches(emailPattern)) {
                    Toast.makeText(IssuesActivity.this, "Please enter a Valid Email ID", Toast.LENGTH_LONG).show();
                } else if (s2.isEmpty()) {
                    Toast.makeText(IssuesActivity.this, "Please enter the Problem", Toast.LENGTH_LONG).show();
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        //we are connected to a network
                        rep.setEmail(email.getText().toString().trim());
                        rep.setReport(report.getText().toString().trim());
                        ref.push().setValue(rep);
                        Toast.makeText(IssuesActivity.this, "Thank you, We will Contact you Shortly!", Toast.LENGTH_SHORT).show();
                        connected = true;
                        Intent intent = new Intent(IssuesActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    } else {
                        connected = false;
                        Toast.makeText(IssuesActivity.this, "Internet Unavailable", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Report Issues");
        }
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
