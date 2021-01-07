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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahil.mychatapp.Adapter.MessageAdapter;
import com.sahil.mychatapp.Model.Chat;
import com.sahil.mychatapp.Model.User;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class MessageActivity extends AppCompatActivity {
    private static final String TAG ="MessageActivity" ;
    private ImageView Message_imageView;
    private TextView Message_username;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private EditText send_msg_txt;
    private ImageButton send_btn;
    private FirebaseUser fuser;
    private DatabaseReference ref;
    private Intent intent ,inten1;
    private List<Chat>mChat;
    private MessageAdapter messageAdapter;
    private ImageButton backBtn;
    private TextView user_status;

     String userid;
     ValueEventListener seenlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ////////
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide(); //hide the title bar
        ///////
        setContentView(R.layout.activity_message);


        Message_imageView = findViewById(R.id.Message_imageView);
        Message_username = findViewById(R.id.Message_username);
        send_msg_txt = findViewById(R.id.send_msg_txt);
        send_btn = findViewById(R.id.send_btn);
        backBtn = findViewById(R.id.back_btn);
        user_status = findViewById(R.id.user_status);

        toolbar = findViewById(R.id.toolbar);

        //Recycler view
        recyclerView = findViewById(R.id.Mess_recyclerView);
//        recyclerView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
//        manager.setStackFromEnd(true);
        manager.canScrollVertically();
        recyclerView.setLayoutManager(manager);


        intent = getIntent();
        userid = intent.getStringExtra("userid");

        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = send_msg_txt.getText().toString();

                if (!msg.equals("")) {
                    sendMessage(msg, userid, fuser.getUid());    // fn to send msg to userid(friend) by you(firebaseUser.getUId)
                } else {
                    Toast.makeText(MessageActivity.this, "Type something..",
                            Toast.LENGTH_SHORT).show();
                }
                send_msg_txt.setText("");
            }

        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessageActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            }
        });

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        ref = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);
        Log.d(TAG, "userId   iss... " + ref);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);    // get the values of data of user and storing in user obj.
                Message_username.setText(user.getUserName());
                user_status.setText(user.getStatus());

                if (user.getImageURL().equals("default")) {
//                    Message_imageView.setImageResource(R.mipmap.ic_launcher_round);
                    Glide.with(getApplicationContext())
                            .load(R.drawable.default_dp)
                            .apply(RequestOptions.circleCropTransform())
                            .into(Message_imageView);
                } else {
//           Adding glide library
                    Glide.with(getApplicationContext())
                            .load(user.getImageURL())
                            .apply(RequestOptions.circleCropTransform())
                            .into(Message_imageView);
                }

                readMessage(fuser.getUid(), userid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d(TAG, "error hai ... " + error);
            }
        });

        seenmessage(userid);

    }



    private  void seenmessage(final String userid) {
        ref = FirebaseDatabase.getInstance().getReference("Chats");

       seenlistener= ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot d : snapshot.getChildren()) {

                    try{
                    Chat chat = d.getValue(Chat.class);
                    if (chat.getReceiver().equals(fuser.getUid()) && chat.getSender().equals(userid)) {

                        HashMap<String, Object> map = new HashMap<>();
                        map.put("isseen", true);
                        d.getRef().updateChildren(map);
                    }
                } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("SEEEN", " f " + error);
            }
        });
    }

    private void sendMessage(String Message, final String Receiver, String Sender) {
        DatabaseReference myreference  = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object>hashMap = new HashMap<>();  // creating a HashMap and storing all tha values in key value pairs and
        hashMap.put("Sender", Sender);                     //set them in address child by push set value.
        hashMap.put("Receiver", Receiver);
        hashMap.put("Message", Message);
        hashMap.put("isseen", false);

        myreference.child("Chats").push().setValue(hashMap);


        //Adding User to chat Fragments: Latest Chats with contacts
        final  DatabaseReference chatref = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(fuser.getUid())
                .child(userid);
        chatref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()){
                    chatref.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        final DatabaseReference chatRefReceiver = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(userid)
                .child(fuser.getUid());
        chatRefReceiver.child("id").setValue(fuser.getUid());


    }

    private void readMessage(final String senderId , final String receiverId) {

        mChat = new ArrayList<>();

    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Chats");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mChat.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                      Chat chat = dataSnapshot.getValue(Chat.class);


                      if (Objects.equals(chat.getSender(), senderId) && Objects.equals(chat.getReceiver(), receiverId)
                              || Objects.equals(chat.getSender(), receiverId) && Objects.equals(chat.getReceiver(), senderId)) {
                          mChat.add(chat);
                      }
                        messageAdapter = new MessageAdapter(MessageActivity.this, mChat);
                      recyclerView.setAdapter(messageAdapter);
                  }
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
     private void checkstatus(String status){
        ref = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());
        HashMap<String ,Object>map = new HashMap<>();
        map.put("status", status);
        ref.updateChildren(map);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkstatus("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.removeEventListener(seenlistener);
        checkstatus("offline");
    }
}