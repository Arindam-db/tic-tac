package com.nrh.tictactoe;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class MatchFind extends AppCompatActivity {

    private PlayerAdapter playerAdapter;
    private List<String> playerEmails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.match_find);

        RecyclerView playersRecyclerView = findViewById(R.id.playersRecyclerView);
        playersRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        playerEmails = new ArrayList<>();
        // Handle invite click, start game with selected player
        playerAdapter = new PlayerAdapter(this, playerEmails, this::startGameWithPlayer);
        playersRecyclerView.setAdapter(playerAdapter);

        loadOnlinePlayers();
    }

    private void loadOnlinePlayers() {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
        usersRef.orderByChild("presence").equalTo("online").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                playerEmails.clear();
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    String email = userSnapshot.child("email").getValue(String.class);
                    if (email != null) {
                        playerEmails.add(email);
                    }
                }
                playerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MatchFind.this, "Failed to load online players: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startGameWithPlayer(String email) {
        // Start game with the selected player
        // You can pass the player's email or other identifying information as needed
        Intent intent = new Intent(MatchFind.this, OnlineGameActivity.class);
        intent.putExtra("opponentEmail", email);
        startActivity(intent);
    }
}
