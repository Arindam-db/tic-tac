package com.nrh.tictactoe;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class OnlinePlayersActivity extends AppCompatActivity {

    private ListView playersListView;
    private ArrayList<String> playersList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_players);

        playersListView = findViewById(R.id.playersListView);
        playersList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, playersList);
        playersListView.setAdapter(adapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                playersList.clear();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    String userName = userSnapshot.child("userName").getValue(String.class);
                    if (userName != null) {
                        playersList.add(userName);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OnlinePlayersActivity.this, "Failed to load players", Toast.LENGTH_SHORT).show();
            }
        });

        playersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String opponentName = playersList.get(position);
                // Initiate game request
                initiateGameWith(opponentName);
            }
        });
    }

    private void initiateGameWith(String opponentName) {
        // Implement game initiation logic here
        Toast.makeText(this, "Request sent to " + opponentName, Toast.LENGTH_SHORT).show();
    }
}
