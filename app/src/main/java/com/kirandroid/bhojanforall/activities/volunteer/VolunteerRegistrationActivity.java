package com.kirandroid.bhojanforall.activities.volunteer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.activities.general.MainActivity;
import com.kirandroid.bhojanforall.modals.Volunteer;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VolunteerRegistrationActivity extends AppCompatActivity {
    public Button b;
    EditText p1, p2, em, phone, name, add;
    String s1, s2, s3 = "";
    private FirebaseAuth firebaseAuth;
    DatabaseReference reff;
    private ProgressDialog progressDialog;
    Volunteer volunteer;
    boolean connected = false;
    int count = 0;
    String TAG = "TOKENS_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        name = (EditText) findViewById(R.id.volunregnam);
        em = (EditText) findViewById(R.id.volunregemail);
        p1 = (EditText) findViewById(R.id.volunregpwd);
        phone = (EditText) findViewById(R.id.volunregphn);
        b = (Button) findViewById(R.id.volunregsubmit);
        TextView singin = findViewById(R.id.volunregsignIn);

        volunteer = new Volunteer();
        reff = FirebaseDatabase.getInstance().getReference().child("Volunteers");
        //Network Service State
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else {
            connected = false;
            Toast.makeText(VolunteerRegistrationActivity.this, "Network Unavailable", Toast.LENGTH_SHORT).show();
        }

        //Buttons activity
        singin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(VolunteerRegistrationActivity.this, VolunteerLoginActivity.class);
                startActivity(in);
            }
        });
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String n = name.getText().toString().trim();
                String ph = phone.getText().toString();
                String e = em.getText().toString().trim();
                String p = p1.getText().toString().trim();
                int count = 0;
                SmsManager sms = SmsManager.getDefault();

                String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

                if (n.isEmpty()) {
                    // Toast.makeText(RegistrationActivity.this, "Please Enter Name", Toast.LENGTH_LONG).show();
                    name.setError("Please Enter Name ");
                } else if (!e.matches(emailPattern) || e.isEmpty()) {
                    //Toast.makeText(RegistrationActivity.this, "Please Enter Valid Email", Toast.LENGTH_LONG).show();
                    em.setError("Please enter Valid Email");
                } else if (p.isEmpty()) {
                    Toast.makeText(VolunteerRegistrationActivity.this, "Please Enter Password", Toast.LENGTH_LONG).show();
                    // p1.setError("Please enter Password");
                } else if (p.length() < 8) {
                    Toast.makeText(VolunteerRegistrationActivity.this, "Password should be more than 8 characters", Toast.LENGTH_LONG).show();
                    // p1.setError("Password should be more than 8 Characters");
                } else if (ph.isEmpty()) {
                    //Toast.makeText(RegistrationActivity.this, "Please Enter Phone Number", Toast.LENGTH_LONG).show();
                    phone.setError("Please Enter Phone Number");
                } else if (ph.length() < 10) {
                    phone.setError("Phone Number is Not Valid !");
                } else {
                    if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                            connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                        registerVolunteer(e, p, ph, n);
                        String msg = "Welcome  " + n + ",\nWe feel very Happy to see you Here.\nYou will be notified when there is a Donation\n\n Happy Volunteering !";
                        Intent broadcastIntent = new Intent(VolunteerRegistrationActivity.this, VolunteerRegistrationActivity.class);
                        broadcastIntent.putExtra("toastMessage", "Hi man !");
                        //startActivity(broadcastIntent);
                        PendingIntent actionIntent = PendingIntent.getBroadcast(VolunteerRegistrationActivity.this,
                                0, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_arrow_forward_white);
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                                VolunteerRegistrationActivity.this
                        )
                                .setSmallIcon(R.drawable.logotrans)
                                .setContentTitle("Thank You for Registering !")
                                .setContentText(msg)
                                .setAutoCancel(true)
                                .setWhen(System.currentTimeMillis())
                                .setDefaults(Notification.DEFAULT_LIGHTS)
                                .setVibrate(new long[]{0, 500, 1000})
                                .setPriority(NotificationCompat.PRIORITY_HIGH)
                                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                                /*.setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(getString(R.string.message))
                                        .setBigContentTitle("Thank You for Registering !")
                                        .setSummaryText("Registration"))*/
                                .setLargeIcon(largeIcon);

                        Intent intent1 = new Intent(VolunteerRegistrationActivity.this, VolunteerLoginActivity.class);
                        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent1.putExtra("message", msg);

                        PendingIntent pendingIntent = PendingIntent.getActivity(VolunteerRegistrationActivity.this,
                                0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
                        builder.setContentIntent(pendingIntent);


                        NotificationManager notificationManager = (NotificationManager) getSystemService(
                                Context.NOTIFICATION_SERVICE
                        );
                        notificationManager.notify(1, builder.build());

                        progressDialog.dismiss();
                    } else {
                        Toast.makeText(VolunteerRegistrationActivity.this, "Internet Unavailable", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Volunteer Registration");
        }
    }

    private boolean isValidEmail(String Emailid) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(Emailid);
        return matcher.matches();
    }

    private Intent getNotificationIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return intent;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void registerVolunteer(String email, String password, String ph, String n) {

        progressDialog.setMessage("Registering, Please Wait...");
        progressDialog.show();
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            progressDialog.dismiss();
                            reff.push().setValue(new Volunteer(n, email, ph, count, "Volunteer"));
                            Intent i = new Intent(VolunteerRegistrationActivity.this, VolunteerLoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            Toast.makeText(VolunteerRegistrationActivity.this, "Volunteer Created Successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            try {
                                progressDialog.dismiss();
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                progressDialog.dismiss();
                                Toast.makeText(VolunteerRegistrationActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();

                            } catch (FirebaseAuthUserCollisionException existEmail) {
                                progressDialog.dismiss();
                                Toast.makeText(VolunteerRegistrationActivity.this, "Volunteer Email Id Already Exists", Toast.LENGTH_SHORT).show();

                            } catch (Exception e) {
                                progressDialog.dismiss();
                                Toast.makeText(VolunteerRegistrationActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }
}
