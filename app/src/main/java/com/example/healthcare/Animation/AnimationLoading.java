package com.example.healthcare.Animation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.Window;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;
import com.example.healthcare.R;
import com.google.android.material.snackbar.Snackbar;

public class AnimationLoading {

    private Activity activity;
    private Dialog alertDialog;
    private String deviceNameHere;
    private Context context;
    private Handler dismissalHandler = new Handler();


    //contructor [params1 - activity]
    public AnimationLoading(Activity activity) {
        this.activity = activity;
    }

    //start animation for login activity
    public void startLoadingDialogLoginActivity() {
        alertDialog = new Dialog(activity);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.loading_lottie_animation);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        alertDialog.setCancelable(false);
        alertDialog.show();

    }

    //start animation for bluetooth scan
    public void startLoadingDialogBlutoothScan(String deviceName, Context context) {
        this.deviceNameHere = deviceName;
        this.context = context;
        alertDialog = new Dialog(activity);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        alertDialog.setContentView(R.layout.loading_scan_animation);
        alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        lottieAutoReplayMethod();
        alertDialog.setCancelable(true);
        alertDialog.show();

        // Schedule the dismissal after 15 seconds
        dismissalHandler.postDelayed(() -> {
            dismissLoadingDialog();

            //snackbar
            if ((activity.getCurrentFocus()) != null) {
                Snackbar.make((activity.getCurrentFocus()), "Device Not Found", Snackbar.LENGTH_SHORT)
                        .setAction("Retry", view -> startLoadingDialogBlutoothScan(deviceNameHere, context))
                        .show();

            }
        }, 15000); // 15 seconds in milliseconds
    }

    //dismiss the animation dialog
    public void dismissLoadingDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();

            // Remove any pending callbacks
            dismissalHandler.removeCallbacksAndMessages(null);
        }

    }


    //scanning lottie continously auto replay method
    public void lottieAutoReplayMethod() {
        LottieAnimationView lottieAnimationView = alertDialog.findViewById(R.id.scanLottieAnimation);
        lottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        lottieAnimationView.playAnimation();

    }

}
