package com.kirandroid.bhojanforall.activities.general;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.kirandroid.bhojanforall.R;
import com.kirandroid.bhojanforall.utilities.DetectConnection;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SplashScreenActivity extends AppCompatActivity {

    ProgressBar progressBar;
    final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

    //MyAppPrefsManager myAppPrefsManager;
    @Override
    protected void onPause() {
        super.onPause();

    }

    Button tryAgain;
    // ImageView splashImage;
    TextView errorText;
    LinearLayout internetNotAvailable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getSupportActionBar().hide();
        progressBar = findViewById(R.id.progressBar);
        //myAppPrefsManager=new MyAppPrefsManager(this);
        tryAgain = findViewById(R.id.tryAgain);
        errorText = findViewById(R.id.errorText);
        internetNotAvailable = findViewById(R.id.internetNotAvailable);

        // Check the internet and get response from API's
        if (DetectConnection.checkInternetConnection(getApplicationContext())) {
            // Start MyTask after 3 seconds
            //progressBar.setVisibility(View.VISIBLE);
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (Build.VERSION.SDK_INT >= 23) {
                        // Marshmallow+
                        SplashScreenActivity.this.permissioncheck();

                    } else {
                        // Pre-Marshmallow
                        SplashScreenActivity.this.LaunchApp();
                    }
                }
            }, 3000);
        } else {
            errorText.setText(getString(R.string.no_internet_connection_available));
            internetNotAvailable.setVisibility(View.VISIBLE);
            // splashImage.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }


        tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DetectConnection.checkInternetConnection(getApplicationContext())) {
                    internetNotAvailable.setVisibility(View.GONE);
                    //    splashImage.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.VISIBLE);
                    // Start MyTask after 3 seconds
                    //progressBar.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (Build.VERSION.SDK_INT >= 23) {
                                // Marshmallow+
                                SplashScreenActivity.this.permissioncheck();

                            } else {
                                // Pre-Marshmallow
                                SplashScreenActivity.this.LaunchApp();
                            }
                        }
                    }, 3000);
                } else {
                    errorText.setText(getString(R.string.no_internet_connection_available));
                    internetNotAvailable.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }
        });


    }

//-************************ permission check ***********************************************************************

    private void permissioncheck() {
        List<String> permissionsNeeded = new ArrayList<>();

        final List<String> permissionsList = new ArrayList<>();
        if (addPermission(permissionsList, Manifest.permission.READ_EXTERNAL_STORAGE))
            permissionsNeeded.add("READ");
       /* if (addPermission(permissionsList, Manifest.permission.SEND_SMS))
            permissionsNeeded.add("SEND_SMS");*/
        if (addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
            permissionsNeeded.add("WRITE");
        if (addPermission(permissionsList, Manifest.permission.CAMERA))
            permissionsNeeded.add("CAMERA");

        if (addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("ACCESS_FINE_LOCATION");

        if (addPermission(permissionsList, Manifest.permission.ACCESS_COARSE_LOCATION))
            permissionsNeeded.add("ACCESS_COARSE_LOCATION");
        if (addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("READ_PHONE_STATE");

        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                StringBuilder message = new StringBuilder("You need to grant access to " + permissionsNeeded.get(0));
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message.append(", ").append(permissionsNeeded.get(i));

                showMessageOKCancel(message.toString(),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (Build.VERSION.SDK_INT >= 23) {
                                    // Marshmallow+
                                    SplashScreenActivity.this.requestPermissions(permissionsList.toArray(new String[0]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);


                                }

                            }
                        });

                return;
            }

            if (Build.VERSION.SDK_INT >= 23) {
                // Marshmallow+
                requestPermissions(permissionsList.toArray(new String[0]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);


            }


        } else {
            // Toast.makeText(this,"Permission",Toast.LENGTH_LONG).show();
            LaunchApp();
        }

        //insertDummyContact();
    }


    private boolean addPermission(List<String> permissionsList, String permission) {

        boolean cond;
        if (Build.VERSION.SDK_INT >= 23) {
            // Marshmallow+
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsList.add(permission);
                // Check for Rationale Option
                shouldShowRequestPermissionRationale(permission);//  return false;
            }
            //  return true;

            cond = true;


        } else {
            // Pre-Marshmallow
            cond = true;
        }

        return cond;

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SplashScreenActivity.this);

        alertDialog.setMessage(message);
        alertDialog.setPositiveButton("OK", okListener);
        alertDialog.setNegativeButton("Cancel", (dialog, which) -> finish());

        AlertDialog alert = alertDialog.create();
        alert.show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        //Checking the request code of our request
        if (requestCode == 23) {

            //If permission is granted
            //Displaying a toast
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this, "Permission granted", Toast.LENGTH_LONG).show();
            else {
                //Displaying another toast if permission is not granted
                Toast.makeText(this, "Permission Needed To Run The App", Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS) {
            Map<String, Integer> perms = new HashMap<>();
            // Initial
            perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            /*perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);*/
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.ACCESS_COARSE_LOCATION, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);


            // Fill with results
            for (int i = 0; i < permissions.length; i++)
                perms.put(permissions[i], grantResults[i]);
            // Check for ACCESS_FINE_LOCATION
            if (perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED
                    && perms.get(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // All Permissions Granted
                // insertDummyContact();

                //Toast.makeText(SplashScreenActivity.this, " Permissions are l", Toast.LENGTH_SHORT).show();
                LaunchApp();

            } else {
                // Permission Denied
                Toast.makeText(SplashScreenActivity.this, "Some Permission is Denied", Toast.LENGTH_SHORT)
                        .show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //Do something after 100
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", SplashScreenActivity.this.getPackageName(), null);
                        intent.setData(uri);
                        SplashScreenActivity.this.startActivityForResult(intent, REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                        SplashScreenActivity.this.finish();
                    }
                }, 3000);
            }

        }
    }

    public void LaunchApp() {
        Thread background = new Thread() {
            public void run() {

                try {
                    // Thread will sleep for 10 seconds
                    sleep(3 * 1000);


                    Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };

        // start thread
        background.start();


    }
}
