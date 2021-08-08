package com.example.loginregister_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity implements View.OnClickListener{
    private TextView txtRegister;
    private Button BTNregister;
    private EditText InputUser,InputEmail,InputPassword,InputConfirmpass;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        BTNregister = (Button)findViewById(R.id.BTNregister);
        BTNregister.setOnClickListener(this);

        InputUser = (EditText)findViewById(R.id.InputUsername);
        InputEmail = (EditText)findViewById(R.id.InputEmail);
        InputPassword = (EditText)findViewById(R.id.InputPassword);
        InputConfirmpass = (EditText)findViewById(R.id.InputConfirmPass);

        progressBar = (ProgressBar)findViewById(R.id.progress_otp);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.BTNregister:
                registerUser();
                break;
        }

    }

    private void registerUser() {
        String name = InputUser.getText().toString().trim();
        String email = InputEmail.getText().toString().trim();
        String password = InputPassword.getText().toString().trim();
        String confirmPass = InputConfirmpass.getText().toString().trim();

        if(name.isEmpty()){
            InputUser.setError("Full name is Requireed!");
            InputUser.requestFocus();
            return;
        }

        if(email.isEmpty()){
            InputEmail.setError("Email is required!");
            InputEmail.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            InputEmail.setError("Please Provide Valid Email");
            InputEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            InputPassword.setError("Password is Required");
            InputPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            InputPassword.setError("Min password length should be 6 characters");
            InputPassword.requestFocus();
            return;
        }

        if(!confirmPass.equals(password)){
            InputConfirmpass.setError("Password does not match!");
            InputConfirmpass.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            User user = new User(name,email);

                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Register.this, "User has been Registered Successfully!", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }else{
                                        Toast.makeText(Register.this, "Failed to Register! try again", Toast.LENGTH_LONG).show();
                                        progressBar.setVisibility(View.GONE);
                                    }

                                }
                            });
                        }else{
                            Toast.makeText(Register.this, "Failed to Register! try again", Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }

                    }
                });
    }
}