package com.Yourstylist.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.Yourstylist.retrofit.Api;
import com.Yourstylist.R;
import com.Yourstylist.Utils;
import com.Yourstylist.Validation;
import com.Yourstylist.retrofit.SignUpRequest;
import com.Yourstylist.retrofit.SignUpResponse;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.Yourstylist.Utils.tag;

public class SignupScreenActivity extends AppCompatActivity {
    private EditText txtFirstName;
    private EditText txtLastName;
    private EditText txtEmail;
    private EditText txtMobile;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private Button btnSignUp;
    private String deviceToken="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_screen);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        deviceToken= FirebaseInstanceId.getInstance().getToken();
        Log.d(tag,"Got device token: "+deviceToken);
        initializeViews();
    }

    private void initializeViews() {
        txtFirstName=(EditText)findViewById(R.id.txtFirstName);
        txtLastName=(EditText)findViewById(R.id.txtLastname);
        txtEmail=(EditText)findViewById(R.id.txtEmail);
        txtMobile=(EditText)findViewById(R.id.txtMobile);
        txtPassword=(EditText)findViewById(R.id.txtPassword);
        txtConfirmPassword=(EditText)findViewById(R.id.txtConfirmPassword);
        btnSignUp=(Button)findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkValidation()){
                    continueToRegistration();
                }
            }
        });
    }

    private boolean checkValidation() {
        if(txtFirstName.getText().toString().isEmpty()){
            txtFirstName.setError("first name required");
            txtFirstName.requestFocus();
            return false;
        }
        else{
            if(txtFirstName.getText().toString().length()<3){
                txtFirstName.setError("minimum 3 characters required");
                txtFirstName.requestFocus();
                return false;
            }
            else{
                txtFirstName.setError(null);
            }
        }

        if(txtLastName.getText().toString().isEmpty()){
            txtLastName.setError("last name required");
            txtLastName.requestFocus();
            return false;
        }
        else{
            txtLastName.setError(null);
        }

        if(txtEmail.getText().toString().isEmpty()){
            txtEmail.setError("email required");
            txtEmail.requestFocus();
            return false;
        }
        else{
            if(Validation.isEmailAddress(txtEmail,true)){
                txtEmail.setError(null);
            }
            else{
                txtEmail.setError("enter valid email");
                return false;
            }
        }

        if(txtMobile.getText().toString().isEmpty()){
            txtMobile.setError("mobile number required");
            txtMobile.requestFocus();
            return false;
        }
        else{
            if(txtMobile.getText().toString().length()<4){
                txtMobile.setError("enter valid mobile number");
                txtMobile.requestFocus();
                return false;
            }
            else{
                txtMobile.setError(null);
            }
        }

        if(txtPassword.getText().toString().isEmpty()){
            txtPassword.setError("password required");
            txtPassword.requestFocus();
            return false;
        }
        else{
            if(txtPassword.getText().toString().compareTo(txtConfirmPassword.getText().toString())!=0){
                txtConfirmPassword.setError("passwords do not match");
                txtConfirmPassword.requestFocus();
                return false;
            }
            else{
                txtPassword.setError(null);
                txtConfirmPassword.setError(null);
            }
        }
        return true;
    }

    private void continueToRegistration() {
        final ProgressDialog progressDialog=new ProgressDialog(SignupScreenActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .readTimeout(10,TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Api service=retrofit.create(Api.class);

        Call<SignUpResponse> call=service.signup(new SignUpRequest(txtFirstName.getText().toString(),
                txtLastName.getText().toString(),txtEmail.getText().toString(),txtMobile.getText().toString(),
                txtPassword.getText().toString(),deviceToken));

        call.enqueue(new Callback<SignUpResponse>() {
            @Override
            public void onResponse(Call<SignUpResponse> call, Response<SignUpResponse> response) {

            }

            @Override
            public void onFailure(Call<SignUpResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
