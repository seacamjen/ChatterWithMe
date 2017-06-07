package com.example.guest.chatterwithme.ui;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.guest.chatterwithme.R;
import com.example.guest.chatterwithme.adapters.FirebaseMessageViewHolder;
import com.example.guest.chatterwithme.models.ChatMessage;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.input) EditText mInput;
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mSearchedMessageReference;
    private FirebaseRecyclerAdapter mFirebaseAdapter;


    protected void onCreate(Bundle savedInstanceState) {

        mSearchedMessageReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child("messages");
        setUpFirebaseAdapter();

        mSearchedMessageReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String message = messageSnapshot.getValue().toString();
                    Log.d("Messages updated", "Messages: " + message);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    getSupportActionBar().setTitle("Howdy, " + user.getDisplayName() + "!");
                } else {

                }
            }
        };
        mFab.setOnClickListener(this);
    }

    private void setUpFirebaseAdapter() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<ChatMessage, FirebaseMessageViewHolder>(ChatMessage.class, R.layout.message, FirebaseMessageViewHolder.class, mSearchedMessageReference) {
            @Override
            protected void populateViewHolder(FirebaseMessageViewHolder viewHolder, ChatMessage model, int position) {
                viewHolder.bindMessage(model);
            }
        };
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mFirebaseAdapter);
    }

    @Override
    public void onClick(View v) {
        if (v == mFab) {
            final String message = mInput.getText().toString().trim();
            DatabaseReference messageRef = FirebaseDatabase
                    .getInstance()
                    .getReference("messages");
            messageRef.push().setValue(new ChatMessage(message, "Person"));
            Log.v("Saving?", "Saved please oooooooooooo");
        }
        mInput.setText("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFirebaseAdapter.cleanup();
    }
}
