package com.example.healthcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationData;
import com.example.healthcare.MedicineClickInterface;
import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationResponse;
import com.example.healthcare.R;

import java.util.ArrayList;
import java.util.List;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationViewHolder> {


    private final MedicineClickInterface medicineClickInterfaceOnClickListener;
    private List<ViewMedicationData> medicationList;
    Context context;


    public MedicationAdapter(Context context, List<ViewMedicationData> medicationList, MedicineClickInterface medicineClickInterfaceOnClickListener) {
        this.medicineClickInterfaceOnClickListener = medicineClickInterfaceOnClickListener;
        this.medicationList = medicationList;

    }


    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.medications_recycler_layout, parent, false);
        context = parent.getContext();
        return new MedicationViewHolder(inflate, medicineClickInterfaceOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        ViewMedicationData medication = medicationList.get(position);

        holder.medicineName.setText(medication.getName());
        holder.medicineFrequency.setText(medication.getFrequency());
        holder.medicineImage.setImageResource(R.drawable.img);
        holder.medicineQuantity.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return medicationList == null ? 0 : medicationList.size();
    }
}

class MedicationViewHolder extends RecyclerView.ViewHolder {
    TextView medicineName, medicineQuantity, medicineFrequency;
    ImageView medicineImage;

    public MedicationViewHolder(@NonNull View itemView, MedicineClickInterface medicineClickInterfaceOnClickListener) {
        super(itemView);
        medicineName = itemView.findViewById(R.id.medicineName);
        medicineQuantity = itemView.findViewById(R.id.medicineQuantity);
        medicineFrequency = itemView.findViewById(R.id.medicineFrequency);
        medicineImage = itemView.findViewById(R.id.medicineImage);

        //onclick listener
        itemView.setOnClickListener(view -> {
            if (medicineClickInterfaceOnClickListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    medicineClickInterfaceOnClickListener.onItemClick(position);
                }

            }
        });

    }
}