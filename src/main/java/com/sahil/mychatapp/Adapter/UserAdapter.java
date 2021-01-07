package com.sahil.mychatapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sahil.mychatapp.MessageActivity;
import com.sahil.mychatapp.Model.Chat;
import com.sahil.mychatapp.Model.User;
import com.sahil.mychatapp.R;

import java.util.List;
import java.util.Objects;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    private Context context;
    private List<User> mUsers;
  private boolean isChat;
  private DatabaseReference myRef;
  private FirebaseUser myfUser;
    private String last_msg;

    //constructor
    public UserAdapter(Context context, List<User> mUsers , boolean isChat) {
        this.context = context;
        this.mUsers = mUsers;
        this.isChat = isChat;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item,
                parent,false);
        return new UserAdapter.ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
     final  User user = mUsers.get(position);

       holder.user_item_name.setText(user.getUserName()); //////////////////


       if(Objects.equals(user.getImageURL(), "default"))
       {
           Glide.with(context)
                   .load(R.drawable.default_dp)
                   .apply(RequestOptions.circleCropTransform())
                   .into(holder.user_item_image);
       }
       else{
//           Adding glide library
           Glide.with(context)
                   .load(user.getImageURL())
                   .apply(RequestOptions.circleCropTransform())
                   .into(holder.user_item_image);
        }
        if (isChat){
            lastMessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }
///////////////////////////

       if(isChat){
           if(user.getStatus().equals("online")){
               holder.statusON.setVisibility(View.VISIBLE);
               holder.statusOFF.setVisibility(View.GONE);
           }
           else{
               holder.statusON.setVisibility(View.GONE);
               holder.statusOFF.setVisibility(View.VISIBLE);
           }
       } else{
           holder.statusON.setVisibility(View.GONE);
           holder.statusOFF.setVisibility(View.GONE);
       }
        myfUser = FirebaseAuth.getInstance().getCurrentUser();
       myRef = FirebaseDatabase.getInstance().getReference("Chats").child(user.getId());

       myRef.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               int c=0;
               for(DataSnapshot d : snapshot.getChildren()){
                   Chat chat = d.getValue(Chat.class);
                   if (chat.getReceiver().equals(myfUser.getUid()) && !chat.isIsseen()){

                   }

               }
           }


           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });





         // Adding a click listener for item view so that when the user clicks the user item ..he reaches the message activity
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Intent intent = new Intent(context, MessageActivity.class);
               intent.putExtra("userid", user.getId());
               context.startActivity(intent);
           }
       });

    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{
        public TextView user_item_name;
        public ImageView user_item_image;
        public ImageView statusON;
        public  ImageView statusOFF;
        public TextView last_msg;


        public ViewHolder (@NonNull View itemView) {
            super(itemView);
            user_item_name = itemView.findViewById(R.id.user_item_name);
           user_item_image = itemView.findViewById(R.id.user_item_image);
           statusON  =itemView.findViewById(R.id.status_on);
           statusOFF = itemView.findViewById(R.id.status_off);
            last_msg  = itemView.findViewById(R.id.last_msg);
        }
    }

    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        this.last_msg = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid())) {
                            UserAdapter.this.last_msg = chat.getMessage();
                        }
                    }
                }

                switch (UserAdapter.this.last_msg){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(UserAdapter.this.last_msg);
                        break;
                }

                UserAdapter.this.last_msg = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
