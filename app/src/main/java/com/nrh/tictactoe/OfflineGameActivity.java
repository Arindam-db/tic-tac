package com.nrh.tictactoe;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class OfflineGameActivity extends AppCompatActivity {

    private GridView gridView;
    private String[] board;
    private boolean isPlayerOneTurn;
    private int playerAWins;
    private int playerBWins;
    private TextView playerAWinsTextView;
    private TextView playerBWinsTextView;
    private TextView currentPlayerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.game_offline);

        gridView = findViewById(R.id.grid_view);
        board = new String[9]; // 3x3 grid
        for (int i = 0; i < board.length; i++) {
            board[i] = "";
        }

        isPlayerOneTurn = true;
        playerAWins = 0;
        playerBWins = 0;

        playerAWinsTextView = findViewById(R.id.player_a_wins);
        playerBWinsTextView = findViewById(R.id.player_b_wins);
        currentPlayerTextView = findViewById(R.id.current_player);

        ImageButton returnButton = findViewById(R.id.btn_return);
        returnButton.setOnClickListener(v -> onBackPressed());

        GridAdapter adapter = new GridAdapter(this, board);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!board[position].equals("")) {
                    return; // Cell already occupied
                }

                if (isPlayerOneTurn) {
                    board[position] = "X";
                    currentPlayerTextView.setText("Player B's Turn");
                } else {
                    board[position] = "O";
                    currentPlayerTextView.setText("Player A's Turn");
                }

                adapter.notifyDataSetChanged();

                if (checkWinner()) {
                    if (isPlayerOneTurn) {
                        playerAWins++;
                        Toast.makeText(OfflineGameActivity.this, "Player A wins!", Toast.LENGTH_LONG).show();
                    } else {
                        playerBWins++;
                        Toast.makeText(OfflineGameActivity.this, "Player B wins!", Toast.LENGTH_LONG).show();
                    }
                    updateWinCounters();
                    resetBoard();
                } else if (isBoardFull()) {
                    Toast.makeText(OfflineGameActivity.this, "It's a draw!", Toast.LENGTH_LONG).show();
                    resetBoard();
                } else {
                    isPlayerOneTurn = !isPlayerOneTurn;
                }
            }
        });
    }

    private boolean checkWinner() {
        // Check rows, columns, and diagonals
        String[][] lines = {
                {board[0], board[1], board[2]},
                {board[3], board[4], board[5]},
                {board[6], board[7], board[8]},
                {board[0], board[3], board[6]},
                {board[1], board[4], board[7]},
                {board[2], board[5], board[8]},
                {board[0], board[4], board[8]},
                {board[2], board[4], board[6]},
        };

        for (String[] line : lines) {
            if (line[0].equals(line[1]) && line[1].equals(line[2]) && !line[0].equals("")) {
                return true;
            }
        }
        return false;
    }

    private boolean isBoardFull() {
        for (String cell : board) {
            if (cell.equals("")) {
                return false;
            }
        }
        return true;
    }

    private void resetBoard() {
        for (int i = 0; i < board.length; i++) {
            board[i] = "";
        }
        isPlayerOneTurn = true;
        currentPlayerTextView.setText("Player A's Turn");
        ((GridAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }

    private void updateWinCounters() {
        playerAWinsTextView.setText("Player A Wins: " + playerAWins);
        playerBWinsTextView.setText("Player B Wins: " + playerBWins);
    }
}
