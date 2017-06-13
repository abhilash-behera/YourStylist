package com.Yourstylist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class Utils {
    public static final String USER_EMAIL="email";
    public static final String USER_FIRST_NAME="firstName";
    public static final String USER_LAST_NAME="lastName";
    public static final String USER_MOBILE="mobile";
    public static final String USER_DEVICE_TOKEN="deviceToken";
    public static final String USER_SHARED_PREFERENCE="sharedPreference";
    public static final String tag="yourStylist";
    public static final String BASE_URL="https://peaceful-falls-14094.herokuapp.com";

    public static void hideSoftKeyboard(Context context) {
        try{
            if(((Activity)context).getCurrentFocus()!=null) {
                InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(), 0);
            }
        }catch(Exception e){
            Log.d("sanketDoc","Exception in hiding keyboard: "+e.toString());
        }
    }

    public static void showAppCloseDialog(final Context context){
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        builder.setTitle("Hang On!")
                .setMessage("Do you want to close this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((Activity)context).finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    public static boolean isNetworkAvailable(Context context,View viewForSnackbar){
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo=connectivityManager.getActiveNetworkInfo();
        boolean available=activeNetworkInfo!=null&&activeNetworkInfo.isConnected();
        if(available)
            return true;
        Snackbar snackbar=Snackbar.make(viewForSnackbar,"No Internet Connection Available",Snackbar.LENGTH_LONG);
        snackbar.show();
        return false;
    }

    // This snippet hides the system bars.
    public static void hideSystemUI(Context context) {
        View mDecorView=((Activity)context).getWindow().getDecorView();
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    // This snippet shows the system bars. It does this by removing all the flags
// except for the ones that make the content appear under the system bars.
    public static void showSystemUI(Context context) {
        View mDecorView=((Activity)context).getWindow().getDecorView();
        mDecorView.setSystemUiVisibility(0);
    }
}
