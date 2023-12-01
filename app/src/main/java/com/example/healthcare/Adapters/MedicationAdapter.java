/*
*   MedicationAdapter - this is a recycler view adapter for medication shown in Medication Fragment.
* */

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
import com.example.healthcare.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MedicationAdapter extends RecyclerView.Adapter<MedicationViewHolder> {


    private final MedicineClickInterface medicineClickInterfaceOnClickListener;
    private List<ViewMedicationData> medicationList;
    Context context;

    /*
     *       constructor
     *           params1 - context
     *           params2 - medicationList from scanCallBack class
     *           params3 - medicineClickInterfaceOnClickListener
     * */
    public MedicationAdapter(Context context, List<ViewMedicationData> medicationList, MedicineClickInterface medicineClickInterfaceOnClickListener) {
        this.medicineClickInterfaceOnClickListener = medicineClickInterfaceOnClickListener;
        this.medicationList = medicationList;
        this.context = context;

    }


    @NonNull
    @Override
    public MedicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //Setup the Layout
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.medications_recycler_layout, parent, false);
        context = parent.getContext();
        return new MedicationViewHolder(inflate, medicineClickInterfaceOnClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicationViewHolder holder, int position) {
        ViewMedicationData medication = medicationList.get(position);

        //Bind the data to the UI
        holder.medicineName.setText(medication.getName());
        holder.medicineFrequency.setText(medication.getFrequency());

        String quantity = String.valueOf(medication.getQuantity());
        holder.medicineQuantity.setText(String.format(" %s", quantity));

        //start Date
        if (medication.getEffectiveDate() == null || medication.getEffectiveDate().trim().isEmpty()) {
            holder.medicineStartDate.setText("----");
        } else {
            String startDate = getFormattedDate(medication.getEffectiveDate());
            holder.medicineStartDate.setText(String.format("%s   to ", startDate));
        }


        //end date
        if (medication.getLastEffectiveDate() == null || medication.getLastEffectiveDate().trim().isEmpty()) {
            holder.medicineEndDate.setText("----");
        } else {
            String endDate = getFormattedDate(medication.getLastEffectiveDate());
            holder.medicineEndDate.setText(endDate);
        }


        // ---------------------Assign a specific SVG to each medicine----------------------------
        for (int i = 1; i < medicationList.size(); i++) {
            int svgResource;
            String drawableName;

            if(i>5){
                drawableName = "medicine" + (position % 5 +1);
            }else{
                drawableName = "medicine" + (position+1);
            }

            svgResource = context.getResources().getIdentifier(drawableName, "drawable", context.getPackageName());
            holder.medicineImage.setImageResource(svgResource);
        }




    }

    @Override
    public int getItemCount() {
        return medicationList == null ? 0 : medicationList.size();
    }

    private static String getFormattedDate(String inputDate) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault());
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(inputDate);
            assert date != null;
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }
}

//Inner Class
class MedicationViewHolder extends RecyclerView.ViewHolder {
    TextView medicineName, medicineQuantity, medicineFrequency, medicineStartDate, medicineEndDate;
    ImageView medicineImage;

    //constructor
    public MedicationViewHolder(@NonNull View itemView, MedicineClickInterface medicineClickInterfaceOnClickListener) {
        super(itemView);

        //assign ID for UI views
        medicineName = itemView.findViewById(R.id.medicineName);
        medicineQuantity = itemView.findViewById(R.id.medicineQuantity);
        medicineFrequency = itemView.findViewById(R.id.medicineFrequency);
        medicineImage = itemView.findViewById(R.id.medicineImage);
        medicineStartDate = itemView.findViewById(R.id.startDateTextView);
        medicineEndDate = itemView.findViewById(R.id.endDateTextView);

        //onclick listener
        itemView.setOnClickListener(view -> {
            if (medicineClickInterfaceOnClickListener != null) {
                int position = getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    medicineClickInterfaceOnClickListener.onItemClick(position);
                }

            }
        });

    }
}