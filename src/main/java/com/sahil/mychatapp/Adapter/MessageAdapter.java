package com.sahil.mychatapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sahil.mychatapp.Model.Chat;
import com.sahil.mychatapp.R;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {
    private static int LEFT =0;
    private static int RIGHT =1;

    private Context context;
    private List<Chat> mChat;

    private FirebaseUser fUser;


    public MessageAdapter(Context context, List<Chat> mChat) {
        this.context = context;
        this.mChat = mChat;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_right, parent,
                    false);
            return new ViewHolder(view);
        }
          else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_left, parent,
                    false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = mChat.get(position);

        try {
            holder.author_msg.setText(chat.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (position == mChat.size()-1){
            if (chat.isIsseen()){
                holder.isseen_msg.setText("seen");
            } else if(!chat.isIsseen()) {
                holder.isseen_msg.setText("Delivered");
            }
        } else {
            holder.isseen_msg.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }


    @Override
    public  int getItemViewType(int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();

        if(mChat.get(position).getSender().equals(fUser.getUid())) {
            return RIGHT;
          }
        else
            return LEFT;
    }


    public static class  ViewHolder extends RecyclerView.ViewHolder{

          public TextView author_msg;
          public  TextView isseen_msg;

        public ViewHolder (@NonNull View itemView) {
            super(itemView);
//              int f = getItemViewType();
//            if(f==RIGHT) {
//                author_msg = itemView.findViewById(R.id.show_msg1);
//            } else{
                author_msg = itemView.findViewById(R.id.show_msg);
                isseen_msg =  itemView.findViewById(R.id.seen_msg1);
//            }
        }
    }
}
