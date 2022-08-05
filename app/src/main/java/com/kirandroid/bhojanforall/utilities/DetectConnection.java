package com.kirandroid.bhojanforall.utilities;

import android.content.Context;
import android.net.ConnectivityManager;


public class DetectConnection {
    public static boolean checkInternetConnection(Context context) {
        // detect internet connection
        ConnectivityManager con_manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        assert con_manager != null;
        return con_manager.getActiveNetworkInfo() != null
                && con_manager.getActiveNetworkInfo().isAvailable()
                && con_manager.getActiveNetworkInfo().isConnected();
    }
}