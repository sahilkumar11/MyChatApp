package com.sahil.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

// instatiating weidgets
    EditText usernameLogin , userPasswordLogin;
    Button Login_btn ,LogintoRegister_btn;

    // Firebase auth
    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;  //creating firebase user

    @Override
    protected void onStart() {
        super.onStart();

        //instantiating firebaseUser
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();  // storing the current user in firebaseUser

        if(firebaseUser!= null){    //checking for user existence
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initializing widgets
        usernameLogin = findViewById(R.id.login_name);
        userPasswordLogin = findViewById(R.id.login_password);
        Login_btn = findViewById(R.id.Login_buttin);
        LogintoRegister_btn = findViewById(R.id.Login_to_Register_btn);

        // instantiating Firebase auth
        mAuth = FirebaseAuth.getInstance();


        //  from Login to register  Activity button
        LogintoRegister_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this , RegisterActivity.class);
                startActivity(intent);
            }
        });

        //Login Button
        Login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameLogin.getText().toString();
                String password = userPasswordLogin.getText().toString();

                //check if it is empty..?
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Please fill the details", Toast.LENGTH_SHORT).show();
                }
                else
                if(password.length() <6){
                    Toast.makeText(LoginActivity.this, "Password should atleast 6 digits long", Toast.LENGTH_SHORT).show();

                } else{
                    Toast.makeText(LoginActivity.this, "Login in progress..", Toast.LENGTH_SHORT).show();

                    mAuth.signInWithEmailAndPassword(email , password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        Intent intent = new Intent(LoginActivity.this , MainActivity.class);
//                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK); /////////
                                        startActivity(intent);
                                        finish();
                                    } else{
                                        Log.d("SAHIL KR", "Log in error is :" + task.getException());
                                        Toast.makeText(LoginActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}