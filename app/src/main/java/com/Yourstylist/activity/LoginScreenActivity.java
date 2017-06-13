package com.Yourstylist.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;


import com.Yourstylist.retrofit.Api;
import com.Yourstylist.R;
import com.Yourstylist.Utils;
import com.Yourstylist.retrofit.LoginRequest;
import com.Yourstylist.retrofit.LoginResponse;
import com.Yourstylist.retrofit.LoginResponseData;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.Yourstylist.Utils.showAppCloseDialog;
import static com.Yourstylist.Utils.tag;


public class LoginScreenActivity extends AppCompatActivity {
    private EditText txtEmail;
    private EditText txtPassword;
    private CheckBox chkShowPassword;
    private Button btnLogin;
    private TextView txtSignup;
    private TextInputLayout input1;
    private TextInputLayout input2;
    private TextView txtForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        initializeViews();
    }

    private void initializeViews() {
        txtEmail=(EditText)findViewById(R.id.txtEmail);
        txtPassword=(EditText)findViewById(R.id.txtPassword);
        chkShowPassword=(CheckBox)findViewById(R.id.chkShowPassword);
        btnLogin=(Button)findViewById(R.id.btnLogin);
        txtSignup=(TextView)findViewById(R.id.txtSignup);
        input1=(TextInputLayout)findViewById(R.id.input1);
        input2=(TextInputLayout)findViewById(R.id.input2);
        txtForgotPassword=(TextView)findViewById(R.id.txtForgotPassword);

        chkShowPassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    txtPassword.setTransformationMethod(null);
                    txtPassword.setSelection(txtPassword.length());
                }
                else{
                    txtPassword.setTransformationMethod(new PasswordTransformationMethod());
                    txtPassword.setSelection(txtPassword.length());
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txtEmail.getText().toString().isEmpty()){
                    input1.setError("Email Required");
                    txtEmail.requestFocus();
                }
                else if(txtPassword.getText().toString().isEmpty()){
                    input2.setError("Password Required");
                    input1.setError(null);
                    txtPassword.requestFocus();
                }
                else{
                    input1.setError(null);
                    input2.setError(null);
                    Utils.hideSoftKeyboard(LoginScreenActivity.this);

                    proceedForLogin();
                }
            }
        });

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEmail.setText("");
                txtPassword.setText("");
                input1.setError(null);
                input2.setError(null);
                Intent intent=new Intent(LoginScreenActivity.this,SignupScreenActivity.class);
                startActivity(intent);
            }
        });

        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginScreenActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
    }

    private void proceedForLogin() {
        final ProgressDialog progressDialog=new ProgressDialog(LoginScreenActivity.this);
        progressDialog.setMessage("Authenticating...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        HttpLoggingInterceptor interceptor=new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(interceptor)
                .readTimeout(30,TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Utils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        Api service=retrofit.create(Api.class);
        Call<LoginResponse> call=service.login(new LoginRequest(txtEmail.getText().toString(),txtPassword.getText().toString()));
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, final Response<LoginResponse> response) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        Log.d(tag,"Success: "+response.body().getSuccess());
                        if(response.code()==200){
                            if(response.body().getSuccess()){
                                LoginResponseData data=response.body().getLoginResponseData();
                                Log.d(tag,"Logged in successfully");
                                SharedPreferences sharedPreferences=getSharedPreferences(Utils.USER_SHARED_PREFERENCE,MODE_PRIVATE);
                                sharedPreferences.edit()
                                        .putString(Utils.USER_FIRST_NAME,data.getFirstName())
                                        .putString(Utils.USER_LAST_NAME,data.getLastName())
                                        .putString(Utils.USER_EMAIL,data.getEmail())
                                        .putString(Utils.USER_MOBILE,data.getMobile())
                                        .putString(Utils.USER_DEVICE_TOKEN,data.getDeviceToken())
                                        .apply();
                                Intent intent=new Intent(LoginScreenActivity.this,HomeScreenActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Snackbar snackbar=Snackbar.make(findViewById(R.id.activity_login_screen),"Invalid Email or Password",Snackbar.LENGTH_LONG);
                                snackbar.show();
                            }
                        }
                        else{
                            Log.d(tag,"Incorrect details");
                        }
                    }
                });
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.d(tag,"Internal Server Error");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        showAppCloseDialog(LoginScreenActivity.this);
    }
}
