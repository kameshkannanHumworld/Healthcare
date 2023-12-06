package com.example.healthcare.Fragments;

import static com.example.healthcare.CommonClass.*;
import static com.example.healthcare.AddMedicationActivity.MEDICTION_ID;
import static com.example.healthcare.Animation.Transition.zoomInTransition;
import static com.example.healthcare.MainActivity.TAG;
import static com.example.healthcare.MainActivity.TOKEN;
import static com.example.healthcare.NotificationsAndAlarm.ReminderManager.PREFERENCE_KEY;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.healthcare.Adapters.MedicationAdapter;
import com.example.healthcare.AddMedicationActivity;
import com.example.healthcare.ApiClass.ApiClient;
import com.example.healthcare.MedicationsModule.DeleteMedications.DeleteApiRequest;
import com.example.healthcare.MedicationsModule.DeleteMedications.DeleteApiResponse;
import com.example.healthcare.MedicationsModule.DeleteMedications.DeleteApiService;
import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationData;
import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationResponse;
import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationService;
import com.example.healthcare.MedicineClickInterface;
import com.example.healthcare.NotificationsAndAlarm.ReminderManager;
import com.example.healthcare.R;
import com.example.healthcare.ViewMedicationActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MedicationsFragment extends Fragment {

    //classes
    ViewMedicationData viewMedicationData;
    MedicationAdapter medicationAdapter;

    //datatypes
    List<ViewMedicationData> medicationList;
    public static Integer RECYCLER_POSITION_MEDICATION;
    private List<String> requestCodeForThisMedication;
    private List<String> deleteRequestCodeForThisMedication;
    private List<String> activeMedicationsList;
    private List<String> autoDeleteUnactiveMedicationsList;

    //UI views
    RecyclerView recyclerMedications;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView noMedicationsTextView;
    Context context;
    ProgressBar progressBarMedicationFragment;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medications, container, false);

        //Assign Id for the UI views here
        assignIdMethod(view);

        // Set click listener for the FAB
        floatingActionButtonMethod(view);

        //updateRecyclerViewMethod
        updateRecyclerViewMethod(view);

        //Item Touch Helper
        itemTouchHelperMethod();

        //swipe refresh layout
        swipeRefreshLayoutMethod();

        return view;
    }


    //Assign Id for the UI views here
    private void assignIdMethod(View view) {

        //assign UI Id's here
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        noMedicationsTextView = view.findViewById(R.id.noMedicationsTextView);
        progressBarMedicationFragment = view.findViewById(R.id.progressBarMedicationFragment);
        recyclerMedications = view.findViewById(R.id.recyclerMedications);

        //assign List here
        requestCodeForThisMedication = new ArrayList<>();
        deleteRequestCodeForThisMedication = new ArrayList<>();
        activeMedicationsList = new ArrayList<>();
        autoDeleteUnactiveMedicationsList = new ArrayList<>();
        medicationList = new ArrayList<>();
    }

    //remainder auto delete, when medication last effective date expired
    @SuppressLint("NewApi")
    private void autoDeleteRemainderMethod() {
        List<String> allRemaindersList = new ArrayList<>();
        // Retrieve the set of reminders from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        Set<String> reminders = preferences.getStringSet(PREFERENCE_KEY, new HashSet<>());

        //filter remainder for selected medicine
        for (String medicineReminder : reminders) {

            // Deserialize JSON string to extract reminder data
            try {
                JSONObject jsonObject = new JSONObject(medicineReminder);
                String requestCode = jsonObject.getString("requestCode");
                allRemaindersList.add(requestCode);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        // Filter allRemaindersList based on the data in activeMedicationsList
        allRemaindersList.removeIf(item -> containsMedicationID(item, activeMedicationsList));

        Log.e(TAG, "autoDeleteRemainderMethod-allRemaindersList : " + allRemaindersList);

        //filter and delete remainder for selected medicine
        for (String medicineReminder : reminders) {
            for (String remainder : allRemaindersList) {
                if (medicineReminder.contains(remainder)) {
                    autoDeleteUnactiveMedicationsList.add(medicineReminder);

                    // Deserialize JSON string to extract reminder data
                    try {
                        JSONObject jsonObject = new JSONObject(medicineReminder);
                        String uuid = jsonObject.getString("uuid");
                        ReminderManager.clearRemindersForMedicine(context, uuid);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        }

        Log.e(TAG, "autoDeleteRemainderMethod-auto Delete Unactive Medications List : " + autoDeleteUnactiveMedicationsList);

    }

    //method for find common medication found in both allRemaindersList and activeMedicationsList
    private boolean containsMedicationID(String item, List<String> medicationIDs) {
        for (String medicationID : medicationIDs) {
            if (item.contains(medicationID)) {
                return true;
            }
        }
        return false;
    }

    //delete remainder when delete the medicine
    private void deleteRemainderMethod(String filterPart) {

        // Retrieve the set of reminders from SharedPreferences
        SharedPreferences preferences = context.getSharedPreferences(PREFERENCE_KEY, Context.MODE_PRIVATE);
        Set<String> reminders = preferences.getStringSet(PREFERENCE_KEY, new HashSet<>());

        //filter remainder for selected medicine
        for (String medicineReminder : reminders) {
            if (medicineReminder.contains(filterPart)) {
                requestCodeForThisMedication.add(medicineReminder);
            }
        }
        Collections.sort(requestCodeForThisMedication);

        // Display reminders with hour and minute
        for (String reminderData : requestCodeForThisMedication) {
            Log.d(TAG, "delete Reminders For this Medicine: " + reminderData);
            String uuid;

            // Deserialize JSON string to extract reminder data
            try {
                JSONObject jsonObject = new JSONObject(reminderData);
                uuid = jsonObject.getString("uuid");
                Log.d(TAG, "displayRemindersForMedicine UUID: " + uuid);
                deleteRequestCodeForThisMedication.add(uuid);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //method for swipe left,right
    private void itemTouchHelperMethod() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getBindingAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) { // From left to right swipe

                    //ask confirmation before delete
                    DialogConfirmationMethod(position, true);
                }

                if (direction == ItemTouchHelper.RIGHT) { // From right to left swipe

                    //update the medication
                    DialogConfirmationMethod(position, false);
                }

                // Auto refresh
//                medicationList.clear();
//                fetchDataFromAPI();
//                medicationAdapter.notifyDataSetChanged();
//                swipeRefreshLayout.setRefreshing(false);

            }


            //method to set swipe left and right button and other styles
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                int whiteColor = ContextCompat.getColor(requireContext(), R.color.white);
                int redColor = ContextCompat.getColor(requireContext(), R.color.red);
                int backColor = ContextCompat.getColor(requireContext(), R.color.k_blue);
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftLabel("Delete")
                        .setSwipeLeftLabelColor(whiteColor)
                        .addSwipeLeftActionIcon(R.drawable.baseline_delete_24)
                        .setSwipeLeftActionIconTint(whiteColor)
                        .addSwipeLeftBackgroundColor(redColor)
                        //below for right, above for left
                        .addSwipeRightLabel("Edit")
                        .setSwipeRightLabelColor(whiteColor)
                        .addSwipeRightActionIcon(R.drawable.baseline_edit_24)
                        .setSwipeRightActionIconTint(whiteColor)
                        .addSwipeRightBackgroundColor(backColor)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerMedications);
    }

    //method ask confirmation before delete , when swipe right to left
    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    public void DialogConfirmationMethod(int position, Boolean isDelete) {

        final Dialog dialog = new Dialog(requireContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        TextView dialogHeader = dialog.findViewById(R.id.dialogHeader);

        if (isDelete) {
            dialogHeader.setText("Confirmation");
            tvMessage.setText("Are you sure want to Delete?");
        } else {
            dialogHeader.setText("Confirmation");
            tvMessage.setText("If you Update your medication, \n Your Remainder time will lost. Set it Again");
        }

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            if (isDelete) {
                deleteItem(position);
            } else {
                updateItem(position);
            }
        });

        btnNo.setOnClickListener(v -> {
            dialog.dismiss();
            medicationAdapter.notifyDataSetChanged();
        });

        dialog.show();
    }


    //method to send the medication data to the ViewMedicationActivity by Intent
    @SuppressLint("NotifyDataSetChanged")
    private void viewItem(int position, View view) {
        viewMedicationData = medicationList.get(position);

        Intent intent = new Intent(requireContext(), ViewMedicationActivity.class);
        intent.putExtra("MED_NAME", viewMedicationData.getName());
        intent.putExtra("FREQUENCY", viewMedicationData.getFrequency());
        intent.putExtra("QUANTITY", viewMedicationData.getQuantity());
        intent.putExtra("RECORD_DATE_TIME", viewMedicationData.getEffectiveDate());
        intent.putExtra("END_DATE_TIME", viewMedicationData.getLastEffectiveDate());
        intent.putExtra("NOTES", viewMedicationData.getNotes());
        intent.putExtra("FREQ_CODE", viewMedicationData.getFrequencyCode());
        MEDICTION_ID = viewMedicationData.getMedicationId();
        RECYCLER_POSITION_MEDICATION = position;
        intent.putExtra("MEDICTION_ID", MEDICTION_ID);
        startActivity(intent,zoomInTransition(view));

        medicationAdapter.notifyDataSetChanged();
    }

    //Method to send the medication data to AddMedicationActivity for Edit/Update by Intent
    @SuppressLint("NotifyDataSetChanged")
    private void updateItem(int position) {

        viewMedicationData = medicationList.get(position);

        //delete remainder when delete the medicine
        deleteRemainderMethod(String.valueOf(viewMedicationData.getMedicationId()));

        //delete the remainder for this medicine
        if (deleteRequestCodeForThisMedication != null && !deleteRequestCodeForThisMedication.isEmpty()) {
            for (String code : deleteRequestCodeForThisMedication) {
                Log.d(TAG, "Medication Fragment - Remainder deleted : " + code);
                ReminderManager.clearRemindersForMedicine(context, code);
            }
            deleteRequestCodeForThisMedication.clear();
        }

        Intent intent = new Intent(requireContext(), AddMedicationActivity.class);
        intent.putExtra("EDIT_MED_NAME", viewMedicationData.getName());
        intent.putExtra("EDIT_FREQUENCY", viewMedicationData.getFrequency());
        intent.putExtra("EDIT_QUANTITY", viewMedicationData.getQuantity());
        intent.putExtra("EDIT_RECORD_DATE_TIME", viewMedicationData.getEffectiveDate());
        intent.putExtra("EDIT_END_DATE_TIME", viewMedicationData.getLastEffectiveDate());
        intent.putExtra("EDIT_NOTES", viewMedicationData.getNotes());
        MEDICTION_ID = viewMedicationData.getMedicationId();
        IS_EDIT = true;
        startActivity(intent);

        medicationAdapter.notifyItemChanged(position);
    }

    //Method to delete the medicine by swipe left
    @SuppressLint("NotifyDataSetChanged")
    private void deleteItem(int position) {
        ViewMedicationData viewMedicationData = medicationList.get(position);

        //delete remainder when delete the medicine
        deleteRemainderMethod(String.valueOf(viewMedicationData.getMedicationId()));

        //set this selected data for delete
        DeleteApiRequest deleteApiRequest = new DeleteApiRequest();
        deleteApiRequest.setPatientId(PATIENT_ID);
        deleteApiRequest.setCareplanId(CAREPLAN_ID);
        deleteApiRequest.setMedicationId(viewMedicationData.getMedicationId());
        deleteApiRequest.setActiveFlag("Y");
        deleteApiRequest.setCareplanLogMessageUserInput("An existing medication \"" + viewMedicationData.getName() + "\" has been deleted");
        deleteApiRequest.setCareplanLogMessage("An existing medication \"" + viewMedicationData.getName() + "\" has been deleted");

        //DeleteApi Retrofit call  below
        DeleteApiService service = ApiClient.getWebClient().create(DeleteApiService.class);
        Call<DeleteApiResponse> call = service.deleteMedications(TOKEN, deleteApiRequest);
        call.enqueue(new Callback<DeleteApiResponse>() {
            @Override
            public void onResponse(@NonNull Call<DeleteApiResponse> call, @NonNull Response<DeleteApiResponse> response) {
                if (response.isSuccessful()) {
                    snackBarMethod("Deleted Sucessfully");

                    //delete the remainder for this medicine
                    if (deleteRequestCodeForThisMedication != null && !deleteRequestCodeForThisMedication.isEmpty()) {
                        for (String code : deleteRequestCodeForThisMedication) {
                            Log.d(TAG, "Medication Fragment - Remainder deleted : " + code);
                            ReminderManager.clearRemindersForMedicine(context, code);
                        }
                        deleteRequestCodeForThisMedication.clear();
                    }

                    //refresh
                    medicationList.remove(position);
                    medicationAdapter.notifyItemRemoved(position);

                    //if recycler view is null, it shows TextView (noMedicationsTextView)
                    int itemCount = medicationAdapter.getItemCount();
                    Log.d(TAG, "Recyeler view count: " + itemCount);
                    if (recyclerMedications.getAdapter() == null || recyclerMedications.getAdapter().getItemCount() == 0) {
                        // The RecyclerView is empty
                        recyclerMedications.setVisibility(View.GONE);
                        noMedicationsTextView.setVisibility(View.VISIBLE);
                    } else {
                        // The RecyclerView is not empty
                        recyclerMedications.setVisibility(View.VISIBLE);
                        noMedicationsTextView.setVisibility(View.GONE);
                    }

                    swipeRefreshLayout.setRefreshing(false);
                } else {
                    snackBarMethod("Please Try Again..");
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeleteApiResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });


    }

    //common snack bar for this fragment
    private void snackBarMethod(String message) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_SHORT).show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        medicationList.clear();
        fetchDataFromAPI();
        medicationAdapter.notifyDataSetChanged();
        swipeRefreshLayout.setRefreshing(false);
    }

    //swipe to refresh the Medicine changes in layout
    @SuppressLint("NotifyDataSetChanged")
    private void swipeRefreshLayoutMethod() {
        Log.d(TAG, "swipeRefreshLayoutMethod: ");

        swipeRefreshLayout.setOnRefreshListener(() -> {
            medicationList.clear();
            medicationAdapter.notifyDataSetChanged();
            fetchDataFromAPI();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    //method to fetch the Medicine data from the API
    private void fetchDataFromAPI() {
        progressBarMedicationFragment.setVisibility(View.VISIBLE);

        //retrofit setup
        ViewMedicationService service = ApiClient.getWebClient().create(ViewMedicationService.class);
        Call<ViewMedicationResponse> call = service.getMedicationsList(PATIENT_ID, CAREPLAN_ID, TOKEN, LOGINER_ID);

        //retrofit call
        call.enqueue(new Callback<ViewMedicationResponse>() {
            @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
            @Override
            public void onResponse(@NonNull Call<ViewMedicationResponse> call, @NonNull Response<ViewMedicationResponse> response) {
                ViewMedicationResponse responseBody = response.body();
                if (responseBody != null) {
                    List<ViewMedicationData> dataList = null;
                    if (response.body() != null) {
                        dataList = response.body().getData();
                    }
                    if (dataList != null) {
                        Log.d(TAG, "onResponse: " + response.code());
                        progressBarMedicationFragment.setVisibility(View.GONE);
                        medicationList.addAll(dataList);
                        medicationAdapter.notifyDataSetChanged();

                        //get active medicines Id in the list for auto delete remainder
                        for (ViewMedicationData s : medicationList) {
                            Log.e(TAG, "onResponse: " + s.getMedicationId());
                            activeMedicationsList.add(String.valueOf(s.getMedicationId()));
                        }

                        //remainder auto delete, when medication last effective date expired
                        autoDeleteRemainderMethod();

                        //if recycler view is null, it shows TextView (noMedicationsTextView)
                        int itemCount = medicationAdapter.getItemCount();
                        Log.d(TAG, "Recyeler view count: " + itemCount);
                        if (recyclerMedications.getAdapter() == null || recyclerMedications.getAdapter().getItemCount() == 0) {
                            // The RecyclerView is empty
                            recyclerMedications.setVisibility(View.GONE);
                            noMedicationsTextView.setVisibility(View.VISIBLE);
                        } else {
                            // The RecyclerView is not empty
                            recyclerMedications.setVisibility(View.VISIBLE);
                            noMedicationsTextView.setVisibility(View.GONE);
                        }
                    }
                } else {
                    Log.d(TAG, "onResponse: else" + response.code());
                    progressBarMedicationFragment.setVisibility(View.GONE);
                    recyclerMedications.setVisibility(View.GONE);
                    noMedicationsTextView.setVisibility(View.VISIBLE);
                    noMedicationsTextView.setText("Network Response Error");
                }
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call<ViewMedicationResponse> call, @NonNull Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                recyclerMedications.setVisibility(View.GONE);
                progressBarMedicationFragment.setVisibility(View.GONE);
                noMedicationsTextView.setVisibility(View.VISIBLE);
                noMedicationsTextView.setText("Please Try Again After Some Time ");
            }
        });
    }

    //set data to the recycler view
    private void updateRecyclerViewMethod(View view) {
        recyclerMedications.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        medicationAdapter = new MedicationAdapter(requireContext(), medicationList, medicineClickInterfaceMethod);
        recyclerMedications.setAdapter(medicationAdapter);
    }

    //floating Action Button Method
    private void floatingActionButtonMethod(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fabMedicalFragment);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddMedicationActivity.class);
            startActivity(intent,zoomInTransition(v));
        });
        fab.setImageTintList(ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.white)));
    }

    //Interface for medicine click listener in recycler view
    private final MedicineClickInterface medicineClickInterfaceMethod = new MedicineClickInterface() {
        @Override
        public void onItemClick(int position, View view) {
            viewMedicationData = medicationList.get(position);
            MEDICTION_ID = viewMedicationData.getMedicationId();
            viewItem(position,view);
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        autoDeleteUnactiveMedicationsList.clear();
        activeMedicationsList.clear();
    }
}