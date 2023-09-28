package com.example.healthcare.BluetoothModule;

import android.annotation.SuppressLint;
import android.bluetooth.le.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthcare.R;

import java.util.List;
import java.util.Objects;

public class ScanResultAdapter extends RecyclerView.Adapter<ScanResultAdapter.ViewHolder> {

    private List<ScanResult> scanResults;
    private OnItemClickListener onItemClickListener;

    public ScanResultAdapter(List<ScanResult> scanResults, OnItemClickListener onItemClickListener) {
        this.scanResults = scanResults;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listitem_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScanResult result = scanResults.get(position);
        holder.bind(result);
    }

    @Override
    public int getItemCount() {
        return scanResults.size();
    }

    public interface OnItemClickListener {
        void onItemClick(ScanResult item);
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView deviceName;
        private TextView deviceAddress;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            deviceAddress = itemView.findViewById(R.id.device_address);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("MissingPermission")
        public void bind(ScanResult result) {
//            if (ActivityCompat.checkSelfPermission(itemView.getContext(), android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
//                return;
//            }
            if (!Objects.equals(result.getDevice().getName(), "Unnamed") || result.getDevice().getName() != null) {
                deviceName.setText(result.getDevice().getName());
            }
                deviceAddress.setText(result.getDevice().getAddress());
        }

        @Override
        public void onClick(View view) {
            int position = getAbsoluteAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                onItemClickListener.onItemClick(scanResults.get(position));
            }
        }
    }

}
