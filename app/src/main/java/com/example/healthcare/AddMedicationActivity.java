package com.example.healthcare;


import static com.example.healthcare.MainActivity.TAG;
import static com.example.healthcare.MainActivity.TOKEN;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

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
import com.example.healthcare.NotificationsAndAlarm.RemainderData;
import com.example.healthcare.NotificationsAndAlarm.ReminderManager;
import com.example.healthcare.NotificationsAndAlarm.ReminderWorker;
import com.example.healthcare.TextWatcher.ClearErrorTextWatcher;
import com.example.healthcare.TextWatcher.MedicineNameTextWatcher;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

    //UI views
    Button submitButton;
    ImageView addMedicineBackButton;
    AutoCompleteTextView medicineNameInput, medicineFrequencyInput;
    TextView toolbarTitle;
    TextInputLayout textInputLayout4, textInputLayout3, textInputLayout5;
    TextInputEditText medicineQuantityInput, endDateTimeInput, notesInput, recordDateTimeInput;
    MaterialTimePicker picker;
    SharedPreferences sharedPreferences;
    CardView remainderCardView;
    private MaterialButton alarmButton1, alarmButton2, alarmButton3, alarmButton4;
    private Switch remainderToggleSwitch;
    private HorizontalScrollView remainderTimeSlotLayout;
    private RelativeLayout AddMedicationActivityRoot;

    //Data types
    String mDateInput, mFrequencyInput;
    public static boolean IS_EDIT = false;
    public static final int ID_DROPDOWN = 33689;
    public static final int PATIENT_ID = 53278;
    public static final int CAREPLAN_ID = 34534;
    public final Integer VISIT_ID = null;
    public static Integer MEDICTION_ID;
    private static boolean isSavedForBackPress = false;
    static String proprietaryNameWithDosage, nonProprietaryNameWithDosage, mediProprietaryName, onlyTime, FREQUENCY_CODE;
    static Integer mediProdId;
    ArrayAdapter<String> arrayAdapterSpinner;
    public static List<RemainderData> remainderList = new ArrayList<>();

    //classes
    private AnimationLoading animationLoading;


    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_medication);
        animationLoading = new AnimationLoading(this);

        //id Assign here
        idAssignHere();

        // Initialize sharedPreferences
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

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
        textInputLayoutDropdownMethod(false, null);

        //date to save
        collectDateToSaveMethod();

        //remainder function
        remainderFunctionalityMethod();

        //button intent
        submitButtonIntentMethod();

        //back button listener
        backButtonListener();

    }


    //remainder function here
    private void remainderFunctionalityMethod() {

        //when you select any frequency, then the remainder cardview will visible
        medicineFrequencyInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                remainderCardView.setVisibility(View.VISIBLE);

                //is toggled switch on or off
                remainderToggleSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (remainderToggleSwitch.isChecked()) {
                        remainderTimeSlotLayout.setVisibility(View.VISIBLE);
                        setTagForAlarmButtonMethod();
                        Log.d(TAG, "onCheckedChanged: " + FREQUENCY_CODE);

                        // Create the notification channel
                        ReminderWorker.createNotificationChannel(getApplicationContext());

                        //alarm count based upon Frequency Code
                        List<Button> alarmButtons = Arrays.asList(alarmButton1, alarmButton2, alarmButton3, alarmButton4);
                        int visibleButtons = getVisibleButtonsCount(FREQUENCY_CODE);

                        for (int i = 0; i < alarmButtons.size(); i++) {
                            Button currentButton = alarmButtons.get(i);
                            if (i < visibleButtons) {
                                currentButton.setVisibility(View.VISIBLE);
                            } else {
                                currentButton.setVisibility(View.INVISIBLE);
                            }
                        }

                    } else {
                        remainderTimeSlotLayout.setVisibility(View.GONE);
                        remainderList.clear();
                    }


                    //date picker listener -  set alarmButton listener
                    alarmButton1.setOnClickListener(view -> showTimePicker(alarmButton1));

                    alarmButton2.setOnClickListener(view -> showTimePicker(alarmButton2));

                    alarmButton3.setOnClickListener(view -> showTimePicker(alarmButton3));

                    alarmButton4.setOnClickListener(view -> showTimePicker(alarmButton4));
                });

            }
        });
    }

    //Method to get the remainder slot count and its visiblity
    private int getVisibleButtonsCount(String frequencyCode) {
        switch (frequencyCode) {
            case "ODAY":
                return 1;
            case "TDAY":
                return 2;
            case "THDA":
                return 3;
            case "FDAY":
                return 4;
            default:
                return 2;
        }
    }


    //unique Tag for each button
    private void setTagForAlarmButtonMethod() {
        alarmButton1.setTag("alarmButton1");
        alarmButton2.setTag("alarmButton2");
        alarmButton3.setTag("alarmButton3");
        alarmButton4.setTag("alarmButton4");
    }


    //time picker to pick the time for remainder
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void showTimePicker(MaterialButton alarmButton) {
        picker = new MaterialTimePicker.Builder()
                .setTheme(R.style.TIME_PICKER)
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();
        picker.show(getSupportFragmentManager(), "Healthcare");

        //positive button listener
        picker.addOnPositiveButtonClickListener(v -> {

            // Check for duplication
            RemainderData newRemainderData = new RemainderData(picker.getHour(), picker.getMinute(), alarmButton.getTag().toString());
            if (!isDuplicateRemainder(newRemainderData)) {

                // Set time in button
                if (picker.getHour() == 12) {
                    alarmButton.setText(String.format("%02d", picker.getHour()) + " : " + String.format("%02d", picker.getMinute()) + " PM");
                } else if (picker.getHour() > 12) {
                    alarmButton.setText(String.format("%02d", (picker.getHour() - 12)) + " : " + String.format("%02d", picker.getMinute()) + " PM");
                } else {
                    alarmButton.setText(picker.getHour() + " : " + picker.getMinute() + " AM");
                }

                // Identify the index based on the alarmButton's unique identifier
                int index = getAlarmButtonIndex(alarmButton.getTag());
                Log.d(TAG, "index for alarm : " + index);

                // Save remainder data for set remainder
                if (index >= 0 && index < remainderList.size()) {
                    // If the index is within the current size of the list
                    remainderList.set(index, newRemainderData);
                } else if (index == remainderList.size()) {
                    // If the index is exactly one position after the last index, add a new element
                    remainderList.add(newRemainderData);
                } else {
                    // Handle invalid index (e.g., index < 0)
                    // You may want to log a warning or throw an exception based on your use case
                    Log.w("TAG", "Invalid index: " + index);
                }

                for (RemainderData data : remainderList) {
                    Log.d("Remainder", "Hour: " + data.getHour() + ", Minute: " + data.getMinute() + ", Tag: " + data.getTag());

                }
            }
        });
    }


    //get each button unique identification
    private int getAlarmButtonIndex(Object tag) {
        String tagString = String.valueOf(tag);

        switch (tagString) {
            case "alarmButton1":
                return 0;
            case "alarmButton2":
                return 1;
            case "alarmButton3":
                return 2;
            case "alarmButton4":
                return 3;
            default:
                return -1; // Handle unknown tag
        }
    }


    //check if any duplicates found in remainder list
    private boolean isDuplicateRemainder(RemainderData newRemainderData) {
        for (RemainderData existingRemainder : remainderList) {
            if (existingRemainder.equals(newRemainderData)) {
                // Duplicate found, do not add
                // You might want to show a message or handle the duplication case in your UI
                return true;
            }
        }
        return false;
    }

    //when swipe the recycler view to the right , method to get data from the particular medicine via Intent
    private void swipeRightEditIntentMehthod() {
        Intent intent = getIntent();

        String medName = intent.getStringExtra("EDIT_MED_NAME");
        String frequency = intent.getStringExtra("EDIT_FREQUENCY");
        int quantity = intent.getIntExtra("EDIT_QUANTITY", 0); // Default value is 0 if not found
        String recordDateTime = intent.getStringExtra("EDIT_RECORD_DATE_TIME");
        String endDateTime = intent.getStringExtra("EDIT_END_DATE_TIME");
        String notes = intent.getStringExtra("EDIT_NOTES");

        medicineNameInput.setText(medName);
        recordDateTimeInput.setText(recordDateTime);
        endDateTimeInput.setText(endDateTime);
        notesInput.setText(notes);

        textInputLayoutDropdownMethod(true, frequency);

        if (quantity == 0) {
            medicineQuantityInput.setText(null);
        } else {
            medicineQuantityInput.setText(String.valueOf(quantity));
        }
    }

    //Method for frequency dropdown
    private void textInputLayoutDropdownMethod(boolean isEdit, String setFrequencyWhenEdit) {
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
            public void onResponse(@NonNull Call<MedicationFrequencyResponse> call, @NonNull Response<MedicationFrequencyResponse> response) {
                if (response.isSuccessful()) {

                    //Use Map (frequency value, freq code)
                    assert response.body() != null;
                    Map<String, MedicationFrequency> data = response.body().getData();
                    Log.d(TAG, "textInputLayoutDropdownMethod: " + data);

                    //save the frequency descriptions here
                    List<String> frequencyDescriptions = new ArrayList<>();
                    List<String> frequencyCodes = new ArrayList<>();
                    for (MedicationFrequency frequency : data.values()) {
                        //loop to print the descriptions and save in the above List
                        frequencyDescriptions.add(frequency.getDescription());
                        frequencyCodes.add(frequency.getCode());
                    }

                    //Set DropDown Layout
                    arrayAdapterSpinner = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown, frequencyDescriptions);
                    medicineFrequencyInput.setAdapter(arrayAdapterSpinner);

                    if (isEdit && setFrequencyWhenEdit != null) {
                        Log.e(TAG, "onResponse: dropdown is edit ");
                        int index = -1;
                        for (int i = 0; i < frequencyDescriptions.size(); i++) {
                            if (Objects.equals(setFrequencyWhenEdit, frequencyDescriptions.get(i))) {
                                index = i;
                                break; // Found the match, no need to continue searching
                            }
                        }

                        if (index != -1) {
                            // Set the text of the selected item
                            medicineFrequencyInput.setText(frequencyDescriptions.get(index));
                            FREQUENCY_CODE = frequencyCodes.get(index);
                        }
                    }


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
            public void onFailure(@NonNull Call<MedicationFrequencyResponse> call, @NonNull Throwable t) {
                // Handle API call failure
                snackBarMethod("Please try Again..",false); //snak bar method
                Log.d(TAG, "textInputLayoutDropdownMethod: " + t.getLocalizedMessage());
            }
        });

    }


    //Method for Date and time to Save
    private void collectDateToSaveMethod() {

        //recorded date and time
        recordDateTimeInput.setOnClickListener(view -> {

            //Date picker Dialog Fragment
            popDateTimePicker(recordDateTimeInput);

        });
        recordDateTimeInput.setLongClickable(false);
        recordDateTimeInput.setTextIsSelectable(false);


        //end date and time
        endDateTimeInput.setOnClickListener(view -> {

            //Date picker dialog fragment
            popDateTimePicker(endDateTimeInput);

        });
        endDateTimeInput.setLongClickable(false);
        endDateTimeInput.setTextIsSelectable(false);
    }

    //M3 Date picker Dialog Fragment
    private void popDateTimePicker(TextInputEditText textInputEditText) {
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

        MaterialDatePicker<Long> materialDatePicker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.DATE_PICKER)
                .setTitleText("Select Date")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder.build()) // Set calendar constraints
                .build();

        materialDatePicker.addOnPositiveButtonClickListener(selection -> {

            mDateInput = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault()).format(new Date(selection));
//                textInputEditText.setText(mDateInput + " " + onlyTime);

            //M3 time picker dialog
            popTimePicker(textInputEditText);
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
            public void onResponse(@NonNull Call<MedicineNameApiResponse> call, @NonNull Response<MedicineNameApiResponse> response) {
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
                        if (!(medicineName == null || medicineName.isEmpty())) {
                            medicineNames.add(medicineName);
                        }
                    }

                    //Set dropdown Layout
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_dropdown_item_1line, medicineNames);
                    medicineNameInput.setAdapter(adapter);

                    //Set dropdown item click Listener
                    medicineNameInput.setOnItemClickListener(
                            (parent, view, position, id) -> {
                                Medicine selectedMedicine = medicines.get(position);

                                //  get these values from PojoClass and set to String
                                proprietaryNameWithDosage = selectedMedicine.getProprietaryNameWithDosage();
                                nonProprietaryNameWithDosage = selectedMedicine.getNonProprietaryNameWithDosage();
                                mediProprietaryName = selectedMedicine.getMediProprietaryName();
                                mediProdId = selectedMedicine.getMediProdId();

                                Log.e(TAG, "proprietaryNameWithDosage: "+proprietaryNameWithDosage );
                                Log.e(TAG, "nonProprietaryNameWithDosage: "+nonProprietaryNameWithDosage );
                                Log.e(TAG, "mediProprietaryName: "+mediProprietaryName );
                                Log.e(TAG, "mediProdId: "+mediProdId );
                            });
                }
            }

            @Override
            public void onFailure(@NonNull Call<MedicineNameApiResponse> call, @NonNull Throwable t) {
                // Handle Retrofit call failure
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
            }
        });


    }

    //autocomplete list view in medications search field
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
        AddMedicationActivityRoot = findViewById(R.id.AddMedicationActivityRoot);
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
        remainderToggleSwitch = findViewById(R.id.remainderToggleSwitch);
        remainderCardView = findViewById(R.id.remainderCardView);
        remainderTimeSlotLayout = findViewById(R.id.remainderTimeSlotLayout);
        alarmButton1 = findViewById(R.id.alarmButton1);
        alarmButton2 = findViewById(R.id.alarmButton2);
        alarmButton3 = findViewById(R.id.alarmButton3);
        alarmButton4 = findViewById(R.id.alarmButton4);

    }

    //Submit Button Method
    private void submitButtonIntentMethod() {

        submitButton.setOnClickListener(view -> {


            //get Values for save
            String medName = Objects.requireNonNull(medicineNameInput.getText()).toString().trim();
            Integer quantity = null;
            String quantityString = Objects.requireNonNull(medicineQuantityInput.getText()).toString();
            if (!quantityString.isEmpty()) {
                quantity = Integer.parseInt(quantityString);
            }
            String endDate = mDateInput;
            String notes = Objects.requireNonNull(notesInput.getText()).toString().trim();

            medicineFrequencyInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout4));
            medicineNameInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout3));
            medicineQuantityInput.addTextChangedListener(new ClearErrorTextWatcher(textInputLayout5));


            //submit validation
            if (medName.trim().isEmpty()) {
                textInputLayout3.setError("Medicine Name is Required");
            } else if (TextUtils.isEmpty(medicineFrequencyInput.getText().toString().trim())) {
                textInputLayout4.setError("Frequency is Required");
            } else if (quantity == null || quantity < 0) {
                textInputLayout5.setError("Quantity is Required");
            } else {

                //animation loading start here
                animationLoading.startLoadingDialogLoginActivity();

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

        });
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
        Log.d(TAG, "Med Quantity: " + Objects.requireNonNull(medicineQuantityInput.getText()));
        Log.d(TAG, "Record date and time: " + Objects.requireNonNull(recordDateTimeInput.getText()));
        Log.d(TAG, "end date and time: " + Objects.requireNonNull(endDateTimeInput.getText()));
        Log.d(TAG, "Notes: " + Objects.requireNonNull(notesInput.getText()));

                /* Below code for Retrofit call
                       params1 - X-Auth-Token
                       params2 - ValidationApiRequest
                */
        Call<ResponseBody> call = validationApiService.sendDatatoValidation(TOKEN, validationApiRequest);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
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
                            snackBarMethod(responseBodyString,false); //snak bar method

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
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                //Handle Retrofit Call Failure
                Log.d(TAG, "onFailure: " + t.getLocalizedMessage());
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
            public void onResponse(@NonNull Call<SaveApiResponse> call, @NonNull Response<SaveApiResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "onResponse: " + response.code());
                    SaveApiResponse saveApiResponse = response.body();
                    if (Objects.equals(saveApiResponse.getStatus(), "success")) {
                        animationLoading.dismissLoadingDialog(); //dismiss the loader

                        if (IS_EDIT) {
                            //set Remainder for that medicine
                            if (remainderToggleSwitch.isChecked()) {

                                // Log and set all remainders in the list
                                for (RemainderData data : remainderList) {
//                                    Log.d("Remainder", "Hour: " + data.getHour() + ", Minute: " + data.getMinute() + ", Tag: " + data.getTag());
                                    String uniqueRemainderRequestCode = PATIENT_ID + "_" + saveApiResponse.getId() + "_" + data.getTag();
                                    Log.d(TAG, "Alarm uniqueRemainderRequestCode: " + uniqueRemainderRequestCode);
                                    ReminderManager.setReminder(getApplicationContext(), uniqueRemainderRequestCode, data.getHour(), data.getMinute(),medicineNameInput.getText().toString());
                                }
                                snackBarMethod("Updated Sucessfully..",true); //snak bar method

                                remainderList.clear();

                            } else {
                                snackBarMethod("Updated Sucessfully..",true); //snak bar method
                            }
                            IS_EDIT = false;


                        } else {

                            //set Remainder for that medicine
                            if (remainderToggleSwitch.isChecked()) {

                                // Log and set all remainders in the list
                                for (RemainderData data : remainderList) {
//                                    Log.d("Remainder", "Hour: " + data.getHour() + ", Minute: " + data.getMinute() + ", Tag: " + data.getTag());
                                    String uniqueRemainderRequestCode = PATIENT_ID + "_" + saveApiResponse.getId() + "_" + data.getTag();
                                    Log.d(TAG, "Alarm uniqueRemainderRequestCode: " + uniqueRemainderRequestCode);
                                    ReminderManager.setReminder(getApplicationContext(), uniqueRemainderRequestCode, data.getHour(), data.getMinute(),medicineNameInput.getText().toString());
                                }
                                snackBarMethod("Saved Sucessfully..",true); //snak bar method


                            } else {
                                snackBarMethod("Saved Sucessfully..",true); //snak bar method
                            }
                        }

                        isSavedForBackPress = true;
                        onBackPressed(); // navigation to previous page
                    } else {
                        snackBarMethod("Saved Unsucessfully..",true); //snak bar method
                    }
                } else {
                    Log.d(TAG, "onResponse: else" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<SaveApiResponse> call, @NonNull Throwable t) {
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


    //this method for M3 TimePickerDialog
    @SuppressLint("SetTextI18n")
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

        picker.addOnPositiveButtonClickListener(v -> {
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
        });

        picker.addOnNegativeButtonClickListener(v -> textInputEditText.setText(""));
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
    @SuppressLint("SetTextI18n")
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

    //common snack bar and Toast for this fragment
    private void snackBarMethod(String message,Boolean isToast) {
        if(!isToast){
            Snackbar.make(AddMedicationActivityRoot, message, Snackbar.LENGTH_SHORT).show();
        }else{
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }

    }

}