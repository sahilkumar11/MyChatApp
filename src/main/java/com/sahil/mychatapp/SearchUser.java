package com.sahil.mychatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahil.mychatapp.Adapter.UserAdapter;
import com.sahil.mychatapp.Model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SearchUser extends AppCompatActivity {


    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private List<User> mUsers;

    private TextView display;
    private ImageButton backBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        setContentView(R.layout.activity_search_user);

//                tool_bar = findViewById(R.id.tool_bar);
//
//
        display = findViewById(R.id.textView2);
        backBtn = findViewById(R.id.back_btn2);


                backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               GoBack();
            }
        });



        recyclerView = findViewById(R.id.search_recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mUsers = new ArrayList<>();
        readUsers();

    }

    private void GoBack() {
        Intent intent = new Intent(SearchUser.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void readUsers() {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("MyUsers");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                mUsers.clear();

                for(DataSnapshot snapshot: datasnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);

                    assert user != null;

                    assert firebaseUser != null;
                    if(!Objects.equals(user.getId(), firebaseUser.getUid())){
                        mUsers.add(user);
                    }
                    userAdapter = new UserAdapter(SearchUser.this, mUsers, false);
                    recyclerView.setAdapter(userAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("SAHILkR ", " ERROR IN USER Fragment is " + error.toException());
            }
        });



    }


}