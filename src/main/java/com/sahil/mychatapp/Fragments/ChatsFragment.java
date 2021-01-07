package com.sahil.mychatapp.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.sahil.mychatapp.Adapter.UserAdapter;
import com.sahil.mychatapp.LoginActivity;
import com.sahil.mychatapp.MainActivity;
import com.sahil.mychatapp.MessageActivity;
import com.sahil.mychatapp.Model.ChatList;
import com.sahil.mychatapp.Model.User;
import com.sahil.mychatapp.R;
import com.sahil.mychatapp.RegisterActivity;
import com.sahil.mychatapp.SearchUser;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private UserAdapter adapter;
    private List<ChatList> userList;
    private RecyclerView chatrecyclerview;
      FirebaseUser fuser;
      DatabaseReference reference;

    private List<User> mUsers;

    private FloatingActionButton fab;

    public ChatsFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        fab = view.findViewById(R.id.floatingActionButton);


        chatrecyclerview = view.findViewById(R.id.chat_recylerview);
        chatrecyclerview.setHasFixedSize(true);
        chatrecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        userList = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("ChatList").child(fuser.getUid());




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 ABC();
            }
        });


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();

                for(DataSnapshot snapshot1: snapshot.getChildren()) {

                    ChatList chatlist = snapshot1.getValue(ChatList.class);
                         userList.add(chatlist);
                }
                Log.d("Count ", "C " + userList.size());
                chatList();

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        return view;

    }



    private void chatList() {

        mUsers = new ArrayList<>();
             reference  = FirebaseDatabase.getInstance().getReference("MyUsers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                   User user = dataSnapshot.getValue(User.class);

                      for(ChatList chatlist : userList){
                          if(user.getId().equals(chatlist.getId()) ){
                              boolean ans = user.getId().equals(chatlist.getId());
                              Log.d("ANS ", "is " + ans);
                              mUsers.add(user);

                          }

                      }
                }
                adapter = new UserAdapter(getContext() ,mUsers,true);
                chatrecyclerview.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CHCH", "here it is  "+error);
            }
        });
    }



    public   void ABC(){
        Intent intent = new Intent(getActivity() , SearchUser.class);
        startActivity(intent);

    }

}
