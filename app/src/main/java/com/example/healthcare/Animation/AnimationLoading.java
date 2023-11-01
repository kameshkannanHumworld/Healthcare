package com.example.healthcare.Animation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.example.healthcare.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class AnimationLoading {
    private Activity activity;
    private Dialog alertDialog;

    //contructor [params1 - activity]
    public AnimationLoading(Activity activity) {
        this.activity = activity;
    }

    //start animation for login activity
    public void startLoadingDialogLoginActivity() {
        alertDialog = new Dialog (activity);
        alertDialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        alertDialog.setContentView (R.layout.loading_lottie_animation);
        alertDialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
        alertDialog.show ();

    }

    //start animation for bluetooth scan
    public void startLoadingDialogBlutoothScan() {
        alertDialog = new Dialog (activity);
        alertDialog.requestWindowFeature (Window.FEATURE_NO_TITLE);
        alertDialog.setContentView (R.layout.loading_lottie_animation);
        alertDialog.getWindow ().setBackgroundDrawableResource (android.R.color.transparent);
        alertDialog.show ();
    }

    //dismiss the animation dialog
    public void dismissLoadingDialog() {
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }


}
