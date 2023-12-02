package com.example.healthcare.Animation;

import android.app.ActivityOptions;
import android.os.Bundle;
import android.view.View;

public class Transition {

    //zoom in - icon to Activity ( Params1 - view )
    public static Bundle zoomInTransition(View view) {

        // Create the bundle for the transition animation
        Bundle bundle = ActivityOptions.makeScaleUpAnimation(
                view,  // View to scale up
                0,      // The starting X coordinate of the view
                0,      // The starting Y coordinate of the view
                view.getWidth(), // The initial width of the view
                view.getHeight() // The initial height of the view
        ).toBundle();

        return bundle;
    }

}
