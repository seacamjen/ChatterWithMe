package com.example.guest.chatterwithme.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.guest.chatterwithme.R;
import com.example.guest.chatterwithme.models.ChatMessage;
import com.example.guest.chatterwithme.ui.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.parceler.Parcels;

import java.util.ArrayList;

public class FirebaseMessageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    View mView;
    Context mContext;

    public FirebaseMessageViewHolder(View itemView) {
        super(itemView);
        mView = itemView;
        mContext = itemView.getContext();
        itemView.setOnClickListener(this);
    }

    public void bindMessage(ChatMessage chatMessage) {
        TextView messageTime = (TextView) mView.findViewById(R.id.message_time);
        TextView messageUser = (TextView) mView.findViewById(R.id.message_user);
        TextView messageText = (TextView) mView.findViewById(R.id.message_text);

        messageUser.setText(chatMessage.getMessageUser());
        messageText.setText(chatMessage.getMessageText());
        messageTime.setText(Long.toString(chatMessage.getMessageTime()));
    }

    @Override
    public void onClick(View view) {
        final ArrayList<ChatMessage> chatMessages = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("messages");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    chatMessages.add(snapshot.getValue(ChatMessage.class));
                }

                int itemPosition = getLayoutPosition();

                Intent intent = new Intent(mContext, MainActivity.class);
                intent.putExtra("position", itemPosition + "");
                intent.putExtra("chatMessages", Parcels.wrap(chatMessages));

                mContext.startActivity(intent);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}