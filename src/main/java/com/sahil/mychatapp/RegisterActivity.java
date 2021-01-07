package com.sahil.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //declaring  widgets
  private   EditText UserNameET , UserPasswordET,UserConfirmPasswordET, UserEmailET;
   private Button RegisterBtn;
    // for FireBase
   private FirebaseAuth mAuth;
//  private   FirebaseDatabase database;
  private   DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //instantiating widgets
        UserNameET = findViewById(R.id.UserName_text);
        UserPasswordET = findViewById(R.id.UserPassword_text);
        UserConfirmPasswordET  =findViewById(R.id.confirem_Password_text);
        UserEmailET = findViewById(R.id.UserEmail_text);
        RegisterBtn = findViewById(R.id.Register_button);

        //Firebase auth ..getting by instance method
        mAuth = FirebaseAuth.getInstance();

        //Adding event Listener for register Button
        RegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username_text = UserNameET.getText().toString();
                String password_text = UserPasswordET.getText().toString();
                String confirm_password_txt  = UserConfirmPasswordET.getText().toString();
                String email_txt = UserEmailET.getText().toString();

                if(TextUtils.isEmpty(username_text) || TextUtils.isEmpty(password_text)||TextUtils.isEmpty(confirm_password_txt)
                        || TextUtils.isEmpty(email_txt)){
                    Toast.makeText(RegisterActivity.this, "Please Fill all details", Toast.LENGTH_SHORT).show();
                } else
                if(password_text.length() <6){
                    Toast.makeText(RegisterActivity.this, "Password should atleast 6 digits long", Toast.LENGTH_SHORT).show();
                }  else
                    if(!password_text.equals(confirm_password_txt)){
                        Toast.makeText(RegisterActivity.this, "Password doesn't match!", Toast.LENGTH_SHORT).show();
                    }
                    else{
                    RegisterNewUser(username_text, password_text, email_txt);
                }
            }
        });
    }

    private  void RegisterNewUser( final String username , String password ,String email){
     mAuth.createUserWithEmailAndPassword(email, password)
             .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                 @Override
                 public void onComplete(@NonNull Task<AuthResult> task) {
                     if(task.isSuccessful()){
                         FirebaseUser firebaseUser = mAuth.getCurrentUser();  // creating  firebase user
                         String userId = firebaseUser.getUid();         //getting userId

//                         database  =  FirebaseDatabase.getInstance();
//                        myRef = database.getReference("MyUsers").child(UserId);
                          myRef = FirebaseDatabase.getInstance()
                                  .getReference("MyUsers")
                                  .child(userId);



                         //creating HashMap to store UserId ,username and imageURL
                         HashMap<String ,String> hashMap = new HashMap<>();
                         hashMap.put("Id", userId);
                         hashMap.put("UserName", username);   //////////
                         hashMap.put("ImageURL", "default");
                         hashMap.put("status", "offline");

                         //if registration is successFull opening the main activity
                    myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 Log.d("SAHIL KR ", " gg " + task.isSuccessful());
                                 if(task.isSuccessful()){
                                     Log.d("SAHIL KR ", "Registeration is " + task.isSuccessful());
                                     Intent intent = new Intent(RegisterActivity.this , MainActivity.class);
                                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); //??????
                                     startActivity(intent);
                                     finish();
                                 }
                             }
                         });


                     } else {
                         Log.d("SAHIL KR", " errror is " + task.getException());
                         Toast.makeText(RegisterActivity.this, "Invalid email or password!", Toast.LENGTH_SHORT).show();
                     }
                 }
             });
    }
}