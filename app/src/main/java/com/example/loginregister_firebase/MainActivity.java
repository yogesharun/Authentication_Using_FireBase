package com.example.loginregister_firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView register, forgotPass;
    private EditText text_email,text_password;
    private Button loginBTN;

    private FirebaseAuth mAuth;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        register = (TextView) findViewById(R.id.text_register);
        register.setOnClickListener(this);

        loginBTN = (Button)findViewById(R.id.ResetBTN);
        loginBTN.setOnClickListener(this);

        text_email = (EditText)findViewById(R.id.text_email);
        text_password = (EditText)findViewById(R.id.text_password);
        progressBar = (ProgressBar)findViewById(R.id.progress_reset);

        mAuth = FirebaseAuth.getInstance();
        forgotPass = (TextView)findViewById(R.id.text_forgotpass);
        forgotPass.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.text_register:
                startActivity(new Intent(this,Register.class));
                break;
            case R.id.ResetBTN:
                userLogin();
                break;

            case R.id.text_forgotpass:
                startActivity(new Intent(this,ForgetPassword.class));
                break;
        }
    }

    private void userLogin() {
        String email = text_email.getText().toString().trim();
        String password = text_password.getText().toString().trim();

        if(email.isEmpty()){
            text_email.setError("Email is required!");
            text_email.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            text_email.setError("Please enter valid email!");
            text_email.requestFocus();
            return;
        }

        if(password.isEmpty()){
            text_password.setError("Password Required!");
            text_password.requestFocus();
            return;
        }

        if(password.length() < 6){
            text_password.setError("min password length is 6 characters");
            text_password.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user.isEmailVerified()){
                        startActivity(new Intent(MainActivity.this,mobile_verification.class));
                    }else{
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                        progressBar.setVisibility(View.GONE);
                    }


                }else{
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);

                }
            }
        });
    }
}