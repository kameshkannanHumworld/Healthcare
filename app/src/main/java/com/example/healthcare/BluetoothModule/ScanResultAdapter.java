package com.example.healthcare.BluetoothModule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.Permissions.BluetoothUtil;
import com.example.healthcare.R;

import java.util.List;
import java.util.Objects;

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {

    private List<ScanResult> scanResults;
    private OnItemClickListener onItemClickListener;

    /*
            Constructor
                parmas1 - List (scan results)
                parmas1 - onItemClickListener (onItemClickListener)
    */
    public ScanResultAdapter(List<ScanResult> scanResults, OnItemClickListener onItemClickListener) {
        this.scanResults = scanResults;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //setup the recycler layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_device, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult result = scanResults.get(position);
            //get the recycler position and binding the data
            holder.bind(result);

    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }


    //interface for on item click listener
    public interface OnItemClickListener {
        void onItemClick(ScanResult item);
    }

    //Inner class view holder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView deviceName;
        private TextView deviceAddress;

        //constructor
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);
            itemView.setOnClickListener(this);
        }


        //bind the data to the UI
        @SuppressLint("MissingPermission")
        public void bind(ScanResult result) {
//            if (ActivityCompat.checkSelfPermission(itemView.getContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
                deviceName.setText(result.getDevice().getName());
                deviceAddress.setText(result.getDevice().getAddress());

        }


        //on item click listener
        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(scanResults.get(position));
            }
        }
    }

}
