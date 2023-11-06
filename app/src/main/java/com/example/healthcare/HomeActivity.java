package com.example.healthcare;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.healthcare.Fragments.HomeFragment;
import com.example.healthcare.Fragments.MedicationsFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    TextView navigationDrawerUserName, navigationDrawerUserUniqId;
    Toolbar toolbar;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Set Tool Bar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Devices");
        setSupportActionBar(toolbar);

        //Assign Id for the UI here
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationDrawerUserName = findViewById(R.id.navigationDrawerUserName);
        navigationDrawerUserUniqId = findViewById(R.id.navigationDrawerUserUniqId);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
         *   Navigation Toggle here
         *   params1 - Activity
         *   params2 - navigation drawer layout
         *   params3 - toolbar layout
         *   params4 - A String resource to describe the "open drawer" action for accessibility (Description)
         *   params5 - A String resource to describe the "Close drawer" action for accessibility (Description)
         * */
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav,
                R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home);
        }
    }

    //Navigation drawer menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            toolbar.setTitle("Devices");
        } else if (itemId == R.id.nav_medications) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MedicationsFragment()).commit();
            toolbar.setTitle("Medications");
        } else if (itemId == R.id.nav_logout) {
//            new MaterialAlertDialogBuilder(this)
//                    .setTitle("Confirmation")  // Set the title
//                    .setMessage("Are you sure want to Logout?")  // Set the message
//                    .setPositiveButton("OK", (dialog, which) -> {
//
//                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        startActivity(intent);
//                        finish();
//
//                    })
//                    .setNegativeButton("Cancel", null)
//                    .show();  // Show the dialog
            showCustomDialogBox("Are you sure want to Logout?", false);

        }

        //after click the menu navigation bar auto close
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            showCustomDialogBox("Are you sure want to Logout?", true);
        }
    }


    private void showCustomDialogBox(String message, boolean isOnBackPressed) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);

        tvMessage.setText(message);

        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnBackPressed) {
                    Intent intent = new Intent(Intent.ACTION_MAIN);
                    intent.addCategory(Intent.CATEGORY_HOME);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


}