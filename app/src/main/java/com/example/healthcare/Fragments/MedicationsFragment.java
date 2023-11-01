package com.example.healthcare.Fragments;

import static com.example.healthcare.AddMedicationActivity.CAREPLAN_ID;
import static com.example.healthcare.AddMedicationActivity.ID_DROPDOWN;
import static com.example.healthcare.AddMedicationActivity.MEDICTION_ID;
import static com.example.healthcare.MainActivity.TOKEN;
import static com.example.healthcare.AddMedicationActivity.PATIENT_ID;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.healthcare.Adapters.MedicationAdapter;

import com.example.healthcare.AddMedicationActivity;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.healthcare.ApiClass.ApiClient;
import com.example.healthcare.MedicationsModule.DeleteMedications.DeleteApiRequest;
import com.example.healthcare.MedicationsModule.DeleteMedications.DeleteApiResponse;
import com.example.healthcare.MedicationsModule.DeleteMedications.DeleteApiService;
import com.example.healthcare.MedicationsModule.FrequencyDropdown.MedicationFrequencyService;
import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationData;
import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationResponse;
import com.example.healthcare.MedicationsModule.ViewMedications.ViewMedicationService;
import com.example.healthcare.MedicineClickInterface;
import com.example.healthcare.R;
import com.example.healthcare.ViewMedicationActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MedicationsFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final String TAG = "TAGi";

    private String mParam1;
    private String mParam2;


    List<ViewMedicationData> medicationList;
    ViewMedicationData viewMedicationData;

    RecyclerView recyclerMedications;
    MedicationAdapter medicationAdapter;
    SwipeRefreshLayout swipeRefreshLayout;

    public MedicationsFragment() {
        // Required empty public constructor
    }

    public static MedicationsFragment newInstance(String param1, String param2) {
        MedicationsFragment fragment = new MedicationsFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_medications, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        //setup data with arraylist
        medicationList = new ArrayList<>();

        // Set click listener for the FAB
        floatingActionButtonMethod(view);

        //updateRecyclerViewMethod
        updateRecyclerViewMethod(view);

        //Item Touch Helper
        itemTouchHelperMethod(view);

        // Fetch data from API
//        fetchDataFromAPI();

        //swipe refresh layout
        swipeRefreshLayoutMethod(view);


        return view;
    }

    //method for swipe left,right
    private void itemTouchHelperMethod(View view) {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                if (direction == ItemTouchHelper.LEFT) { // From left to right swipe
                    deleteItem(position); // Delete the item from the database
                }

                if (direction == ItemTouchHelper.RIGHT) { // From right to left swipe
                    updateItem(position);
                }

                // Auto refresh
                medicationList.clear();
                fetchDataFromAPI();
                medicationAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

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


    //method to send the medication data to the ViewMedicationActivity by Intent
    @SuppressLint("NotifyDataSetChanged")
    private void viewItem(int position) {
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
        intent.putExtra("MEDICTION_ID", MEDICTION_ID);
        startActivity(intent);

        medicationAdapter.notifyDataSetChanged();
    }

    //Method to send the medication data to AddMedicationActivity for Edit/Update by Intent
    @SuppressLint("NotifyDataSetChanged")
    private void updateItem(int position) {
         viewMedicationData = medicationList.get(position);

        Intent intent = new Intent(requireContext(), AddMedicationActivity.class);
        intent.putExtra("EDIT_MED_NAME", viewMedicationData.getName());
        intent.putExtra("EDIT_FREQUENCY", viewMedicationData.getFrequency());
        intent.putExtra("EDIT_QUANTITY", viewMedicationData.getQuantity());
        intent.putExtra("EDIT_RECORD_DATE_TIME", viewMedicationData.getEffectiveDate());
        intent.putExtra("EDIT_END_DATE_TIME", viewMedicationData.getLastEffectiveDate());
        intent.putExtra("EDIT_NOTES", viewMedicationData.getNotes());
        MEDICTION_ID = viewMedicationData.getMedicationId();
        startActivity(intent);

        medicationAdapter.notifyDataSetChanged();
    }

    //Method to delete the medicine by swipe left
    @SuppressLint("NotifyDataSetChanged")
    private void deleteItem(int position) {
        ViewMedicationData viewMedicationData = medicationList.get(position);

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
            public void onResponse(Call<DeleteApiResponse> call, Response<DeleteApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Deleted Sucessfully", Toast.LENGTH_SHORT).show();
                    medicationAdapter.notifyDataSetChanged();
                }else{
                    Toast.makeText(requireContext(), "Please try again", Toast.LENGTH_SHORT).show();
                    medicationAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<DeleteApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                medicationAdapter.notifyDataSetChanged();
            }
        });



    }

    //method to undo the delete the medicine
    private void undoMethodHere() {
        Snackbar.make(recyclerMedications, "Item deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo", view -> {
                    Toast.makeText(requireContext(), "Undo worked", Toast.LENGTH_SHORT).show();
                }).show();
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
    private void swipeRefreshLayoutMethod(View view) {
        Log.d(TAG, "swipeRefreshLayoutMethod: ");

        swipeRefreshLayout.setOnRefreshListener(() -> {
            medicationList.clear();
            fetchDataFromAPI();
            medicationAdapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    //method to fetch the Medicine data from the API
    private void fetchDataFromAPI() {

        //retrofit setup
        ViewMedicationService service = ApiClient.getWebClient().create(ViewMedicationService.class);
        Call<ViewMedicationResponse> call = service.getMedicationsList(PATIENT_ID, CAREPLAN_ID, TOKEN, ID_DROPDOWN);

        //retrofit call
        call.enqueue(new Callback<ViewMedicationResponse>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(Call<ViewMedicationResponse> call, Response<ViewMedicationResponse> response) {
                ViewMedicationResponse responseBody = response.body();
                if (responseBody != null) {
                    List<ViewMedicationData> dataList = response.body().getData();
                    if (dataList != null) {
                        Log.d(TAG, "onResponse: " + response.code());
                        medicationList.addAll(dataList);
                        medicationAdapter.notifyDataSetChanged();
                    }
                } else {
                    Log.d(TAG, "onResponse: else" + response.code());
                }
            }

            @Override
            public void onFailure(Call<ViewMedicationResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }

    //set data to the recycler view
    private void updateRecyclerViewMethod(View view) {
        recyclerMedications = view.findViewById(R.id.recyclerMedications);
        recyclerMedications.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        medicationAdapter = new MedicationAdapter(requireContext(), medicationList, medicineClickInterfaceMethod);
        recyclerMedications.setAdapter(medicationAdapter);
    }

    //floating Action Button Method
    private void floatingActionButtonMethod(View view) {
        FloatingActionButton fab = view.findViewById(R.id.fabMedicalFragment);
        fab.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), AddMedicationActivity.class));
        });
    }

    //Interface for medicine click listener in recycler view
    private final MedicineClickInterface medicineClickInterfaceMethod = new MedicineClickInterface() {
        @Override
        public void onItemClick(int position) {
            viewMedicationData = medicationList.get(position);
            MEDICTION_ID = viewMedicationData.getMedicationId();
            viewItem(position);
        }
    };
}