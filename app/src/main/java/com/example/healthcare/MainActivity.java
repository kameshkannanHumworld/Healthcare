package com.example.healthcare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.healthcare.Animation.AnimationLoading;
import com.example.healthcare.ApiClass.ApiClient;
import com.example.healthcare.LoginModule.LoginResponse;
import com.example.healthcare.LoginModule.LoginWebResponse;
import com.example.healthcare.LoginModule.UserService;
import com.example.healthcare.LoginModule.UserServiceWeb;
import com.example.healthcare.TextWatcher.AlphanumericTextWatcher;
import com.example.healthcare.TextWatcher.ClearErrorTextWatcher;
import com.example.healthcare.TextWatcher.PasswordTextWatcher;
import com.example.healthcare.TextWatcher.UsernameTextWatcher;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {
    Button loginButton;
    private final String TAG = "TAGi";
    public static String TOKEN;
    TextInputEditText userNameInput, passwordInput;
    TextInputLayout usernameTextInputLayout, passwordTextInputLayout;
    private AnimationLoading animationLoading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assing Id Here
        idAssignMethod();
        animationLoading = new AnimationLoading(this);
        statusBarColorMethod();

        //sharedpreference
        sharedPreferenceMethod();

        //Input validation
        inputValidationMethod();

        //Intent
        intentMethods();


    }

    private void statusBarColorMethod() {
        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.k_blue));
    }

    // Save the Username and Password using Shared Preference
    private void sharedPreferenceMethod() {
        userNameInput.setText("test_supriyaa");
        passwordInput.setText("Humworld@1");
    }

    //Validate the Username and Password field using TextWatcher
    private void inputValidationMethod() {
        userNameInput.addTextChangedListener(new AlphanumericTextWatcher(userNameInput)); //validate username field
        passwordInput.addTextChangedListener(new AlphanumericTextWatcher(passwordInput)); //validate Password field
        userNameInput.addTextChangedListener(new UsernameTextWatcher(userNameInput, usernameTextInputLayout)); //validate username field
        passwordInput.addTextChangedListener(new PasswordTextWatcher(passwordInput, passwordTextInputLayout)); //validate Password field
    }

    //login Button Intent Here
    private void intentMethods() {
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = Objects.requireNonNull(userNameInput.getText()).toString();
                String password = Objects.requireNonNull(passwordInput.getText()).toString();

                //set error null first
                if (userNameInput == null && passwordInput == null) {
                    usernameTextInputLayout.setError(null);
                    passwordTextInputLayout.setError(null);
                }

                //check is required or not
                if (name.equals("")) {
                    usernameTextInputLayout.setError("*Required");
                } else if (password.equals("")) {
                    passwordTextInputLayout.setError("*Required");
                } else {
                    assert userNameInput != null;
                    userNameInput.addTextChangedListener(new ClearErrorTextWatcher(usernameTextInputLayout));  //Clear the error
                    passwordInput.addTextChangedListener(new ClearErrorTextWatcher(passwordTextInputLayout));  //Clear the error

                    //loading animation
                    animationLoading.startLoadingDialogLoginActivity();
                    Handler handler = new Handler();
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            //login credentials to database here
                            //loginMethod(name, password);   //for mobdev base url
                            loginMethodWeb(name, password);  // for web dev base url
                        }
                    };
                    handler.postDelayed(runnable, 500);

                }
            }
        });

    }

    /*  Login Functionality for the Web Base Url
            param1 - username
            param2- Password
    * */
    private void loginMethodWeb(String name, String password) {
        UserServiceWeb apiService = ApiClient.getWebClient().create(UserServiceWeb.class);

        Log.e(TAG, "loginMethod: " + name);
        Log.e(TAG, "loginMethod: " + password);

        //Retrofit Api call
        Call<LoginWebResponse> call = apiService.loginUser(name, password);
        call.enqueue(new Callback<LoginWebResponse>() {
            @Override
            public void onResponse(Call<LoginWebResponse> call, Response<LoginWebResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    LoginWebResponse loginWebResponse = response.body();
                    Log.d(TAG, "Response: " + response.body().toString());

                    //if Sucess message from Api, then it Intent to next Activity
                    if (Objects.equals(loginWebResponse.getStatus(), "success")) {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        Toast.makeText(MainActivity.this, "Login Sucessfull", Toast.LENGTH_SHORT).show();

                        //Disable the Loader
                        animationLoading.dismissLoadingDialog();

                        TOKEN = loginWebResponse.getToken();
                        Log.d(TAG, "Login response token: " + TOKEN);


                    } else {
                        // Handle unsuccessful login (Bad Request)
                        Log.e(TAG, "Error Response: " + response.code());

                        //Disable the Loader
                        animationLoading.dismissLoadingDialog();

                        usernameTextInputLayout.setError("Invalid Credentials");
                        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    //handle other unsucessfull login (Build)
                    Toast.makeText(MainActivity.this, "Response Unsucessfull", Toast.LENGTH_SHORT).show();

                    //Disable the Loader
                    animationLoading.dismissLoadingDialog();


                    startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                    Log.d(TAG, "onResponse: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<LoginWebResponse> call, Throwable t) {
                //Handle API doesn't call
                Log.e(TAG, "Throwable: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                animationLoading.dismissLoadingDialog();
            }
        });
    }

    private void loginMethod(String name, String password) {
        // Get the Retrofit instance
        UserService apiService = ApiClient.getWebClient().create(UserService.class);

        // Create a LoginRequest object with your credentials
        Log.e(TAG, "loginMethod: " + name);
        Log.e(TAG, "loginMethod: " + password);

        // Make the network request
        Call<LoginResponse> call = apiService.loginUser(name, password);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<LoginResponse> call, @NonNull Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    LoginResponse loginResponse = response.body();
                    Log.d(TAG, "Response: " + response.body().toString());


                    if (Objects.equals(loginResponse.getStatus(), "success")) {
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        Toast.makeText(MainActivity.this, "Login Sucessfull", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "Login response token: " + TOKEN);


                    } else {
                        // Handle unsuccessful login
                        Log.e(TAG, "Error Response: " + response.code());
                        usernameTextInputLayout.setError("Invalid Credentials");
                        Toast.makeText(getApplicationContext(), "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(MainActivity.this, "Response Unsucessfull", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "onResponse: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginResponse> call, @NonNull Throwable t) {
                // Handle network failure
                Log.e(TAG, "Throwable: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Login Failure", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
            }
        });
    }

    //Assign Id for the UI here
    private void idAssignMethod() {
        loginButton = findViewById(R.id.loginButton);
        userNameInput = findViewById(R.id.userNameInput);
        passwordInput = findViewById(R.id.passwordInput);
        usernameTextInputLayout = findViewById(R.id.usernameTextInputLayout);
        passwordTextInputLayout = findViewById(R.id.passwordTextInputLayout);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        animationLoading.dismissLoadingDialog();
    }


}

