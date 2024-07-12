package com.nrh.tictactoe;

import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class OnlineGameActivity extends AppCompatActivity {
    private DatabaseReference gameRef;
    private ValueEventListener gameListener;

    private String[] board = new String[9]; // Assuming a 3x3 Tic Tac Toe board
    private boolean isPlayerOneTurn = true;
    private String playerOneId;
    private String playerTwoId;
    private GridAdapter adapter; // Reuse your existing GridAdapter

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_online);

        // Initialize board with empty values
        for (int i = 0; i < board.length; i++) {
            board[i] = "";
        }

        // Initialize Firebase Database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        gameRef = database.getReference("games").child("gameId"); // Replace "gameId" with a unique game ID

        // Set up GridView and adapter (assuming you have a GridView in your layout and a GridAdapter class)
        GridView gridView = findViewById(R.id.grid_view); // Replace with your GridView ID
        adapter = new GridAdapter(this, board); // Initialize your adapter
        gridView.setAdapter(adapter);

        // Listen for changes in game state
        gameListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GameState gameState = dataSnapshot.getValue(GameState.class);
                if (gameState != null) {
                    board = gameState.board;
                    isPlayerOneTurn = gameState.isPlayerOneTurn;
                    adapter.updateBoard(board); // Update the adapter with new board state
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(OnlineGameActivity.this, "Failed to load game state.", Toast.LENGTH_SHORT).show();
            }
        };
        gameRef.addValueEventListener(gameListener);

        // Initialize player IDs (replace with actual IDs from authentication or other source)
        playerOneId = "playerOneId"; // Replace with actual player one ID
        playerTwoId = "playerTwoId"; // Replace with actual player two ID

        // ... other initialization code
    }

}