package com.example.healthcare.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.healthcare.BluetoothModule.MyBluetoothGattCallback;
import com.example.healthcare.BottomSheetDialog.MyBottomSheetDialogFragment;
import com.example.healthcare.DeviceInfoActivity;
import com.example.healthcare.Permissions.BluetoothUtil;
import com.example.healthcare.Permissions.LocationUtil;
import com.example.healthcare.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HomeFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;
    Context context;
    ImageView weighScaleImage, bpMeterImage, ecgMeterImage, glucometerImage;




    public HomeFragment() {

    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Assign Id here
        idAssignMethod(view);

        //location and Bluetooth check
//        LocationUtil.requestLocationEnable(requireActivity());
//        BluetoothUtil.requestBluetoothEnable(requireActivity(),requireContext());

        // Set click listener for the FAB
        floatingActionButtonMethod(view);

        //Image Listeners
        ImageListenersMethod();

        return view;
    }

    private void ImageListenersMethod() {

        //Weight Scale Listener
        weighScaleImage.setOnClickListener(view -> {
          startActivity(new Intent(requireContext(), DeviceInfoActivity.class));
        });

        //BP Meter Listener
        bpMeterImage.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), DeviceInfoActivity.class));
        });

        //ECG meter Listener
        ecgMeterImage.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), DeviceInfoActivity.class));
        });

        //Glucometer Listener
        glucometerImage.setOnClickListener(view -> {
            startActivity(new Intent(requireContext(), DeviceInfoActivity.class));
        });
    }

    private void idAssignMethod(View view) {
        weighScaleImage = view.findViewById(R.id.weighScaleImage);
        glucometerImage = view.findViewById(R.id.glucometerImage);
        bpMeterImage = view.findViewById(R.id.bpMeterImage);
        ecgMeterImage = view.findViewById(R.id.ecgMeterImage);
    }


    private void floatingActionButtonMethod(View view) {
            FloatingActionButton fab = view.findViewById(R.id.fabHomeFragment);
            fab.setOnClickListener(v -> {
                // Show the bottom sheet dialog
                MyBottomSheetDialogFragment bottomSheetDialogFragment = new MyBottomSheetDialogFragment(context);
                bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
            });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


}