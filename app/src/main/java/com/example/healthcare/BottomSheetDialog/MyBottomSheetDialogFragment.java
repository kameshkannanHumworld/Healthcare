package com.example.healthcare.BottomSheetDialog;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.example.healthcare.R;

public class MyBottomSheetDialogFragment extends BottomSheetDialogFragment {

    ImageView bluetoothImage;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    TextView turnOnBluetoothTextView;
    BluetoothManager bluetoothManager;
    private static final int REQUEST_DISCOVER_BT = 300;
    private static final String TAG = "TAGi";
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch bluetoothSwitch;
    TextView tapToWakeUpTextView;

    private BluetoothAdapter bluetoothAdapter;
    CardView cardViewBottomSheet;
    ImageView cancelButton;
    ListView bluetoothDeviceListView;

    LinearLayout linearLayoutAvailableDevices;

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottomsheet_layout, container, false);

        // Initialize BluetoothAdapter
        bluetoothManager = requireContext().getSystemService(BluetoothManager.class);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //Assign Id here
        idAssigningMethod(view);

        //auto check Bluetooth on
        autoCheckBluetooth();

        //cancel button Method
        cancelButtonMethod();

        return view;
    }


    private void cancelButtonMethod() {
        cancelButton.setOnClickListener(view -> dismiss());
    }

    private void autoCheckBluetooth() {

        if (!bluetoothAdapter.isEnabled()) {
            bluetoothSwitchMethod();

        } else {
            bluetoothSwitch.setVisibility(View.GONE);
            turnOnBluetoothTextView.setVisibility(View.GONE);
            bluetoothImage.setVisibility(View.GONE);
            cardViewBottomSheet.setVisibility(View.VISIBLE);
            linearLayoutAvailableDevices.setVisibility(View.VISIBLE);

        }
    }


    private void bluetoothSwitchMethod() {
        bluetoothSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    bluetoothOnMethod();
                    bluetoothImage.setVisibility(View.GONE);
                    turnOnBluetoothTextView.setVisibility(View.GONE);
                    bluetoothSwitch.setVisibility(View.GONE);
                } else {
                    bluetoothImage.setVisibility(View.VISIBLE);
                    turnOnBluetoothTextView.setVisibility(View.VISIBLE);
                    bluetoothSwitch.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void bluetoothOnMethod() {
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(requireContext(), "Turning on bluetooth", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent, REQUEST_ENABLE_BT);
        }
    }

    private void idAssigningMethod(View view) {
        bluetoothImage = view.findViewById(R.id.bluetoothImage);
        bluetoothSwitch = view.findViewById(R.id.bluetoothSwitch);
        turnOnBluetoothTextView = view.findViewById(R.id.turnOnBluetoothTextView);
        cardViewBottomSheet = view.findViewById(R.id.cardViewBottomSheet);
        cancelButton = view.findViewById(R.id.cancelButton);
        bluetoothDeviceListView = view.findViewById(R.id.bluetoothDeviceListView);
        linearLayoutAvailableDevices = view.findViewById(R.id.linearLayoutAvailableDevices);
        tapToWakeUpTextView = view.findViewById(R.id.tapToWakeUpTextView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode != RESULT_OK) {
                bluetoothImage.setVisibility(View.VISIBLE);
                turnOnBluetoothTextView.setVisibility(View.VISIBLE);
                bluetoothSwitch.setVisibility(View.VISIBLE);
                bluetoothSwitch.setChecked(false);
                Toast.makeText(requireContext(), "Bluetooth not enabled", Toast.LENGTH_SHORT).show();
            } else {
                cardViewBottomSheet.setVisibility(View.VISIBLE);
                linearLayoutAvailableDevices.setVisibility(View.VISIBLE);

            }
        }

    }

}