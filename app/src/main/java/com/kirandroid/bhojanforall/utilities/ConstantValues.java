package com.kirandroid.bhojanforall.utilities;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import androidx.core.app.ShareCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.kirandroid.bhojanforall.receiver.NetworkStateChangeReceiver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ConstantValues contains some constant variables, used all over the App.
 **/


public class ConstantValues {

    public static boolean IS_USER_LOGGED_IN;
    public static final String TYPE_TEXT_PLAIN = "text/plain";
    public static final String SHARE = "Share";
    public static final String SINGLE_HYPHEN = "-";

    public final static String AUTH_KEY_FCM = "AAAAtzzcl_0:APA91bFs1dfhLsTLg1H-idacozPd7ScC6Dyxjt_Q9KYMqPguD4iHKjdeBnFlml7JkuDfIfHmCGibVoYNaqhUbqlYHi7LrWPkTQuRZevLBSZL3OPF02C2_DNUKi50-_U0KacXQ-T9J9s-";

    /*   Validating Fileds */
    // Validating email id
    public static boolean isValidEmail(String email1) {

        String EMAIL_PATTERN = "^([_A-Za-z0-9-+].{2,})+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";


        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email1);
        return !matcher.matches();

    }


    public static void shareDeepLink(Activity activity, String str) {
        if (activity != null) {
            Intent intent = ShareCompat.IntentBuilder.from(activity).setType(TYPE_TEXT_PLAIN).setText(str).setChooserTitle((CharSequence) SHARE).getIntent();
            if (intent.resolveActivity(activity.getPackageManager()) != null) {
                activity.startActivity(intent);
            }
        }
    }

    // Validating pincode
    public static boolean isValidPincode(String pass1) {

        return pass1 == null || pass1.length() != 6;
    }

    // Validating password
    public static boolean isValidPassword1(String pass1) {

        return pass1 == null || pass1.length() <= 5;
    }


    // validating password with retype password

    public static boolean isValidPassword(String pass1) {

        String PASSWORD_PATTERN =
                "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,20})";

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(pass1);
        return !matcher.matches();
    }


    //Validating Address
    public static boolean validAddress(String pass1) {

        String pat = "^[a-zA-Z0-9]+([-/:\\s,][a-zA-Z0-9]+)*?$";
        return !pass1.matches(pat) || pass1.length() <= 2;

    }

    public static boolean validSchool(String pass1) {

        return !pass1.matches("^[a-zA-Z0-9]+(\\s[a-zA-Z0-9]+)*?$") || pass1.length() <= 2;

    }

    //Validtaing Names
    public static boolean validateFirstName(String firstName) {
        return !firstName.matches("^[a-zA-Z]+(\\s[a-zA-Z]+)*?$");

    }

    //Validating Mobile
    public static boolean isValidMoblie(String pass1) {

        return pass1 == null || pass1.length() != 10;

    }

    public static boolean isValidOTP(String pass1) {

        return pass1 == null || pass1.length() != 6;

    }

    private static boolean isStringValid(String str) {
        return str != null && !str.isEmpty() && str.trim().length() > 0;
    }

    public static String getFormattedDate(String str, String j) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd hh:mm:sss");

        Date newDate = null;
        try {
            newDate = spf.parse(j);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (isStringValid(str)) {
            spf = new SimpleDateFormat(str, Locale.ENGLISH);
        } else {
            spf = new SimpleDateFormat(MyAppPrefsManager.DD_MMM_YYYY_DATE_FORMAT, Locale.ENGLISH);
        }
        return spf.format(newDate);
    }


    public static String getAppVersion(Context context) {
        String app_ver = "";
        try {
            app_ver = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return app_ver;
    }


    public static void internetCheck(Activity context) {
        IntentFilter intentFilter = new IntentFilter(NetworkStateChangeReceiver.NETWORK_AVAILABLE_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isNetworkAvailable = intent.getBooleanExtra(NetworkStateChangeReceiver.IS_NETWORK_AVAILABLE, false);
                String networkStatus = isNetworkAvailable ? "Available" : "Not Available";

                if (networkStatus.equals("Not Available")) {
                    Toast.makeText(context, "No Internet Found", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(context, " Internet Found", Toast.LENGTH_SHORT).show();

                }
            }
        }, intentFilter);
    }
}
