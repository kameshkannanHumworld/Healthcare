package com.example.healthcare;


import static com.example.healthcare.MainActivity.TOKEN;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthcare.Animation.AnimationLoading;
import com.example.healthcare.ApiClass.ApiClient;
import com.example.healthcare.MedicationsModule.FrequencyDropdown.MedicationFrequency;
import com.example.healthcare.MedicationsModule.FrequencyDropdown.MedicationFrequencyResponse;
import com.example.healthcare.MedicationsModule.FrequencyDropdown.MedicationFrequencyService;
import com.example.healthcare.MedicationsModule.MedicationsSaveApi.SaveApiRequest;
import com.example.healthcare.MedicationsModule.MedicationsSaveApi.SaveApiResponse;
import com.example.healthcare.MedicationsModule.MedicationsSaveApi.SaveApiService;
import com.example.healthcare.MedicationsModule.MedicationsValidations.ValidationApiRequest;
import com.example.healthcare.MedicationsModule.MedicationsValidations.ValidationApiService;
import com.example.healthcare.MedicationsModule.Medicine;
import com.example.healthcare.MedicationsModule.MedicineNameApiResponse;
import com.example.healthcare.MedicationsModule.MedicineNameApiService;
import com.example.healthcare.TextWatcher.ClearErrorTextWatcher;
import com.example.healthcare.TextWatcher.MedicineNameTextWatcher;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddMedicationActivity extends AppCompatActivity {
    Button submitButton;
    ImageView addMedicineBackButton;
    AutoCompleteTextView medicineNameInput, medicineFrequencyInput;
    TextView toolbarTitle;
    String mDateInput, mFrequencyInput;
    TextInputLayout textInputLayout4, textInputLayout3, textInputLayout5;
    TextInputEditText medicineQuantityInput, endDateTimeInput, notesInput, recordDateTimeInput;
    private final String TAG = "TAGi";
    public static boolean IS_EDIT = false;
    public static final int ID_DROPDOWN = 33689;
    public static final int PATIENT_ID = 53278;
    public static final int CAREPLAN_ID = 34534;
    public final Integer VISIT_ID = null;
    public static Integer MEDICTION_ID;
    private static boolean isSavedForBackPress = false;
    private final String MEDICTION_NAME = "";
    String medName, frequency, recordDateTime, endDateTime, notes;
    MaterialTimePicker picker;
    int quantity;
    static String proprietaryNameWithDosage, nonProprietaryNameWithDosage, mediProprietaryName, onlyTime, FREQUENCY_CODE;
    static Integer mediProdId;
    ArrayAdapter<String> arrayAdapterSpinner;
    private AnimationLoading animationLoading;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);
        animationLoading = new AnimationLoading(this);

        //id Assign here
        idAssignHere();

        //input validation
        medicineNameInput.addTextChangedListener(new MedicineNameTextWatcher(medicineNameInput));

        //toolbar title
        if (IS_EDIT) {
            toolbarTitle.setText("Edit Medication");
            submitButton.setText("UPDATE");
        } else {
            toolbarTitle.setText("Add Medication");
            submitButton.setText("SAVE");
        }

        //swipe right edit from MedicationFragment.class
        swipeRightEditIntentMehthod();

        //Autocomplete Medicine method
        AutocompleteMedicineMethod();

        //textinput layout - Dropdown
        textInputLayoutDropdownMethod();

        //date to save
        collectDateToSaveMethod();

        //button intent
        submitButtonIntentMethod();

        //back button listener
        backButtonListener();

    }


    private void swipeRightEditIntentMehthod() {
        Intent intent = getIntent();

        String medName = intent.getStringExtra("EDIT_MED_NAME");
        String frequency = intent.getStringExtra("EDIT_FREQUENCY");
        int quantity = intent.getIntExtra("EDIT_QUANTITY", 0); // Default value is 0 if not found
        String recordDateTime = intent.getStringExtra("EDIT_RECORD_DATE_TIME");
        String endDateTime = intent.getStringExtra("EDIT_END_DATE_TIME");
        String notes = intent.getStringExtra("EDIT_NOTES");

        medicineNameInput.setText(medName);
        medicineFrequencyInput.setText(frequency);
        recordDateTimeInput.setText(recordDateTime);
        endDateTimeInput.setText(endDateTime);
        notesInput.setText(notes);

        if (quantity == 0) {
            medicineQuantityInput.setText(null);
        } else {
            medicineQuantityInput.setText(String.valueOf(quantity));
        }
    }

    //Method for frequency dropdown
    private void textInputLayoutDropdownMethod() {
        // Retrofit instance set up
        MedicationFrequencyService service = ApiClient.getWebClient().create(MedicationFrequencyService.class);

        /*
            Below code for Retrofit Call
                Params1 - X-Auth_Token
                parms2 - login User Id value from TOKEN, which was decoded from JWT Decoder(Here hardcode)
        */
        Call<MedicationFrequencyResponse> call = service.getMedicationFrequencies(TOKEN, ID_DROPDOWN);
        call.enqueue(new Callback<MedicationFrequencyResponse>() {
            @Override
            public void onResponse(Call<MedicationFrequencyResponse> call, Response<MedicationFrequencyResponse> response) {
                if (response.isSuccessful()) {

                    //Use Map (frequency value, freq code)
                    Map<String, MedicationFrequency> data = response.body().getData();
                    Log.d(TAG, "textInputLayoutDropdownMethod: " + data);

                    //save the frequency descriptions here
                    List<String> frequencyDescriptions = new ArrayList<>();
                    for (MedicationFrequency frequency : data.values()) {
                        //loop to print the descriptions and save in the above List
                        frequencyDescriptions.add(frequency.getDescription());
                    }

                    //Set DropDown Layout
                    arrayAdapterSpinner = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown, frequencyDescriptions);
                    medicineFrequencyInput.setAdapter(arrayAdapterSpinner);

                    // Set default selection if needed
//                    int defaultSelectionIndex = 0; // Change this to the index of the default item
//                    medicineFrequencyInput.setText(arrayAdapterSpinner.getItem(defaultSelectionIndex), false);

                    // Set dropdown item click listener
                    medicineFrequencyInput.setOnItemClickListener((adapterView, view, i, l) -> {
                        String selectedDescription = adapterView.getItemAtPosition(i).toString();
                        for (Map.Entry<String, MedicationFrequency> entry : data.entrySet()) {
                            if (entry.getValue().getDescription().equals(selectedDescription)) {
                                mFrequencyInput = entry.getValue().getDescription();
                                FREQUENCY_CODE = entry.getValue().getCode();
                                break;
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MedicationFrequencyResponse> call, Throwable t) {
                // Handle API call failure
                Toast.makeText(getApplicationContext(), "Please try again", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "textInputLayoutDropdownMethod: " + t.getLocalizedMessage());
            }
        });

    }


    //Method for Date and time to Save
    private void collectDateToSaveMethod() {

        //recorded date and time
        recordDateTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Date picker Dialog Fragment
                popDateTimePicker(recordDateTimeInput);

            }
        });
        recordDateTimeInput.setLongClickable(false);
        recordDateTimeInput.setTextIsSelectable(false);


        //end date and time
        endDateTimeInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Date picker dialog fragment
                popDateTimePicker(endDateTimeInput);

            }
        });
        endDateTimeInput.setLongClickable(false);
        endDateTimeInput.setTextIsSelectable(false);
    }

    private void popDateTimePicker(TextInputEditText textInputEditText) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.DATE_PICKER)
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build()) // Set calendar constraints
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {

                mDateInput = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
//                textInputEditText.setText(mDateInput + " " + onlyTime);

                //M3 time picker dialog
                popTimePicker(textInputEditText);
            }
        });
        materialDatePicker.show(getSupportFragmentManager(), "Healthcare");
    }


    //Method for Medicine Search
    private void apiMedicineAutocompleteMethod(String medName) {
        // Retrofit setup
        MedicineNameApiService medicineNameApiService = ApiClient.getWebClient().create(MedicineNameApiService.class);

        /*
            parms1 - X-Auth-Token
            params2 - medicineName(type search by autocomplete field)
            params3 - Y/N (Here hardCode) default "Y"
        */
        Call<MedicineNameApiResponse> call = medicineNameApiService.searchMedicine(
                TOKEN,
                String.valueOf(medName),
                "Y"
        );

        //Retrofit call
        call.enqueue(new Callback<MedicineNameApiResponse>() {
            @Override
            public void onResponse(Call<MedicineNameApiResponse> call, Response<MedicineNameApiResponse> response) {
                if (response.isSuccessful()) {

                    //list to save medicines
                    assert response.body() != null;
                    List<Medicine> medicines = response.body().getData().getOthers();

                    // List to save medicine names only
                    List<String> medicineNames = new ArrayList<>();
                    for (Medicine medicine : medicines) {
                        String medicineNameFirst = medicine.getProprietaryNameWithDosage();
                        String medicineNameSecond = medicine.getNonProprietaryNameWithDosage();
                        String medicineName = medicineNameFirst + " " + medicineNameSecond;
//                        Log.d(TAG, "Full medicineName: " + medicineName);
                        if (medicineName != null && !medicineName.isEmpty()) {
                            medicineNames.add(medicineName);
                        }
                    }

                    //Set dropdown Layout
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, medicineNames);
                    medicineNameInput.setAdapter(adapter);

                    //Set dropdown item click Listener
                    medicineNameInput.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Medicine selectedMedicine = medicines.get(position);

                            //  get these values from PojoClass and set to String
                            proprietaryNameWithDosage = selectedMedicine.getProprietaryNameWithDosage();
                            nonProprietaryNameWithDosage = selectedMedicine.getNonProprietaryNameWithDosage();
                            mediProprietaryName = selectedMedicine.getMediProprietaryName();
                            mediProdId = selectedMedicine.getMediProdId();

                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<MedicineNameApiResponse> call, Throwable t) {
                // Handle Retrofit call failure
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });


    }


    private void AutocompleteMedicineMethod() {
        // Set up a TextWatcher
        medicineNameInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // after changed and call the autocomplete method here
                apiMedicineAutocompleteMethod(s.toString());
                Log.d(TAG, "afterTextChanged: " + s);
                medicineNameInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout3));
            }
        });
    }


    // Assign ID for the UI here
    private void idAssignHere() {
        submitButton = findViewById(R.id.submitButton);
        addMedicineBackButton = findViewById(R.id.addMedicineBackButton);
        medicineNameInput = findViewById(R.id.medicineNameInput);
        medicineFrequencyInput = findViewById(R.id.medicineFrequencyInput);
        medicineQuantityInput = findViewById(R.id.medicineQuantityInput);
        endDateTimeInput = findViewById(R.id.endDateTimeInput);
        recordDateTimeInput = findViewById(R.id.recordDateTimeInput);
        notesInput = findViewById(R.id.notesInput);
        textInputLayout4 = findViewById(R.id.textInputLayout4);
        textInputLayout3 = findViewById(R.id.textInputLayout3);
        textInputLayout5 = findViewById(R.id.textInputLayout5);
        toolbarTitle = findViewById(R.id.toolbarTitle);
    }

    //Submit Button Method
    private void submitButtonIntentMethod() {

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //animation loading start here
                animationLoading.startLoadingDialogLoginActivity();

                //get Values for save
                String medName = Objects.requireNonNull(medicineNameInput.getText()).toString().trim();
                Integer quantity = null;
                String quantityString = Objects.requireNonNull(medicineQuantityInput.getText()).toString();
                if (!quantityString.isEmpty()) {
                    quantity = Integer.parseInt(quantityString);
                }
                String endDate = mDateInput;
                String notes = Objects.requireNonNull(notesInput.getText()).toString().trim();


                //submit validation
                if (medName.trim().isEmpty()) {
                    textInputLayout3.setError("Medicine Name is Required");
                    medicineQuantityInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout5));
                    medicineFrequencyInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout4));
                } else if (mFrequencyInput == null) {
                    textInputLayout4.setError("Frequency is Required");
                    medicineNameInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout3));
                    medicineQuantityInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout5));
                } else if (quantity == null || quantity < 0) {
                    textInputLayout5.setError("Quantity is Required");
                    medicineFrequencyInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout4));
                    medicineNameInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout3));
                } else {
                    medicineFrequencyInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout4));
                    medicineNameInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout3));
                    medicineQuantityInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout5));

                    Log.d(TAG, "onClick: " + medName);
                    Log.d(TAG, "onClick: " + quantity);
                    Log.d(TAG, "onClick: " + mFrequencyInput);
                    Log.d(TAG, "onClick: " + endDate);
                    Log.d(TAG, "onClick: " + notes);
                    Log.d(TAG, "-----------------------------: ");
                    Log.d(TAG, "mediProdId: " + mediProdId);
                    Log.d(TAG, "FREQUENCY_CODE: " + FREQUENCY_CODE);


                    //medication validation method here
                    isSavedForBackPress = true;
                    medicationValidationMethod();
                }

            }


            //Method to Validate the medication here
            private void medicationValidationMethod() {

                //RetroFit Setup
                ValidationApiService validationApiService = ApiClient.getWebClient().create(ValidationApiService.class);

                ValidationApiRequest validationApiRequest = new ValidationApiRequest();
                validationApiRequest.setPatientId(PATIENT_ID);

                if (IS_EDIT) {
                    validationApiRequest.setMedicationId(MEDICTION_ID);
                    Log.d(TAG, "MEDICTION_ID: " + MEDICTION_ID);
                } else {
                    validationApiRequest.setMedicationId(null);
                    Log.d(TAG, "MEDICTION_ID: is null");
                }

                validationApiRequest.setCareplanId(CAREPLAN_ID);
                validationApiRequest.setName(medicineNameInput.getText().toString());
                validationApiRequest.setEffectiveDate(Objects.requireNonNull(recordDateTimeInput.getText()).toString());
                validationApiRequest.setLastEffectiveDate(endDateTimeInput.getText().toString());


                Log.d(TAG, "PATIENT_ID: " + PATIENT_ID);
                Log.d(TAG, "CAREPLAN_ID: " + CAREPLAN_ID);
                Log.d(TAG, "Med Name: " + medicineNameInput.getText().toString());
                Log.d(TAG, "Med Freqency: " + medicineFrequencyInput.getText().toString());
                Log.d(TAG, "Med Quantity: " + Objects.requireNonNull(medicineQuantityInput.getText()).toString());
                Log.d(TAG, "Record date and time: " + Objects.requireNonNull(recordDateTimeInput.getText()).toString());
                Log.d(TAG, "end date and time: " + Objects.requireNonNull(endDateTimeInput.getText()).toString());
                Log.d(TAG, "Notes: " + Objects.requireNonNull(notesInput.getText()).toString());

                /* Below code for Retrofit call
                       params1 - X-Auth-Token
                       params2 - ValidationApiRequest
                */
                Call<ResponseBody> call = validationApiService.sendDatatoValidation(TOKEN, validationApiRequest);
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        ResponseBody responseBody = response.body();
                        if (responseBody != null) {
                            try {
                                String responseBodyString = responseBody.string();
                                Log.d(TAG, "Response Body: " + responseBodyString);
                                Log.d(TAG, "Response Code: " + response.code());

                                // Display responseBodyString in a Snackbar
                                if (responseBodyString.equals("true")) {
                                    //Medications Save Api Method
                                    medicationsSaveApiMethod();
                                } else {
                                    animationLoading.dismissLoadingDialog(); //dismiss the loader
                                    Snackbar.make(submitButton, responseBodyString, Snackbar.LENGTH_LONG).show();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Request failed
                            Log.d(TAG, "onResponse: else working " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        //Handle Retrofit Call Failure
                        Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
                    }
                });


            }
        });
    }


    //Method to Save Medications in API
    private void medicationsSaveApiMethod() {

        //Retrofit Setup
        SaveApiService saveApiService = ApiClient.getWebClient().create(SaveApiService.class);
        String notes = Objects.requireNonNull(notesInput.getText()).toString().trim();
        String quantityText = Objects.requireNonNull(medicineQuantityInput.getText()).toString();

        SaveApiRequest saveApiRequest = new SaveApiRequest();
        saveApiRequest.setPatientId(PATIENT_ID);
        saveApiRequest.setCareplanId(CAREPLAN_ID);

        if (IS_EDIT) {
            saveApiRequest.setMedicationId(MEDICTION_ID);
        } else {
            saveApiRequest.setMedicationId(null);
        }

        saveApiRequest.setCode(mediProdId);
        saveApiRequest.setName(medicineNameInput.getText().toString());
        saveApiRequest.setNotes(notes);
        saveApiRequest.setEffectiveDate(Objects.requireNonNull(recordDateTimeInput.getText()).toString());
        saveApiRequest.setLastEffectiveDate(Objects.requireNonNull(endDateTimeInput.getText()).toString());
        saveApiRequest.setFrequency(FREQUENCY_CODE);
        saveApiRequest.setCustomFrequency("");
        if (!quantityText.isEmpty()) {
            saveApiRequest.setQuantity(Integer.valueOf(quantityText));
        } else {
            saveApiRequest.setQuantity(null);
        }
        saveApiRequest.setActiveFlag("Y");
        saveApiRequest.setProductCode("CCM");
        saveApiRequest.setVisitId(VISIT_ID);
        saveApiRequest.setIsFavoriteFlag("N");
//        saveApiRequest.setLogId(336);
        saveApiRequest.setCareplanLogMessageUserInput("A new medication " + medicineNameInput.getText().toString() + " has been added");
        saveApiRequest.setCareplanLogMessage("An existing medication " + medicineNameInput.getText().toString() + " has been added");


        /*  Retrofit Call
                Params1 - X-Auth-Token
                params2 - saveApiRequest
        */
        Call<SaveApiResponse> call = saveApiService.sendDatatoDatabase(TOKEN, saveApiRequest);
        call.enqueue(new Callback<SaveApiResponse>() {
            @Override
            public void onResponse(Call<SaveApiResponse> call, Response<SaveApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.code());
                    SaveApiResponse saveApiResponse = response.body();
                    if (Objects.equals(saveApiResponse.getStatus(), "success")) {
                        animationLoading.dismissLoadingDialog(); //dismiss the loader
                        if (IS_EDIT) {
                            Toast.makeText(getApplicationContext(), "Updated Sucessfully", Toast.LENGTH_SHORT).show();
                            IS_EDIT = false;
                        } else {
                            Toast.makeText(getApplicationContext(), "Saved Sucessfully", Toast.LENGTH_SHORT).show();
                        }

                        isSavedForBackPress = true;
                        onBackPressed(); // navigation to previous page
                    } else {
                        Toast.makeText(getApplicationContext(), "Saved Unsucessfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d(TAG, "onResponse: else" + response.code());
                }
            }

            @Override
            public void onFailure(Call<SaveApiResponse> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });
    }


    //method ask confirmation for exit , when Click back button
    private void backButtonListener() {
        addMedicineBackButton.setOnClickListener(v -> {
            if (!medicineNameInput.getText().toString().isEmpty() || !medicineFrequencyInput.getText().toString().isEmpty() || !medicineQuantityInput.getText().toString().isEmpty()) {
                showCustomDialogBox("Are you want to Exit? \n your Entered Data will be Lost..");
            } else {
                onBackPressed();
            }
        });

    }


    //this method for TimePickerDialog
    private void popTimePicker(TextInputEditText textInputEditText) {
        final Calendar calendar = Calendar.getInstance();   //get calender


        /*  Below code will trigger M3 TimePickerDialog  */
        picker = new MaterialTimePicker.Builder()
                .setTheme(R.style.TIME_PICKER)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();
        picker.show(getSupportFragmentManager(), "Healthcare");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                onlyTime = timeFormat.format(calendar.getTime());
                textInputEditText.setText(mDateInput + " " + onlyTime);

//                if (selectedTime.after(Calendar.getInstance())) {
//                    // The selected time is in the future
//                } else {
//                    Toast.makeText(getApplicationContext(), "Please select valid time ", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        picker.addOnNegativeButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textInputEditText.setText("");
            }
        });
    }


    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        if (isSavedForBackPress) {
            AddMedicationActivity.super.onBackPressed();
        } else if (!medicineNameInput.getText().toString().isEmpty() || !medicineFrequencyInput.getText().toString().isEmpty() || !medicineQuantityInput.getText().toString().isEmpty()) {
            showCustomDialogBox("Are you want to Exit? \n your Entered Data will be Lost..");

        } else {
            AddMedicationActivity.super.onBackPressed();
        }

    }

    //Custom Dialog
    private void showCustomDialogBox(String message) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_dialog_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        TextView tvMessage = dialog.findViewById(R.id.tvMessage);
        Button btnYes = dialog.findViewById(R.id.btnYes);
        Button btnNo = dialog.findViewById(R.id.btnNo);
        TextView dialogHeader = dialog.findViewById(R.id.dialogHeader);

        dialogHeader.setText("Confirmation");
        tvMessage.setText(message);

        btnYes.setOnClickListener(v -> {
            dialog.dismiss();
            AddMedicationActivity.super.onBackPressed();
        });

        btnNo.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }




}